package com.zqz.jvm.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.zqz.jvm.bean.Node;
import com.zqz.jvm.jmx.notification.NotificationManager;
import com.zqz.jvm.jmx.notification.ZQZNotificationListerer;

/**
 * 2018-02-08 00:41
 * @author zqz
 */
public class MBeanUtil {

	private static Logger logger = LoggerFactory.getLogger(MBeanUtil.class);

	/** 用于构造MBean树形菜单 **/
	private final static String MBEAN_TREE_KEY_SPLIT = "####";
	private final static String MBEAN_TREE_ATTRIBUTE_NODE_ICON = "/static/zTree_v3/css/zTreeStyle/img/diy/2.png";

	/**
	 * 获取全部MXBean的Domains
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String[] getDomains(JVM jvm) throws Exception {
		try {
			return jvm.getClient().getMbsc().getDomains();
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
	}

	/**
	 * 获取MBeanInfo
	 * 
	 * @param objectName
	 * @return
	 * @throws Exception
	 */
	public static MBeanInfo getMBeanInfo(String objectName, JVM jvm) throws Exception {
		return jvm.getClient().getMbsc().getMBeanInfo(new ObjectName(objectName));
	}

	/**
	 * 获取参数对象的值
	 * 
	 * @param objectName
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public static Object getObjectNameValue(ObjectName objectName, String attrName, JVM jvm) throws Exception {
		if (jvm == null || objectName == null || attrName == null) {
			throw new IllegalArgumentException();
		}
		try {
			return jvm.getClient().getMbsc().getAttribute(objectName, attrName);
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		} catch (java.lang.UnsupportedOperationException e) {
			return "Unsupported";
		}
	}

	/**
	 * 执行方法
	 * 
	 * @param objectName
	 * @param methodName
	 * @param params
	 * @param signature
	 * @return
	 * @throws Exception
	 */
	public static Object execute(String objectName, String methodName, Object[] params, String[] signature, JVM jvm)
			throws Exception {
		try {
			Object[] params2 = new Object[params.length];
			for (int i = 0; i < signature.length; i++) {
				// 2018-02-04 21:58
				// 增加参数数据类型转换,解决调用方法传参类型都是字符串的问题,添加参数long与boolean类型转换
				if (signature[i].equals("long")) {
					params2[i] = Long.parseLong(String.valueOf(params[i]));
				} else if (signature[i].equals("boolean")) {
					params2[i] = Boolean.parseBoolean(String.valueOf(params[i]));
				} else {
					params2[i] = params[i];
				}
			}
			return JMXTypeUtil.getResult(jvm.getClient().getMbsc().invoke(ObjectName.getInstance(objectName),
					methodName, params2, signature));
		} catch (javax.management.ReflectionException e) {
			e.printStackTrace();
			return "Target VM does not support \"" + methodName + "\"";
		} catch (javax.management.RuntimeMBeanException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * 订阅
	 * 
	 * @param jvm
	 * @param objectName
	 * @throws Exception
	 */
	public static void subscribe(String userId, JVM jvm, String objectName, SimpMessagingTemplate template)
			throws Exception {
		NotificationListener newlistener = new ZQZNotificationListerer(jvm.getId(), objectName, template);
		if (NotificationManager.getListener(jvm.getId(), objectName) != null) {
			NotificationManager.add(userId, jvm.getId(), objectName);
			logger.debug(objectName + " listener is exist");
			return;
		}
		jvm.getClient().getMbsc().addNotificationListener(new ObjectName(objectName), newlistener, null, null);
		NotificationManager.add(userId, jvm.getId(), objectName);
		NotificationManager.addListener(jvm.getId(), objectName, newlistener);
	}

	/**
	 * 取消订阅
	 * 
	 * @param jvm
	 * @param objectName
	 * @throws Exception
	 */
	public static void unSubscribe(JVM jvm, String objectName) throws Exception {
		NotificationListener listener = NotificationManager.getListener(jvm.getId(), objectName);
		if (listener != null)
			jvm.getClient().getMbsc().removeNotificationListener(new ObjectName(objectName), listener);
	}

	/**
	 * 获取jmx树形列表 jconsle中属性结构目录为 第一级节点为domain
	 * 第二级节点开始为objectName.getKeyPropertyListString()
	 * 以逗号分隔，每一组等号后面的值为当前节点名称，最后一个节点是MBean名称 例如： { "className":
	 * "org.apache.tomcat.util.modeler.BaseModelMBean", "objectName": {
	 * "canonicalKeyPropertyListString":
	 * "name=HttpRequest2,type=RequestProcessor,worker=\"http-nio-8080\"",
	 * "domain": "Tomcat", "domainPattern": false, "keyPropertyList": { "name":
	 * "HttpRequest2", "type": "RequestProcessor", "worker": "\"http-nio-8080\""
	 * }, "keyPropertyListString":
	 * "type=RequestProcessor,worker=\"http-nio-8080\",name=HttpRequest2",
	 * "pattern": false, "propertyListPattern": false, "propertyPattern": false,
	 * "propertyValuePattern": false } 构建树相关的字段为： objectName.domain="tomcat"
	 * objectName.keyPropertyListString=
	 * "type=RequestProcessor,worker=\"http-nio-8080\",name=HttpRequest2"
	 * 在jconsole MBean中它的树形节点层级为： tomcat //objectName.domain 一级节点
	 * ----RequestProcessor //objectName.keyPropertyListString.type 二级节点
	 * --------http-nio-8080 //objectName.keyPropertyListString.worker 三级节点
	 * ------------HttpRequest2 //objectName.keyPropertyListString.name 四级节点
	 * 
	 * @return
	 * @throws InstanceNotFoundException
	 * @throws IntrospectionException
	 * @throws ReflectionException
	 * @throws IOException
	 * @throws MalformedObjectNameException
	 */
	public static Node getJMXTree(JVM jvm) throws Exception {
		String[] domains = getDomains(jvm);
		Map<String, Object> all = new HashMap<String, Object>();
		for (int i = 0; i < domains.length; i++) {
			all.put(domains[i], new HashMap<String, Object>());
		}
		Set<ObjectInstance> MBeanset = queryBeanNames(jvm, null, null);
		Iterator<ObjectInstance> mbeansetIterator = MBeanset.iterator();
		while (mbeansetIterator.hasNext()) {
			ObjectInstance objectInstance = (ObjectInstance) mbeansetIterator.next();
			ObjectName objectName = objectInstance.getObjectName();
			Object obj = all.get(objectName.getDomain());
			Map<String, Object> child;
			if (obj != null) {
				child = (Map<String, Object>) obj;
			} else {
				child = new HashMap<String, Object>();
				all.putAll(child);
			}
			String[] keyLists = objectName.getKeyPropertyListString().split(",");
			createMaps(child, keyLists, 0, objectName.getCanonicalName());
		}
		Node node = new Node();
		nodeTree(node, all);
		return node;
	}
	

	/**
	 * 递归生成jms树的中间结果
	 * 
	 * @param parent
	 * @param keyLists
	 * @param i
	 */
	private static void createMaps(Map<String, Object> parent, String[] keyLists, int i, String objectName) {
		String key = keyLists[i].substring(keyLists[i].indexOf("=") + 1);
		Object obj = parent.get(key);
		Map<String, Object> child;
		if (obj != null) {
			child = (Map<String, Object>) obj;
		} else {
			child = new HashMap<String, Object>();
			if (i < keyLists.length - 1) {
				parent.put(key, child);
			} else {
				parent.put(new StringBuilder(key).append(MBEAN_TREE_KEY_SPLIT).append(objectName).toString(), child);
			}
		}

		if (i < keyLists.length - 1) {
			createMaps(child, keyLists, ++i, objectName);
		}
	}

	/**
	 * 递归生成树的最终数据；
	 * 
	 * @param parentNode
	 * @param all
	 */
	private static void nodeTree(Node parentNode, Map<String, Object> all) {
		if (all == null || all.size() == 0) {
			return;
		}
		Set<Entry<String, Object>> set = all.entrySet();
		Node[] children = new Node[set.size()];
		parentNode.setChildren(children);
		Iterator<Entry<String, Object>> it = set.iterator();
		int i = 0;
		while (it.hasNext()) {
			Map.Entry<String, Object> nextNodes = (Map.Entry<String, Object>) it.next();
			Node nextItem = new Node();
			children[i++] = nextItem;
			Map<String, Object> nextValue = (Map<String, Object>) nextNodes.getValue();
			if (nextValue != null && nextValue.size() > 0) {
				nextItem.setName(nextNodes.getKey());
				nodeTree(nextItem, nextValue);
			} else {
				String[] names = nextNodes.getKey().split(MBEAN_TREE_KEY_SPLIT);
				// System.out.println(names[0]);
				nextItem.setName(names[0]);
				nextItem.setObjectName(names[1]);
				nextItem.setType((short) 1);
				nextItem.setIcon(MBEAN_TREE_ATTRIBUTE_NODE_ICON);
			}
		}
	}
	
	
	/**
	 * 设置jvm虚拟机参数
	 * @param jvm
	 * @param name
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static Object setHotSpotVmOption(JVM jvm , String name, String value) throws Exception {
		try {
		return jvm.getClient().getMbsc().invoke(ObjectName.getInstance("com.sun.management:type=HotSpotDiagnostic"), "setVMOption",
				new Object[] { name, value }, new String[] { "java.lang.String", "java.lang.String" });
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
	}
	
	/**
	 * 获取jvm参数
	 * @param jvm
	 * @param name
	 * @return  {name："PrintGCDetails",origin:"DEFAULT","value":false,"writeable":true}
	 * @throws Exception
	 */
	public static Map<String,Object> getHotSpotVmOption(JVM jvm , String name) throws Exception {
		ObjectName beanName;
		try {
			beanName = ObjectName.getInstance("com.sun.management:type=HotSpotDiagnostic");
			Object result = jvm.getClient().getMbsc().invoke(beanName, "getVMOption", new Object[] { name },
					new String[] { "java.lang.String" });
			return result == null ? null : JMXTypeUtil.managerCompositeDataSupport(result);
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
	}

	/**
	 * 获取ObjectName集合
	 * 
	 * @return
	 * @throws IOException
	 * @throws SchedulerException
	 */
	public static Set<ObjectInstance> queryBeanNames(JVM jvm, ObjectName name, QueryExp query)
			throws IOException, SchedulerException {
		try {
			return jvm.getClient().getMbsc().queryMBeans(name, query);
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
	}

	/**
	 * 获取ObjectName集合
	 * 
	 * @return
	 * @throws IOException
	 * @throws SchedulerException
	 */
	public static Set<ObjectName> queryNames(JVM jvm, ObjectName name, QueryExp query)
			throws IOException, SchedulerException {
		try {
			return jvm.getClient().getMbsc().queryNames(name, query);
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
	}
	
	/**
	 * dump堆文件
	 * 
	 * @param outputFile
	 * @param live
	 * @return
	 * @throws Exception
	 */
	public Object dump(JVM jvm,String outputFile, boolean live) throws Exception {
		try {
			return jvm.getClient().getMbsc().invoke(
					ObjectName.getInstance("com.sun.management:type=HotSpotDiagnostic")
					, "dumpHeap"
					, new Object[] { outputFile, live }
					, new String[] { "java.lang.String", "boolean" });
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
	}

	

	/**
	 * vm thread
	 * 去遍历所有线程的信息，由于是单线程处理，如果线程数量多的话是会影响到性能的，因为在扫描堆栈过程中，是在softpoint的状态。
	 * 
	 * 在函数 dumpAllThreads(boolean lockedMonitors, boolean
	 * lockedSynchronizers)里有2个参数 lockedMonitor, 和 lockedSynchronizer
	 * 而这两个参数分别控制两种锁ThreadInfo .getLockedMonitors() 和
	 * ThreadInfo.getLockedSynchronizers() a. Monitor 锁
	 * 就是我们传统使用的synchronized(Object obj), 可以通过MonitorInfo[]得到具体的锁的数量和信息 b.
	 * Locked ownable synchronizers 锁 常指的ReentrantLock 和 ReentrantReadWriteLock
	 * 锁 通过得到LockInfo[] 可以得到具体的类，锁的数量和信息
	 * 
	 * @throws IOException
	 * @throws NullPointerException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 */
	public CompositeData[] dumpThread(JVM jvm, boolean lockedMonitors, boolean lockedSynchronizers)throws Exception {
		Object obj = null;
		try {
			obj = jvm.getClient().getMbsc().invoke(
					ObjectName.getInstance("java.lang:type=Threading")
					, "dumpAllThreads"
					, new Object[] { lockedMonitors, lockedSynchronizers }
					, new String[] { "boolean", "boolean" });
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
		if (obj != null) {
			return (CompositeData[]) obj;
		}
		return null;
	}

	/**
	 * 查询一个mb是否被注册
	 * 
	 * @param objectName
	 * @return
	 * @throws IOException
	 */
	public static boolean isObjectNameRegistered(ObjectName objectName, JVM jvm) throws IOException {
		try {
			return jvm.getClient().getMbsc().isRegistered(objectName);
		} catch (java.rmi.ConnectException e) {
			try {
				jvm.getClient().changeConnectStatus(false);
			} catch (SchedulerException e1) {
				logger.error("", e1);
			}
			throw e;
		}
	}

	/** START -- 获取数据不同类别的数据 ***********************************************/

	/**
	 * 获取String数组
	 * 
	 * @param mbeanServer
	 * @param objectName
	 * @param attrName
	 * @param jvm
	 * @return
	 * @throws Exception
	 */
	public static String[] getStringArray(ObjectName objectName, String attrName, JVM jvm) throws Exception {
		if (jvm == null || objectName == null || attrName == null) {
			throw new IllegalArgumentException();
		}
		try {
			return (String[]) jvm.getClient().getMbsc().getAttribute(objectName, attrName);
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
	}

	/**
	 * 使用MBeanServer获取对象名为[objName]的MBean的[objAttr]属性值 静态代码: return
	 * MBeanServer.getAttribute(ObjectName name, String attribute)
	 * 
	 * @param mbeanServer
	 *            - MBeanServer实例
	 * @param objectName
	 *            - MBean的对象名
	 * @param attrName
	 *            - MBean的某个属性名
	 * @return 属性值
	 * @throws Exception
	 */
	public static String getString(ObjectName objectName, String attrName, JVM jvm) throws Exception {
		if (jvm == null || objectName == null || attrName == null) {
			throw new IllegalArgumentException();
		}
		try {
			return String.valueOf(jvm.getClient().getMbsc().getAttribute(objectName, attrName));
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		}
	}

	/**
	 * 使用MBeanServer获取对象名为[objName]的MBean的[objAttr]属性值 静态代码: return
	 * MBeanServer.getAttribute(ObjectName name, String attribute)
	 * 
	 * @param mbeanServer
	 *            - MBeanServer实例
	 * @param objectName
	 *            - MBean的对象名
	 * @param attrName
	 *            - MBean的某个属性名
	 * @return 属性值
	 * @throws Exception
	 */
	public static long getLong(ObjectName objectName, String attrName, JVM jvm) throws Exception {
		return Long.valueOf(getString(objectName, attrName, jvm));
	}

	/**
	 * 获取CompositeData属相的对象
	 * 
	 * @param mbeanServer
	 * @param objectName
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public static CompositeData getCompositeData(ObjectName objectName, String attrName, JVM jvm) throws Exception {
		try {
			Object obj = jvm.getClient().getMbsc().getAttribute(objectName, attrName);
			if (obj instanceof CompositeData) {
				return (CompositeData) jvm.getClient().getMbsc().getAttribute(objectName, attrName);
			} else if (obj instanceof CompositeDataSupport) {
				return (CompositeData) jvm.getClient().getMbsc().getAttribute(objectName, attrName);
			}
			return null;
		} catch (java.rmi.ConnectException e) {
			jvm.getClient().changeConnectStatus(false);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/** END -- 获取数据不同类别的数据 ***********************************************/
}

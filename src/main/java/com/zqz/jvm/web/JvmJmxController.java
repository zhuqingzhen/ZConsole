package com.zqz.jvm.web;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zqz.jvm.bean.Node;
import com.zqz.jvm.bean.ReponseMessage;
import com.zqz.jvm.jmx.JMXTypeUtil;
import com.zqz.jvm.jmx.bean.OperatingSystemInfo;
import com.zqz.jvm.jmx.bean.RuntimeInfo;
import com.zqz.jvm.jmx.notification.NotificationManager;
import com.zqz.jvm.service.JvmJmxService;

/**
 * 
 * @author zhuqz
 *
 */
@RestController
@RequestMapping(value = "/jmx")
public class JvmJmxController {
	
	private static Logger logger = LoggerFactory.getLogger(JvmJmxController.class);
	
	/**
	 * 点对点websocket通信
	 */
	@Autowired
    private SimpMessagingTemplate wsTemplate;

	@Autowired
	private JvmJmxService jvmJmxService;
	
	/**
	 * 获取session信息
	 * @param jvmId
	 * @param objectName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/tree/getJSessionId")
	public String getSessionId(HttpSession session) throws Exception {
		return session.getId();
	}
	
	/**
	 * 获取jmx树
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/tree")
	public Node[] getTree(@RequestParam(name="jvmId") long jvmId) throws Exception {
		return jvmJmxService.getTree(jvmId);
	}
	
	/**
	 * 获取节点信息
	 * @param jvmId
	 * @param objectName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/tree/nodeInfo")
	public MBeanInfo getMBeanInfo(
			@RequestParam(name="jvmId") long jvmId,
			@RequestParam(name="objectName") String objectName
			) throws Exception {
		return jvmJmxService.getMBeanInfo(jvmId, objectName);
	}
	
	/**
	 * 订阅通知
	 * @param jvmId
	 * @param userId
	 * @param objectName
	 * @throws Exception 
	 */
	@RequestMapping("/tree/subscribe/notification")
	public boolean subscribe(
			@RequestParam(name="jvmId") long jvmId,
			@RequestParam(name="userId") String userId,
			@RequestParam(name="objectName") String objectName
			) throws Exception{
		if(NotificationManager.add(userId,jvmId, objectName)){
			jvmJmxService.subscribe(jvmId, userId, objectName, wsTemplate);
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * 订阅通知
	 * @param jvmId
	 * @param userId
	 * @param objectName
	 * @throws Exception 
	 */
	@RequestMapping("/tree/unsubscribe/notification")
	public boolean unSubscribe(@RequestParam(name="userId") String userId) throws Exception{
		if(NotificationManager.remove(userId)){
			jvmJmxService.unSubscribe(userId);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 调用方法
	 * @param jvmId
	 * @param objectName
	 * @param methodName
	 * @param params
	 * @param signature
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/tree/execute")
	public Object execute(@RequestParam(name="jvmId") long jvmId, 
			@RequestParam(name="objectName") String objectName, 
			@RequestParam(name="methodName") String methodName,
			@RequestParam(value = "params[]",required=false ) String[] params,
			@RequestParam(value = "signature[]",required = false) String[] signature
			) throws Exception{
		return JMXTypeUtil.getResult(jvmJmxService.execute(jvmId, objectName, methodName, params, signature));
	}

	
	/**
	 * 导出线程栈
	 * 在扫描堆栈过程中，是在softpoint的状态,去遍历所有线程的信息,会导致jvm处于stop the word状态，而且dump线程栈是单线程处理，停顿时间与jvm内部线程数量是正比关系，在线程很多的情况下会导致长时间的停顿。
	 * 
	 * 在函数 dumpAllThreads(boolean lockedMonitors, boolean lockedSynchronizers)里有2个参数 lockedMonitor和 lockedSynchronizer
	 * 这两个参数分别控制两种锁ThreadInfo .getLockedMonitors() 和ThreadInfo.getLockedSynchronizers() 
	 * a. Monitor 锁就是我们传统使用的synchronized(Object obj), 可以通过MonitorInfo[]得到具体的锁的数量和信息 
	 * b. Locked ownable synchronizers 锁 常指的ReentrantLock 和 ReentrantReadWriteLock锁, 通过得到LockInfo[] 可以得到具体的类，锁的数量和信息,这种锁在jstack -l 里面会在线程栈最后 “Locked ownable synchronizers:”列出，例如：
"RTlock has Lock1" #13 prio=5 os_prio=0 tid=0x0000000018c05000 nid=0x156fc waiting on condition [0x0000000019d4e000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleep(Native Method)
	at com.zqz.oom.ThreadWaitMonitor$RTlockLocked.run(ThreadWaitMonitor.java:41)

   Locked ownable synchronizers:
	- <0x00000000d6250058> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)
	 * 
	 * 
	 * 
	 *	阻塞总数
	 *	Blocked count is the total number of times that the thread blocked to enter or reenter a monitor. I.e. the number of times a thread has been in the java.lang.Thread.State.BLOCKED state.
	 *	等待总数
	 *	Waited count is the total number of times that the thread waited for notification. i.e. the number of times that a thread has been in the ava.lang.Thread.State.WAITING or java.lang.Thread.State.TIMED_WAITING state.
	 * 当线程试图获取一个内部的对象锁(不是java.util.concurrent库中的锁)，而锁被其它线程占有，则该线程进入阻塞状态。
	 * 当线程等待另外一个线程通知调度器的一个条件的时候，它自己进入等待状态。在调用Object.wait()或Thread.join()方法，或者等待java.util.concurrent库中的Lock或Condition时，会出现等待状况
	 * http://blog.csdn.net/mangmang2012/article/details/7106692
     * http://stackoverflow.com/questions/7170235/what-does-blocked-count-and-waited-count-in-a-java-thread-mean
	 * jconsole线程面板中的阻塞总数和等待总数(转)
	 * 关于jconsole的线程监控参数blocked Count ，Waited Count
	 * 
	 * @param jvmId
	 * @param lockedMonitors
	 * @param lockedSynchronizers
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/dumpThread")
	public Object dumpThread(@RequestParam(name="jvmId") long jvmId,
			@RequestParam(name="lockedMonitors") boolean lockedMonitors,
			@RequestParam(name="lockedSynchronizers") boolean lockedSynchronizers
			) throws Exception{
		return jvmJmxService.execute(jvmId, "java.lang:type=Threading", "dumpAllThreads", new Object[]{lockedMonitors,lockedSynchronizers}, new String[]{"boolean","boolean"});
	}
	
	/**
	 * 获取节点的属性值
	 * @param jvmId
	 * @param objectName
	 * @param attrNames
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/tree/attribute/value")
	public Map<String, Object> getAttributesValue(@RequestParam(name="jvmId") long jvmId,
			@RequestParam(name="objectName") String objectName,
			@RequestParam(value = "attrNames[]") String[] attrNames
			) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		ObjectName os = new ObjectName(objectName);
		for (int i = 0, l = attrNames.length; i < l; i++) {
			Object obj = null;
			try {
				obj = jvmJmxService.getAttributeValue(jvmId, os, attrNames[i]);
			} catch (Throwable e) {
				if (e.getMessage().startsWith("java.lang.UnsupportedOperationException")) {
					obj = "<span style='color:red'>Unsupported</span>";
				} else {
					throw e;
				}
			}
			if(obj == null){
				result.put(attrNames[i], null);
			}else if(obj.getClass().isArray()) {
				Object[] list = null;
				try{
					list = (Object[]) obj;
					Object[] resultList = new Object[list.length];
					for (int j = 0; j < list.length; j++) {
						resultList[j] = JMXTypeUtil.getResult(list[j]);
					}
					result.put(attrNames[i], resultList);
				}catch(java.lang.ClassCastException e){
					result.put(attrNames[i], obj);
				}
			} else {
				result.put(attrNames[i], JMXTypeUtil.getResult(obj));
			}
		}
		return result;
	}
	
	
	@RequestMapping(value="/runtime")
	public RuntimeInfo getRuntime(@RequestParam(name="jvmId") long jvmId) throws Exception{
	    return jvmJmxService.getRuntime(jvmId);
	}
	
	@RequestMapping(value="/operatingSystem")
	public OperatingSystemInfo getOperatingSystemInfo(@RequestParam(name="jvmId") long jvmId) throws Exception{
	    return jvmJmxService.getOperatingSystemInfo(jvmId);
	}
	
	
	@RequestMapping(value="/cpuInfo")
	public ReponseMessage getJVMCpuInfo(@RequestParam(name="jvmId") long jvmId) throws Exception{
	    return new ReponseMessage(ReponseMessage.SUCCESS, jvmJmxService.getJVMCpuInfo(jvmId));
	}
	
	@RequestMapping(value="/threadInfo")
	public ReponseMessage getJVMThreadInfo(@RequestParam(name="jvmId") long jvmId) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJmxService.getJVMThreadInfo(jvmId));
	}
	
	/**
	 * 直接内存使用情况
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/nioMemory")
	public ReponseMessage getNioMemoryInfo(@RequestParam(name="jvmId") long jvmId) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJmxService.getNioMemoryInfo(jvmId));
	}
	
	/**
	 * hotsport 虚拟机诊断参数
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/hotSpotDiagnostic")
	public ReponseMessage getHotSpotDiagnostic(@RequestParam(name="jvmId") long jvmId) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJmxService.getHotSpotDiagnostic(jvmId));
	}
	
	/**
	 * 获取类直方图；
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/gcClassHistogram")
	public ReponseMessage gcClassHistogram(
			@RequestParam(name="jvmId") long jvmId,
			@RequestParam(required=false,name="all") boolean all) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJmxService.gcClassHistogram(jvmId,all));
	}
	
	
	
	/**
	 * 获取类直方图；
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/help")
	public ReponseMessage help(
			@RequestParam(name="jvmId") long jvmId,
			@RequestParam(name="all") boolean all,
			@RequestParam(name="cmd") String cmd
			) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJmxService.help(jvmId,all,cmd));
	}
	
	/**
	 * 获取类直方图；
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/threadPrint")
	public ReponseMessage threadPrint(
			@RequestParam(name="jvmId") long jvmId,
			@RequestParam(name="printLock") boolean printLock
			) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJmxService.threadPrint(jvmId,printLock));
	}
	
	/**
	 * gc 次数和时间信息
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/gcInfo")
	public ReponseMessage getGCInfo(@RequestParam(name="jvmId") long jvmId) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJmxService.getGCInfo(jvmId));
	}
}

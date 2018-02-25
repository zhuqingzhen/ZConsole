package com.zqz.jvm.service;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanInfo;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zqz.jvm.bean.Node;
import com.zqz.jvm.jmx.JMXTypeUtil;
import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.MBeanUtil;
import com.zqz.jvm.jmx.bean.OperatingSystemInfo;
import com.zqz.jvm.jmx.bean.RuntimeInfo;
import com.zqz.jvm.jmx.notification.NotificationManager;

@Service
public class JvmJmxService {
	
	private static Logger logger = LoggerFactory.getLogger(JvmJmxService.class);
	
	ObjectName objectName_Runtime = null;
	ObjectName objectName_OperatingSystem = null;
	ObjectName objectName_thread = null;
	ObjectName objectName_BufferPool_direct = null;
	ObjectName objectName_BufferPool_mapped = null;
	ObjectName objectName_HotSpotDiagnostic = null;
	/**
	 * 获取jvm运行时cpu使用状况
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getJVMCpuInfo(long jvmId) throws Exception {
		if(objectName_Runtime == null){
			objectName_Runtime = new ObjectName("java.lang:type=Runtime");
		}
		if(objectName_OperatingSystem == null){
			objectName_OperatingSystem = new ObjectName("java.lang:type=OperatingSystem");
		}
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("SystemLoadAverage", String.valueOf(MBeanUtil.getObjectNameValue(objectName_OperatingSystem, "SystemLoadAverage", jvm)));
		// cpu使用率；
		result.put("SystemCpuLoad", String.valueOf(MBeanUtil.getObjectNameValue(objectName_OperatingSystem, "SystemCpuLoad", jvm)));
		// 当前jvm的cpu使用率；
		result.put("ProcessCpuLoad", String.valueOf(MBeanUtil.getObjectNameValue(objectName_OperatingSystem, "ProcessCpuLoad", jvm)));
		// 当前jvm占用cpu总时间；
		result.put("ProcessCpuTime", Long.parseLong(String.valueOf(MBeanUtil.getObjectNameValue(objectName_OperatingSystem, "ProcessCpuTime", jvm))));
		
		result.put("AvailableProcessors",Short.parseShort(
				String.valueOf( 
						MBeanUtil.getObjectNameValue(objectName_OperatingSystem, "AvailableProcessors", jvm)
				)
			));
		// 当前jvm启动时间
		result.put("Uptime", Long.parseLong(String.valueOf(MBeanUtil.getObjectNameValue(objectName_Runtime, "Uptime", jvm))));
		return result;
	}
	
	/**
	 * 直接内存使用情况
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getNioMemoryInfo(long jvmId) throws Exception{
		if(objectName_BufferPool_direct == null){
			objectName_BufferPool_direct = new ObjectName("java.nio:type=BufferPool,name=direct");
		}
		if(objectName_BufferPool_mapped == null){
			objectName_BufferPool_mapped = new ObjectName("java.nio:type=BufferPool,name=mapped");
		}
		
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("direct_Count", Long.parseLong(String.valueOf(MBeanUtil.getObjectNameValue(objectName_BufferPool_direct, "Count", jvm))));
		
		 /*An estimate of the memory that the Java virtual machine is using
	     for this buffer pool in bytes, or {@code -1L} if an estimate of
	     the memory usage is not available*/
		result.put("direct_MemoryUsed", Long.parseLong(String.valueOf(MBeanUtil.getObjectNameValue(objectName_BufferPool_direct, "MemoryUsed", jvm))));
		
		result.put("mapped_Count", Long.parseLong(String.valueOf(MBeanUtil.getObjectNameValue(objectName_BufferPool_mapped, "Count", jvm))));
		
		result.put("mapped_MemoryUsed", Long.parseLong(String.valueOf(MBeanUtil.getObjectNameValue(objectName_BufferPool_mapped, "MemoryUsed", jvm))));
		return result;
	}
	
	/**
	 * jvm线程信息
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getJVMThreadInfo(long jvmId) throws Exception {
		if(objectName_thread == null){
			objectName_thread = new ObjectName("java.lang:type=Threading");
		}
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		//当前线程占用cpu时间，纳秒
		result.put("CurrentThreadCpuTime", Long.parseLong(String.valueOf(MBeanUtil.getObjectNameValue(objectName_thread, "CurrentThreadCpuTime", jvm))));
		//当前线程占用cpu状态时间；
		result.put("CurrentThreadUserTime", Long.parseLong(String.valueOf(MBeanUtil.getObjectNameValue(objectName_thread, "CurrentThreadUserTime", jvm))));
		//守护线程数量
		result.put("DaemonThreadCount", Integer.parseInt(String.valueOf(MBeanUtil.getObjectNameValue(objectName_thread, "DaemonThreadCount", jvm))));
		//存活线程峰值
		result.put("PeakThreadCount", Integer.parseInt(String.valueOf(MBeanUtil.getObjectNameValue(objectName_thread, "PeakThreadCount", jvm))));
		//当前存活的线程
		result.put("ThreadCount", Integer.parseInt(String.valueOf(MBeanUtil.getObjectNameValue(objectName_thread, "ThreadCount", jvm))));
		//启动过的线程总数
		result.put("TotalStartedThreadCount", Integer.parseInt(String.valueOf(MBeanUtil.getObjectNameValue(objectName_thread, "TotalStartedThreadCount", jvm))));
		return result;
	}

	/**
	 * 订阅通知
	 * 
	 * @param jvmId
	 * @param userId
	 * @param template
	 *            点对点推送消息
	 * @param objectName
	 * @throws Exception
	 */
	public void subscribe(long jvmId, String userId, String objectName, SimpMessagingTemplate template)
			throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return;
		}
		MBeanUtil.subscribe(userId, jvm, objectName, template);
	}

	/**
	 * 订阅通知
	 * 
	 * @param jvmId
	 * @param userId
	 * @param template
	 *            点对点推送消息
	 * @param objectName
	 * @throws Exception
	 */
	public void unSubscribe(String userId) throws Exception {
		String value = NotificationManager.getSubscribeNames(userId);
		if (NotificationManager.remove(userId)) {
			if (value == null)
				return;
			String[] params = value.split("::");
			if (params != null && params.length == 2) {
				JVM jvm = JVMManager.get(Long.valueOf(params[0]));
				if (jvm == null) {
					return;
				}
				MBeanUtil.unSubscribe(jvm, params[1]);
			}
		}
	}

	/**
	 * 获取jmx tree
	 * 
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	public Node[] getTree(long jvmId) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		Node node = MBeanUtil.getJMXTree(jvm);
		return node.getChildren();
	}

	/**
	 * 获取属性值
	 * 
	 * @param jvmId
	 * @param objectName
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public Object getAttributeValue(long jvmId, ObjectName objectName, String attrName) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return MBeanUtil.getObjectNameValue(objectName, attrName, jvm);
	}

	/**
	 * 获取MBeanInfo
	 * 
	 * @param objectName
	 * @return
	 * @throws Exception
	 */
	public MBeanInfo getMBeanInfo(long jvmId, String objectName) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return MBeanUtil.getMBeanInfo(objectName, jvm);
	}

	/**
	 * 调用jmx方法
	 * 
	 * @param jvmId
	 * @param objectName
	 * @param methodName
	 * @param params
	 * @param signature
	 * @return
	 * @throws Exception
	 */
	public Object execute(long jvmId, String objectName, String methodName, Object[] params, String[] signature)
			throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return MBeanUtil.execute(objectName, methodName, params, signature, jvm);
	}

	public String dumpThread() {
		return null;
	}

	/**
	 * 获取JVM运行时信息
	 * 
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	public RuntimeInfo getRuntime(long jvmId) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return jvm.getRuntime();
	}

	/**
	 * 获取OperatingSystem
	 * 
	 * @return
	 * @throws Exception
	 */
	public OperatingSystemInfo getOperatingSystemInfo(long jvmId) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return jvm.getOperatingSystemInfo();
	}
	
	/**
	 * hotsport 虚拟机诊断参数
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	public Object[] getHotSpotDiagnostic(long jvmId) throws Exception{
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		if(objectName_HotSpotDiagnostic==null)
			objectName_HotSpotDiagnostic = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
		return  (Object[])JMXTypeUtil.getResult( MBeanUtil.getObjectNameValue(objectName_HotSpotDiagnostic, "DiagnosticOptions", jvm));
	}
	
	
	/**
	 * 验证当前jvm潜在的问题；
	 */
	public Map<String,Object> checkJVM(long jvmId) {
		Map<String,Object> result = new HashMap<String,Object>();
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		try {
			result.put("HeapDumpOnOutOfMemoryError", checkHeapDumpOnOutOfMemoryError(jvm));
		} catch (Exception e) {
			logger.error("check HeapDumpOnOutOfMemoryError status error", e);
		}
		try {
			result.put("PrintGC",checkGcLogStatus(jvm));
		} catch (Exception e) {
			logger.error("check gclog status error", e);
		}

		try {
			result.put("DisableExplicitGC", checkDisableExplicitGC(jvm));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("check DisableExplicitGC status error", e);
		}

		try {
			checkGCState(jvm);
		} catch (Exception e) {
			logger.error("check gc status error", e);
		}
		return result;
	}
	

	/**
	 * statusDisableExplicitGC statusForHeapDumpOnOutOfMemoryError
	 * statusForGCLog 检查系统异常时,是否需要dump堆
	 * 
	 * @return
	 * @throws Exception
	 */
	private Boolean checkHeapDumpOnOutOfMemoryError(JVM jvm) throws Exception {
		Map<String, Object> result = MBeanUtil.getHotSpotVmOption(jvm, "HeapDumpOnOutOfMemoryError");
		if (result != null && result.size() == 4) {
			return  Boolean.valueOf((String) result.get("value"));
		}
		logger.warn(JSON.toJSONString(result));
		return null;
	}

	/**
	 * 检查gc日志是否打开
	 * 
	 * @return
	 * @throws Exception
	 */
	private Boolean checkGcLogStatus(JVM jvm) throws Exception {
		Map<String, Object> result = MBeanUtil.getHotSpotVmOption(jvm, "PrintGC");
		if (result != null && result.size() == 4) {
			return  Boolean.valueOf((String) result.get("value"));
		}
		logger.warn(JSON.toJSONString(result));
		return null;
	}
	
	/**
	 * 检查系统异常时,是否需要dump堆
	 * 
	 * @return
	 * @throws Exception
	 */
	private Boolean checkDisableExplicitGC(JVM jvm) throws Exception {
		Map<String, Object> result = MBeanUtil.getHotSpotVmOption(jvm, "DisableExplicitGC");
		if (result != null && result.size() == 4) {
			return Boolean.valueOf((String) result.get("value"));
		}
		logger.warn(JSON.toJSONString(result));
		return null;
	}

	/**
	 * 检查gc状态
	 * @throws Exception
	 */
	private void checkGCState(JVM jvm) throws Exception {
		if(objectName_Runtime==null)
			objectName_Runtime = new ObjectName("java.lang:type=Runtime");
		long uptime = (Long) MBeanUtil.getObjectNameValue(objectName_Runtime, "Uptime", jvm);
		ObjectName  ygcObjectName = jvm.getYGCName();
		long ygcCollectionTime = (Long) MBeanUtil.getObjectNameValue(ygcObjectName, "CollectionTime", jvm);
		long ygcCollectionCount = (Long) MBeanUtil.getObjectNameValue(ygcObjectName, "CollectionCount", jvm);
		long ygcWasteTimeAVG = ygcCollectionTime / ygcCollectionCount;
		long ygcFrequency = uptime / ygcCollectionCount;

		// 获取FullGc类型
		ObjectName  fgcObjectName =	jvm.getFullGCName();
		long fullCollectionTime = (Long) MBeanUtil.getObjectNameValue(fgcObjectName, "CollectionTime", jvm);
		long fullCollectionCount = (Long) MBeanUtil.getObjectNameValue(fgcObjectName, "CollectionCount", jvm);
		long fullWasteTimeAVG = fullCollectionTime / fullCollectionCount;
		long fullFrequency = uptime / fullCollectionCount;

	}
	
	/**
	 * 获取类直方图；
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	public String gcClassHistogram(long jvmId) throws Exception{
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return MBeanUtil.gcClassHistogram(jvm);
	}

}

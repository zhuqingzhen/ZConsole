package com.zqz.jvm.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.zqz.common.Util;
import com.zqz.jvm.jmx.bean.GCInfo;
import com.zqz.jvm.jmx.bean.GCMemoryInfo;

public class JVM {
	
	private static Logger logger = LoggerFactory.getLogger(JVM.class);
	private final String CODE_CACHE = "Code Cache";
	private final String EDEN_SPACE = "Eden Space";
	private final String OLD_GEN = "Old Gen";
	private final String TENURED_GEN = "Tenured Gen";
	private final String SURVIVOR_SPACE = "Survivor Space";
	private final String PERM_GEN = "Perm Gen";
	private final String COMPRESSED_CLASS_SPACE = "Compressed Class Space";
	private final String METASPACE = "Metaspace";
	
	/**jvm的id**/
	private long id;
	/**jvm的名称*/
	private String name;

	/**gc信息收集周期，单位毫秒 ，默认为1秒**/
	public long interval=1000;
	//jmx连接状态
	private JMXClient client;
	
	private int jvmVersion = -1;
	private boolean first = true;
	
	//gc日志打开状态,true打开,false未打开
	private Boolean statusForGCLog = true;
	/*****************************************************jvm垃圾回收相关*/
	
	/**当前虚拟机的垃圾回收器类型**/
	private ObjectName GC_Y_OBJECT_NAME = null;
	private ObjectName GC_FULL_OBJECT_Name = null;
	
	/**上一次回收Id，根据下面两个id哪个改变了，来判断发生的是什么gc**/
	private long lastYgcId = 0 ;
	private long lastFullgcId = 0 ;
	/**最后一次垃圾回收方式，true ： ygc；false ：full gc*/
	private Boolean isYoungGc = true;
	
	/**最后一次回收信息详情**/
	private GCInfo lastygc = null;
	private GCInfo lastfullgc = null;
	/**定时任务最后一次更新gc数据的时间**/
	private long updateGCInfoTime=0;
	/**gcInfo数据最后一次读取时间，用来判断是否要停掉GCJstat任务**/
	private long gcInfoLastVisitTime ;
	
	/**当前内存信息*/
	private GCMemoryInfo curMemory;
	/**定时任务最后一次更新curMemory数据的时间**/
	private long updateCurMemoryTime=0;
	/**curMemory数据最后一次读取时间，用来判断是否要停掉定时任务**/
	private long curMemoryLastVisitTime;
	
	/*****************************************************end jvm垃圾回收相关*/
	
	public void updateCurMemoryLastVisitTime() {
		this.curMemoryLastVisitTime = System.currentTimeMillis();
	}
	public long getUpdateCurMemoryTime() {
		return updateCurMemoryTime;
	}
	public void setUpdateCurMemoryTime(long updateGCMemoryTime) {
		this.updateCurMemoryTime = updateGCMemoryTime;
	}
	public void updateGcInfoLastVisitTime() {
		this.gcInfoLastVisitTime = System.currentTimeMillis();
	}
	
	public long getCurMemoryLastVisitTime() {
		return curMemoryLastVisitTime;
	}
	
	public GCMemoryInfo getCurMemory() {
		curMemoryLastVisitTime = System.currentTimeMillis();
		return curMemory;
	}

	public void setCurMemory(GCMemoryInfo curMemory) {
		this.curMemory = curMemory;
	}
	
	public Boolean getIsYoungGc() {
		return isYoungGc;
	}

	public void setIsYoungGc(Boolean isYoungGc) {
		this.isYoungGc = isYoungGc;
	}

	public long getLastYgcId() {
		return lastYgcId;
	}

	public void setLastYgcId(long lastYgcId) {
		this.lastYgcId = lastYgcId;
	}

	public long getLastFullgcId() {
		return lastFullgcId;
	}

	public void setLastFullgcId(long lastFullgcId) {
		this.lastFullgcId = lastFullgcId;
	}

	public GCInfo getLastygc() {
		return lastygc;
	}

	public void setLastygc(GCInfo lastygc) {
		this.lastygc = lastygc;
	}

	public GCInfo getLastfullgc() {
		return lastfullgc;
	}

	public void setLastfullgc(GCInfo lastfullgc) {
		this.lastfullgc = lastfullgc;
	}

	public long getUpdateGCInfoTime() {
		return updateGCInfoTime;
	}

	public void setUpdateGCInfoTime(long getGCInfoTime) {
		this.updateGCInfoTime = getGCInfoTime;
	}

	public long getJstatLastVisitTime() {
		return gcInfoLastVisitTime;
	}


	/**
	 * @param id  虚拟机id
	 * @param client jmx client
	 */
	public JVM(long id,JMXClient client){
		this.id = id;
		this.client = client;
		JVMManager.addJVM(this);
	}
	
	public JMXClient getClient() {
		return client;
	}

	public void setClient(JMXClient client) {
		this.client = client;
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * java.lang:type=MemoryPool,* 下全部的ObjectName集合
	 */
	private Set<ObjectName> memoryPoolObjectNames= null;
	
	public Set<ObjectName> getMemoryPoolObjectNames() throws MalformedObjectNameException, IOException, SchedulerException{
		if(memoryPoolObjectNames == null || memoryPoolObjectNames.size() <  1){
			ObjectName memoryObjectName = new ObjectName("java.lang:type=MemoryPool,*");
			return  memoryPoolObjectNames = MBeanUtil.queryNames(this, memoryObjectName, null);
		}
		return memoryPoolObjectNames;
	}
	
	/**
	 * 获取内存使用情况
	 * @return
	 * @throws Exception
	 */
	public GCMemoryInfo getMemoryInfo() throws Exception{
		Set<ObjectName> objectNames = getMemoryPoolObjectNames();
		GCMemoryInfo gcInfo = new GCMemoryInfo();
		for (ObjectName obj : objectNames) {
            Object resultObj = MBeanUtil.getObjectNameValue(new ObjectName(obj.getCanonicalName()), "Usage",this);
            Map<String, Object> value = JMXTypeUtil.managerCompositeDataSupport(resultObj);
            MemoryUsage mu = new MemoryUsage(
            		Long.valueOf(String.valueOf(value.get("init")))
            		,Long.valueOf(String.valueOf(value.get("used")))
            		,Long.valueOf(String.valueOf(value.get("committed")))
            		,Long.valueOf(String.valueOf(value.get("max")))
            		);
            String name= obj.getKeyProperty("name");
            if( name.endsWith(OLD_GEN) || name.endsWith(TENURED_GEN)){
 				gcInfo.setOldGen(mu);
 			}else if(name.endsWith(EDEN_SPACE)){
 				gcInfo.setEdenSpace(mu);
 			}else if(name.endsWith(SURVIVOR_SPACE)){
 				gcInfo.setSurvivorSpace(mu);
 			}else if(name.equals(CODE_CACHE)){
 				gcInfo.setCodecache(mu);
 			}else if(name.equals(COMPRESSED_CLASS_SPACE)){
 				gcInfo.setCompressedClassSpace(mu);
 			}else if(name.equals(METASPACE)){
 				gcInfo.setMetaspace(mu);
 			}else if(name.equals(PERM_GEN)){
 				gcInfo.setPermgen(mu);
 			}
		}
		ObjectName memory = new ObjectName("java.lang:type=Memory");
		Object resultObj = MBeanUtil.getObjectNameValue(memory, "HeapMemoryUsage",this);
		Map<String, Object> value = JMXTypeUtil.managerCompositeDataSupport(resultObj);
		gcInfo.setHeapMemory(new MemoryUsage(
					 Long.valueOf(String.valueOf(value.get("init")))
            		,Long.valueOf(String.valueOf(value.get("used")))
            		,Long.valueOf(String.valueOf(value.get("committed")))
            		,Long.valueOf(String.valueOf(value.get("max")))
            	));
		resultObj = MBeanUtil.getObjectNameValue(memory, "NonHeapMemoryUsage",this);
		value = JMXTypeUtil.managerCompositeDataSupport(resultObj);
		gcInfo.setNonHeapMemory(new MemoryUsage(
					Long.valueOf(String.valueOf(value.get("init")))
					,Long.valueOf(String.valueOf(value.get("used")))
					,Long.valueOf(String.valueOf(value.get("committed")))
					,Long.valueOf(String.valueOf(value.get("max")))
				));
		updateCurMemoryTime = System.currentTimeMillis();
		return curMemory = gcInfo;
	}

	/**
	 * 收集gc信息
	 * 
	 * 此处更改为只获取young区的gc数据，
	 * @throws Exception
	 */
	public void getGCINFO() throws Exception{
		updateGCInfoTime = System.currentTimeMillis();
		if(!this.client.isConnected()){
			return;
		}
		//获取ygc信息
		if(GC_Y_OBJECT_NAME == null){
			this.getYGCName();
		}
		CompositeData lastGcInfo = MBeanUtil.getCompositeData(GC_Y_OBJECT_NAME, "LastGcInfo",this);
		long lastGcId,startTime;//startTime为执行gc操作的时间
		if(lastGcInfo!=null){//已经执行了一次gc操作
			//根据id判断有没有发生新的gc
			lastGcId =  (Long)lastGcInfo.get("id");
			startTime = (Long)lastGcInfo.get("startTime");
			if(lastGcId > lastYgcId ){//如果id大于上次gc的id继续执行；
				getYGC(MBeanUtil.getCompositeData( GC_Y_OBJECT_NAME, "LastGcInfo",this));
			} else if (lastGcId < lastYgcId){//如果id小于上次gc的id，这是不可能的；
				throw new Exception("ygc Id error");
			} //如果id相等，说明没有发生gc，什么也不做;
			
		}else{//还没有执行gc操作
			startTime = 0;
		}
		//获取FullGc类型
		if(GC_FULL_OBJECT_Name == null){
			this.getFullGCName();
		}
		lastGcInfo = MBeanUtil.getCompositeData( GC_FULL_OBJECT_Name, "LastGcInfo",this);
		if(lastGcInfo!=null){
			//根据id判断有没有发生新的gc
			lastGcId = (Long)lastGcInfo.get("id");
			if(lastGcId > lastFullgcId ){ //如果id大于上次gc的id继续执行；
				this.getFullGC(lastGcInfo);
			} else if (lastGcId < lastFullgcId){//如果id小于上次gc的id，这是不可能的；
				throw new Exception("fullgc Id error");
			} //如果id相等，说明没有发生gc，什么也不做;
			if(startTime > (Long)lastGcInfo.get("startTime")){
				isYoungGc = true;
			}else{
				isYoungGc = false;
			}
		}else{
			isYoungGc = true;
		}
	}
	
	
	/**
	 * 获取年轻带gc信息
	 * @param lastGcInfo
	 * @throws Exception
	 */
	private void getYGC(CompositeData lastGcInfo) throws Exception {
		GCInfo gcInfo  = new GCInfo();
		
		//获取gc前内存信息
		Object obj = lastGcInfo.get("memoryUsageBeforeGc");
		if(obj!=null){
			gcInfo.setGcMemoryInfoBefore(this.getGcMemoryData((TabularDataSupport)obj));
		}
		//获取gc后内存信息
		obj = lastGcInfo.get("memoryUsageAfterGc");
		if(obj!=null){
			gcInfo.setGcMemoryInfoAfter(this.getGcMemoryData((TabularDataSupport)obj));
		}
		
		gcInfo.setCount((Long)lastGcInfo.get("id"));
		//gc线程数
		gcInfo.setGcThreadCount((Integer)lastGcInfo.get("GcThreadCount"));
		//gc持续时间
		gcInfo.setDuration((Long)lastGcInfo.get("duration"));
		//gc结束时间
		gcInfo.setEndTime((Long)lastGcInfo.get("endTime"));
		//ygc花费的总时间
		gcInfo.setTotalTime(MBeanUtil.getLong(GC_Y_OBJECT_NAME, "CollectionTime",this));
		//记录gc次数
		lastYgcId = gcInfo.getCount();
		lastygc = gcInfo;
	}
	
	
	/**
	 * 获取fullGc信息
	 * @param lastGcInfo
	 * @throws Exception
	 */
	private void getFullGC(CompositeData lastGcInfo) throws Exception {
		GCInfo gcInfo  = new GCInfo();
		//获取gc前内存信息
		Object obj = lastGcInfo.get("memoryUsageBeforeGc");
		if(obj!=null){
			gcInfo.setGcMemoryInfoBefore(this.getGcMemoryData((TabularDataSupport)obj));
		}
		//获取gc后内存信息
		obj = lastGcInfo.get("memoryUsageAfterGc");
		if(obj!=null){
			gcInfo.setGcMemoryInfoAfter(this.getGcMemoryData((TabularDataSupport)obj));
		}
		gcInfo.setCount((Long)lastGcInfo.get("id"));
		//gc线程数
		gcInfo.setGcThreadCount((Integer)lastGcInfo.get("GcThreadCount"));
		//gc持续时间
		gcInfo.setDuration((Long)lastGcInfo.get("duration"));
		//gc结束时间
		gcInfo.setEndTime((Long)lastGcInfo.get("endTime"));
		//gc花费的总时间
		gcInfo.setTotalTime(MBeanUtil.getLong(GC_Y_OBJECT_NAME, "CollectionTime",this));
		//记录gc次数
		lastFullgcId = gcInfo.getCount();
		lastfullgc = gcInfo;
	}
	
	
	/**
	 * 检查gc日志是否打开
	 * @return
	 * @throws Exception
	 */
	public Boolean checkGcLogStatus() throws Exception{
		Map<String, Object>  result = MBeanUtil.getHotSpotVmOption(this, "PrintGC");
		if(result!=null && result.size() == 4){
			return statusForGCLog=(boolean)result.get("value");
		}
		logger.warn(JSON.toJSONString(result));
		return null;
	}
	
	
	/**
	 * 打开或关闭gc日志
	 * 
	 * @param flag true打开，false关闭
	 * @throws IOException
	 * @throws NullPointerException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 */
	public void gcLog(boolean flag) throws Exception {
		String value;
		if (flag) {
			value = "true";
		} else {
			value = "false";
		}
		MBeanUtil.setHotSpotVmOption(this,"PrintGC", value);
		MBeanUtil.setHotSpotVmOption(this,"PrintGCDetails", value);
		MBeanUtil.setHotSpotVmOption(this,"PrintGCTimeStamps", value);
		MBeanUtil.setHotSpotVmOption(this,"PrintGCDateStamps", value);
	}
	
	
	/**
	 * 开启或关闭对dump
	 * 
	 * @param flag :true打开，false关闭
	 * @param dumpPath :导出堆文件路径
	 * @throws Exception
	 */
	public void openDump(boolean flag, String dumpPath) throws Exception {
		String value;
		if (flag) {
			value = "true";
		} else {
			value = "false";
		}
		MBeanUtil.setHotSpotVmOption(this,"HeapDumpOnOutOfMemoryError", value);
		if (dumpPath != null && dumpPath.length() > 0) {
			MBeanUtil.setHotSpotVmOption(this,"HeapDumpPath", dumpPath);
		}
	}
	
	
	/**
	 * 获取ygc垃圾回收器名称
	 * @return
	 */
	public ObjectName getYGCName() {
		if(GC_Y_OBJECT_NAME!=null){
			return GC_Y_OBJECT_NAME;
		}
        try {
            if (MBeanUtil.isObjectNameRegistered(ObjectNameUtil.ojbectName_ParNew,this)) {  
            	GC_Y_OBJECT_NAME =  ObjectNameUtil.ojbectName_ParNew;
            } else if(MBeanUtil.isObjectNameRegistered(ObjectNameUtil.ojbectName_G1YoungGeneration,this)){
            	GC_Y_OBJECT_NAME = ObjectNameUtil.ojbectName_G1YoungGeneration;
            } else if(MBeanUtil.isObjectNameRegistered(ObjectNameUtil.ojbectName_PSScavenge,this)){  
            	GC_Y_OBJECT_NAME = ObjectNameUtil.ojbectName_PSScavenge;
            } else if (MBeanUtil.isObjectNameRegistered(ObjectNameUtil.ojbectName_Copy,this)) {  
            	GC_Y_OBJECT_NAME = ObjectNameUtil.ojbectName_Copy; 
            }  
        } catch (IOException e) {  
            throw new RuntimeException(e);  
        }
        return GC_Y_OBJECT_NAME;
    }  
  
	/**
	 * 获取老年代垃圾回收器名称
	 * @return
	 */
	public ObjectName getFullGCName() {
    	if(GC_FULL_OBJECT_Name!=null){
			return GC_FULL_OBJECT_Name;
		}
        try {  
            if (MBeanUtil.isObjectNameRegistered(ObjectNameUtil.ojbectName_ConcurrentMarkSweep,this)) {//cms 
            	GC_FULL_OBJECT_Name = ObjectNameUtil.ojbectName_ConcurrentMarkSweep;  
            } else if (MBeanUtil.isObjectNameRegistered(ObjectNameUtil.ojbectName_G1OldGeneration,this)) {//G1
            	GC_FULL_OBJECT_Name = ObjectNameUtil.ojbectName_G1OldGeneration;
            } else if(MBeanUtil.isObjectNameRegistered(ObjectNameUtil.ojbectName_PSMarkSweep,this)){//parold
            	GC_FULL_OBJECT_Name = ObjectNameUtil.ojbectName_PSMarkSweep;
            } else if(MBeanUtil.isObjectNameRegistered(ObjectNameUtil.ojbectName_MarkSweepCompact,this)){//UseSerialGC
            	GC_FULL_OBJECT_Name = ObjectNameUtil.ojbectName_MarkSweepCompact;
            }
        } catch (IOException e) {  
            throw new RuntimeException(e);  
        }
        return GC_FULL_OBJECT_Name;
    }  

	
	/**
	 * 将TabularDataSupport类型转换为 GCMemoryInfo
	 * @param memoryUsageGc
	 * @return
	 * @throws Exception
	 */
	private GCMemoryInfo getGcMemoryData(TabularDataSupport memoryUsageGc) throws Exception{
		GCMemoryInfo gcInfo = new GCMemoryInfo();
		Map<String, Object> obj = JMXTypeUtil.managerTabularDataSupport(memoryUsageGc);
		String name;
		Object objValue;
		for(Entry<String, Object> key :obj.entrySet()){
			name = key.getKey();
			objValue =key.getValue();
			if(objValue==null){
				continue;
			}
			HashMap<String,Long> value = (HashMap<String,Long>)objValue;
			MemoryUsage mu = new MemoryUsage(value.get("init"),value.get("used"),value.get("committed"),value.get("max"));
			if( name.endsWith(OLD_GEN) || name.endsWith(TENURED_GEN)){
				gcInfo.setOldGen(mu);
			}else if(name.endsWith(EDEN_SPACE)){
				gcInfo.setEdenSpace(mu);
			}else if(name.endsWith(SURVIVOR_SPACE)){
				gcInfo.setSurvivorSpace(mu);
			}else if(name.equals(CODE_CACHE)){
				gcInfo.setCodecache(mu);
			}else if(name.equals(COMPRESSED_CLASS_SPACE)){
				gcInfo.setCompressedClassSpace(mu);
			}else if(name.equals(METASPACE)){
				gcInfo.setMetaspace(mu);
			}else if(name.equals(PERM_GEN)){
				gcInfo.setPermgen(mu);
			}
		}
		return gcInfo;
	}
	
}


package com.zqz.jvm.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.MBeanUtil;

/**
 * JFR start jdk7u4以上版本支持
 * 20180225
 * @author zqz
 * */
@Service
public class JvmJFRService {
	
	private static Logger logger = LoggerFactory.getLogger(JvmJFRService.class);
	
	/** 
	 * Flight Recorder  enabled.
	 * Use -XX:+UnlockCommercialFeatures -XX:+FlightRecorder to enable.
	 * @throws Exception 
	 ***/
	public Boolean checkJFR(long jvmId) throws Exception{
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		Map<String, Object> result = MBeanUtil.getHotSpotVmOption(jvm, "UnlockCommercialFeatures");
		if (result != null && result.size() == 4) {
			if(Boolean.valueOf((String) result.get("value"))){
				 result = MBeanUtil.getHotSpotVmOption(jvm, "FlightRecorder");
				 if(Boolean.valueOf((String) result.get("value"))){
					 return true;
				 }
			}
		}
		return false;
	}
	
	/**
	 * 启用JFR
	 * @param jvm
	 * @return
	 * @throws Exception
	 */
	public Boolean enableJFR(long jvmId) throws Exception{
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		Map<String, Object> result = MBeanUtil.getHotSpotVmOption(jvm, "UnlockCommercialFeatures");
		if (result != null && result.size() == 4) {
			if(!Boolean.valueOf((String) result.get("value"))){
				Object obj = MBeanUtil.setHotSpotVmOption(jvm,"UnlockCommercialFeatures","true");
			}
		}
		result = MBeanUtil.getHotSpotVmOption(jvm, "FlightRecorder");
		if (result != null && result.size() == 4) {
			if(!Boolean.valueOf((String) result.get("value"))){
				Object obj = MBeanUtil.setHotSpotVmOption(jvm,"FlightRecorder","true");
			}
		}
		return true;
	}

	
	
}

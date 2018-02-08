package com.zqz.jvm.jmx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zqz.jvm.bean.JVMList;

/**
 * @author zqz
 */
public class JVMManager {
	
	// 当前注册的jvm
	private static Map<Long, JVM> JVMS = new ConcurrentHashMap<Long, JVM>();
	
	/**
	 * 移除JVMS
	 * @param jvmId
	 * @return
	 */
	public static boolean remove(long jvmId){
		return JVMS.remove(jvmId)==null?false:true;
	}
	
	/**
	 * 根据jvmId获取JVM
	 * @param jvmId
	 * @return
	 * 961285586646339600
	 * 961285586646339584
	 */
	public static JVM get(long jvmId){
		return JVMS.get(jvmId);
	}

	/**
	 * 添加jvm，true添加成功，false jvm已经存在
	 * @param jvm
	 * @return
	 */
	public static boolean addJVM(JVM jvm) {
		JVM jmb = JVMS.get(jvm.getId());
		//jvm已经存在
		if(jmb != null){
			return false;
		}
		JVMS.put(jvm.getId(), jvm);
		return true;
	}
	
	/**
	 * 获取全部jvm列表
	 * @return
	 */
	public static JVMList[] getAll(){
		if(JVMS.size()>0){
			JVMList[] list = new JVMList[JVMS.size()];
			int i = 0;
			JVM tmp;
			for(Map.Entry<Long, JVM> entry : JVMS.entrySet()){
				tmp = entry.getValue();
				JVMList jvm = new JVMList();
				jvm.setId(String.valueOf(tmp.getId()));
				jvm.setName(tmp.getName());
				jvm.setConnected(tmp.getClient().isConnected());
				list[i++] = jvm;
			}
			return list;
		}
		return null;
	}
	
	
}
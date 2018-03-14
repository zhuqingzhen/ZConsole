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
	 * 检查jvm是否已经添加过
	 * @param ip
	 * @param port
	 * @return
	 */
	public static boolean checkRemoteExist(String ip,String port){
		JVM tmp;
		for(Map.Entry<Long, JVM> entry : JVMS.entrySet()){
			tmp = entry.getValue();
			if(tmp.getId()>0){
				if(tmp.getClient().getIp().equals(ip) && tmp.getClient().getPort().equals(port)){
					return true;
				}
			}
		}
		return false;
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
				//如果是远程jvm则返回端口号和ip；
				if(tmp.getId()>0){
					jvm.setIp(tmp.getClient().getIp());
					jvm.setPort(tmp.getClient().getPort());
				}
				try {
					if(jvm.isConnected())
					jvm.setJdk(tmp.getJVMVersion());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					jvm.setOs(tmp.getOperationVersion());
				} catch (Exception e) {
					e.printStackTrace();
				}
				list[i++] = jvm;
			}
			return list;
		}
		return null;
	}
	
	
}
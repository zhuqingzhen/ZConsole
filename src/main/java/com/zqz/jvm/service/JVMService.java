package com.zqz.jvm.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.zqz.common.IdWorker;
import com.zqz.jvm.bean.JVMEntity;
import com.zqz.jvm.bean.JVMList;
import com.zqz.jvm.jmx.JMXClient;
import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.bean.LocalVMInfo;
import com.zqz.jvm.task.ZQZTaskManager;
import com.zqz.jvm.task.job.ConnectTask;

/**
 *  
 * @author zqz
 */
@Service
public class JVMService {
	
	private static Logger logger = LoggerFactory.getLogger(JVMService.class);
	
	/**
	 * 添加本地JVM实例
	 * @param jvmId
	 * @return
	 * @throws IOException
	 * @throws AgentLoadException
	 * @throws AgentInitializationException
	 */
	public boolean addLocalVM(long jvmId) throws IOException, AgentLoadException, AgentInitializationException{
    	JMXClient client = new JMXClient(jvmId);
    	List<LocalVMInfo> list = JMXClient.listLocalVM();
    	LocalVMInfo localvm = null;
    	for(LocalVMInfo vm :list){
    		if(vm.getId().equals(String.valueOf(jvmId))){
    			localvm = vm;
    			break;
    		}
    	}
    	if(localvm!=null){
    		JVM jvm = new JVM(jvmId, client);
    		client.getLocalVmMBeanServer(localvm);
    		jvm.setName(localvm.getName());
    		return true;
    	}
    	return false;
    }
	
	
	/**
	 * 添加远程jvm实例
	 * @param jvmEntity
	 * @return
	 */
	public JVMList addRemoteJVM(JVMEntity jvmEntity) {
    	long id=IdWorker.nextId();
    	if(id > 0 ){
    		jvmEntity.setId(id);
    	}else{
    		return null;
    	}
    	JMXClient  client = new JMXClient(jvmEntity.getId());
		JVM jvm = new JVM(jvmEntity.getId(),client);
		jvm.setName(jvmEntity.getName());
		if(!client.connect(jvmEntity.getIp(), String.valueOf(jvmEntity.getPort()))){
			try {
				new ConnectTask(jvm);
			} catch (Exception e) {
				logger.error("", e);
			};
		}else{
			System.out.println("建连成功");
		}
		JVMList jvmList = new JVMList();
    	jvmList.setName(jvmEntity.getName());
    	jvmList.setId(String.valueOf(jvmEntity.getId()));
		jvmList.setConnected(jvm.getClient().isConnected());
		jvmList.setTaskNames(ZQZTaskManager.getJobNames(id));
		return jvmList;
    }
	
	
	/**
	 * 获取JVM列表
	 * @return
	 */
	public JVMList[] getJVMList(){
		JVMList[] list = JVMManager.getAll();
		if(list != null && list.length > 0 ){
			JVMList tmp ;
			for(int i = 0 ; i < list.length ; i++){
				tmp = list[i];
				tmp.setTaskNames(ZQZTaskManager.getJobNames(Long.valueOf(tmp.getId())));
			}
		}
		return list;
	}
	
}

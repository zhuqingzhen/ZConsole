package com.zqz.jvm.web;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.zqz.common.IdWorker;
import com.zqz.jvm.bean.JVMList;
import com.zqz.jvm.jmx.JMXClient;
import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.bean.LocalVMInfo;
import com.zqz.jvm.jmx.notification.NotificationManager;
import com.zqz.jvm.bean.JVMEntity;
import com.zqz.jvm.service.JVMService;
import com.zqz.jvm.task.ZQZTaskManager;
import com.zqz.jvm.task.job.ConnectTask;

@RestController
@RequestMapping(value = "/jvm")
public class JvmController {
	
	private static Logger logger = LoggerFactory.getLogger(JvmController.class);
	
	@Autowired
	private JVMService jvmService;
	
	@RequestMapping("/getJVMList")
	public JVMList[] getJVMs() {
		return jvmService.getJVMList();
	}
	
    @RequestMapping(value="/delete/{jvmId}")
    public void delete(@PathVariable("jvmId") Long jvmId) {
    	ZQZTaskManager.clearJob(jvmId);
    	NotificationManager.clearNotification(jvmId);
    	JVMManager.remove(jvmId);
    }
    
    
    @RequestMapping(value="/add")
    public JVMList add(JVMEntity jvmEntity) {
    	long id=IdWorker.nextId();
    	System.out.println(id);
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
		}
		JVMList jvmList = new JVMList();
    	jvmList.setName(jvmEntity.getName());
    	jvmList.setId(String.valueOf(jvmEntity.getId()));
		jvmList.setConnected(jvm.getClient().isConnected());
		jvmList.setTaskNames(ZQZTaskManager.getJobNames(id));
		return jvmList;
    }
    
    
    @RequestMapping(value="/localvm/list")
    public List<LocalVMInfo> getLocal() throws IOException, AgentLoadException, AgentInitializationException{
    	return JMXClient.listLocalVM();
    }
    
    @RequestMapping(value="/localvm/add")
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
}

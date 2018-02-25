package com.zqz.jvm.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.zqz.jvm.bean.JVMList;
import com.zqz.jvm.jmx.JMXClient;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.bean.LocalVMInfo;
import com.zqz.jvm.jmx.notification.NotificationManager;
import com.zqz.jvm.bean.JVMEntity;
import com.zqz.jvm.service.JVMService;
import com.zqz.jvm.task.ZQZTaskManager;

/**
 * 
 * @author zqz
 *
 */
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
    	ZQZTaskManager.clearAllJob(jvmId);
    	NotificationManager.clearNotification(jvmId);
    	JVMManager.remove(jvmId);
    }
    
    
    @RequestMapping(value="/add")
    public JVMList addRemoteJVM(JVMEntity jvmEntity) {
		return jvmService.addRemoteJVM(jvmEntity);
    }
    
    
    @RequestMapping(value="/localvm/list")
    public List<LocalVMInfo> getLocal() throws IOException, AgentLoadException, AgentInitializationException{
    	return JMXClient.listLocalVM();
    }
    
    @RequestMapping(value="/localvm/add")
    public boolean addLocalVM(@RequestParam(name="jvmId") long jvmId) throws IOException, AgentLoadException, AgentInitializationException{
    	return jvmService.addLocalVM(jvmId);
    }
    
    @RequestMapping(value="/version")
    public Map<String,String> getVersion(@RequestParam(name="jvmId") long jvmId) throws Exception{
    	return jvmService.getVersion(jvmId);
    }
    
  
}

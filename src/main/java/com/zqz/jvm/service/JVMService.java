package com.zqz.jvm.service;

import org.springframework.stereotype.Service;

import com.zqz.jvm.bean.JVMList;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.task.ZQZTaskManager;

@Service
public class JVMService {
	
	
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
				tmp.setTaskNames(ZQZTaskManager.getJobNames(tmp.getId()));
			}
		}
		return list;
	}
}

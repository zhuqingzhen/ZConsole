package com.zqz.jvm.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zqz.jvm.bean.ReponseMessage;
import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.task.job.JVMCurMemoryInfoTask;
import com.zqz.jvm.task.job.JVMGCInfoTask;

@RestController
@RequestMapping(value = "/memory/chart")
public class MemoryChartController {

	private static Logger logger = LoggerFactory.getLogger(MemoryChartController.class);
	
	@ResponseBody
	@RequestMapping("/gcInfo")
	public ReponseMessage gcInfo(long jvmId) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		//如果gcinfo数据更新时间大于5秒，那么就启动定时任务；
		if (System.currentTimeMillis() - jvm.getUpdateGCInfoTime() > 5000){
			new JVMGCInfoTask(jvm);
		}
		//设置JVMGCInfoTask中判断退出的条件，否则超过一定时间没有访问，任务就会被删除
		jvm.updateGcInfoLastVisitTime();
		return new ReponseMessage(
				ReponseMessage.SUCCESS, 
				jvm.getIsYoungGc() ? jvm.getLastygc() : jvm.getLastfullgc());
	}
	
	
	@ResponseBody
	@RequestMapping("/curInfo")
	public ReponseMessage getCurMemory(long jvmId) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		//如果gcinfo数据更新时间大于5秒，那么就启动定时任务；
		if (System.currentTimeMillis() - jvm.getUpdateCurMemoryTime()> 5000){
			new JVMCurMemoryInfoTask(jvm);
		}
		//设置JVMGCInfoTask中判断退出的条件，否则超过一定时间没有访问，任务就会被删除
		jvm.updateGcInfoLastVisitTime();
		return new ReponseMessage(ReponseMessage.SUCCESS,jvm.getCurMemory());
	}
}

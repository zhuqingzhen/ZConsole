package com.zqz.jvm.task.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zqz.jvm.jmx.JVM;

/**
 * 获取jvm gc信息任务
 * @author zqz
 */
public class JVMGCInfoTask extends BaseTask {
	
	private static Logger logger = LoggerFactory.getLogger(JVMGCInfoTask.class);
	/**1分钟 ，单位毫秒，如果这个时间内jvm的jvm.getJstatLastVisitTime()没有更新，则停止当前任务*/
	private final static long STOP_WAIT_TIME = 1000 * 60 ;
	
	public JVMGCInfoTask(JVM jvm) throws Exception{
		type = BaseTask.JOB_TYPE_CLIENT_GCINFO;
		this.jvm = jvm;
		startOneSecondJob();
		//将gcInfo获取时间设置为当前时间，防止重复去创建当前定时任务；
		jvm.getUpdateGCInfoTime();
	}
	
	@Override
	public void doJob() {
		try {
			jvm.getGCINFO();
		} catch (Exception e) {
			logger.error("获取gc信息异常",e);
		}
		//如果gc信息一直没有人访问则停止任务；
		if(System.currentTimeMillis() - jvm.getJstatLastVisitTime() > STOP_WAIT_TIME ) {
			this.count = 0;
		}
	}

}

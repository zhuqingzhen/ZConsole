package com.zqz.jvm.task.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zqz.jvm.jmx.JVM;

/**
 * 获取jvm gc信息任务
 * @author zqz
 */
public class JVMCurMemoryInfoTask extends BaseTask {
	
	private static Logger logger = LoggerFactory.getLogger(JVMCurMemoryInfoTask.class);
	/**1分钟 ，单位毫秒，如果这个时间内jvm的jvm.getJstatLastVisitTime()没有更新，则停止当前任务*/
	private final static long STOP_WAIT_TIME = 1000 * 60 ;
	
	/**
	 * 
	 * @param jvm
	 * @throws Exception
	 */
	public JVMCurMemoryInfoTask(JVM jvm) throws Exception{
		type = BaseTask.JOB_TYPE_CLIENT_CUR_MEMORY;
		this.jvm = jvm;
		//启动定时任务，并将当任务加入到缓存中
		startOneSecondJob();
		//将curMemory获取时间设置为当前时间，防止重复去创建当前定时任务；
		jvm.getUpdateCurMemoryTime();
	}
	
	@Override
	public void doJob() {
		try {
			jvm.getMemoryInfo();
		} catch (Exception e) {
			logger.error("获取Memory信息异常",e);
		}
		//如果gc信息一直没有人访问则停止任务；
		if(System.currentTimeMillis() - jvm.getCurMemoryLastVisitTime() > STOP_WAIT_TIME ) {
			this.count = 0;
		}
	}

}

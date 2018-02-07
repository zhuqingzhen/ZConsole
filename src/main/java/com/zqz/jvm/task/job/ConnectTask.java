package com.zqz.jvm.task.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zqz.jvm.jmx.JVM;

/**
 * 获取jvm 尝试jmx连接
 * @author zqz
 */
public class ConnectTask extends BaseTask {
	
	private static Logger logger = LoggerFactory.getLogger(ConnectTask.class);
	
	public ConnectTask(JVM jvm) throws Exception{
		this.jvm = jvm;
		this.type = BaseTask.JOB_TYPE_SYS_CONNECT;
		startOneMinuteJob();
	}
	
	@Override
	public void doJob() {
		try {
			if(this.jvm.getClient().tryConnect()){//如果连接成功
				this.count = 0;
			}else{
				this.count = 1;
			}
		} catch (Exception e) {
			logger.error("获取gc信息异常",e);
		}
	}

}

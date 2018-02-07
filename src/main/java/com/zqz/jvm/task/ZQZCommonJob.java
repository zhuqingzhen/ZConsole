package com.zqz.jvm.task;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 201712131311
 * 封装quartz的Job任务，屏蔽任务的差异,使其更易扩展
 * 此类每次执行任务quartz都会重新生成
 * @author zqz
 */
public class ZQZCommonJob implements Job {
	
	public final static String JOB_PARAM_NAME = "Runnable";
	
	/**
	 * zqz
	 * 自带的传参方式很烂;
	 * 所有任务都封装到实现了Runnable接口的对象里面，通过参数将Runable的任务对象传递进来,并执行其run方法来实现执行任务，任务的所有内容都封装在Runnable对象里面;
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		/**获取Runnable参数,Runnable参数就是要执行的任务**/
		Object obj = context.getJobDetail().getJobDataMap().get(JOB_PARAM_NAME);
		if(obj == null || !(obj instanceof Runnable)){
			return;
		}
		//执行任务
		((Runnable)obj).run();
	}

}

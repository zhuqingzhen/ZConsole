package com.zqz.jvm.task.job;

import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.task.ZQZTaskManager;

/**
 * 任务基类，实现doJob方法；
 * @author zqz
 */
public abstract class BaseTask implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(BaseTask.class);
	/**任务名称后缀  gcinfo*/
	public final static String JOB_TYPE_CLIENT_GCINFO = "gcInfo";
	/**任务名称后缀  curMemory*/
	public final static String JOB_TYPE_CLIENT_CUR_MEMORY = "curMemoryInfo";
	/**任务名称后缀  auto connect**/
	public final static String JOB_TYPE_SYS_CONNECT = "connect";
	
	/**任务名称后缀**/
	public String type = null;
	
	/** 控制任务， 重复执行次数,如果是无限循环任务count=-1 ,也可以通过执行过程中改变count的值来控制任务的执行次数 */
	public final static int LOOP_COUNT = -9999;
	
	public final static String LOOP_FIVE_MINUTE = ".300."+LOOP_COUNT+".";
	public final static String LOOP_ONE_MINUTE = ".60."+LOOP_COUNT+".";
	public final static String LOOP_ONE_SECOND = ".1."+LOOP_COUNT+".";

	/** jvm */
	public JVM jvm;


	public int count = LOOP_COUNT;
	
	/**
	 * jobName是识别任务的唯一标示，删除定时任务时需要用到
	 * 任务名称 ,默认自动生成， 
	 * jvmId.intervalInSeconds.repeatCount.type
	 **/
	public JobKey jobKey;

	/**
	 * 要实现的接口，要执行的任务都在doJob方法里面实现；
	 */
	public abstract void doJob();
	
	@Override
	public void run() {
		//执行任务
		this.doJob();
		try {
			//判断是否需要清理任务， 如果是非循环任务则执行删除任务操作
			this.removeNoLoopJob();
		} catch (Exception e) {
			logger.error("清理任务异常", e);
		}
	}

	/**
	 * 添加任务
	 * @param jobName
	 * @throws Exception
	 */
	public void startFiveMinuteJob() throws Exception {
		String jobName =  jvm.getId() + LOOP_FIVE_MINUTE+type;
		jobKey = ZQZTaskManager.startLoopJob(this, jobName, ZQZTaskManager.get5MinTrigger(jobName));
	
	}

	/**
	 * add a job，do it every  minute
	 * @param jobName
	 * @throws Exception
	 */
	public void startOneMinuteJob() throws Exception {
		String jobName =  jvm.getId() + LOOP_ONE_MINUTE+type;
		jobKey = ZQZTaskManager.startLoopJob(this, jobName, ZQZTaskManager.get1MinTrigger(jobName));
	}
	
	/**
	 * 添加1秒钟循环定时任务
	 * 
	 * @throws Exception
	 */
	public void startOneSecondJob() throws Exception {
		String jobName =  jvm.getId() +  LOOP_ONE_SECOND + type;
		jobKey = ZQZTaskManager.startLoopJob(this, jobName, ZQZTaskManager.get1SecondTrigger(jobName));
	}


	/**
	 * 添加任务 任务名称为 jvmId.startTime.repeatCount
	 * 
	 * @param jvm
	 * @param startTime
	 * @param repeatCount
	 * @param intervalInSeconds
	 * @throws Exception
	 */
	public void startJob(long startTime, int repeatCount, int intervalInSeconds) throws Exception {
		count = repeatCount;
		String jobName = new StringBuilder(String.valueOf(jvm.getId())).append(".")
				.append(intervalInSeconds)
				.append(".").append(repeatCount).toString();
		jobKey = ZQZTaskManager.startJob(this, jobName, startTime, repeatCount, intervalInSeconds);
		ZQZTaskManager.addJob(jobKey);
	}

	/**
	 * 删除任务
	 * 
	 * @throws Exception
	 */
	public void removeJob() throws Exception {
		ZQZTaskManager.stopJob(jobKey);
	}

	/**
	 * 删除非无限循环任务，并且执行次数count为0的任务
	 * 
	 * @throws Exception
	 */
	public void removeNoLoopJob() throws Exception {
		// 对于非无限循环任务
		if (count != LOOP_COUNT && count-- <= 0) {
			this.removeJob();
		}
	}

}

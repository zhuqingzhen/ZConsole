package com.zqz.jvm.task;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zqz.jvm.jmx.JMXClient;
import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.task.job.JVMGCInfoTask;


/**
 * jobName两种命名规则，
 * 1.非循环，只需要执行一定次数的任务;
 * jvmId.intervalInSeconds.repeatCount.action
 * 2.循环任务，第三个为“-9999”字符串结尾;
 * jvmId.intervalInSeconds.loop.action
 */
/**
 * 秒 0-59 , - * / 分 0-59 , - * / 小时 0-23 , - * / 日期 1-31 , - * ? / L W C 月份 1-12
 * 或者 JAN-DEC , - * / 星期 1-7 或者 SUN-SAT , - * ? / L C # 年（可选） 留空, 1970-2099 , -
 * * /
 * 
 * 表达式 意义 "0 0 12 * * ?" 每天中午12点触发 "0 15 10 ? * *" 每天上午10:15触发 "0 15 10 * * ?"
 * 每天上午10:15触发 "0 15 10 * * ? *" 每天上午10:15触发 "0 15 10 * * ? 2005"
 * 2005年的每天上午10:15触发 "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发 "0 0/5 14 * * ?"
 * 在每天下午2点到下午2:55期间的每5分钟触发 "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
 * "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发 "0 10,44 14 ? 3 WED"
 * 每年三月的星期三的下午2:10和2:44触发 "0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发 "0 15 10 15 * ?"
 * 每月15日上午10:15触发 "0 15 10 L * ?" 每月最后一日的上午10:15触发 "0 15 10 ? * 6L"
 * 每月的最后一个星期五上午10:15触发 "0 15 10 ? * 6L 2002-2005"
 * 2002年至2005年的每月的最后一个星期五上午10:15触发 "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发
 * 
 * 特殊字符 意义 表示所有值； ? 表示未说明的值，即不关心它为何值； - 表示一个指定的范围； , 表示附加一个可能值； /
 * 符号前表示开始时间，符号后表示每次递增的值； L("last") ("last") "L" 用在day-of-month字段意思是
 * "这个月最后一天"；用在 day-of-week字段, 它简单意思是 "7" or "SAT"。
 * 如果在day-of-week字段里和数字联合使用，它的意思就是 "这个月的最后一个星期几" – 例如： "6L" means "这个月的最后一个星期五".
 * 当我们用“L”时，不指明一个列表值或者范围是很重要的，不然的话，我们会得到一些意想不到的结果。 W("weekday")
 * 只能用在day-of-month字段。用来描叙最接近指定天的工作日（周一到周五）。例如：在day-of-month字段用“15W”指“
 * 最接近这个月第15天的工作日”，即如果这个月第15天是周六，那么触发器将会在这个月第14天即周五触发；如果这个月第15天是周日，
 * 那么触发器将会在这个月第16天即周一触发；如果这个月第15天是周二，那么就在触发器这天触发。注意一点：这个用法只会在当前月计算值，不会越过当前月。“W”
 * 字符仅能在day-of-month指明一天，不能是一个范围或列表。也可以用“LW”来指定这个月的最后一个工作日。 #
 * 只能用在day-of-week字段。用来指定这个月的第几个周几。例：在day-of-week字段用"6#3"指这个月第3个周五（6指周五，3指第3个）。
 * 如果指定的日期不存在，触发器就不会触发。 C 指和calendar联系后计算过的值。例：在day-of-month
 * 字段用“5C”指在这个月第5天或之后包括calendar的第一天；在day-of-week字段用“1C”指在这周日或之后包括calendar的第一天
 * 
 * @author zqz
 *
 */
public class ZQZTaskManager {

	private static Logger logger = LoggerFactory.getLogger(ZQZTaskManager.class);
	// 通过schedulerFactory获取一个调度器
	private static SchedulerFactory schedulerfactory = new StdSchedulerFactory();

	private static String JOB_GROUP_NAME = "group";
	
	// 当前注册的jvm的任务<jvmId,Set<JobKey>>
	private static Map<Long, Set<JobKey>> jobs = new ConcurrentHashMap<Long, Set<JobKey>>();
	
	/**
	 * 清理掉某jvm全部的任务
	 * @return
	 */
	public static void clearJob(long  jvmId){
		Set<JobKey> jobKeys = jobs.remove(jvmId);
		if(jobKeys!=null)
		for(JobKey item : jobKeys){
			try {
				delJob(item);
			} catch (Exception e) {
				logger.error("移除job异常,jvmId="+jvmId+"  jobKeys:"+jobKeys.toString(), e);
			}
		}
	}
	
	/**
	 * 获取指定jvm的任务名称列表
	 * @param jvmId
	 * @return
	 */
	public static String[] getJobNames(long jvmId){
		Set<JobKey> set = jobs.get(jvmId);
		if(set != null && set.size() > 0 ){
			String[] list = new String[set.size()];
			Iterator<JobKey> i = set.iterator();
			int index = 0;
			while(i.hasNext()){
				list[index++] = i.next().getName();
			}
			return list;
		}
		return null;
	}
	
	/**
	 * 添加定时任务
	 * @param jobName
	 * @throws Exception 
	 */
	public static void addJob(JobKey jobKey) throws Exception {
		JobName jobName = JobName.praiseJobName(jobKey.getName());
		Set<JobKey> jmb = jobs.get(jobName.getJvmId());
		if(jmb != null && jmb.size() > 0 ){
			Iterator<JobKey> i = jmb.iterator();
			while(i.hasNext()){
				if(i.next().getName().equals(jobKey.getName())){
					return;
				}
			}
			jmb.add(jobKey);
			logger.debug("--add job："+jobKey.getName());
		}else{
			Set<JobKey> set = new HashSet<JobKey>();
			set.add(jobKey);
			jobs.put(jobName.getJvmId(), set);
		}
	}
	
	

	/**
	 * 判断任务是否存在
	 * 解析JobName，拿到jvmId
	 * 根据jvmId获取全部的任务列表，然后根据jobName去找是否存在该任务
	 * @param jobName
	 * @return
	 * @throws Exception 
	 */
	public static boolean isExist(String jobName) throws Exception {
		Set<JobKey> jmb = jobs.get(JobName.praiseJobName(jobName).getJvmId());
		if(jmb != null && jmb.size() > 0 ){
			Iterator<JobKey> i = jmb.iterator();
			JobKey tmp = null;
			Scheduler scheduler = schedulerfactory.getScheduler();
			while(i.hasNext()){
				tmp = i.next();
				if(tmp.getName().equals(jobName) && scheduler.checkExists(tmp)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 删除定时任务
	 * 
	 * @param jobName
	 * @throws Exception 
	 */
	private static void delJob(JobKey jobKey) throws Exception {
		long jvmId = JobName.praiseJobName(jobKey.getName()).getJvmId();
		Set<JobKey> jmb = jobs.get(JobName.praiseJobName(jobKey.getName()).getJvmId());
		if(jmb != null && jmb.size()>0){
			Iterator<JobKey> i = jmb.iterator();
			while(i.hasNext()){
				if(i.next().getName().equals(jobKey.getName())){
					i.remove();
					logger.debug("--del job："+jobKey.getName());
				}
			}
		}else{
			jobs.remove(jvmId);
		}
	}
	
	/**
	 * 暂停任务
	 * @param jvmId
	 * @throws SchedulerException
	 */
	public static void pauseJobsByJVM(long jvmId) throws SchedulerException{
		Set<JobKey> jmb = jobs.get(jvmId);
		if(jmb != null && jmb.size() > 0){
			Iterator<JobKey> i = jmb.iterator();
			JobKey tmp;
			while(i.hasNext()){
				tmp = i.next();
				if(!tmp.getName().endsWith(".connectloop")){
					Scheduler scheduler = schedulerfactory.getScheduler();
					scheduler.pauseJob(tmp);
					logger.debug("--pause job："+tmp.getName());
				}
			}
		}
	}
	
	/**
	 * 重新启动任务
	 * @param jvmId
	 * @throws SchedulerException
	 */
	public static void resumeJobsByJVM(long jvmId) throws SchedulerException{
		Set<JobKey> jmb = jobs.get(jvmId);
		if(jmb != null && jmb.size() > 0){
			Iterator<JobKey> i = jmb.iterator();
			JobKey tmp;
			while(i.hasNext()){
				tmp = i.next();
				if(!tmp.getName().endsWith(".connectloop")){
					Scheduler scheduler = schedulerfactory.getScheduler();
					scheduler.resumeJob(tmp);
					logger.debug("--resume job："+tmp.getName());
				}
			}
		}
	}

	/** 5分钟触发一次 **/
	private final static TriggerBuilder<CronTrigger> TRIGGER_BUILDER_FIVE_MINUTE = TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?")) // 定时执行
			.endAt(null) // 结束时间
			.startAt(null) // 开始时间
			.startNow();

	public static Trigger get5MinTrigger(String name) {
		return TRIGGER_BUILDER_FIVE_MINUTE.withIdentity(name).build();
	}

	/** 1分钟触发一次 **/
	private final static TriggerBuilder<CronTrigger> TRIGGER_BUILDER_ONE_MINUTE = TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")) // 定时执行
			.endAt(null) // 结束时间
			.startAt(null) // 开始时间
			.startNow();

	public static Trigger get1MinTrigger(String name) {
		return TRIGGER_BUILDER_ONE_MINUTE.withIdentity(name).build();
	}

	/** 1秒钟触发一次 **/
	private final static TriggerBuilder<CronTrigger> TRIGGER_BUILDER_ONE_SECOND = TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?")) // 定时执行
			.endAt(null) // 结束时间
			.startAt(null) // 开始时间
			.startNow();

	public static Trigger get1SecondTrigger(String name) {
		return TRIGGER_BUILDER_ONE_SECOND.withIdentity(name).build();
	}


	/**
	 * 添加任务
	 * 
	 * @param task
	 *            要执行的任务
	 * @param jobName
	 *            任务名称
	 * @param trigger
	 *            触发器
	 * @throws Exception 
	 */
	public static JobKey startLoopJob(Runnable task, String jobName, Trigger trigger) throws Exception {
		// 创建JobDetial对象 ,绑定Job实现类 ,指明job的名称，所在组的名称;
		JobDetail jobDetail = JobBuilder.newJob(ZQZCommonJob.class).withIdentity(jobName, JOB_GROUP_NAME).build();
		return startLoopJob(task, jobDetail, trigger);
	}


	/**
	 * 添加无限循环执行的任务,根据表达式指定任务执行规则
	 * 
	 * @param task
	 *            要执行的任务
	 * @param jobName
	 *            任务名称
	 * @param triggerName
	 *            触发器名称
	 * @param timeExpression
	 *            触发规则
	 * @throws Exception 
	 */
	public static JobKey startLoopJob(Runnable task, String jobName, String timeExpression) throws Exception {
		// 创建Trigger对象,traggerName与jobName相同
		CronTriggerImpl trigger = (CronTriggerImpl) TriggerBuilder.newTrigger().withIdentity(jobName)
				.withSchedule(CronScheduleBuilder.cronSchedule(timeExpression)) // 定时执行
				.endAt(null) // 结束时间
				.startAt(null) // 开始时间
				.startNow().build();
		return startLoopJob(task, jobName, trigger);
	}
	
	/**
	 * 添加无限循环执行的任务,
	 * 
	 * @param task
	 * @param jobDetail
	 * @param trigger
	 * @throws Exception 
	 */
	public static JobKey startLoopJob(Runnable task, JobDetail jobDetail, Trigger trigger) throws Exception {
		if(ZQZTaskManager.isExist(jobDetail.getKey().getName())){
			logger.debug(" The job \"" +jobDetail.getKey().getName() + "\" has already Exist;");
			return jobDetail.getKey();
		}
		jobDetail.getJobDataMap().put(ZQZCommonJob.JOB_PARAM_NAME, task);
		Scheduler scheduler = schedulerfactory.getScheduler();
		// 把作业和触发器注册到任务调度中
		scheduler.scheduleJob(jobDetail, trigger);
		// 并执行启动、关闭等操作
		if (!scheduler.isShutdown() && !scheduler.isStarted()) {
			scheduler.start();
		} 
		//将任务添加进队列
		ZQZTaskManager.addJob(jobDetail.getKey());
		return jobDetail.getKey();
	}
	
	/**
	 * 添加一个执行有限次数的任务;
	 * 
	 * @param startTime
	 *            多少秒后开始执行
	 * @param repeatCount
	 *            重复执行次数
	 * @param intervalInSeconds
	 * @throws Exception 
	 */
	public static JobKey startJob(Runnable task, String jobName, long startTime, int repeatCount, int intervalInSeconds) throws Exception {
		if (repeatCount > 0){
			repeatCount--;
		}
		SimpleTriggerImpl trigger = (SimpleTriggerImpl) TriggerBuilder.newTrigger()
				.withIdentity("NOT_LOOP_TRIGGER" + startTime + "-" + repeatCount + "-" + intervalInSeconds, "NOT_LOOP")
				.startAt(new Date(System.currentTimeMillis() + startTime * 1000))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(intervalInSeconds)
						.withRepeatCount(repeatCount))// 重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
				.build();
		// 1、创建JobDetial对象 ,绑定Job实现类 ,指明job的名称，所在组的名称;
		JobDetail jobDetail = JobBuilder.newJob(ZQZCommonJob.class).withIdentity(jobName, JOB_GROUP_NAME).build();
		if(ZQZTaskManager.isExist(jobName)){
			logger.debug(" The job \"" +jobDetail.getKey().getName() + "\" has already Exist;");
			return jobDetail.getKey();
		}
		jobDetail.getJobDataMap().put(ZQZCommonJob.JOB_PARAM_NAME, task);
		Scheduler scheduler = schedulerfactory.getScheduler();
		// 把作业和触发器注册到任务调度中
		scheduler.scheduleJob(jobDetail, trigger);
		// 4、并执行启动、关闭等操作
		if (!scheduler.isShutdown() && !scheduler.isStarted()) {
			scheduler.start();
		}
		//将任务放入队列
		ZQZTaskManager.addJob(jobDetail.getKey());
		return jobDetail.getKey();
	}
	
	/**
	 * 删除任务
	 * 
	 * @param jobName
	 * @throws SchedulerException
	 */
	public static void stopJob(JobKey jobKey) throws Exception {
		Scheduler scheduler = schedulerfactory.getScheduler();
		if (!scheduler.isShutdown()) {
			scheduler.deleteJob(jobKey);
		}
		delJob(jobKey);
	}

	public static void main(String[] args) throws SchedulerException, InterruptedException {

		String port = "6666";
		String host = "192.168.2.74";
	
		JMXClient client = new JMXClient(1);
		while (!client.connect(host, port)) {
			Thread.sleep(3000);
		}
		JVM jvm = new JVM(1, client);
		try {
			new JVMGCInfoTask(jvm);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

/**
 * 两种命名规则，
 * 1.非循环，只需要执行一定次数的任务;
 * jvmId.intervalInSeconds.repeatCount.action
 * 2.循环任务，第三个为“-9999”字符串结尾;
 * jvmId.intervalInSeconds.loop.action
 * */
class JobName{
	
	private long jvmId;
	
	private long intervalInSeconds;
	
	private long count;
	
	private String action;
	
	JobName(long jvmId,long intervalInSeconds,long count,String action){
		this.jvmId = jvmId;
		this.intervalInSeconds = intervalInSeconds;
		this.count = count;
		this.action = action;
	}
	
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * 两种命名规则，
	 * 1.非循环，只需要执行一定次数的任务;
	 * jvmId.intervalInSeconds.repeatCount.action
	 * 2.循环任务，第三个为“-9999”字符串结尾;
	 * jvmId.intervalInSeconds.loop.action
	 * @param jobName
	 * @return
	 * @throws Exception 
	 */
	public static JobName praiseJobName(String jobName) throws Exception{
		if(jobName == null || jobName.length() == 0){
			return null;
		}
		String[] arr = jobName.split("\\.");
		if(arr.length != 4){
			throw new Exception("error job name:"+jobName);
		}
		return  new JobName(Long.parseLong(arr[0]),Long.parseLong(arr[1]),Long.parseLong(arr[2]),arr[3]);
	}
	
	public long getJvmId() {
		return jvmId;
	}
	
	public void setJvmId(long jvmId) {
		this.jvmId = jvmId;
	}
	
	public long getIntervalInSeconds() {
		return intervalInSeconds;
	}
	
	public void setIntervalInSeconds(long intervalInSeconds) {
		this.intervalInSeconds = intervalInSeconds;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
}

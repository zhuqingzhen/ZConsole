package com.zqz.jvm.task;

public class TaskError {
/**
 *从任务调度的触发时机来分，这里主要是针对作业使用的触发器，主要有以下两种：
 *
 *1、每隔指定时间则触发一次，在Quartz中对应的触发器为：
 *quartz 对应org.quartz.impl.triggers.SimpleTriggerImpl
 *spring-task 对应org.springframework.scheduling.quartz.SimpleTriggerBean
 *2、每到指定时间则触发一次，在Quartz中对应的调度器为：
 *quartz 对应org.quartz.impl.triggers.CronTriggerImpl
 *spring-task 对应org.springframework.scheduling.quartz.CronTriggerBean
 * 
 * 劣势
 * 一个trigger只能帮顶一个job;
 * 定时任务创建时只能传入class类，每次执行任务都会根据classes类new 一个Job对象，如果定时任务很多，任务执行频繁，会导致频繁创建短生命周期的对象，增加垃圾回收开销；
 *
 *springtask无法暂停/恢复，删除任务;
 */
}

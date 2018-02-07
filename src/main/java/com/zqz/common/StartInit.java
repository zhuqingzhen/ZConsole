package com.zqz.common;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.zqz.jvm.task.job.ConnectTask;

/**
 * @author zqz
 * @create 2018-01-02 21:54
 * @description
 */
public class StartInit implements ApplicationListener<ContextRefreshedEvent> {
	private static Logger logger = LoggerFactory.getLogger(ConnectTask.class);

	/**
	 * 启动时初始化放在这里执行
	 */
	@Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    	System.out.println("初始化");
    }
}
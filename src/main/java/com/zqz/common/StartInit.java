package com.zqz.common;



import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	static{
		RuntimeMXBean osBean = ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
		Map<String,String> map = osBean.getSystemProperties();
		Set<Entry<String,String>> set = map.entrySet();
		for(Entry<String,String> e: set){
			System.out.println(e.getKey()+"=="+e.getValue());
		}
	}

	/**
	 * 启动时初始化放在这里执行
	 */
	@Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    	System.out.println("初始化");
    }
}
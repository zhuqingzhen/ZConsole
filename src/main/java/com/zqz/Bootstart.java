package com.zqz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.zqz.common.StartInit;

/**
 * 2018-01-08 23:06
 * @author zqz
 */
@SpringBootApplication
public class Bootstart {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Bootstart.class); 
		app.addListeners(new StartInit());
        app.run(args);
	}
	
	
   /**
	* 注册hook程序，保证线程能够完整执行
	* @param checkThread
	*/
	private static void addShutdownHook() {
		//为了保证TaskThread不在中途退出，添加ShutdownHook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("收到关闭信号，hook起动，开始检测线程状态 ...");
				//執行關閉任務
				System.out.println("检测超时，放弃等待，退出hook，此时线程会被强制关闭");
			}
		});
	}

}

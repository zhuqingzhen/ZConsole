package com.zqz.jvm.jmx.notification;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.alibaba.fastjson.JSON;
import com.zqz.jvm.jmx.JMXTypeUtil;
import com.zqz.jvm.jmx.notification.websocket.Constant;

/**
 * 监听通知
 * @author zqz
 */
public class ZQZNotificationListerer  implements NotificationListener {
	
	private static Logger logger = LoggerFactory.getLogger(ZQZNotificationListerer.class);
	private long jvmId;
	private String objectName;
	private SimpMessagingTemplate template;
	
	public ZQZNotificationListerer(long jvmId,String objectName,SimpMessagingTemplate template){
		this.jvmId = jvmId;
		this.objectName = objectName;
		this.template = template;
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		// TODO Auto-generated method stub
		//execute this method when listener receive notification
		String[] userIds = NotificationManager.getSubscribeUserIds(jvmId,objectName);
		if(userIds!=null && userIds.length>0){
			try {
				notification.setUserData(JMXTypeUtil.getResult(notification.getUserData()));
			} catch (Exception e) {
				logger.error("格式化jvm通知订阅数据异常", e);
			}
			System.out.println(JSON.toJSONString(notification));
			for(int i = 0 ;i < userIds.length ;i++ ){
				template.convertAndSendToUser(userIds[i], Constant.P2P_PUSH_PATH, notification);
		    };
		}
        if (handback != null) {  
            System.out.println(handback.getClass().getName());
        }  
	}

}

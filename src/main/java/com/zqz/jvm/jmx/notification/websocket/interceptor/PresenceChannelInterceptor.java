package com.zqz.jvm.jmx.notification.websocket.interceptor;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.MBeanUtil;
import com.zqz.jvm.jmx.notification.NotificationManager;
import com.zqz.jvm.jmx.notification.websocket.Constant;

/**
 * stomp连接处理类
 * @author zqz
 */
@Component
public class PresenceChannelInterceptor extends ChannelInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(PresenceChannelInterceptor.class);

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
		// ignore non-STOMP messages like heartbeat messages
		if (sha.getCommand() == null) {
			return;
		}
		String uid = null;
		if (StompCommand.CONNECT.equals(sha.getCommand())) {
			Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
			if (raw!=null && raw instanceof Map ) {
				Map<String,List<Object>> map = (Map<String,List<Object>>)raw;
				if(map.size()>0){
					uid = String.valueOf((map.get("userId")).get(0));
					sha.getSessionAttributes().put(Constant.SESSIONID, uid);
				}
			}
		}else if(StompCommand.DISCONNECT.equals(sha.getCommand())){
			uid = (String)sha.getSessionAttributes().get(Constant.SESSIONID);
		}
		
		// 判断客户端的连接状态
		switch (sha.getCommand()) {
		case CONNECT:
			connect(uid);
			break;
		case CONNECTED:
			break;
		case DISCONNECT:
			try {
				disconnect(uid, sha);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("websocket断开连接时发生异常",e);
			}
			break;
		default:
			break;
		}
	}

	// 连接成功
	private void connect(String uid) {
		logger.debug(" STOMP Connect [sessionId: " + uid + "]");
		System.out.println("连接成功"+uid);
	}

	// 断开连接
	private void disconnect(String uid, StompHeaderAccessor sha) throws Exception {
		logger.debug("STOMP Disconnect [sessionId: " + uid + "]");
		System.out.println("断开连接"+uid);
		String value = NotificationManager.getSubscribeNames(uid);
		if(value!=null){
			String[] args = value.split("::");
			if(args!=null && args.length == 2){
				JVM jvm = JVMManager.get(Long.valueOf(args[0]));
				if (jvm == null) {
					return;
				}
				if(NotificationManager.remove(uid)){
					MBeanUtil.unSubscribe(jvm, args[1]);
					System.out.println("取消监听器:"+jvm.getId()+"--"+args[1]);
				}
				
			}
		}
	}

}

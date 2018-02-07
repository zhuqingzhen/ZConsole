package com.zqz.jvm.jmx.notification.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.zqz.jvm.jmx.notification.websocket.interceptor.PresenceChannelInterceptor;

/**
 * jmx订阅功能websocket配置
 * @author zqz
 */
@Configuration
@EnableWebSocketMessageBroker
public class JMXWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
		// 注册一个Stomp的节点（endpoint）,并指定使用SockJS协议。
		stompEndpointRegistry.addEndpoint(Constant.WEBSOCKET_PATH)
		//允许指定的域名或IP(含端口号)建立长连接，如果只允许自家域名访问，这里轻松设置。如果不限时使用"*"号，如果指定了域名，则必须要以http或https开头
		.setAllowedOrigins("*") 
		.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 服务端发送消息给客户端的域,多个用逗号隔开,这句表示在topic和user这两个域上可以向客户端发消息
		registry.enableSimpleBroker(Constant.WEBSOCKET_BROADCAST_PATH, Constant.P2P_PUSH_BASEPATH);
		// 定义一对一推送的时候前缀
		registry.setUserDestinationPrefix(Constant.P2P_PUSH_BASEPATH);
		// 定义websoket前缀,这句表示客户端向服务端发送时的主题上面需要加 WEBSOCKET_PATH_PREFIX = "/ws-push"作为前
		registry.setApplicationDestinationPrefixes(Constant.WEBSOCKET_PATH_PREFIX);
	}
	
	

    /** 
     * 消息传输参数配置 
     */
    @Override  
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {  
        registry.setMessageSizeLimit(8192) //设置消息字节数大小
                .setSendBufferSizeLimit(8192)//设置消息缓存大小
                .setSendTimeLimit(10000); //设置消息发送时间限制毫秒
    }  
	
	 /** 
     * 输入通道参数设置 
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4)//设置消息输入通道的线程池线程数
                .maxPoolSize(8)//最大线程数
                .keepAliveSeconds(60);//线程活动时间
        //监控上线
        registration.interceptors(presenceChannelInterceptor());
    }
  
    /** 
     * 输出通道参数设置 
     */  
    @Override  
    public void configureClientOutboundChannel(ChannelRegistration registration) {  
        registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
        //监控断线
        registration.interceptors(presenceChannelInterceptor()); 
    }
    
    
    /**
     * 用户上下线监控
     * @return
     */
    @Bean  
    public PresenceChannelInterceptor presenceChannelInterceptor() {  
        return new PresenceChannelInterceptor();  
    }  
}

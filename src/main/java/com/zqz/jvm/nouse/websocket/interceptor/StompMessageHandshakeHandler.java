package com.zqz.jvm.nouse.websocket.interceptor;
//package com.zqz.jvm.websocket.interceptor;
//
//import java.security.Principal;
//import java.util.Map;
//
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//
///**
// * 获取客户端连接前对客户端的session、cookie等信息进行握手处理， 也就是可以在这里可以进行一些用户认证？
// * 这是我个人的理解。这里没有做任何处理
// *
// */
//public class StompMessageHandshakeHandler extends DefaultHandshakeHandler{
//	@Override
//	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
//			Map<String, Object> attributes) {
//		return super.determineUser(request, wsHandler, attributes);
//	}
//}

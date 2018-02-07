package com.zqz.jvm.nouse.websocket.interceptor;
//package com.zqz.jvm.websocket.interceptor;
//
//import java.util.Map;
//
//import javax.servlet.http.HttpSession;
//
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
//
//import com.zqz.jvm.jmx.notification.websocket.Constant;  
//
//
///**
// * 功能说明：websocket连接的拦截器
// * 点对对式发送消息到某一个用户，需要把某一个用户的id存放在session中（项目使用accountId表示用户的唯一性，已在登录的时候，把这个accountId保存到HttpSession中），这里直接获取accountId就可以
// * WebSocket握手请求的拦截器. 检查握手请求和响应
// * 
// * @date 2018-01-23 00:49 
// * @author zqz
// */
//@Component  
//public class HttpSessionIdHandshakeInterceptor extends HttpSessionHandshakeInterceptor  {  
//  
//	//在握手之前执行该方法, 继续握手返回true, 中断握手返回false. 通过attributes参数设置WebSocketSession的属性
//	/**
//	 * 注意attributes不可以传递值为null的参数否则会抛出异常
//	 */
//    @Override  
//    public boolean beforeHandshake(ServerHttpRequest request,  
//                                   ServerHttpResponse response, WebSocketHandler wsHandler,  
//                                   Map<String, Object> attributes) throws Exception {  
//        //解决The extension [x-webkit-deflate-frame] is not supported问题  
//        if(request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {  
//            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");  
//        }
//        
//        //检查session的值是否存在  
//        if (request instanceof ServletServerHttpRequest) {  
//            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;  
//            HttpSession session = servletRequest.getServletRequest().getSession(false);
//            if (session!=null) {
//				System.out.println("ok"+session.getId());
//				attributes.put(Constant.SESSIONID, session.getId());
//				//可以在此处存放要保存的信息，然后在建立连接或断开连接的时候就可以获取到这些信息；
////				String accountId = (String) session.getAttribute(Constant.SKEY_ACCOUNT_ID);  
////	            //把session和accountId存放起来   ,
////	            attributes.put(Constant.SESSIONID, session.getId());
////	            attributes.put(Constant.SKEY_ACCOUNT_ID, accountId);
//			}
//              
//        }  
//        
//        return super.beforeHandshake(request, response, wsHandler, attributes);  
//    }  
//  
//    //在握手之后执行该方法. 无论是否握手成功都指明了响应状态码和响应头.
//    @Override  
//    public void afterHandshake(ServerHttpRequest request,  
//                               ServerHttpResponse response, WebSocketHandler wsHandler,  
//                               Exception ex) {
//        super.afterHandshake(request, response, wsHandler, ex);  
//    }  
//  
//  
//}

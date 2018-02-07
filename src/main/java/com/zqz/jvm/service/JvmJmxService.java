package com.zqz.jvm.service;

import javax.management.MBeanInfo;
import javax.management.ObjectName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.zqz.jvm.bean.Node;
import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.MBeanUtil;
import com.zqz.jvm.jmx.notification.NotificationManager;

@Service
public class JvmJmxService {
	
	@Autowired
    private SimpMessagingTemplate template;
	
	/**
	 * 订阅通知
	 * @param jvmId
	 * @param userId
	 * @param template 点对点推送消息
	 * @param objectName
	 * @throws Exception 
	 */
	public void subscribe(long jvmId,String userId,String objectName,SimpMessagingTemplate template) throws Exception{
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return;
		}
		MBeanUtil.subscribe(userId,jvm, objectName,template);
	}
	
	/**
	 * 订阅通知
	 * @param jvmId
	 * @param userId
	 * @param template 点对点推送消息
	 * @param objectName
	 * @throws Exception 
	 */
	public void unSubscribe(String userId) throws Exception{
		String  value = NotificationManager.getSubscribeNames(userId);
		if(NotificationManager.remove(userId)){
			if(value==null)
				return ;
			String[] params = value.split("::");
			if(params!=null && params.length == 2){
				JVM jvm = JVMManager.get(Long.valueOf(params[0]));
				if (jvm == null) {
					return;
				}
				MBeanUtil.unSubscribe(jvm, params[1]);
			}
		}
	}
	
	/**
	 * 获取jmx数
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	public Node[] getTree(long jvmId) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		Node node = MBeanUtil.getJMXTree(jvm);
		return node.getChildren();
	}
	
	/**
	 * 获取属性值
	 * @param jvmId
	 * @param objectName
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public Object getAttributeValue(long jvmId,ObjectName objectName,String attrName) throws Exception{
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return MBeanUtil.getObjectNameValue(objectName, attrName, jvm);
	}
	
	/**
	 * 获取MBeanInfo
	 * @param objectName
	 * @return
	 * @throws Exception
	 */
	public MBeanInfo getMBeanInfo(long jvmId,String objectName) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return MBeanUtil.getMBeanInfo(objectName, jvm);
	}
	
	
	/**
	 * 调用jmx方法
	 * @param jvmId
	 * @param objectName
	 * @param methodName
	 * @param params
	 * @param signature
	 * @return
	 * @throws Exception
	 */
	public Object execute(long jvmId,String objectName,String methodName,Object[] params,String[] signature ) throws Exception {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
			return null;
		}
		return MBeanUtil.execute(objectName, methodName, params, signature, jvm);
	}
}

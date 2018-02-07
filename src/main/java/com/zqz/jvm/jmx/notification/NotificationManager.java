package com.zqz.jvm.jmx.notification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.NotificationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.MBeanUtil;


/**
 * 管理MBean的通知
 * @author zqz
 */
public class NotificationManager {
	
	
	private static Logger logger = LoggerFactory.getLogger(NotificationManager.class);
	/**
	 * key---value  
	 * userId---jvmId::objectName
	 */
	private static ConcurrentHashMap<String,String> userListener = new ConcurrentHashMap<String,String>();
	/**
	 * key---value  
	 * jvmId::objectName --- Set<userId>
	 */
	private static ConcurrentHashMap<String,HashSet<String>> listenerUser = new ConcurrentHashMap<String,HashSet<String>>();
	
	
	/**
	 * key---value  
	 * jvmId::objectName --- NotificationListener
	 */
	private static ConcurrentHashMap<String,NotificationListener> listeners = new ConcurrentHashMap<String,NotificationListener>();
	
	/**
	 * 清除jvm的通知
	 * @param jvmId
	 */
	public static void clearNotification(long jvmId){
		String jvmIdStr = String.valueOf(jvmId)+"::";
		Set<Entry<String, HashSet<String>>> entity = listenerUser.entrySet();
		List<String> objectNames = new ArrayList<String>();
		List<Set<String>> userIds = new ArrayList<Set<String>>();
		for(Entry<String, HashSet<String>> e :entity){
			if(e.getKey().startsWith(jvmIdStr)){
				objectNames.add(e.getKey());
				userIds.add(e.getValue());
			}
		}
		JVM jvm = JVMManager.get(jvmId);
		boolean isConnected = jvm.getClient().isConnected();
		String objectName = null;
		for(int i =0 , j = objectNames.size() ;i < j ;i++ ){
			objectName = objectNames.get(i);
			//清理用户列表
			listenerUser.remove(objectName);
			//清理监听器列表
			if (jvm != null && isConnected) {
				try {
					MBeanUtil.unSubscribe(jvm, objectName.substring(objectName.indexOf("::")+2));
				} catch (Exception e1) {
					logger.error("取消订阅jmx消息异常", e1);
				}
			}
			listeners.remove(objectName);
		}
		Set<String> ids = null;
		for(int i =0 , j = userIds.size() ;i < j ;i++ ){
			ids = userIds.get(i);
			if(ids.size()>0){
				for(String id:ids){
					userListener.remove(id);
				}
			}
		}
		
	}
	
	/**
	 * 根据uid获取订阅的通知
	 * @param userId
	 * @return
	 */
	public static String getSubscribeNames(String userId){
		return userListener.get(userId);
	}
	
	/**
	 * 根据jvmId和objectName获取订阅的uid列表
	 * @param jvmId
	 * @param objectName
	 * @return
	 */
	public static String[] getSubscribeUserIds(long jvmId,String objectName){
		HashSet<String> set = listenerUser.get(new StringBuffer(String.valueOf(jvmId)).append("::").append(objectName).toString());
		if(set==null || set.size() ==0){
			return null;
		}
		Iterator<String> iterator = set.iterator();
		String[] result = new String[set.size()];
		int i =0;
		while(iterator.hasNext()){
			result[i++] = iterator.next();
		}
		return result;
	}
	
	/**
	 * 查找监听
	 * @param objectName
	 * @return  listener
	 */
	public static NotificationListener getListener(long jvmId,String objectName){
		return listeners.get(objectName);
	}
	
	/**
	 * 添加监听
	 * @param objectName
	 * @param listener
	 * @return  是否已经存在监听器,已经存在则返回旧的监听器，不存在则返回null，并添加新的监听器
	 */
	public static NotificationListener addListener(long jvmId,String objectName,NotificationListener listener){
		String key = new StringBuffer(String.valueOf(jvmId)).append("::").append(objectName).toString();
		NotificationListener old = listeners.get(key);
		if(old == null ){
			listeners.put(key, listener);
			return null;
		}
		return old;
	}
	
	/**
	 * 添加监听
	 * @param userId
	 * @param jvmId
	 * @param objectName
	 * @return 是否需要取消该监听器   true 需要添加监听器，false 已经添加过了不需要添加
	 */
	public static boolean add(String userId,long jvmId,String objectName){
		boolean result =false;
		String oldValue = userListener.get(userId);
		if(oldValue == null ){
			//更新缓存1
			String value = new StringBuffer(String.valueOf(jvmId)).append("::").append(objectName).toString();
			userListener.put(userId, value);
			//更新缓存2
			HashSet<String> userIds = listenerUser.get(value);
			if(userIds == null || userIds.size() == 0){
				userIds = new HashSet<String>();
				listenerUser.put(value, userIds);
				//只有当key监听对象不存在时返回true
				result = true;
			}
			if(!userIds.contains(userId)){
				userIds.add(userId);
			}
		}
		return result;
	}
	
	/**
	 * 移除某uid的监听记录
	 * @param userId
	 * @return true  是否需要取消该监听器
	 */
	public static boolean remove(String userId){
		String objectName = userListener.get(userId);
		if(objectName !=null ){
			userListener.remove(userId);
		}else{
			return false;
		}
		HashSet<String> userIds = listenerUser.get(objectName);
		if(userIds != null ){
			userIds.remove(userId);
			if(userIds.size() == 0){
				listenerUser.remove(objectName);
				return true;
			}
			return false;
		}else{
			return true;
		}
		
	}
}

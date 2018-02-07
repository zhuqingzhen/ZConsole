package com.zqz.jvm.jmx;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;

/**
 * jmx类型转换工具类
 * @author zqz
 * @date   2018-01-21 23:26
 */
public class JMXTypeUtil {
	
	private static Field CompositeDataSupport_contentField = null;
	private static Field TabularDataSupport_dataMap = null;

	static {
		init();
	}
	
	public static void init(){
		Field[] fs = CompositeDataSupport.class.getDeclaredFields();
		for (int j = 0; j < fs.length; j++) {
			if (fs[j].getName().equals("contents")) {
				fs[j].setAccessible(true); // 设置些属性是可以访问的
				CompositeDataSupport_contentField = fs[j];
				break;
			}
		}
		fs = TabularDataSupport.class.getDeclaredFields();
		for (int j = 0; j < fs.length; j++) {
			if (fs[j].getName().equals("dataMap")) {
				fs[j].setAccessible(true); // 设置些属性是可以访问的
				TabularDataSupport_dataMap = fs[j];
				break;
			}
		}
	}
	
	/**
	 * 解析特殊数据类型，CompositeDataSupport与TabularDataSupport类型中的数据无法正常解析为json串
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Object getResult(Object obj) throws Exception {
		if(obj==null ){
			return null;
		}/*else if(obj.equals("")){
			return "返回值为空";
		}*/
		if(obj.getClass().isArray()){//2018-02-04 21:55,解析dumpThread结果为CompositeDataSupport的数组的情况
			Object[] arr = (Object[])obj;
			Object[] result = new Object[arr.length];
			for(int i = 0 ;i<arr.length ;i++){
				result[i] = getResult(arr[i]);
			}
			return result;
		} else if (obj instanceof CompositeDataSupport) {
			return JMXTypeUtil.managerCompositeDataSupport(obj);
		} else if (obj instanceof ObjectName) {
			return ((ObjectName) obj).getCanonicalName();
		} else if (obj instanceof TabularDataSupport) {
			return JMXTypeUtil.managerTabularDataSupport(obj);
		} else {
			return obj;
		}
	}
	
	
	/**
	 * 将TabularDataSupport对象转换为map
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> managerTabularDataSupport(Object obj) throws Exception {
		if (!(obj instanceof TabularDataSupport)) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		Map<Object, CompositeData> data = (Map<Object, CompositeData>) TabularDataSupport_dataMap.get(obj);
		Set<Entry<Object, CompositeData>> set = data.entrySet();
		Iterator<Entry<Object, CompositeData>> itera = set.iterator();
		while (itera.hasNext()) {
			Entry<Object, CompositeData> item = itera.next();
			Map<String, Object> val = managerCompositeDataSupport(item.getValue());// 得到此属性的值
			result.put(val.get("key").toString(), val.get("value"));
		}
		return result;
	}
	
	/**
	 * @param 将CompositeDataSupport对象转换为map
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> managerCompositeDataSupport(Object obj) throws Exception {
		if (!(obj instanceof CompositeDataSupport)) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = (Map<String, Object>) CompositeDataSupport_contentField.get(obj);// 得到此属性的值
		Set<Entry<String, Object>> set = data.entrySet();
		Iterator<Entry<String, Object>> itera = set.iterator();
		while (itera.hasNext()) {
			Entry<String, Object> item = itera.next();
			if(item.getValue() == null){
				result.put(item.getKey(), null);
			}else if (item.getValue() instanceof TabularDataSupport) {
				Map<String, Object> tmpValue = managerTabularDataSupport(item.getValue());
				result.put(item.getKey(), tmpValue);
			} else if (item.getValue() instanceof CompositeDataSupport) {
				Map<String, Object> tmpValue = managerCompositeDataSupport(item.getValue());
				result.put(item.getKey(), tmpValue);
			} else if (item.getValue().getClass().isArray()){//2018-02-04 21:55,解析dumpThread结果为CompositeDataSupport的数组
				result.put(item.getKey(), getResult(item.getValue()));
			} else {
				result.put(item.getKey(), item.getValue());
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		 Integer[] a = new  Integer[10];
		 if(a.getClass().isArray()){
			Object[] obj = (Object[])a;
		 }
	}
}

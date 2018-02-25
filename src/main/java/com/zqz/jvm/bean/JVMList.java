package com.zqz.jvm.bean;

/**
 * 
 * @author zqz
 *
 */
public class JVMList {
	private String id;
	private String name;
	private boolean isConnected;
	private String jdk;
	private String os;
	private String[] taskNames;
	
	public String getJdk() {
		return jdk;
	}
	public void setJdk(String jdk) {
		this.jdk = jdk;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	public String[] getTaskNames() {
		return taskNames;
	}
	public void setTaskNames(String[] taskNames) {
		this.taskNames = taskNames;
	}
	
	
}

package com.zqz.jvm.bean;


public class JVMList {
	private long id;
	private String name;
	private boolean isConnected;
	private String[] taskNames;
	public long getId() {
		return id;
	}
	public void setId(long id) {
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

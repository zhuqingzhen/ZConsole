package com.zqz.jvm.jmx.bean;

import java.util.Properties;


public class LocalVMInfo {
	private String name;
	private String id;
	private String localConnectorAddress;
	private Properties systemProperties;
	
	public String getLocalConnectorAddress() {
		return localConnectorAddress;
	}
	
	public void setLocalConnectorAddress(String localConnectorAddress) {
		this.localConnectorAddress = localConnectorAddress;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Properties getSystemProperties() {
		return systemProperties;
	}
	
	public void setSystemProperties(Properties systemProperties) {
		this.systemProperties = systemProperties;
	}
	
}

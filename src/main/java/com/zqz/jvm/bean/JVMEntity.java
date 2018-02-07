package com.zqz.jvm.bean;

import java.io.Serializable;

public class JVMEntity  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long  id;
	private int port;
	private String ip;
	private String name;
	
	private String jmxUserName;
	private String jmxPassword;
	
	public String getJmxUserName() {
		return jmxUserName;
	}
	public void setJmxUserName(String jmxUserName) {
		this.jmxUserName = jmxUserName;
	}
	public String getJmxPassword() {
		return jmxPassword;
	}
	public void setJmxPassword(String jmxPassword) {
		this.jmxPassword = jmxPassword;
	}
	public boolean isConnect() {
		return isConnect;
	}
	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}
	/**是否正常连接，true 连接正常，false无法连接**/
	private boolean isConnect;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

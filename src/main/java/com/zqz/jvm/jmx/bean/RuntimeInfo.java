package com.zqz.jvm.jmx.bean;

import java.util.Map;

/**
 * java.lang:type=Runtime
 * @author zqz
 */
public class RuntimeInfo {
	/**启动时间**/
	private long StartTime;
	/**已经运行的时常**/
	private long Uptime;
	/**虚拟机名称 		Java HotSpot(TM) 64-Bit Server VM**/
	private String VmName;
	/**虚拟机版本号 	1.8**/
	private String SpecVersion;
	/**虚拟机小版本号  	25.25-b02**/
	private String VmVersion;
	
	/**命令行输入的参数**/
	private String[] InputArguments;
	
	/**操作系统参数**/
	private Map<String,Object> SystemProperties;
	
	/**系统lib目录**/
	private String LibraryPath;
	/**虚拟机classpath目录**/
	private String BootClassPath;
	/**应用classpath**/
	private String ClassPath;
	
	/**进程id  Name值@前面的部分**/
	private String pid;
	/**hostName Name值@后面的部分**/
	private String hostName;
	
	
	public long getStartTime() {
		return StartTime;
	}
	public void setStartTime(long startTime) {
		StartTime = startTime;
	}
	public String getVmName() {
		return VmName;
	}
	public void setVmName(String vmName) {
		VmName = vmName;
	}
	public String getSpecVersion() {
		return SpecVersion;
	}
	public void setSpecVersion(String specVersion) {
		SpecVersion = specVersion;
	}
	public String getVmVersion() {
		return VmVersion;
	}
	public void setVmVersion(String vmVersion) {
		VmVersion = vmVersion;
	}
	public Map<String, Object> getSystemProperties() {
		return SystemProperties;
	}
	public String[] getInputArguments() {
		return InputArguments;
	}
	public void setInputArguments(String[] inputArguments) {
		InputArguments = inputArguments;
	}
	public void setSystemProperties(Map<String, Object> systemProperties) {
		SystemProperties = systemProperties;
	}
	public String getLibraryPath() {
		return LibraryPath;
	}
	public void setLibraryPath(String libraryPath) {
		LibraryPath = libraryPath;
	}
	public String getBootClassPath() {
		return BootClassPath;
	}
	public void setBootClassPath(String bootClassPath) {
		BootClassPath = bootClassPath;
	}
	public String getClassPath() {
		return ClassPath;
	}
	public void setClassPath(String classPath) {
		ClassPath = classPath;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public long getUptime() {
		return Uptime;
	}
	public void setUptime(long uptime) {
		Uptime = uptime;
	}
	
}

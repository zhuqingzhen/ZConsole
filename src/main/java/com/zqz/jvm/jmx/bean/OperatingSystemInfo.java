package com.zqz.jvm.jmx.bean;

/***
 * java.lang:type=OperatingSystem 
 * @author zqz
 */
public class OperatingSystemInfo {
	/**操作系统名称**/
	private String Name;
	/**操作系统版本**/
	private String Version;
	/**cpu数**/
	private short AvailableProcessors;
	/**物理内存**/
	private long TotalPhysicalMemorySize;
	/**空闲物理内存**/
	private long FreePhysicalMemorySize;
	/**减缓区空间**/
	private long TotalSwapSpaceSize;
	/**空闲交换区**/
	private long FreeSwapSpaceSize;
	
	/**最大文件描述符**/
	private long MaxFileDescriptorCount;
	/**已经使用的文件描述符**/
	private long OpenFileDescriptorCount;
	
	/**系统load值**/
	private String SystemLoadAverage;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public short getAvailableProcessors() {
		return AvailableProcessors;
	}

	public void setAvailableProcessors(short availableProcessors) {
		AvailableProcessors = availableProcessors;
	}

	public long getTotalPhysicalMemorySize() {
		return TotalPhysicalMemorySize;
	}

	public void setTotalPhysicalMemorySize(long totalPhysicalMemorySize) {
		TotalPhysicalMemorySize = totalPhysicalMemorySize;
	}

	public long getFreePhysicalMemorySize() {
		return FreePhysicalMemorySize;
	}

	public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {
		FreePhysicalMemorySize = freePhysicalMemorySize;
	}

	public long getTotalSwapSpaceSize() {
		return TotalSwapSpaceSize;
	}

	public void setTotalSwapSpaceSize(long totalSwapSpaceSize) {
		TotalSwapSpaceSize = totalSwapSpaceSize;
	}

	public long getFreeSwapSpaceSize() {
		return FreeSwapSpaceSize;
	}

	public void setFreeSwapSpaceSize(long freeSwapSpaceSize) {
		FreeSwapSpaceSize = freeSwapSpaceSize;
	}

	public long getMaxFileDescriptorCount() {
		return MaxFileDescriptorCount;
	}

	public void setMaxFileDescriptorCount(long maxFileDescriptorCount) {
		MaxFileDescriptorCount = maxFileDescriptorCount;
	}

	public long getOpenFileDescriptorCount() {
		return OpenFileDescriptorCount;
	}

	public void setOpenFileDescriptorCount(long openFileDescriptorCount) {
		OpenFileDescriptorCount = openFileDescriptorCount;
	}

	public String getSystemLoadAverage() {
		return SystemLoadAverage;
	}

	public void setSystemLoadAverage(String systemLoadAverage) {
		SystemLoadAverage = systemLoadAverage;
	}
	
}

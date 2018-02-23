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
	
	/**系统最后一分钟load值**/
    /**
     * Returns the system load average for the last minute.
     * The system load average is the sum of the number of runnable entities
     * queued to the {@linkplain #getAvailableProcessors available processors}
     * and the number of runnable entities running on the available processors
     * averaged over a period of time.
     * The way in which the load average is calculated is operating system
     * specific but is typically a damped time-dependent average.
     * <p>
     * If the load average is not available, a negative value is returned.
     * <p>
     * This method is designed to provide a hint about the system load
     * and may be queried frequently.
     * The load average may be unavailable on some platform where it is
     * expensive to implement this method.
     *
     * @return the system load average; or a negative value if not available.
     *
     * @since 1.6
     */
	private String SystemLoadAverage;
	
	// What % load the overall system is at, from 0.0-1.0
	/**
     * Returns the "recent cpu usage" for the whole system. This value is a
     * double in the [0.0,1.0] interval. A value of 0.0 means that all CPUs
     * were idle during the recent period of time observed, while a value
     * of 1.0 means that all CPUs were actively running 100% of the time
     * during the recent period being observed. All values betweens 0.0 and
     * 1.0 are possible depending of the activities going on in the system.
     * If the system recent cpu usage is not available, the method returns a
     * negative value.
     *
     * @return the "recent cpu usage" for the whole system; a negative
     * value if not available.
     * @since   1.7
     */
	private String SystemCpuLoad;
	// What % CPU load this current JVM is taking, from 0.0-1.0
	 /**
     * Returns the "recent cpu usage" for the Java Virtual Machine process.
     * This value is a double in the [0.0,1.0] interval. A value of 0.0 means
     * that none of the CPUs were running threads from the JVM process during
     * the recent period of time observed, while a value of 1.0 means that all
     * CPUs were actively running threads from the JVM 100% of the time
     * during the recent period being observed. Threads from the JVM include
     * the application threads as well as the JVM internal threads. All values
     * betweens 0.0 and 1.0 are possible depending of the activities going on
     * in the JVM process and the whole system. If the Java Virtual Machine
     * recent CPU usage is not available, the method returns a negative value.
     *
     * @return the "recent cpu usage" for the Java Virtual Machine process;
     * a negative value if not available.
     * @since   1.7
     */
	private String ProcessCpuLoad;
	
	/**
     * Returns the CPU time used by the process on which the Java
     * virtual machine is running in nanoseconds.  The returned value
     * is of nanoseconds precision but not necessarily nanoseconds
     * accuracy.  This method returns <tt>-1</tt> if the
     * the platform does not support this operation.
     *
     * @return the CPU time used by the process in nanoseconds,
     * or <tt>-1</tt> if this operation is not supported.
     */
	private long ProcessCpuTime;
	  /**
     * Returns the amount of virtual memory that is guaranteed to
     * be available to the running process in bytes,
     * or <tt>-1</tt> if this operation is not supported.
     *
     * @return the amount of virtual memory that is guaranteed to
     * be available to the running process in bytes,
     * or <tt>-1</tt> if this operation is not supported.
     */
	private long CommittedVirtualMemorySize;
	
	
	public String getSystemCpuLoad() {
		return SystemCpuLoad;
	}

	public void setSystemCpuLoad(String systemCpuLoad) {
		SystemCpuLoad = systemCpuLoad;
	}

	public String getProcessCpuLoad() {
		return ProcessCpuLoad;
	}

	public void setProcessCpuLoad(String processCpuLoad) {
		ProcessCpuLoad = processCpuLoad;
	}

	public long getProcessCpuTime() {
		return ProcessCpuTime;
	}

	public void setProcessCpuTime(long processCpuTime) {
		ProcessCpuTime = processCpuTime;
	}

	public long getCommittedVirtualMemorySize() {
		return CommittedVirtualMemorySize;
	}

	public void setCommittedVirtualMemorySize(long committedVirtualMemorySize) {
		CommittedVirtualMemorySize = committedVirtualMemorySize;
	}

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

package com.zqz.jvm.jmx.bean;

/**
 * gc回收信息
 * @author zqz
 * */
public class GCInfo{
	
	//gc线程数
	private int gcThreadCount;
	//gc持续时间
	private long duration;
	//gc次数
	private long count;
	//gc结束时间
	private long endTime;
	//gc总计时间
	private long totalTime;
	//gc前内存信息
	private GCMemoryInfo gcMemoryInfoBefore;
	//gc后内存信息
	private GCMemoryInfo gcMemoryInfoAfter;
	
	public long getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}
	public GCMemoryInfo getGcMemoryInfoBefore() {
		return gcMemoryInfoBefore;
	}
	public void setGcMemoryInfoBefore(GCMemoryInfo gcMemoryInfoBefore) {
		this.gcMemoryInfoBefore = gcMemoryInfoBefore;
	}
	public GCMemoryInfo getGcMemoryInfoAfter() {
		return gcMemoryInfoAfter;
	}
	public void setGcMemoryInfoAfter(GCMemoryInfo gcMemoryInfoAfter) {
		this.gcMemoryInfoAfter = gcMemoryInfoAfter;
	}
	public int getGcThreadCount() {
		return gcThreadCount;
	}
	public void setGcThreadCount(int gcThreadCount) {
		this.gcThreadCount = gcThreadCount;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
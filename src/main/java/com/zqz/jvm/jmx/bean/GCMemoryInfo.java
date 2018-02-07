package com.zqz.jvm.jmx.bean;

import java.lang.management.MemoryUsage;


/**
 * jvm内存信息
 * @author zqz
 */
public class GCMemoryInfo {
	private MemoryUsage heapMemory;
	private MemoryUsage oldGen;
	private MemoryUsage edenSpace;
	private MemoryUsage survivorSpace;
	private MemoryUsage nonHeapMemory;
	private MemoryUsage permgen;
	private MemoryUsage metaspace;
	private MemoryUsage compressedClassSpace;
	private MemoryUsage codecache;
	
	
	public MemoryUsage getHeapMemory() {
		return heapMemory;
	}
	public void setHeapMemory(MemoryUsage heapMemory) {
		this.heapMemory = heapMemory;
	}
	public MemoryUsage getNonHeapMemory() {
		return nonHeapMemory;
	}
	public void setNonHeapMemory(MemoryUsage nonHeapMemory) {
		this.nonHeapMemory = nonHeapMemory;
	}
	public MemoryUsage getPermgen() {
		return permgen;
	}
	public void setPermgen(MemoryUsage permgen) {
		this.permgen = permgen;
	}
	public MemoryUsage getMetaspace() {
		return metaspace;
	}
	public void setMetaspace(MemoryUsage metaspace) {
		this.metaspace = metaspace;
	}
	public MemoryUsage getCodecache() {
		return codecache;
	}
	public void setCodecache(MemoryUsage codecache) {
		this.codecache = codecache;
	}
	public MemoryUsage getCompressedClassSpace() {
		return compressedClassSpace;
	}
	public void setCompressedClassSpace(MemoryUsage compressedClassSpace) {
		this.compressedClassSpace = compressedClassSpace;
	}
	public MemoryUsage getEdenSpace() {
		return edenSpace;
	}
	public void setEdenSpace(MemoryUsage edenSpace) {
		this.edenSpace = edenSpace;
	}
	public MemoryUsage getOldGen() {
		return oldGen;
	}
	public void setOldGen(MemoryUsage oldGen) {
		this.oldGen = oldGen;
	}
	public MemoryUsage getSurvivorSpace() {
		return survivorSpace;
	}
	public void setSurvivorSpace(MemoryUsage survivorSpace) {
		this.survivorSpace = survivorSpace;
	}

}

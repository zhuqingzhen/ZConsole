package com.zqz.jvm.service;

import java.lang.management.MemoryUsage;

import org.springframework.stereotype.Service;

import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;
import com.zqz.jvm.jmx.bean.GCInfo;
import com.zqz.jvm.jmx.bean.JstatGCInfo;

@Service
public class JvmJstatService {
	
	
	private JstatGCInfo getInfo(JstatGCInfo info,GCInfo gc){
		MemoryUsage mu = gc.getGcMemoryInfoAfter().getEdenSpace();
		info.setEC(mu.getCommitted()/1024);
		info.setEU(mu.getUsed()/1024);
		mu = gc.getGcMemoryInfoAfter().getMetaspace();
		if (mu != null) {
			info.setMC(mu.getCommitted()/1024);
			info.setMU(mu.getUsed()/1024);
			mu = gc.getGcMemoryInfoAfter().getCompressedClassSpace();
			info.setCCSC(mu.getCommitted()/1024);
			info.setCCSU(mu.getUsed()/1024);
		}else{
			mu = gc.getGcMemoryInfoAfter().getPermgen();
			info.setPC(mu.getCommitted()/1024);
			info.setPU(mu.getUsed()/1024);
		}
		mu = gc.getGcMemoryInfoAfter().getCodecache();
		info.setCCC(mu.getCommitted()/1024);
		info.setCCU(mu.getUsed()/1024);
		mu = gc.getGcMemoryInfoAfter().getOldGen();
		info.setOC(mu.getCommitted()/1024);
		info.setOU(mu.getUsed()/1024);
		mu = gc.getGcMemoryInfoAfter().getSurvivorSpace();
		info.setSU(mu.getUsed()/1024);
		info.setSC(mu.getCommitted()/1024);
		return info;
	}
}

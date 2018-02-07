package com.zqz.jvm.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zqz.jvm.jmx.JVM;
import com.zqz.jvm.jmx.JVMManager;


@RestController
@RequestMapping(value = "/jstat")
public class JstatController {

	@RequestMapping("/gcutil")
	public void gcutil(Long jvmId, int count, int interval) {
		JVM jvm = JVMManager.get(jvmId);
		if (jvm == null) {
		}
	}
}

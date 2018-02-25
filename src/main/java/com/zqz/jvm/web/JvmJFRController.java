package com.zqz.jvm.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zqz.jvm.bean.ReponseMessage;
import com.zqz.jvm.service.JvmJFRService;

/**
 * 201802252222
 * @author zhuqz
 *
 */
@RestController
@RequestMapping(value = "/jmx/jfr")
public class JvmJFRController {
	
	private static Logger logger = LoggerFactory.getLogger(JvmJFRController.class);
	

	@Autowired
	private JvmJFRService jvmJFRService;
	
	
	
	/**
	 * 检查jfr是否启用
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/check")
	public ReponseMessage checkJFR(long jvmId) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJFRService.checkJFR(jvmId));
	}
	
	/**
	 * 启用jfr
	 * @param jvmId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/enable")
	public ReponseMessage enableJFR(long jvmId) throws Exception{
		return new ReponseMessage(ReponseMessage.SUCCESS, jvmJFRService.enableJFR(jvmId));
	}
}

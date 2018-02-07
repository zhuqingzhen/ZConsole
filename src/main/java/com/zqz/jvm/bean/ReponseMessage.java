package com.zqz.jvm.bean;

/**
 * 返回的数据
 * @author zqz
 */
public class ReponseMessage {
	
	public transient final static int SUCCESS = 1;
	public transient final static int ERROR = 2;
	
	//代码
	private int code;
	//数据
	private Object data;
	
	public ReponseMessage(int code,Object data){
		this.code = code;
		this.data = data;
	}
	
	public void setSuccess(Object data){
		code = ReponseMessage.SUCCESS;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}

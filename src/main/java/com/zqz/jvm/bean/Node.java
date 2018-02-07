package com.zqz.jvm.bean;

/**
 * jmx树形目录节点 
 * @author zqz
 */
public class Node {
	private String name;
	private Node[] children=null;
	private String icon;
	private String objectName;
	/**0:包名节点；1.mbean节点；2.属性节点；3.方法节点；4.通知节点,5 .属性子节点；6.方法子节点 ；7.通知子节点**/
	private short type=0;
	
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Node[] getChildren() {
		return children;
	}
	public void setChildren(Node[] children) {
		this.children = children;
	}
}

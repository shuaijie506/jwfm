package com.dx.jwfm.framework.core.model;

/**
 * 业务按钮权限结构
 * @author Administrator
 *
 */
public class ButtonAuth {

	/**按钮名称*/
	private String name;
	/**功能简称*/
	private String code;
	/**页面中的调用JS方法名*/
	private String funName;
	/**按钮在页面中的ID*/
	private String btnId;
	/**按钮的图标样式*/
	private String iconCls;
	/**按钮或权限的说明信息*/
	private String note;
	
	public ButtonAuth() {
		super();
	}
	public ButtonAuth(String name, String code, String funName, String btnId, String iconCls, String note) {
		super();
		this.name = name;
		this.code = code;
		this.funName = funName;
		this.btnId = btnId;
		this.iconCls = iconCls;
		this.note = note;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFunName() {
		return funName;
	}
	public void setFunName(String funName) {
		this.funName = funName;
	}
	public String getBtnId() {
		return btnId;
	}
	public void setBtnId(String btnId) {
		this.btnId = btnId;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}

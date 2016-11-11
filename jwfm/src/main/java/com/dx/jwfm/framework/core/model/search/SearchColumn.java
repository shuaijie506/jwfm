package com.dx.jwfm.framework.core.model.search;

public class SearchColumn {

	/**查询条件标题*/
	private String vcTitle;
	/**查询条件编码，一般用英文字母、数字或下划线组成*/
	private String vcCode;
	/**输入模式 文本text、日期date、字典项dict:字典名称、SQL项目sqlDict:SQL语句*/
	private String vcEditorType;
//	/**输入框HTML内容*/
//	private String vcInputHtml;
	/**输入框在页面加载后执行的JS代码*/
	private String vcEditorJs;
	/**输入控件的显示宽度，单位为像素，正常网页每个汉字宽度为12px*/
	private int width;
	/**查询条件的默认值，由用户指定的默认值解析器解析，找不到解析器的按字符串常量解析*/
	private String defaults;
	/**查询过滤类型，相等，模糊匹配，日期>=，日期<=，自定义*/
	private String sqlSearchType;
	/**查询SQL语句片断，可由用户自行修改*/
	private String sqlFragment;
	
	public SearchColumn() {
		super();
	}
	public SearchColumn(String vcTitle, String vcCode, String vcEditorType, int width, String defaults, String sqlSearchType,
			String sqlFragment) {
		super();
		this.vcTitle = vcTitle;
		this.vcCode = vcCode;
		this.vcEditorType = vcEditorType;
//		this.vcInputHtml = vcInputHtml;
//		this.vcInputJs = vcInputJs;
		this.width = width;
		this.defaults = defaults;
		this.sqlSearchType = sqlSearchType;
		this.sqlFragment = sqlFragment;
	}
	public String getVcTitle() {
		return vcTitle;
	}
	public void setVcTitle(String vcTitle) {
		this.vcTitle = vcTitle;
	}
	public String getVcCode() {
		return vcCode;
	}
	public void setVcCode(String vcCode) {
		this.vcCode = vcCode;
	}
	
//	public String getVcInputHtml() {
//		return vcInputHtml;
//	}
//	public void setVcInputHtml(String vcInputHtml) {
//		this.vcInputHtml = vcInputHtml;
//	}
	
	public int getWidth() {
		return width;
	}
	public String getVcEditorType() {
		return vcEditorType;
	}
	public void setVcEditorType(String vcEditorType) {
		this.vcEditorType = vcEditorType;
	}
	public String getVcEditorJs() {
		return vcEditorJs;
	}
	public void setVcEditorJs(String vcEditorJs) {
		this.vcEditorJs = vcEditorJs;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getDefaults() {
		return defaults;
	}
	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}
	public String getSqlSearchType() {
		return sqlSearchType;
	}
	public void setSqlSearchType(String sqlSearchType) {
		this.sqlSearchType = sqlSearchType;
	}
	public String getSqlFragment() {
		return sqlFragment;
	}
	public void setSqlFragment(String sqlFragment) {
		this.sqlFragment = sqlFragment;
	}
	
}

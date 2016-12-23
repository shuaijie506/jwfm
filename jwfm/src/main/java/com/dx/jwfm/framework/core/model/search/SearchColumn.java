package com.dx.jwfm.framework.core.model.search;

import java.util.List;
import java.util.Map;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.web.logic.SQLConditionParser;

public class SearchColumn {

	/**查询条件标题*/
	private String vcTitle;
	/**查询条件编码，一般用英文字母、数字或下划线组成*/
	private String vcCode;
	/** 编辑时控件类型，默认为输入框，公共字典非空时默认为单选框  text,textarea,select,date,combotree,html
	 * select支持 select:sql:SQL语句和select:dict:字典项名称 两种格式，可以自动选取sql查询结果中的值和公共字典中的值为下拉选项
	 * date支持date:yyyy-MM-dd HH:mm格式，可以按指定格式展示
	 * combobox支持combobox:{url:'treeurl',....}格式，冒号后指定combotree的options参数
	 * combotree支持combotree:{url:'treeurl',....}格式，冒号后指定combotree的options参数
	 * html支持html:自定义HTML代码，支持使用<script>标签调用JS */
	private String vcEditorType;
//	/**输入框HTML内容*/
//	private String vcInputHtml;
	/**输入框在页面加载后执行的JS代码*/
	private String vcEditorJs;
	/**输入控件的显示宽度，单位为像素，正常网页每个汉字宽度为12px*/
//	private int width;
	/**查询条件的默认值，由用户指定的默认值解析器解析，找不到解析器的按字符串常量解析*/
	private String defaults;
	/**查询过滤类型，= 相等，like 模糊匹配，date>= 日期>=，date<=日期<=，dateRange日期范围，userdefine自定义*/
	private String sqlSearchType;
	/**查询SQL语句片断，可由用户自行修改*/
	private String sqlFragment;
	
	public SearchColumn() {
		super();
	}
	public SearchColumn(String vcTitle, String vcCode, String vcEditorType, String defaults, String sqlSearchType,
			String sqlFragment) {
		super();
		this.vcTitle = vcTitle;
		this.vcCode = vcCode;
		this.vcEditorType = vcEditorType;
//		this.vcInputHtml = vcInputHtml;
//		this.vcInputJs = vcInputJs;
//		this.width = width;
		this.defaults = defaults;
		this.sqlSearchType = sqlSearchType;
		this.sqlFragment = sqlFragment;
	}
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月25日 上午9:11:28
	 * 功能描述: 返回参与SQL查询的条件语句
	 * 方法的参数和返回值: 
	 * @param paramMap 
	 * @return
	 */
	public String getSqlFragmentFinal(Map<String, Object> paramMap) {
		if(sqlSearchType==null){
			return sqlFragment;
		}
		List<SQLConditionParser> list = SystemContext.getSQLConditionParserList();
		for(SQLConditionParser scp:list){
			String res = scp.createSqlFragment(this, paramMap);
			if(res!=null){
				return res;
			}
		}
		return sqlFragment;
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
	
//	public int getWidth() {
//		return width;
//	}
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
//	public void setWidth(int width) {
//		this.width = width;
//	}
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

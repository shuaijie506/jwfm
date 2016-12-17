package com.dx.jwfm.framework.core.dao.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.dx.jwfm.framework.util.FastUtil;

public class FastColumn {

	/** 名称，一般为汉字 */
	protected String title;

	/** 编码，一般为大写英文字母、数字和下划线组成 */
	protected String name;

	/** 备注说明，此列为空时备注取name中的值 */
	protected String comment;

	/** 类型，字符串、数字、日期等 */
	protected String type;

	/** 类型长度，type为字符串时此值有效 */
	protected int typeLen;

	/** 默认值，非数据库表的默认值，是程序在添加时的默认值，支持用户扩展，
	 * 可以通过实现IDefaultValueParser接口并通过调用FastContext.addDefaultInterface增加接口配置 */
	protected String defaults;

	/** 如果取值范围为公共字典，在此列填写公共字典的名称 */
	protected String dictName;

	/** 值是否可以为null */
	protected boolean canNull;

	/** 是否为主键 */
	protected boolean primaryKey;
	/** 默认显示格式 */
	protected String format;
	/** 编辑时控件类型，默认为输入框，公共字典非空时默认为单选框  text,textarea,select,date,combotree,html
	 * select支持 select:sql:SQL语句和select:dict:字典项名称 两种格式，可以自动选取sql查询结果中的值和公共字典中的值为下拉选项
	 * date支持date:yyyy-MM-dd HH:mm格式，可以按指定格式展示
	 * combotree支持combotree:{url:'treeurl',....}格式，冒号后指定combotree的options参数
	 * html支持html:自定义HTML代码，支持使用<script>标签调用JS */
	protected String editorType;

	/** 是否做为查询条件 */
	protected boolean searchCondition;

	/** 是否做为查询结果列 */
	protected boolean serachResultCol;

	public FastColumn() {
		super();
	}

	public FastColumn(String title, String name, String comment, String type, int typeLen, String defaults, String dictName, boolean canNull, boolean primaryKey) {
		super();
		this.title = title;
		this.name = name;
		this.comment = comment;
		this.type = type;
		this.typeLen = typeLen;
		this.defaults = defaults;
		this.dictName = dictName;
		this.canNull = canNull;
		this.primaryKey = primaryKey;
	}

	public Object getRsObject(ResultSet rs,int columnIndex) throws SQLException{
		switch(type){
		case FastColumnType.String:
			return rs.getString(columnIndex);
		case FastColumnType.Integer:
			return rs.getInt(columnIndex);
		case FastColumnType.Long:
			return rs.getLong(columnIndex);
		case FastColumnType.Float:
			return rs.getFloat(columnIndex);
		case FastColumnType.Double:
			return rs.getDouble(columnIndex);
		case FastColumnType.Date:
			return rs.getTimestamp(columnIndex);
		}
		return rs.getObject(columnIndex);
	}

	public Object getRsObject(ResultSet rs,String columnName) throws SQLException{
		switch(type){
		case FastColumnType.String:
			return rs.getString(columnName);
		case FastColumnType.Integer:
			return rs.getInt(columnName);
		case FastColumnType.Long:
			return rs.getLong(columnName);
		case FastColumnType.Float:
			return rs.getFloat(columnName);
		case FastColumnType.Double:
			return rs.getDouble(columnName);
		case FastColumnType.Date:
			return rs.getTimestamp(columnName);
		}
		return rs.getObject(columnName);
	}

	public String getTitle() {
		return FastUtil.nvl(title,comment);
	}

	public void setTitle(String title) {
		this.title = title==null?null:title.toUpperCase();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name==null?null:name.toUpperCase();
	}

	public String getComment() {
		return FastUtil.nvl(comment,title);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTypeLen() {
		return typeLen;
	}

	public void setTypeLen(int typeLen) {
		this.typeLen = typeLen;
	}

	public String getDefaults() {
		return defaults;
	}

	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}

	public boolean isCanNull() {
		return canNull;
	}

	public void setCanNull(boolean canNull) {
		this.canNull = canNull;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getEditorType() {
		if(editorType==null){
			if(FastColumnType.Date.equals(type)){
				editorType = "date:yyyy-MM-dd HH:mm";
			}
			else if(FastColumnType.String.equals(type) && (typeLen<=0 || typeLen>=500)){
				editorType = "textarea";
			}
		}
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isSearchCondition() {
		return searchCondition;
	}

	public void setSearchCondition(boolean searchCondition) {
		this.searchCondition = searchCondition;
	}

	public boolean isSerachResultCol() {
		return serachResultCol;
	}

	public void setSerachResultCol(boolean serachResultCol) {
		this.serachResultCol = serachResultCol;
	}
	
}

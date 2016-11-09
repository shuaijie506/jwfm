package com.dx.jwfm.framework.core.dao.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FastColumn {

	/** 名称，一般为汉字 */
	protected String name;

	/** 编码，一般为大写英文字母、数字和下划线组成 */
	protected String code;

	/** 备注说明，此列为空时备注取name中的值 */
	protected String comment;

	/** 类型，字符串、数字、日期等 */
	protected FastColumnType type;

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
	protected boolean isPrimaryKey;
	/** 默认显示格式 */
	protected String format;
	/** 编辑时控件类型，默认为输入框，公共字典非空时默认为单选框  text,textarea,select,multiselect,date,combotree,html
	 * select和multiselect支持 select:sql格式，可以自动选取sql中的值为下拉选项
	 * date支持date:yyyy-MM-dd HH:mm格式，可以按指定格式展示
	 * combotree支持combotree:{url:'treeurl',....}格式，冒号后指定combotree的options参数
	 * html支持html:自定义HTML代码，支持使用<script>标签调用JS */
	protected String editorType;
	
	public FastColumn() {
		super();
	}

	public FastColumn(String name, String code, String comment, FastColumnType type, int typeLen, String defaults, String dictName, boolean canNull, boolean isPrimaryKey) {
		super();
		this.name = name;
		this.code = code;
		this.comment = comment;
		this.type = type;
		this.typeLen = typeLen;
		this.defaults = defaults;
		this.dictName = dictName;
		this.canNull = canNull;
		this.isPrimaryKey = isPrimaryKey;
	}

	public Object getRsObject(ResultSet rs,int columnIndex) throws SQLException{
		switch(type){
		case String:
			return rs.getString(columnIndex);
		case Integer:
			return rs.getInt(columnIndex);
		case Long:
			return rs.getLong(columnIndex);
		case Float:
			return rs.getFloat(columnIndex);
		case Double:
			return rs.getDouble(columnIndex);
		case Date:
			return rs.getTimestamp(columnIndex);
		}
		return rs.getObject(columnIndex);
	}

	public Object getRsObject(ResultSet rs,String columnName) throws SQLException{
		switch(type){
		case String:
			return rs.getString(columnName);
		case Integer:
			return rs.getInt(columnName);
		case Long:
			return rs.getLong(columnName);
		case Float:
			return rs.getFloat(columnName);
		case Double:
			return rs.getDouble(columnName);
		case Date:
			return rs.getTimestamp(columnName);
		}
		return rs.getObject(columnName);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name==null?null:name.toUpperCase();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code==null?null:code.toUpperCase();
	}

	public String getComment() {
		return comment==null?name:comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public FastColumnType getType() {
		return type;
	}

	public void setType(FastColumnType type) {
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
		return true;
	}

	public void setCanNull(boolean canNull) {
		this.canNull = canNull;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}
	
	

}

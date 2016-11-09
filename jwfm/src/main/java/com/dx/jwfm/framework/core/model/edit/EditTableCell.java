package com.dx.jwfm.framework.core.model.edit;

@Deprecated
public class EditTableCell {

	/**单元格位置*/
	private int rowIdx,colIdx;
	/**单元格为合并单元格时的跨行数和跨列数*/
	private int rowSpan,colSpan;
	/**单元格类型  文本、文本框、下拉选择框、日期选择框、下拉树*/
	private String cellType;
	/**单元格提交值到的字段名称，可使用po.VC_ID或，items[0].vcId
	 * 此值在页面提交时要做为参数名称*/
	private String code;
	/**单元格显示的文本，支持${}表达式*/
	private String text;
	/**单元格内部存储值，支持${}表达式*/
	private String value;
	/**单元格允许输入内容的最大长度*/
	private int maxLength;
	/**单元格是否必填*/
	private boolean notNull;
	/**该单元格的默认值，由用户指定的默认值解析器解析，找不到解析器的按字符串常量解析*/
	private String defaults;

	/**单元格的附加css样式，不含<style></style>标签部分*/
	private String customCss;
	/**单元格的附加js代码，不含<script></script>标签部分
	 * window.isAdd==true时表示为添加页面，否则为修改页面*/
	private String customJs;
	
	public int getRowSpan() {
		return rowSpan<=0?1:rowSpan;
	}
	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}
	public int getColSpan() {
		return colSpan<=0?1:colSpan;
	}
	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}
	public String getCellType() {
		return cellType;
	}
	public void setCellType(String cellType) {
		this.cellType = cellType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public boolean isNotNull() {
		return notNull;
	}
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
	public String getDefaults() {
		return defaults;
	}
	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}
	public String getCustomCss() {
		return customCss;
	}
	public void setCustomCss(String customCss) {
		this.customCss = customCss;
	}
	public String getCustomJs() {
		return customJs;
	}
	public void setCustomJs(String customJs) {
		this.customJs = customJs;
	}
	public int getRowIdx() {
		return rowIdx;
	}
	public void setRowIdx(int rowIdx) {
		this.rowIdx = rowIdx;
	}
	public int getColIdx() {
		return colIdx;
	}
	public void setColIdx(int colIdx) {
		this.colIdx = colIdx;
	}
	
}

package com.dx.jwfm.framework.core.model.search;

public class SearchResultColumn {

	/**表格标题*/
	private String vcTitle;
	/**字段编码*/
	private String vcCode;
	/**数据格式，文本text、日期date、整数long、浮点数double、字典项dict:字典名称、SQL项目sqlDict:SQL语句*/
//	private String vcType;
	/**输入控件的显示宽度，单位为像素，正常网页每个汉字宽度为12px*/
	private int width;
	/**显示格式，对日期型字段和数字型字段有用*/
	private String vcFormat;
	/**是否能够按此字段排序 为空时不排序 desc默认倒序 asc 默认正序*/
	private String canSort;
	/**表头样式和css*/
	private String vcThStyle,vcThCss;
	/**表格内容样式和css*/
	private String vcTdStyle,vcTdCss;
	/**是否冻结此列*/
	private boolean frozen;
	/**显示时的对齐方式，默认为居中 */
	private String align;
	/**是否隐藏此列*/
	private boolean hidden;
	public SearchResultColumn() {
	}
	public SearchResultColumn(String vcTitle, String vcCode, int width, String vcFormat, String canSort) {
		this.vcTitle = vcTitle;
		this.vcCode = vcCode;
//		this.vcType = vcType;
		this.width = width;
		this.vcFormat = vcFormat;
		this.canSort = canSort;
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
//	public String getVcType() {
//		return vcType;
//	}
//	public void setVcType(String vcType) {
//		this.vcType = vcType;
//	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getVcFormat() {
		return vcFormat;
	}
	public void setVcFormat(String vcFormat) {
		this.vcFormat = vcFormat;
	}
	public String getCanSort() {
		return canSort;
	}
	public void setCanSort(String canSort) {
		this.canSort = canSort;
	}
	public String getVcThStyle() {
		return vcThStyle;
	}
	public void setVcThStyle(String vcThStyle) {
		this.vcThStyle = vcThStyle;
	}
	public String getVcThCss() {
		return vcThCss;
	}
	public void setVcThCss(String vcThCss) {
		this.vcThCss = vcThCss;
	}
	public String getVcTdStyle() {
		return vcTdStyle;
	}
	public void setVcTdStyle(String vcTdStyle) {
		this.vcTdStyle = vcTdStyle;
	}
	public String getVcTdCss() {
		return vcTdCss;
	}
	public void setVcTdCss(String vcTdCss) {
		this.vcTdCss = vcTdCss;
	}
	public boolean isFrozen() {
		return frozen;
	}
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
	public String getAlign() {
		return align==null?"center":align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
}

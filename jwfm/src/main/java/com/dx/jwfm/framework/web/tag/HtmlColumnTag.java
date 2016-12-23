package com.dx.jwfm.framework.web.tag;

import javax.servlet.jsp.JspException;

import com.dx.jwfm.framework.util.FastUtil;

public class HtmlColumnTag extends BaseViewTag{
	private static final long serialVersionUID = 1L;
	
	private String width,text,field,format,sort;
	private boolean disabled;

	public int doStartTag() throws JspException {
		HtmlTableTag t = (HtmlTableTag) this.getParent();
		if(!disabled){
			t.cols.add(t.new Column(width, text, field, format,sort, getHtmlAttrString(), disabled));
		}
		t.fieldMap.put(FastUtil.isBlank(sort)?field.toUpperCase():sort.toUpperCase(), text);
		return SKIP_BODY;
	}

	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
}

package com.dx.jwfm.framework.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class PropertyTag extends BaseViewTag {

	/**  */
	private static final long serialVersionUID = 1L;
	/** HTML中的name属性，同时做为在bean中取值的属性名 */
	protected String name;
	/** 如果所得值为数字和日期时，所显示的格式化串 */
	protected String format;

	@Override
	public int doEndTag() throws JspException {
	    JspWriter out = pageContext.getOut();
	    try{
			Object val = getBeanValue(name);
			String value = getFormatValue(val,format);
			if(value!=null && "html".equals(format)){
				value = value.replaceAll("\\r*\\n\\r*", "<br/>");
			}
			if(value!=null){
				out.print(value);
			}
		} catch (IOException ex) {
			logger.error(ex);
		} catch (Exception e) {
			throw new JspException(e);
		}
	    name = null;format=null;
		return SKIP_BODY;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
}

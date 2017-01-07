package com.dx.jwfm.framework.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.util.FastUtil;

public class HiddenTag extends BaseViewTag {
	/**  */
	private static final long serialVersionUID = 1L;
	/** HTML中的name属性，同时做为在bean中取值的属性名 */
	protected String name;
	/** 如果所得值为数字和日期时，所显示的格式化串 */
	protected String format;
	/** 指定的value值 */
	protected String value;

	@Override
	public int doEndTag() throws JspException {
	    JspWriter out = pageContext.getOut();
	    try{
	    	out.print("<input type=hidden name=\"");
	    	out.print(name);
	    	out.print("\"");
			for(String k:attr.keySet()){
		    	out.print(" ");
		    	out.print(k);
				if(attr.get(k)!=null){
			    	out.print("=\"");
			    	out.print(attr.get(k).replace("\"", "\\\"").replaceAll("\\r|\\n", ""));
			    	out.print("\"");
				}
			}
			out.print(" value=\"");
	    	out.print(getValue().replaceAll("\"", "&quot;"));
	    	out.print("\"");
			out.println(" />");
		} catch (IOException ex) {
			logger.error(ex);
		} catch (Exception e) {
			throw new JspException(e);
		}
	    name = null;format=null;value=null;
		return SKIP_BODY;
	}
	
	public String getValue() {
		if(value == null){
			Object val = RequestContext.getBeanValue(name);
			value = FastUtil.format(val,format);
		}
		return value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}

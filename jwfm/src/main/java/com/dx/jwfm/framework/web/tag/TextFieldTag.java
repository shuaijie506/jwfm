package com.dx.jwfm.framework.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class TextFieldTag extends HiddenTag {
	/**  */
	private static final long serialVersionUID = 1L;
	/** 是否为多行文本 */
	protected boolean multiLine;

	@Override
	public int doEndTag() throws JspException {
	    JspWriter out = pageContext.getOut();
	    try{
	    	if(multiLine){
		    	out.print("<textarea name=\"");
	    	}
	    	else{
	    		out.print("<input type=text name=\"");
	    	}
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
	    	if(multiLine){
				out.print(" >");
		    	out.print(getValue());
		    	out.print("</textarea>");
	    	}
	    	else{
				out.print(" value=\"");
		    	out.print(getValue().replaceAll("\"", "\\\""));
		    	out.print("\"");
				out.println(" />");
	    	}
		} catch (IOException ex) {
			logger.error(ex);
		} catch (Exception e) {
			throw new JspException(e);
		}
	    name = null;format=null;value=null;
		return SKIP_BODY;
	}
	
	public boolean isMultiLine() {
		return multiLine;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

}

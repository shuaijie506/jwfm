package com.dx.jwfm.framework.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.util.FastUtil;

/**
 * @author 宋帅杰
 * 输出指定的HTML内容，支持使用${}取bean中的值
 */
public class WriteHtmlTag extends BaseViewTag {
	/**  */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(WriteHtmlTag.class);
	
	/**
	 * 要输出的bean名称和属性链，如需支持使用${}取bean中的值并再次取值时，请将内容写入标签body中
	 */
	private String value;
	
	@Override
	public int doEndTag() throws JspException {
		BodyContent bc = getBodyContent();
		if(FastUtil.isBlank(value)){
			if(bc!=null && bc.getString()!=null){
				value = bc.getString();
			}
		}
		else{
			value = "${"+value+"}";
		}
	    JspWriter out = pageContext.getOut();
	    try{
	    	if(FastUtil.isNotBlank(value)){
		    	out.print(replaceVars(value));
	    	}
		} catch (IOException ex) {
			logger.error(ex);
		}
	    value = null;
		return super.doEndTag();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
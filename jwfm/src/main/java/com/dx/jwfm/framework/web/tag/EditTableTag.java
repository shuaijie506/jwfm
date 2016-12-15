package com.dx.jwfm.framework.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.web.view.Node;

/**
 * @author 宋帅杰
 * 编辑页面标签
 */
public class EditTableTag extends BaseViewTag {
	/**  */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(EditTableTag.class);
	
	/**
	 * HTML页面的id值，如edit,view等
	 */
	private String type;
	
	@Override
	public int doEndTag() throws JspException {
	    FastModel model = RequestContext.getFastModel();
	    if(model==null){
			return super.doEndTag();
	    }
	    JspWriter out = pageContext.getOut();
	    try{
	    	long curTimeL = System.currentTimeMillis();
	    	Node n = model.getModelStructure().getPageHTMLNode(type);
	    	String html = null;
	    	if(n==null){
	    		html = "您尚未配置ID为["+type+"]的页面内容！";
	    	}
	    	else{
	    		html = n.getData();
	    	}
	    	if(html==null){
	    		html = "";
	    	}
    		out.println("<div id='editDiv"+curTimeL+"'>");
	    	out.println(replaceVars(html));
    		out.println("</div>");
		} catch (IOException ex) {
			logger.error(ex);
		}
		return super.doEndTag();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
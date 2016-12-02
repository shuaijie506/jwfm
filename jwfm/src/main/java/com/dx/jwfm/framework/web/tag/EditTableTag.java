package com.dx.jwfm.framework.web.tag;

import java.io.IOException;
import java.io.StringWriter;

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
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月4日 上午8:31:55
	 * 功能描述: 将内容中的${vars.propts:format}进行值替换，值会从page action request session servletcontext中依次取值
	 * 方法的参数和返回值: 
	 * @param str
	 * @return
	 */
	private String replaceVars(String str) {
		if(str==null || str.length()==0){
			return "";
		}
		StringBuffer buff = new StringBuffer();
		int pos = str.indexOf("${");
		int lastpos = 0;
		if(pos>=0){
			while(pos>=0){
				buff.append(str.substring(lastpos, pos));
				int nextpos = str.indexOf("}",pos);
				String key = str.substring(pos+2, nextpos);
				Object val = getFormatValue(getVarValue(key),null);
				if(val!=null){
					buff.append(val);
				}
				lastpos = nextpos+1;
				pos = str.indexOf("${",lastpos);
			}
			if(lastpos<str.length()){
				buff.append(str.substring(lastpos));
			}
		}
		else{
			return str;
		}
		return buff.toString();
	}

	private Object getVarValue(String key) {
		//处理${$select$fieldName:sql:SQL语句}
		if(key.startsWith("$select$")){
			int pos = key.indexOf(":",8);
			if(pos<0){
				return "下拉选择框格式错误，请使用${$select$fieldName:sql:SQL语句}或${$select$fieldName:dict:字典名称}定义下拉选择框";
			}
			String fieldName = key.substring(8,pos);
			Object val = getBeanValue(fieldName);
			SelectTag sel = new SelectTag();
			sel.setName(fieldName);
			sel.setValue(val==null?"":val.toString());
			sel.setEmptyOption(true);
			sel.setList(key.substring(pos+1));
			sel.setId(fieldName.replaceAll("\\.", "_"));
			StringWriter sw = new StringWriter();
			sel.writeHtml(sw);
			return sw.toString();
		}
		return getBeanValue(key);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
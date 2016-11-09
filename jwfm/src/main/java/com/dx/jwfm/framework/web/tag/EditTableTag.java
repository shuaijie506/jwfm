package com.dx.jwfm.framework.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.model.FastModel;

/**
 * @author 宋帅杰
 * 编辑页面标签
 */
public class EditTableTag extends BaseViewTag {
	/**  */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(EditTableTag.class);
	
	/**
	 * 菜单地址，非必要属性。用于在页面中获取某个菜单项的功能按钮权限
	 * 使用举例（如字典表的权限操作，字典子表的操作权限需要与字典主表的操作权限一致，因此需要传入字典主表的菜单地址，以获取和主表相同的操作功能）
	 */
	private String menuUrl;
	
	@Override
	public int doEndTag() throws JspException {
	    FastModel model = RequestContext.getFastModel();
	    if(model==null){
			return super.doEndTag();
	    }
	    JspWriter out = pageContext.getOut();
	    try{
	    	long curTimeL = System.currentTimeMillis();
	    	String html = model.getModelStructure().getEditTable();
	    	if(html==null){
	    		html = "";
	    	}
    		out.println("<div id='editDiv"+curTimeL+"'>");
	    	out.println(replaceVars(html));
    		out.println("</div>");
    		out.println("<script>");
    		out.println("$('#editDiv"+curTimeL+" select').each(function(){$(this).val($(this).attr('val'));});");
    		out.println("</script>");
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
				Object val = getFormatValue(getBeanValue(key),null);
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

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}
	
}
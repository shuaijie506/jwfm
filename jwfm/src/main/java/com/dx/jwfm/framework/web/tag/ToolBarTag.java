package com.dx.jwfm.framework.web.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.model.ButtonAuth;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.web.builder.IDatagridBuilder;
import com.dx.jwfm.framework.web.exception.DatagridBuilderNotFound;


/**
 * @author 宋帅杰
 * 按钮工具条标签
 */
public class ToolBarTag extends TagSupport {
	/**  */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(ToolBarTag.class);
	
	/**
	 * 菜单地址，非必要属性。用于在页面中获取某个菜单项的功能按钮权限
	 * 使用举例（如字典表的权限操作，字典子表的操作权限需要与字典主表的操作权限一致，因此需要传入字典主表的菜单地址，以获取和主表相同的操作功能）
	 */
	private String menuUrl;
	/**
	 * 显示模式，可选值为easyui,html
	 */
	private String type;
	
	@Override
	public int doEndTag() throws JspException {
	    FastModel model = RequestContext.getFastModel();
	    JspWriter out = pageContext.getOut();
	    if(model==null){
			return super.doEndTag();
	    }
	    try{
	    	List<ButtonAuth> btns = model.getButtonAuths();
	    	if(btns!=null){
	    		if("html".equals(type)){
		    		StringBuffer buff = new StringBuffer();
			    	for(ButtonAuth btn:btns){
			    		buff.append("\n<a href='javascript:void(0)' id='").append(btn.getBtnId()).append("' onclick='")
			    		.append(btn.getFunName()).append("()'><span><font class='").append(btn.getIconCls()).append("'>")
			    		.append(btn.getName()).append("</font></span></a>");
			    	}
			    	if(buff.length()>0){
			    		out.print(buff.substring(1));
			    	}
	    		}
	    		else{
	    			try {
						IDatagridBuilder builder = SystemContext.getDatagridBuilder(type);
						if(builder!=null){
							out.print(builder.buildToolbar(btns));
						}
						else{
							throw new DatagridBuilderNotFound("The type ["+type+"] is not found! please set [datagridBuilder."+type+
									"] in fast.properties,the value should be implements IDatagridBuilder");
						}
					} catch (Exception e) {
						throw new DatagridBuilderNotFound("The datagridBuilder."+type+
								"'s value in fast.properties can't be load as Class!",e);
					}
	    		}
	    	}
		} catch (IOException ex) {
			logger.error(ex);
		} catch (DatagridBuilderNotFound e) {
			throw new JspException(e);
		}
		return super.doEndTag();
	}
	
	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}

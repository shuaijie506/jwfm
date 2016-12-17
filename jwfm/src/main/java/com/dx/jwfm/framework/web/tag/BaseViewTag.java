package com.dx.jwfm.framework.web.tag;

import java.io.StringWriter;
import java.util.LinkedHashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.util.FastUtil;

public class BaseViewTag extends BodyTagSupport implements DynamicAttributes {

	/**  */
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(this.getClass());

	protected LinkedHashMap<String,String> attr = new LinkedHashMap<String, String>();

	/**
	 * ${}中的变量取值范围，默认为page、action、request、session、servletContext、macros依次取值
	 * 指定时仅从指定的范围中取
	 */
	private String scope;
	
	@Override
	public void setPageContext(PageContext pageContext) {
		if(pageContext!=null){
			pageContext.getRequest().setAttribute("PageContext", pageContext);
		}
		super.setPageContext(pageContext);
	}

	public void setDynamicAttribute(String uri,String localeName,Object value) throws JspException {
		attr.put(localeName, ""+value);
	}

	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月4日 上午8:31:55
	 * 功能描述: 将内容中的${vars.propts:format}进行值替换，值会从page action request session servletcontext中依次取值
	 * 方法的参数和返回值: 
	 * @param str
	 * @return
	 */
	protected String replaceVars(String str) {
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
				if(nextpos<0){
					pos+=2;
					continue;
				}
				String key = str.substring(pos+2, nextpos).trim();
				Object val = FastUtil.format(getVarValue(key),null);
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
			Object val = RequestContext.getBeanValue(fieldName,scope);
			SelectTag sel = new SelectTag();
			sel.setName(fieldName);
			sel.setValue(val==null?"":val.toString());
			sel.setEmptyOption(true);
			sel.setList(key.substring(pos+1).replaceAll("&#125;", "}"));
			sel.setId(fieldName.replaceAll("\\.", "_"));
			StringWriter sw = new StringWriter();
			sel.writeHtml(sw);
			return sw.toString();
		}
		return RequestContext.getBeanValue(key,scope);
	}
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}

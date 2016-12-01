package com.dx.jwfm.framework.web.tag;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.util.FastUtil;

public class BaseViewTag extends TagSupport implements DynamicAttributes {

	/**  */
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(this.getClass());

	protected LinkedHashMap<String,String> attr = new LinkedHashMap<String, String>();

	public void setDynamicAttribute(String uri,String localeName,Object value) throws JspException {
		attr.put(localeName, ""+value);
	}

	protected String getFormatValue(Object val,String format) {
		if(val==null || "".equals(val)){
			return "";
		}
		else if(val instanceof Date){
			Date d = (Date) val;
			if(FastUtil.isBlank(format)){
				format = "yyyy-MM-dd HH:mm";
			}
			return new SimpleDateFormat(format).format(d);
		}
		else if(val instanceof java.sql.Date){
			java.sql.Date d = (java.sql.Date) val;
			if(FastUtil.isBlank(format)){
				format = "yyyy-MM-dd HH:mm";
			}
			return new SimpleDateFormat(format).format(d);
		}
		else if(val instanceof Number){
			Number n = (Number) val;
			if(FastUtil.isBlank(format)){
				return new DecimalFormat().format(n);
			}
			else{
				return new DecimalFormat(format).format(n);
			}
		}
		return val.toString();
	}

	protected Object getBeanValue(String name){
		int pos = name.indexOf(":");
		String format = null;
		if(pos>0){//存在格式化内容
			format = name.substring(pos+1);
			name = name.substring(0,pos);
		}
		pos = name.indexOf(".");
		Object val = null;
		if(pos>0){
			Object bean = getBean(name.substring(0,pos));
			val = getProptValue(bean,name.substring(pos+1));
		}
		else{
			val = getBean(name);
		}
		if(val!=null && format!=null){
			val = getFormatValue(val,format);
		}
		return val;
	}

	protected Object getProptValue(Object bean, String name) {
		int pos = name.indexOf(".");
		if(pos>0){
			Object proptbean = getBean(name.substring(0,pos));
			Object val = getProptValue(proptbean,name.substring(pos+1));
			return val;
		}
		else{
			Object val = null;
			if(bean instanceof Map){
				Map<?,?> map = (Map<?,?>) bean;
				val = map.get(name);
			}
			try {
				val = PropertyUtils.getProperty(bean, name);
			} catch (Exception e) {
				logger.debug(e.getMessage(),e);
			}
			return val;
		}
	}

	protected Object getBean(String name) {
		if(pageContext==null){
			return null;
		}
		//优先从page中取值
		Object obj = pageContext.getAttribute(name);
		HttpServletRequest request = null;
		HttpSession session = null;
		if(obj==null){//page中取不到值时从action的属性中取值
			Object action = RequestContext.getRequestAction();
			obj = action==null?null:getProptValue(action, name);
		}
		if(obj==null){//action属性中取不到值时从Request中取值
			request = (HttpServletRequest) pageContext.getRequest();
			obj = request==null?null:request.getAttribute(name);
		}
		if(obj==null){//从session中取值
			session = request.getSession();
			obj = session==null?null:session.getAttribute(name);
		}
		if(obj==null){//从ServletContext中取值
			ServletContext sc = session.getServletContext();
			obj = sc==null?null:sc.getAttribute(name);
		}
		return obj;
	}
}

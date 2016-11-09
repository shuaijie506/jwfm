package com.dx.jwfm.framework.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.util.FastUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
* 创建人：宋帅杰
* 创建日期：2010-5-20
* 创建时间：上午11:19:01
* 功能描述：系统的Action基类，提供验证权限、直接输出HTML,JavaScript等元素的方法
* ==============================================
* 修改历史
* 修改人		修改时间		修改原因
*
* ==============================================
*/
public class BaseAction {
	protected Logger log = Logger.getLogger(this.getClass());
	
	protected HttpServletRequest getRequest(){
		return RequestContext.getRequest();
	}

	protected HttpServletResponse getResponse(){
		return RequestContext.getResponse();
	}
	
	/**
	 * 写COOKIE值
	 * @param name		名称
	 * @param value		COOKIE值
	 * @param expire	有效期，单位为天
	 * @param path		绑定路径
	 */
	protected void setCookie(String name,String value,int expire,String path){
		try {
			value = URLEncoder.encode(value,"UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		Cookie c = new Cookie(name,value);
		c.setPath(path);
		c.setMaxAge(expire*24*60*60);
		getResponse().addCookie(c);
	}

	/**
	 * 读取COOKIE的值
	 * @param name
	 * @return
	 */
	protected String getCookie(String name){
		if(name==null){
			return null;
		}
		for(Cookie c:getRequest().getCookies()){
			if(name.equals(c.getName())){
				try {
					return URLDecoder.decode(c.getValue(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					return c.getValue();
				}
			}
		}
		return null;
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-5-20  上午11:24:08
	* 功能描述: 获得URL请求中附带的参数值
	* 方法的参数和返回值: 
	* @param name
	* @return
	*/
	public String getParameter(String name){
		if(this.getRequest().getParameter(name)!=null)
		    return this.getRequest().getParameter(name).trim();
		else
			return null;
	}
	public String getParameter(String name,String defaul){
		if(this.getRequest().getParameter(name)!=null)
		    return this.getRequest().getParameter(name).trim();
		else
			return defaul;
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-5-20  下午02:11:47
	* 功能描述: 获得URL请求中附带的参数值(数组)
	* 方法的参数和返回值: 
	* @param name
	* @return
	*/
	public String[] getParameterValues(String name){
		return this.getRequest().getParameterValues(name);
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-11-6  下午12:58:47
	* 功能描述: 获取request的attribute的字符串值
	* 方法的参数和返回值: 
	* @param name
	* @return
	*/
	public String getAttr(String name){
		Object obj = this.getRequest().getAttribute(name);
		if(obj==null){
			return null;
		}
		return obj.toString();
	}
	public String getAttr(String name,String defaul){
		Object obj = this.getRequest().getAttribute(name);
		if(obj==null){
			return defaul;
		}
		return obj.toString();
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-11-6  下午12:59:03
	* 功能描述: 获取request的attribute的对象值
	* 方法的参数和返回值: 
	* @param name
	* @return
	*/
	public Object getAttribute(String name){
		if(getRequest()==null)return null;
		return getRequest().getAttribute(name);
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-11-6  下午12:59:14
	* 功能描述: 设置request的attribute的对象值
	* 方法的参数和返回值: 
	* @param name
	* @param value
	*/
	public void setAttribute(String name,Object value){
		this.getRequest().setAttribute(name,value);
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-11-4  下午02:40:15
	* 功能描述: 对某个对象的属性进行赋值的操作
	* 方法的参数和返回值: 
	* @param bean
	* @param proptName
	* @param value
	* @return
	*/
	protected boolean setBeanPropt(Object bean,String proptName,Object value){
		try {
			BeanUtils.copyProperty(bean, proptName, value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-5-20  上午10:44:05
	* 功能描述: 直接输出JavaScript脚本
	* 方法的参数和返回值: 
	* @param html
	* @return
	*/
	protected String writeJavaScript(String js){
		return this.writeHTML("<script type='text/javascript'>\n"+js+"\n</script>");
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-5-20  上午10:43:40
	* 功能描述: 直接输出HTML文本
	* 方法的参数和返回值: 
	* @param html
	* @return
	*/
	protected String writeHTML(String html){
		HttpServletResponse response = this.getResponse();
		try {
			response.setContentType("text/html; charset=UTF-8"); // 设置 content-type
			response.setCharacterEncoding("UTF-8");  // 设置响应数据编码格式 (输出)
			PrintWriter out = response.getWriter();
			out.print(html);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月3日 上午10:12:25
	 * 功能描述: 将对象转为json串输出
	 * 方法的参数和返回值: 
	 * @param obj
	 * @return
	 */
	protected String writeJson(Object obj){
		JSONObject jobj = JSONObject.fromObject(obj,FastUtil.getJsonConfigDefault());
		return writeHTML(jobj.toString());
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月3日 上午10:12:25
	 * 功能描述: 将对象转为json串输出
	 * 方法的参数和返回值: 
	 * @param list
	 * @return
	 */
	protected String writeJson(List<Object> list){
		JSONArray ary = new JSONArray();
		ary.addAll(list,FastUtil.getJsonConfigDefault());
		return writeHTML(ary.toString());
	}
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月3日 上午10:19:21
	 * 功能描述: 以json格式返回处理结果，同时可附加其他信息
	 * 方法的参数和返回值: 
	 * @param result 结果正常时返回 ok 否则返回其他值
	 * @param info	  结果的其他附加信息
	 * @return
	 */
	protected String writeResult(String result,String info){
		JSONObject obj = new JSONObject();
		obj.put("result", result);
		obj.put("info", info);
		return writeHTML(obj.toString());
	}
}

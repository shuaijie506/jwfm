package com.dx.jwfm.framework.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.contants.RequestContants;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.parser.IDefaultValueParser;
import com.dx.jwfm.framework.core.wrapper.ParameterRequestWrapper;
import com.dx.jwfm.framework.util.FastUtil;

public class RequestContext {

	static String HTTP_REQUEST = "HTTP_REQUEST";
	static String HTTP_RESPONSE = "HTTP_RESPONSE";
	static String SERVLET_CONTEXT = "SERVLET_CONTEXT";
	
	static Logger logger = Logger.getLogger(RequestContext.class);
	
	/**
	 * 线程变量，用来存放与用户请求时生成的RequestContext对象
	 */
	static ThreadLocal<RequestContext> context = new ThreadLocal<RequestContext>();
	
	/**
	 * 框架主线程的变量，用来在独立线程中访问数据库等操作
	 */
	static RequestContext mainContext;

	/**
	 * 线程变量MAP，用来存在与用户请求时相关的对象、参数等信息
	 */
	private HashMap<String,Object> map = new HashMap<String,Object>();

	/**
	 * 线程变量list，用来保存线程中用到的未释放连接的数据库连接，在处理结束后如果未释放，则自动提交事务并释放数据库连接
	 */
	List<DbHelper> dblist = new ArrayList<DbHelper>();
	
	/**
	 * FastFilter的实例
	 */
	private FastFilter filter;

	/**
	 * 获得当前线程中用户的Request对象
	 * @return
	 */
	public static HttpServletRequest getRequest(){
		RequestContext rc = context.get();
		if(rc!=null){
			return (HttpServletRequest) rc.map.get(HTTP_REQUEST);
		}
		return null;
	}
	/**
	 * 获得当前请求对应的模型
	 * @return
	 */
	public static FastModel getFastModel(){
		HttpServletRequest request = getRequest();
		return (FastModel)request.getAttribute(RequestContants.REQUEST_FAST_MODEL);
	}
	/**
	 * 获得当前线程中用户的Response对象
	 * @return
	 */
	public static HttpServletResponse getResponse(){
		RequestContext rc = context.get();
		if(rc!=null){
			return (HttpServletResponse) rc.map.get(HTTP_RESPONSE);
		}
		return null;
	}
	/**
	 * 获得当前线程中用于处理用户请求的Action对象
	 * @return
	 */
	public static Object getRequestAction(){
		HttpServletRequest request = getRequest();
		if(request!=null){
			return request.getAttribute(RequestContants.REQUEST_FAST_ACTION);
		}
		return null;
	}
	/**
	 * Servlet在对用户请求进行处理时，如果为.action或.jsp时，会调用本方法将用户请求信息设置到线程变量
	 * @param request
	 * @param response
	 * @param servletParam
	 */
	static void setRequestInfo(FastFilter filter,HttpServletRequest request,HttpServletResponse response){
		RequestContext rc = context.get();
		if(rc==null){
			rc = new RequestContext();
			context.set(rc);
		}
		rc.filter = filter;
		rc.map.put(HTTP_REQUEST, request);
		rc.map.put(HTTP_RESPONSE, response);
		if(request==null){
			mainContext = rc;
		}
	}
	public static void addRequestParamMap(Map<String,String> map){
		RequestContext rc = context.get();
		if(rc!=null){
			HttpServletRequest req = (HttpServletRequest) rc.map.get(HTTP_REQUEST);
			ParameterRequestWrapper request=new ParameterRequestWrapper(req,map);
			rc.map.put(HTTP_REQUEST, request);
		}
	}
	/**
	 * 一个简洁调用 request.setAttribute(name, val);的方法
	 * 相当于：
	 * HttpServletRequest request = getRequest();
		if(request!=null){
			request.setAttribute(name, val);
		}
	 * @param name
	 * @param val
	 */
	public static void setRequestAttr(String name, Object val) {
		HttpServletRequest request = getRequest();
		if(request!=null){
			request.setAttribute(name, val);
		}
	}
	/**
	 * 获取用户请求参数值
	 * @param name
	 * @return
	 */
	public static String getParameter(String name) {
		HttpServletRequest request = getRequest();
		if(request!=null){
			return request.getParameter(name);
		}
		return null;
	}
	/**
	 * 获取用户请求参数值（数组类型）
	 * @param name
	 * @return
	 */
	public static String[] getParameterValues(String name) {
		HttpServletRequest request = getRequest();
		if(request!=null){
			return request.getParameterValues(name);
		}
		return null;
	}
	/**
	 * 获得当前线程中用于处理默认值的对象列表，已包含系统指定的默认值对象列表
	 * @return
	 */
	public static List<IDefaultValueParser> getDefaultValueParser() {
		FastModel model = getFastModel();
		if(model!=null){
			return model.getActionDefaultValueParser();
		}
		return getSystemDefaultValueParser();
	}

	/**
	 * 获得当前系统中指定的处理默认值的对象列表
	 * @return
	 */
	public static List<IDefaultValueParser> getSystemDefaultValueParser() {
		RequestContext rc = context.get();
		if(rc!=null){
			return rc.filter.defaultValueParser;
		}
		return null;
	}
	
	/**
	 * 获得数据库表的ID字段名
	 * @return
	 */
	public static String getDbIdField(){
		RequestContext rc = context.get();
		if(rc!=null){
			return FastUtil.nvl(rc.filter.getInitParameter("databaseTableIdFieldName"),"VC_ID");
		}
		return "VC_ID";
	}
	/**
	 * 获得数据库表的删除列的字段名
	 * @return
	 */
	public static String getDbDelFlagField(){
		RequestContext rc = context.get();
		if(rc!=null){
			return FastUtil.nvl(rc.filter.getInitParameter("databaseTableDelFlagFieldName"),"N_DEL");
		}
		return "N_DEL";
	}
}
package com.dx.jwfm.framework.core.process;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dx.jwfm.framework.core.FastFilter;

/**
 * Fast中用户请求的拦截处理接口
 * @author Administrator
 *
 */
public interface IFastProcess {
	
	/**
	 * 系统启动后的初始化方法
	 */
	public void init(FastFilter filter);
	
	/**
	 * 用户请求时的拦截处理方法
	 * @param request
	 * @param response
	 * @return	已处理完成返回true，否则返回false,由WEB容器继续处理
	 */
	public boolean processRequest(HttpServletRequest request,HttpServletResponse response,String uri,String uriExt);

}

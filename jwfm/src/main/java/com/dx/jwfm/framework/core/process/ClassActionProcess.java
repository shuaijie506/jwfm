package com.dx.jwfm.framework.core.process;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.core.contants.RequestContants;
import com.dx.jwfm.framework.core.exception.ForwardException;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.parser.ParameterActionParser;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.action.ActionCreator;

public class ClassActionProcess implements IFastProcess {

	static Logger logger = Logger.getLogger(FastActionProcess.class);
	private String basePath;
	private String basePackage;
	/**
	 * 正常URL请求的扩展名
	 */
	private String actionExt;
	/** 参数转换处理器 */
	private ParameterActionParser paramParser = new ParameterActionParser();
	
	public void init(FastFilter filter) {
		actionExt = filter.getActionExt();
		basePath = filter.getInitParameter("classModelBasePath");
		basePackage = filter.getInitParameter("classModelBasePackage");
		if(!basePackage.endsWith(".")){
			basePackage += ".";
		}
	}
	
	public boolean processRequest(HttpServletRequest request, HttpServletResponse response,String uri,String uriExt) {
		if(!uriExt.equals(actionExt)){//如果不是以指定内容结尾，则不处理
			return false;
		}
		int endpos = uri.indexOf("?");
		if(endpos<0){
			endpos = uri.length();
		}
		int endpos2 = uri.lastIndexOf(".",endpos);
		if(endpos2>0 && endpos2<endpos){
			endpos = endpos2;//结束位置到.action之前
		}
		int bpos = request.getContextPath().length()+basePath.length();//起始计算位置
		while(uri.length()>bpos+1 && uri.charAt(bpos)!='/'){
			bpos++;
		}
		int mpos = uri.indexOf("_",bpos);
		if(mpos<0 || mpos>endpos){// _xxx的位置，如果没有_xx，则取.action的位置
			mpos = endpos;
		}
		if(bpos+1>=mpos){
			return false;
		}
		String clsNameExt = uri.substring(bpos+1,mpos);
		String className = clsNameExt.replaceAll("\\/+", ".");
		if(!className.startsWith("com.")){
			className = basePackage + className;
		}
		String method = mpos==endpos?"execute":uri.substring(mpos+1,endpos);
		String uriPre = uri.substring(0,mpos);//解析出菜单URL前缀
		String menuUrl = uriPre+actionExt;//解析出菜单URL
		request.setAttribute(RequestContants.REQUEST_URI, menuUrl);
		request.setAttribute(RequestContants.REQUEST_URI_PRE, uriPre);
		request.setAttribute(RequestContants.REQUEST_URI_METHOD, method);
		request.setAttribute(RequestContants.REQUEST_URI_ACTIONEXT, actionExt);
		FastModel model = null;
		try {
			Object action = FastUtil.newInstance(className);
			paramParser.parseParam(request, action);
			request.setAttribute(RequestContants.REQUEST_FAST_ACTION, action);
			if(action instanceof ActionCreator){
				ActionCreator caction = (ActionCreator) action;
				model = caction.getFastModel();
				request.setAttribute(RequestContants.REQUEST_FAST_MODEL,model);
				caction.init();
			}
			Method m = action.getClass().getMethod(method, new Class[0]);
			String res = (String) m.invoke(action, new Object[0]);
			if(res!=null){
				//以/开头的返回值默认为forward到指定的JSP页面，否则从配置中获取JSP路径
				if(model!=null && !res.toLowerCase().endsWith(".jsp")){
					String forwardname = res;
					res = model.getModelStructure().getForward(res);
					if(res==null){
						throw new ForwardException("can't find forward ["+forwardname+"] in ["+model.getVcUrl()+"]'s config");
					}
				}
				String redirectPre = "redirect:";
				if(res.startsWith(redirectPre )){//如果是以redirect:开头，则进行跳转
					response.sendRedirect(res.substring(redirectPre.length()));
				}
				else{
					if(res.charAt(0)!='/'){//以/开头的返回值默认为forward到指定的JSP页面，否则加上路径前缀
						int pos = clsNameExt.lastIndexOf("/");
						String pathPre = pos>0?clsNameExt.substring(0,pos+1):"/";
						res = "/"+pathPre+res;
					}
					logger.info(res);
					//forward到指定的地址
					JspProcess.forward(request, response, res);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			try {
				PrintWriter out = response.getWriter();
				String msg = "访问页面时发生了错误！";
				if(ex instanceof ClassNotFoundException){
					msg = "指定的页面地址不存在！";
				}
				out.println("<h1 style='margin:30px auto 10px;display:inline-block;'>"+msg+"</h1>" +
						"<script>function showInfo(){document.getElementsByTagName('pre')[0].style.display='block';}</script>"+
						"<a href='javascript:showInfo();' style='font-size:12px;'>详细原因</a><pre style='display:none;'>");
				ex.printStackTrace(out);
				out.println("</pre>");
			} catch (IOException e2) {
				logger.error(e2.getMessage(),e2);
			}
		}
		return true;
	}

}

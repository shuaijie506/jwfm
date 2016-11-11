package com.dx.jwfm.framework.core.process;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.core.contants.RequestContants;
import com.dx.jwfm.framework.core.exception.ForwardException;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.util.ClassUtil;
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
	/**
	 * 用户自定义Action处理器
	 */
	private ArrayList<IActionHandel> actionHandels = new ArrayList<IActionHandel>();

	@SuppressWarnings("rawtypes")
	
	public void init(FastFilter filter) {
		actionExt = filter.getActionExt();
		basePath = filter.getInitParameter("classModelBasePath");
		basePackage = filter.getInitParameter("classModelBasePackage");
		if(!basePackage.endsWith(".")){
			basePackage += ".";
		}
		//加载系统指定拦截处理器
		String val = filter.getInitParameter("ActionHandle");
		String[] ary = FastUtil.nvl(val,"").split(",");
		for(int i=0;i<ary.length;i++){
			if(ary[i]==null || ary[i].trim().length()==0){
				continue;
			}
			String clsName = ary[i].trim();
			try {
				IActionHandel proc = (IActionHandel) FastUtil.newInstance(clsName);
				actionHandels.add(proc);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}

        List<Class> classes = ClassUtil.getAllClassBySuperClass(basePackage.substring(0,basePackage.length()-1),ActionCreator.class); 
        for(Class cls:classes){
        	try {
				ActionCreator ac = (ActionCreator) cls.newInstance();
				ac.getFastModel().init();
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
        }
	}

	
	public boolean processRequest(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getRequestURI();
		if(!path.endsWith(actionExt)){//如果不是以指定内容结尾，则不处理
			return false;
		}
		int endpos = path.indexOf("?");
		if(endpos<0){
			endpos = path.length();
		}
		int endpos2 = path.lastIndexOf(".",endpos);
		if(endpos2>0 && endpos2<endpos){
			endpos = endpos2;
		}
		int bpos = request.getContextPath().length()+basePath.length();
		while(path.charAt(bpos)!='/'){
			bpos++;
		}
		int mpos = path.indexOf("_",bpos);
		if(mpos<0 || mpos>endpos){
			mpos = endpos;
		}
		String clsNameExt = path.substring(bpos+1,mpos);
		String className = basePackage+clsNameExt.replaceAll("\\/+", ".");
		String method = mpos==endpos?"execute":path.substring(mpos+1,endpos);
		String uriPre = path.substring(0,mpos);//解析出菜单URL前缀
		String menuUrl = uriPre+actionExt;//解析出菜单URL
		request.setAttribute(RequestContants.REQUEST_URI, menuUrl);
		request.setAttribute(RequestContants.REQUEST_URI_PRE, uriPre);
		request.setAttribute(RequestContants.REQUEST_URI_METHOD, method);
		request.setAttribute(RequestContants.REQUEST_URI_ACTIONEXT, actionExt);
		FastModel model = null;
		try {
			Object action = FastUtil.newInstance(className);
			request.setAttribute(RequestContants.REQUEST_FAST_ACTION, action);
			if(action instanceof ActionCreator){
				ActionCreator caction = (ActionCreator) action;
				model = caction.getFastModel();
				request.setAttribute(RequestContants.REQUEST_FAST_MODEL,model);
				caction.init();
			}
			for(IActionHandel ah:actionHandels){//执行系统定义处理器
				if(ah.beforeExecute(action,method)){
					return true;//返回true时跳过action的处理代码
				}
			}
			Method m = action.getClass().getMethod(method, new Class[0]);
			String res = (String) m.invoke(action, new Object[0]);
			for(IActionHandel ah:actionHandels){//执行系统定义处理器
				ah.afterExecute(action,method);
			}
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

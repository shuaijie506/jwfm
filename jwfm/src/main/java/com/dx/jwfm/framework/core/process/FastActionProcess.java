package com.dx.jwfm.framework.core.process;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.contants.RequestContants;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.exception.ForwardException;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.action.MainActionCreator;

public class FastActionProcess implements IFastProcess {

	static Logger logger = Logger.getLogger(FastActionProcess.class);

	static String DEFAULT_ERROR_PAGE = "/fast/err/error.jsp";
	
//	private FastFilter filter;

	/**错误后的转向页面*/
	private String errorPage;
	
	/**
	 * 正常URL请求的扩展名
	 */
	private String actionExt;

	/**
	 * 用户自定义Action处理器
	 */
	private ArrayList<IActionHandel> actionHandels = new ArrayList<IActionHandel>();

	private HashMap<String,FastModel> menuMap = new HashMap<String, FastModel>();
	
	
	public void init(FastFilter filter) {
//		this.filter = filter;
		errorPage = FastUtil.nvl(filter.getInitParameter("errorPage"), DEFAULT_ERROR_PAGE);
		actionExt = filter.getActionExt();
		FastModel main = MainActionCreator.getModel();//主框架模型
		testModelTableExist(main);
		String sql = "select * from "+SystemContext.dbObjectPrefix+"T_MENU_LIB where vc_url like '%"+actionExt+"' order by vc_group,vc_name";
		DbHelper db = new DbHelper();
		List<FastPo> list = null;
		final ArrayList<FastModel> flist = new ArrayList<FastModel>();
		try {
			list = db.executeSqlQuery(sql);
			for(FastPo po:list){
				FastModel fm = new FastModel();
				fm.setVcId(po.getString("VC_ID"));
				fm.setVcName(po.getString("VC_NAME"));
				fm.setVcGroup(po.getString("VC_GROUP"));
				fm.setVcUrl(po.getString("VC_URL"));
				fm.setVcAuth(po.getString("VC_AUTH"));
				fm.setVcStructure(po.getString("VC_STRUCTURE"));
				menuMap.put(SystemContext.getPath()+fm.getVcUrl(), fm);
				flist.add(fm);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		MainActionCreator.initFrameworkModel(flist, menuMap);//初始化框架中的所有功能菜单
		new Thread(){
			public void run(){
				for(int i=0;i<flist.size();i++){
					flist.get(i).init();
				}
			}
		}.start();
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
	}
	/**
	 * 处理用户请求，已处理完成返回true，否则返回false,由WEB容器继续处理
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean processRequest(HttpServletRequest request,HttpServletResponse response){
		String uri = request.getRequestURI();
		if(!uri.endsWith(actionExt)){//如果不是以指定内容结尾，则不处理
			return false;
		}
		uri = uri.substring(0,uri.length()-actionExt.length());
		String method = null;
		int pos = uri.lastIndexOf("_");
		if(pos>=0){
			method = uri.substring(pos+1);//解析出指定要执行的方法
			uri = uri.substring(0,pos);
		}
		if(method==null){//先从.action之前截取方法名，取不到时取op参数值做为参数名
			method = request.getParameter("op");
		}
		if(method==null){//如果没有指定方法，默认调用execute方法
			method = "execute";
		}
		String menuUrl = uri+actionExt;//解析出菜单URL
		request.setAttribute(RequestContants.REQUEST_URI, menuUrl);
		request.setAttribute(RequestContants.REQUEST_URI_PRE, uri);
		request.setAttribute(RequestContants.REQUEST_URI_METHOD, method);
		request.setAttribute(RequestContants.REQUEST_URI_ACTIONEXT, actionExt);
		FastModel model = menuMap.get(menuUrl);
		if(model==null){
			return false;
		}
		model.init();
		request.setAttribute(RequestContants.REQUEST_FAST_MODEL, model);
		try {
			Object action = model.getActionClass().newInstance();//得到Action实例
			request.setAttribute(RequestContants.REQUEST_FAST_ACTION, action);
			for(IActionHandel ah:actionHandels){//执行系统定义处理器
				if(ah.beforeExecute(action,method)){
					return true;//返回true时跳过action的处理代码
				}
			}
			IActionHandel actionHandle = null;
			if(model.getActionHandel()!=null) {
				actionHandle = model.getActionHandel().newInstance();
			}
			if(actionHandle!=null && actionHandle.beforeExecute(action,method)){
				return true;//返回true时跳过action的处理代码
			}
			Method m = model.getActionClass().getMethod(method, new Class[0]);
			String res = (String) m.invoke(action, new Object[0]);//反射调用方法
			if(actionHandle!=null){
				actionHandle.afterExecute(action, method);
			}
			for(IActionHandel ah:actionHandels){//执行系统定义处理器
				ah.afterExecute(action,method);
			}
			if(res!=null){
				if(res.charAt(0)!='/'){//以/开头的返回值默认为forward到指定的JSP页面，否则从配置中获取JSP路径
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
					logger.info(res);
					//forward到指定的地址
					JspProcess.forward(request, response, res);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute(RequestContants.REQUEST_EXCEPTION, e);
			try {
				JspProcess.forward(request, response, errorPage);
			} catch (Exception e1) {
				logger.error(e1.getMessage(),e1);
				try {
					e1.printStackTrace(response.getWriter());
				} catch (IOException e2) {
					logger.error(e2.getMessage(),e2);
				}
			}
		}
		return true;
	}

	public void updateFastModel(FastModel model){
		menuMap.put(model.getVcUrl(), model);
	}
	public void testModelTableExist(FastModel main){
		main.init();
	}
}

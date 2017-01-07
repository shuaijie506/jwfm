package com.dx.jwfm.framework.core.process;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.contants.RequestContants;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.exception.ForwardException;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.parser.ParameterActionParser;
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

	/** 参数转换处理器 */
	private ParameterActionParser paramParser = new ParameterActionParser();

	private HashMap<String,FastModel> menuMap = new HashMap<String, FastModel>();
	
	
	public void init(FastFilter filter) {
//		this.filter = filter;
		filter.actionProc = this;
		errorPage = FastUtil.nvl(filter.getInitParameter("errorPage"), DEFAULT_ERROR_PAGE);
		actionExt = filter.getActionExt();
		FastModel main = MainActionCreator.getModel();//主框架模型
		testModelTableExist(main);
		String sql = "select * from "+SystemContext.dbObjectPrefix+"T_MENU_LIB where n_del=0 order by vc_group,vc_name";
		DbHelper db = new DbHelper();
		List<FastPo> list = null;
		final ArrayList<FastModel> flist = new ArrayList<FastModel>();
		try {
			list = db.executeSqlQuery(sql);
			for(FastPo po:list){
				FastModel fm = new FastModel(po);
				fm.setFromDB(true);
				String key = SystemContext.getPath()+fm.getVcUrl()+actionExt;
				FastModel old = menuMap.get(key);
				if(old!=null && old.getVcVersion().compareTo(fm.getVcVersion())>0){
					flist.add(old);
					continue;
				}
				menuMap.put(key , fm);
				flist.add(fm);
			}
			for(FastModel fm:filter.defaultModels){
				String key = SystemContext.getPath()+fm.getVcUrl()+actionExt;
				FastModel that = menuMap.get(key);
				if(that!=null && that.getVcVersion().compareTo(fm.getVcVersion())>=0){
					continue;//如果数据库中的版本比当前版本新或一样，则不处理
				}
				menuMap.put(key , fm);
				flist.add(fm);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		MainActionCreator.initFrameworkModel(flist, menuMap, actionExt);//初始化框架中的所有功能菜单
		new Thread(){
			public void run(){
				for(int i=0;i<flist.size();i++){
					flist.get(i).init();
				}
			}
		}.start();
	}
	/**
	 * 处理用户请求，已处理完成返回true，否则返回false,由WEB容器继续处理
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean processRequest(HttpServletRequest request,HttpServletResponse response,String uri,String uriExt){
		if(!uriExt.equals(actionExt)){//如果不是以指定内容结尾，则不处理
			return false;
		}
		uri = uri.substring(0,uri.length()-actionExt.length());
		String method = "execute";//如果没有指定方法，默认调用execute方法
		int pos = uri.lastIndexOf("_");
		if(pos>=0){
			method = uri.substring(pos+1);//解析出指定要执行的方法
			uri = uri.substring(0,pos);
		}
		String menuUrl = uri+actionExt;//解析出菜单URL
		request.setAttribute("path", SystemContext.path);
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
			if(FastUtil.isNotBlank(model.getModelStructure().getSearchClassName())){
				try{
					Object srh = FastUtil.newInstance(model.getModelStructure().getSearchClassName());
					PropertyUtils.setProperty(action, "search", srh);
				}
				catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
			paramParser.parseParam(request, action);
			request.setAttribute(RequestContants.REQUEST_FAST_ACTION, action);
			Method m = model.getActionClass().getMethod(method, new Class[0]);
			String res = (String) m.invoke(action, new Object[0]);//反射调用方法
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
		menuMap.put(SystemContext.getPath()+model.getVcUrl()+actionExt, model);
	}
	public void testModelTableExist(FastModel main){
		main.init();
	}
}

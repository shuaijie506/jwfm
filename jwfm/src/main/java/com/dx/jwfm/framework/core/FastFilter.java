package com.dx.jwfm.framework.core;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.dao.dialect.impl.MySqlDialect;
import com.dx.jwfm.framework.core.dao.dialect.impl.OracleDialect;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.parser.MacroValueNode;
import com.dx.jwfm.framework.core.process.FastActionProcess;
import com.dx.jwfm.framework.core.process.IFastProcess;
import com.dx.jwfm.framework.core.servlet.VirtualServletConfig;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.util.UtilPrepareClass;

public class FastFilter implements Filter {

	static Logger logger = Logger.getLogger(FastFilter.class);
	static Logger urlLogger = Logger.getLogger("URL");

	/**
	 * 本filter只允许单例，定义多个Filter时将抛出异常
	 */
	static FastFilter filter;

	/**
	 * web.xml文件中配置的Filter初始化参数列表
	 */
	HashMap<String,String> map = new HashMap<String,String>();
	
	static boolean useSpring;//使用Spring
	
	/** 正常URL请求的扩展名 */
	private String actionExt = "action";
	/** 下载文件时的虚拟路径前缀 */
	private String uploadfilePrev = "/uploadfile/";
	
	DatabaseDialect dialect;
	
	/**
	 * 虚拟的Servlet配置对象
	 */
	VirtualServletConfig virtualServletConfig;
	
	/**
	 * 用户自定义拦截处理器
	 */
	private ArrayList<IFastProcess> procList = new ArrayList<IFastProcess>();

	/**
	 * 用户自定义默认值处理类
	 */
	HashMap<String,MacroValueNode> macroValueMap = new LinkedHashMap<String,MacroValueNode>();

	/** 用户请求字符集设置 */
	private String charset;
	/** 系统启动时预置的业务模块 */
	public List<FastModel> defaultModels = new ArrayList<FastModel>();
	public FastActionProcess actionProc;

	@SuppressWarnings("unchecked")
	
	public void init(FilterConfig config) throws ServletException {
		if(filter!=null){
			throw new ServletException("FastFilter在一个工程中只允许配置一个，请删除web.xml中多余的配置项");
		}
		filter = this;
		RequestContext.setRequestInfo(this, null, null);
		if(SystemContext.systemParam==null){//加载系统参数
			loadFastInitParam();
		}
		virtualServletConfig = new VirtualServletConfig(config.getServletContext());
		Enumeration<String> en = config.getInitParameterNames();
		while(en.hasMoreElements()){//将Servlet自身的参数放入到map中
			String name = en.nextElement();
			map.put(name, config.getInitParameter(name));
		}
		SystemContext.filterParam = map;
		if(map.containsKey("dbObjectPrefix")){//数据库对象前缀
			SystemContext.dbObjectPrefix = map.get("databaseObjectPrefix");
		}
		charset  = FastUtil.nvl(getInitParameter("encoding"),"UTF-8");
		//先取本filter中的配置参数，如果没有此参数，则使用公共配置
		useSpring = "true".equals(getInitParameter("useSpring"));
		if(SystemContext.appContext==null && useSpring){//设置Spring上下文对象
			SystemContext.appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		}
		if(SystemContext.appPath==null){//系统运行时工程根目录路径
			SystemContext.appPath = config.getServletContext().getRealPath(".");
		}
		if(SystemContext.path==null){//系统运行时的URL根路径
			SystemContext.path = config.getServletContext().getContextPath();
			if("/".equals(SystemContext.path)){
				SystemContext.path = "";
			}
		}
		logger.info("启动War包："+SystemContext.path);
		logger.info("系统目录："+SystemContext.appPath);
		actionExt = "."+FastUtil.nvl(getInitParameter("actionExt"),actionExt);
		uploadfilePrev = SystemContext.path+FastUtil.nvl(getInitParameter("uploadfilePrev"),uploadfilePrev);
		
		//初始化拦截处理器
		for(IFastProcess proc:procList){
			proc.init(this);
		}
	}

	
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		request.setCharacterEncoding(charset);
		response.setCharacterEncoding(charset);
		String uri = request.getRequestURI();
		int pos = uri.lastIndexOf(".");
		
		String uriExt = pos>0?uri.substring(pos):"";
		RequestContext.setRequestInfo(this, request, response);
		String uriExtLow = uriExt.toLowerCase();
		if(".jsp".equals(uriExtLow) || actionExt.equals(uriExtLow)){//如果由拦截处理器处理，则打印URL
			urlLogger.info(uri);
		}
		boolean finish = false;
		for(int i=0;i<procList.size();i++){//使用用户自定义拦截处理器
			finish = procList.get(i).processRequest(request, response, uri, uriExt);
			if(finish){//如果已完成处理，则跳出循环，不再执行下面的拦截处理器
				break;
			}
		}
		if(!finish){
			//如果均未处理，把控制权交回系统，由系统继续处理
			arg2.doFilter(arg0, arg1);
		}
		//处理结束后关闭所有打开的数据库链接并提交数据
		RequestContext rc = RequestContext.context.get();
		for(DbHelper db:rc.dblist){
			db.close();
		}
	}

	
	public void destroy() {
	}

	@SuppressWarnings("unchecked")
	private void loadFastInitParam() {
		HashMap<String,String> map = new HashMap<String, String>();
		SystemContext.systemParam = map;
		try {
			Enumeration<URL> urls = RequestContext.class.getClassLoader().getResources("fast.properties");
			while(urls.hasMoreElements()){
				URL url = urls.nextElement();
				logger.info(url.toString());
				Properties p = new Properties();
				try {
					p.load(url.openStream());
					for(Object okey:p.keySet()){
						String key = okey.toString();
						if(!map.containsKey(key)){//不覆盖，以先加载的配置文件内容为准
							String val = p.getProperty(key);
							map.put(key, val==null?"":val.trim());
						}
					}
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		try {
			Enumeration<URL> urls = RequestContext.class.getClassLoader().getResources("fast.xml");
			while(urls.hasMoreElements()){
				URL url = urls.nextElement();
				logger.info(url.toString());
				try {
					SAXReader reader = new SAXReader();  
					Document doc = reader.read(url.openStream());  
					Element root = doc.getRootElement();
					List<Element> list = root.selectNodes("config/item");//一般配置项
					for(Element e:list){
						String val = e.getText();
						map.put(e.attributeValue("name"), val==null?"":val.trim());
					}
					UtilPrepareClass.loadFastXml(root);//注册项的默认值读取
					list = root.selectNodes("macrovalue/item");//系统内默认值处理器
					for(Element e:list){
						MacroValueNode n = MacroValueNode.genNode(e.attributeValue("code"), e.attributeValue("name"), e.getText());
						if(n!=null && !macroValueMap.containsKey(n.getCode())){//以第一次加载为准
							macroValueMap.put(n.getCode(), n);
						}
					}
					list = root.selectNodes("modules/item");//系统内置模块处理
					for(Element e:list){
						FastModel n = new FastModel(e.attributeValue("name"), e.attributeValue("uri"), e.attributeValue("version"), e.getText());
						defaultModels .add(n);
					}
					//加载系统指定拦截处理器
					list = root.selectNodes("actionProc/item");//系统拦截处理器
					for(Element e:list){
						String clsName = e.getText();
						try {
							IFastProcess proc = (IFastProcess) FastUtil.newInstance(clsName);
							procList.add(proc);
						} catch (Exception e1) {
							logger.error(e1.getMessage(),e1);
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 获取数据库方言
	 */
	public DatabaseDialect getDialect() {
		if(dialect==null){//先从配置文件加载方言对象
			String clsName = getInitParameter("databaseDialect");
			if(clsName!=null){
				try {
					dialect = (DatabaseDialect) FastUtil.newInstance(clsName);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		if(dialect==null){//如果未加载成功，则自动判断数据库方言
			DbHelper db = new DbHelper();
			try {
				Connection con = db.getConnection();
				String drv = con.getMetaData().getDriverName().toLowerCase();
				if(drv.indexOf("oracle")>=0){
					dialect = new OracleDialect();
				}
				else if(drv.indexOf("mysql")>=0){
					dialect = new MySqlDialect();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
		return dialect;
	}
	
	/**
	 * 用户在界面实时更新模板时可以调用此方法刷新模板
	 * @param model
	 */
	public static void updateFastModel(FastModel model){
		if(filter.actionProc!=null){
			filter.actionProc.updateFastModel(model);
		}
	}
	public static void addDbHelper(DbHelper db){
		RequestContext rc = RequestContext.context.get();
		if(rc!=null){
			rc.dblist.add(db);
		}
	}

	/**
	 * URL后缀，默认为action
	 * @return
	 */
	public String getActionExt() {
		return actionExt;
	}

	/**
	 * 获取系统配置参数，默认从web.xml中的filter的配置中获取，如果没有设置项目，则从fast.properties中读取
	 * @param name
	 * @return
	 */
	public String getInitParameter(String name){
		return FastUtil.nvl(map.get(name),SystemContext.systemParam.get(name));
	}

	public VirtualServletConfig getVirtualServletConfig() {
		return virtualServletConfig;
	}
	
}

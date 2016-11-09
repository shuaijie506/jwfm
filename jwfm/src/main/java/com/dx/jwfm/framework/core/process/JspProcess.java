package com.dx.jwfm.framework.core.process;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.contants.RequestContants;
import com.dx.jwfm.framework.core.exception.JspClassNotFound;

public class JspProcess implements IFastProcess {

	static Logger logger = Logger.getLogger(JspProcess.class);

	public static HashMap<String, String> classPathMap = new HashMap<String, String>();
	
	public static HashMap<String, HttpServlet> classMap = new HashMap<String, HttpServlet>();
	
	private FastFilter filter;
	
	public void init(FastFilter filter) {
		this.filter = filter;
		loadClassMappingFile();
		instance = this;
	}

	
	public boolean processRequest(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getServletPath();
		String baseDir = SystemContext.getAppPath();
		File f = new File(baseDir+uri);
		if(f.exists()){//如果文件（包含JSP文件）在系统中存在，则不处理，使用系统自动处理
			return false;
		}
//		if(uri.endsWith(".jsp")){//如果是JSP文件，则判断是否从类中加载
			processJsp(request,response,uri);
//		}
		return true;
	}
	
	private static JspProcess instance;
	public static void forward(HttpServletRequest request, HttpServletResponse response,String uri) throws ServletException, IOException{
		String baseDir = SystemContext.getAppPath();
		File f = new File(baseDir+uri);
		Object action = request.getAttribute(RequestContants.REQUEST_FAST_ACTION);
		if(action!=null){
			try {
				PropertyDescriptor[] ps = PropertyUtils.getPropertyDescriptors(action);
				for(PropertyDescriptor p:ps){
					try {
						Object o = PropertyUtils.getProperty(action, p.getName());
						request.setAttribute(p.getName(), o);
					} catch (Exception e) {
						logger.debug(e.getMessage(),e);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		if(instance==null || f.exists()){//如果文件（包含JSP文件）在系统中存在，则不处理，使用系统自动处理
			request.getRequestDispatcher(uri).forward(request, response);
		}
		else{
			instance.processJsp(request, response, uri);
		}
	}

	private void processJsp(HttpServletRequest request, HttpServletResponse response, String uri) {
		try {
			HttpServlet servlet = getServlet(uri);
			if(servlet==null){
				return;
			}
			servlet.init(filter.getVirtualServletConfig());
			servlet.service(request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
//			if(errorPage.equals(uri)){
				try {
					e.printStackTrace(response.getWriter());
				} catch (IOException e1) {
					logger.error(e1.getMessage(),e1);
				}
//			}
		}
	}
	public HttpServlet getServlet(String uri) throws Exception{
		HttpServlet hs = null;
		Class<?> clazz = null;
		String className = null;
		hs = (HttpServlet) classMap.get(uri);
		if(hs!=null){
			return hs;
		}
		className = (String) classPathMap.get(uri);
		if(className != null){
			try {
				clazz = this.getClass().getClassLoader().loadClass(className);
				hs = (HttpServlet)clazz.newInstance();
				classMap.put(uri,hs);
				return hs;
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(),e);
				throw new JspClassNotFound("class ["+className+"] not found!");
			}
		}
		return null;
	}
	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-30 20:23:38
	* 功能描述: 从类资源中加载配置文件
	* 方法的参数和返回值: 
	* @param filePath
	* @return
	*/
	private static void loadClassMappingFile(){
		logger.info("===loading jspMappingFiles......");
		long t = System.currentTimeMillis();
		Enumeration<URL> urls = null;
		try {
			urls = FastResourceProcess.class.getClassLoader().getResources("jspServletMapping.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		URL u = null;
		BufferedReader in = null;
		while(urls!=null && urls.hasMoreElements()){
			u = urls.nextElement();
			try {
				in = new BufferedReader(new InputStreamReader(u.openStream()));
				logger.info("loading "+u.toString());
				readMappingFile(new BufferedReader(new InputStreamReader(u.openStream())),classPathMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("===jspMappingFiles load success! use "+(System.currentTimeMillis()-t)+" ms");
    }
	private static Map<String, String> readMappingFile(BufferedReader reader,Map<String, String> p){
    	if(p==null){
    		p = new HashMap<String, String>();
    	}
    	try {
			String line = null;
			String servletName=null,urlPattern=null;
			String preName = "<servlet-mapping>";
			String servletNamePre = "<servlet-name>",servletNameExt = "</servlet-name>";
			String urlPatternPre = "<url-pattern>",urlPatternExt = "</url-pattern>";
			int servletNameLen = servletNamePre.length();
			int urlPatternlen = urlPatternPre.length();
			int cnt = 0;
			while((line=reader.readLine())!=null){
				if(line.trim().length()==0)continue;
				if(line.indexOf(preName)>=0){
					line = reader.readLine();
					servletName = line.substring(line.indexOf(servletNamePre)+servletNameLen,line.indexOf(servletNameExt));
					line = reader.readLine();
					urlPattern = line.substring(line.indexOf(urlPatternPre)+urlPatternlen,line.indexOf(urlPatternExt));
					if(p.get(urlPattern)==null){
						p.put(urlPattern, servletName);
						cnt ++;
					}
				}
			}
			logger.info("find "+ cnt + " mapping.");
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
    	return p;
    }
}

package com.dx.jwfm.framework.core.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.core.SystemContext;

public class FastResourceProcess implements IFastProcess {

	static Logger logger = Logger.getLogger(FastResourceProcess.class);
	
	public static HashMap<String, String> classPathMap = new HashMap<String, String>();
	
	public static HashMap<String, HttpServlet> classMap = new HashMap<String, HttpServlet>();
	
//	private FastFilter filter;
	/**错误后的转向页面*/
//	private String errorPage;
	
	
	public void init(FastFilter filter) {
//		this.filter = filter;
//		errorPage = FastUtil.nvl(filter.getInitParameter("errorPage"), FastActionProcess.DEFAULT_ERROR_PAGE);
	}
	/**
	 * 处理用户请求，已处理完成返回true，否则返回false,由WEB容器继续处理
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean processRequest(HttpServletRequest request,HttpServletResponse response,String uri,String uriExt){
		String baseDir = SystemContext.getAppPath();
		File f = new File(baseDir+uri);
		if(f.exists()){//如果文件（包含JSP文件）在系统中存在，则不处理，使用系统自动处理
			return false;
		}
		String uril = uri.toLowerCase();
		if(".jsp".equals(uriExt.toLowerCase())){//如果是JSP文件，则判断是否从类中加载
//			processJsp(request,response,uri);
			return false;
		}
		else{
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(uri.substring(1));
			if(in==null){//如果不存在指定路径的资源，则交由系统处理
				return false;
			}
			try {
				if(uril.endsWith(".js")){
					response.setHeader("content-type", "application/x-javascript");
				}
				else if(uril.endsWith(".css")){
					response.setHeader("content-type", "text/css");
				}
				ServletOutputStream out = response.getOutputStream();
				byte[] buff = new byte[8192];
				int len = 0;
				while((len=in.read(buff))!=-1){
					out.write(buff,0,len);
				}
				in.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
		return true;
	}
}

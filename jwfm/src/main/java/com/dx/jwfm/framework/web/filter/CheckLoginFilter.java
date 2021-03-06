package com.dx.jwfm.framework.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.util.EncryptUtil;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.util.LinkedQueueSet;

public class CheckLoginFilter implements Filter {

	protected static Logger logger = Logger.getLogger(CheckLoginFilter.class);

	/** 用户请求字符集设置 */
	private String charset;
	/** 用户信息在Session中保存的key值 */
	private static String userSessionKey;
	/** 用户登录界面的URL */
	private String loginUrl;
	/** 根据用户ID创建Session中的user对象 */
	private LoginUserCreator loginUserCreator;
	
	private LinkedQueueSet ssoKeys = new LinkedQueueSet(1000);
	
	private List<String> excludeURI = new ArrayList<String>();
	private List<String> excludeURIStartWith = new ArrayList<String>();
	private List<Pattern> excludeURIReg = new ArrayList<Pattern>();

	public void init(FilterConfig conf) throws ServletException {
		charset = FastUtil.nvl(conf.getInitParameter("charset"),"UTF-8");
		userSessionKey = FastUtil.nvl(conf.getInitParameter("userSessionKey"),"SESSION_USER");
		loginUrl = FastUtil.nvl(conf.getInitParameter("loginUrl"),conf.getServletContext().getContextPath());
		String loginUserCreatorClsName = conf.getInitParameter("loginUserCreator");
		if(FastUtil.isBlank(loginUserCreatorClsName)){
			throw new ServletException("please config the parameter loginUserCreator!");
		}
		try {
			loginUserCreator = (LoginUserCreator) FastUtil.newInstance(loginUserCreatorClsName);
		} catch (Exception e) {
			throw new ServletException("error at create instance of "+loginUserCreatorClsName,e);
		}
		String exclude = conf.getInitParameter("excludeURI");
		if(FastUtil.isNotBlank(exclude)){
			String[] ary = exclude.split(",");
			for(String str:ary){
				if(FastUtil.isBlank(str)){
					continue;
				}
				str = str.trim();
				if(str.startsWith("REG:")){
					excludeURIReg.add(Pattern.compile(str.substring(4)));
				}
				else{
					if(str.endsWith("*")){
						excludeURIStartWith.add(str.substring(0, str.length()-1));
					}
					else{
						excludeURI.add(str);
					}
				}
			}
		}
		new Thread(){
			public void run() {
				this.setName("clearSsoInfoData");
				while(true){//每小时执行一次
					try {
						Thread.sleep(60*6000);
						DbHelper db = new DbHelper();
						Calendar c = Calendar.getInstance();
						c.add(Calendar.DAY_OF_MONTH, -1);
						db.executeSqlUpdate("delete from "+SystemContext.dbObjectPrefix+"T_SSO_INFO where dt_add<?",new Object[]{c.getTime()});
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
		}.start();
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		request.setCharacterEncoding(charset);
		response.setCharacterEncoding(charset);
        String url = request.getRequestURI();
        String path = SystemContext.getPath();
        if(path.length()>0){
        	url = url.substring(path.length());
        }
        if(isExclude(url)){
    		arg2.doFilter(arg0, response);
    		return;
        }
        Object user = request.getSession().getAttribute(userSessionKey);
        String sessionSsoId = (String)request.getSession().getAttribute("SSO_ID");
        String ssoId = null;
        String ssoKey = null;
        if(user!=null && (ssoId=getParamValue(request,"SSO_ID"))!=null && !ssoId.equals(sessionSsoId)){//如果请求的SSO_ID与Session中的SSO_ID不一致，此时表示系统已切换了登录用户，要将原用户退出重新登录
        	removeSession(request.getSession());
        	user = null;
        }
        if(user==null && (ssoId=getParamValue(request,"SSO_ID"))!=null){//如果用户未登录，且有SSO_ID，此时尝试模拟登录
        	String userId = getUserId(ssoId);
        	if(FastUtil.isNotBlank(userId)){//如果查找到用户ID，模拟登录
        		user = loginUserCreator.createUser(userId);
            	setSessionUser(request, response, user, userId, ssoId);
        	}
        }
        if(user==null && (ssoKey=getParamValue(request,"SSO_KEY"))!=null){//如果用户未登录，且有SSO_KEY，此时尝试模拟登录
        	if(!ssoKeys.contains(ssoKey)){//如果SSO_KEY值已经被使用过，则不能再次被使用
        		String userId = getUserIdBySsoKey(ssoKey);
            	if(FastUtil.isNotBlank(userId)){//如果查找到用户ID，模拟登录
            		user = loginUserCreator.createUser(userId);
                	setSessionUser(request, response, user, userId, ssoId);
            	}
        	}
        }
        if(user==null){
    		try {
    			response.setContentType("text/html; charset=UTF-8"); // 设置 content-type
    			response.setCharacterEncoding("UTF-8");  // 设置响应数据编码格式 (输出)
    			PrintWriter out = response.getWriter();
    			out.print("您尚未登录系统或长期未操作导致Session失效，请<a href='"+loginUrl+"'>重新登录</a><script>try{top.openLoginDialog();}catch(e){;}</script>");
    		} catch (IOException e) {
    			logger.error(e.getMessage(),e);
    		}
    		return;
        }
		arg2.doFilter(arg0, response);
	}

	private String getUserIdBySsoKey(String ssoKey) throws UnsupportedEncodingException {
    	String str = EncryptUtil.decryptDes(ssoKey, FastUtil.nvl(FastUtil.getRegVal("SYSTEM_SSO_KEY"), SystemContext.getSysParam("SYSTEM_SSO_KEY","dx123~!@")));
    	int pos = 0;
    	if(str!=null && (pos=str.indexOf("|"))>0){
    		int pos2 = str.indexOf("|",pos+1);
    		String timekey = str.substring(0, pos);
    		String timestr = str.substring(pos+1, pos2);
    		String userIdDes = str.substring(pos2+1);
    		long l = 0;
    		try {
				l = Long.valueOf(timestr);
			} catch (NumberFormatException e) {
				logger.error(e.getMessage(),e);
			}
    		if(Math.abs(System.currentTimeMillis()-l)>30*60000){//时间误差控制在30分钟之内，如果超出时间，则认为是超时无效key
    			return null;
    		}
			String userId = EncryptUtil.decryptDes(userIdDes, timekey);
			if("defaultUser".equals(userId)){
				userId = FastUtil.nvl(FastUtil.getRegVal("SYSTEM_SSO_DEFAULT_USER"), SystemContext.getSysParam("SYSTEM_SSO_DEFAULT_USER"));
			}
			return userId;
    	}
    	return null;
	}

	static public void setSessionUser(HttpServletRequest request, HttpServletResponse response, Object user, String userId, String ssoId) {
		removeSession(request.getSession());
		request.getSession().setAttribute(userSessionKey, user);
		request.getSession().setAttribute("curUserId", userId);
		request.getSession().setAttribute("curUserName", FastUtil.getProptValue(user, "VC_NAME"));

		if(FastUtil.isBlank(ssoId)){
			ssoId = FastUtil.getUuid();
			DbHelper db = new DbHelper();
			try {
				db.executeSqlUpdate("insert into "+SystemContext.dbObjectPrefix+"T_SSO_INFO(sso_id,vc_user_id,dt_add) values(?,?,?)",
						new Object[]{ssoId,userId,new Date()});
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
		request.getSession().setAttribute("SSO_ID", ssoId);
		setCookie(response, "SSO_ID", ssoId, 1, "/");
	}

	private String getUserId(String ssoId) {
		DbHelper db = new DbHelper();
		String userId = null;
		try {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -1);
			userId = db.getFirstStringSqlQuery("select vc_user_id from "+SystemContext.dbObjectPrefix+
					"T_SSO_INFO where dt_add>? and sso_id=?",new Object[]{c.getTime(),ssoId});
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		if(FastUtil.isBlank(userId)){//如果本地未获取到USERID，尝试从远程服务器上获取
			String ssoRemoteUrl = FastUtil.getRegVal("SYS_SSOID_REMOTE_URL");
			if(FastUtil.isNotBlank(ssoRemoteUrl)){
				try {
					userId = FastUtil.getUrlContent(ssoRemoteUrl+ssoId);
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return userId;
	}

	private static String getParamValue(HttpServletRequest request, String name){
		if(name==null || name.length()==0){
			return null;
		}
		String ssoid = null;
		if((ssoid=request.getParameter(name))!=null){
			return ssoid;
		}
    	if(request.getCookies()!=null){
        	for(Cookie c:request.getCookies()){
        		if(name.equals(c.getName())){//使用COOKIE中的UUID登录
        			return c.getValue();
        		}
        	}
    	}
    	return null;
    }

	@SuppressWarnings("unchecked")
	private static void removeSession(HttpSession session){
		//退出后清空其他session信息，否则换用户登录后，由于不关闭浏览器，session还是同一个，有些值会仍取上一个帐号的信息
		try {
			Enumeration<String> s=session.getAttributeNames();
			while(s.hasMoreElements()){
				   String elementName = s.nextElement();
//			   System.out.println("====remove====="+elementName+":"+session.getAttribute(elementName));
				   session.setAttribute(elementName,null);
			}
		} catch (Exception e) {
		}
	}
	protected static void setCookie(HttpServletResponse response,String name,String value,int expire,String path){
		try {
			value = URLEncoder.encode(value,"UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		Cookie c = new Cookie(name,value);
		c.setPath(path);
		c.setMaxAge(expire*24*60*60);
		response.addCookie(c);
	}
	private boolean isExclude(String url) {
        for(String str:excludeURI){
        	if(str.equals(url)){
        		return true;
        	}
        }
        for(String str:excludeURIStartWith){
        	if(url.startsWith(str)){
        		return true;
        	}
        }
        for(Pattern pat:excludeURIReg){
        	if(pat.matcher(url).matches()){
        		return true;
        	}
        }
		return false;
	}

	public void destroy() {
		
	}

}

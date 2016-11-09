package com.dx.jwfm.framework.core.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastColumnType;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.util.Uuid;


public abstract class AbstractCheckLoginProcess implements IFastProcess
{
	protected String path;
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private static long lastUpdate;//上次更新数据库中SESSION_DATE的时间，值取自System.currentTimeMillis()

	private static long updateInteval = 60*60*1000;//更新数据库中SESSION_DATE的时间的频率，单位为毫秒
	
	protected List<String> logUrlExt = new ArrayList<String>();//哪些扩展后缀打印URL日志
	protected List<String> excludeStr = new ArrayList<String>();//哪些页面在未登录状态下不跳转到登录界面
	protected List<Pattern> excludePat = new ArrayList<Pattern>();
	
    public void init(FastFilter filter) {
    	String urls = filter.getInitParameter("checkLoginExclude");
    	if(urls!=null && urls.length()>0){//将排除URL解析并放到相应的列表中
    		String[] ary = urls.split("\\s*,\\s*");
    		for(String str:ary){
    			if(str.length()==0){
    				continue;
    			}
    			if(str.startsWith("REG:")){//正则表达式
    				excludePat.add(Pattern.compile(str.substring("REG:".length())));
    			}
    			else{//一般匹配
    				excludeStr.add(str);
    			}
    		}
    	}
    	String[] ary = FastUtil.nvl(filter.getInitParameter("logUrlExt"),".jsp,.action").split(",");
    	for(String s:ary){
    		logUrlExt.add(s);
    	}
    	//清除一天之前的SESSION数据
    	DbHelper db = new DbHelper();
    	try {
			if(!db.getDatabaseDialect().isTableExist(SystemContext.dbObjectPrefix+"T_SESSION_INFO")){
				db.executeSqlUpdate(getSessionTable().getObjectCreateSql(db.getDatabaseDialect()));
			}
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -1);
			db.executeSqlUpdate("delete from "+SystemContext.dbObjectPrefix+"T_SESSION_INFO where DT_LOGIN<?",new Object[]{c.getTime()});
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
    }

	public boolean processRequest(HttpServletRequest request,HttpServletResponse response) {
        if(path==null){
        	path = request.getContextPath();
        }
        String pagePath = getPagePath(request);
        String url = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        logUrl(url, queryString);
        String userId = getLoginUserId(request);
        String ssoid = getSsoid(request);
        initSessionId(request, response);
        if(userId!=null && ssoid==null){//用户登录后，写入SSO_ID到cookie中
        	DbHelper db = new DbHelper();
        	ssoid = Uuid.getUuid();
		    String sql = "insert into "+SystemContext.dbObjectPrefix+"T_SESSION_INFO(sso_id,user_id,dt_login) values(?,?,?)";
			try {
				db.executeSqlUpdate(sql ,new Object[]{ssoid,userId,new Date()});
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
			setCookie(response, "SSO_ID", ssoid, 0, "/");
        }
        if(userId==null){
        	userId = getLoginUserId(request);
        }
    	if(userId==null){
			setCookie(response, "SSO_ID", "", 0, "/");
            if(isExclude(request.getRequestURI())){//如果在排除URL范围内，则不进行转向
            	return false;
            }
    		request.getSession().setAttribute("loginToPage", pagePath);
			String loginPage = getLoginUrl();
			if(FastUtil.isNotBlank(loginPage)){//跳转到登录页面
				writeHTML(response,"<script>location.href='"+loginPage+"';</script>");
				return true;
			}
    		writeHTML(response,"<h1 style='margin:80px auto;'>登录信息已过期，请重新登录！</h1>");//提示重新登录
            return true;
    	}
    	return false;
    }

	private void logUrl(String url, String queryString) {
		for(String str:logUrlExt){
			if(url.endsWith(str)){
				logger.info(url+(queryString!=null?"?"+queryString:""));
				return;
			}
		}
	}

    /**
     * 根据指定用户的ID模拟用户登录，写入SESSION登录信息
     * @param usrId
     */
    abstract protected boolean setUserLogin(String usrId);

    /**
     * 模拟用户退出登录的操作
     * @param session
     */
    abstract protected void removeUserSession(HttpSession session);

	/**
	 * 获得已登录用户的ID
	 * @param request
	 * @return
	 */
	abstract protected String getLoginUserId(HttpServletRequest request);
	
    /**
     * 获得登录页面的地址
     * @return
     */
    abstract protected String getLoginUrl();

	private static FastTable getSessionTable() {
		FastTable tbl = new FastTable();
		tbl.setName("用户登录SESSION信息表");
		tbl.setCode(SystemContext.dbObjectPrefix+"T_SESSION_INFO");
		tbl.getColumns().add(new FastColumn("SSO_ID", "SSO_ID", null, FastColumnType.String, 50, "", null, false, false));
		tbl.getColumns().add(new FastColumn("用户ID", "USER_ID", null, FastColumnType.String, 50, "", null, false, false));
		tbl.getColumns().add(new FastColumn("登录时间", "DT_LOGIN", null, FastColumnType.Date, 50, "", null, true, false));
		return tbl;
	}

    private boolean isExclude(String uri){
    	for(String str:excludeStr){
    		if(str.equals(uri)){
    			return true;
    		}
    	}
    	for(Pattern pat:excludePat){
    		if(pat.matcher(uri).matches()){
    			return true;
    		}
    	}
    	return false;
    }
    
    public void initSessionId(HttpServletRequest request,HttpServletResponse response) {
        String ssoid = getSsoid(request);
        if(FastUtil.isNotBlank(ssoid)){
        	setCookie(response,"SSO_ID",ssoid, 0, "/");
        }
    	DbHelper db = new DbHelper();
        String userId = getLoginUserId(request);
        //如果COOKIE中的SSO_ID与SESSION中的SSO_ID不一致，说明用户退出后又重新登录了，此时清空SESSION
        String sessoid = (String) request.getSession().getAttribute("SSO_ID");
        if(ssoid!=null&&sessoid!=null&&userId!=null&&!ssoid.equals(sessoid)){
			System.out.println(path+"\tSESSION_REMOVED:"+request.getSession().getAttribute("SSO_ID"));
        	removeUserSession(request.getSession());
        	userId = null;
        }
        if(userId == null && ssoid!=null){
			System.out.println(path+"\tSSO_ID:"+ssoid);
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -1);
			String usrId = null;
			try {
				usrId = db.getFirstStringSqlQuery("select user_id from "+SystemContext.dbObjectPrefix+"T_SESSION_INFO where sso_id=? and dt_login>?",
						new Object[]{ssoid,c.getTime()});
			} catch (SQLException e1) {
				logger.error(e1.getMessage(),e1);
			}
			if(usrId==null){//如果从本地库中未找到用户登录信息，则从远程库中查找
				String checkUrl = FastUtil.getRegVal("SYSTEM_REMOTE_CHECK_URL");
				if(FastUtil.isNotBlank(checkUrl)){
					try {
						usrId = getUrlContent(checkUrl+"?ssoid="+ssoid);
					} catch (Exception e) {
//						LogUtil.logError(e);
					}
					if(FastUtil.isNotBlank(usrId)){//如果从远程查询到用户ID,则记录到本地库中
						setUserLogin(usrId);
						if(usrId!=null && usrId.length()>0){
						    String sql = "insert into "+SystemContext.dbObjectPrefix+"T_SESSION_INFO(sso_id,user_id,dt_login) values(?,?,?)";
							try {
								db.executeSqlUpdate(sql ,new Object[]{ssoid,usrId,new Date()});
							} catch (SQLException e) {
								logger.error(e.getMessage(),e);
							}
						}
					}
				}
			}
			if(FastUtil.isNotBlank(usrId)){
				setUserLogin(usrId);
				try {
					db.executeSqlUpdate("update "+SystemContext.dbObjectPrefix+"T_SESSION_INFO set dt_login=? where sso_id=?" ,new Object[]{new Date(),ssoid});
				} catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
    			request.getSession().setAttribute("SSO_ID", ssoid);
			}
        }
    	if(System.currentTimeMillis()-lastUpdate>updateInteval){//每隔一小时，更新一次数据库的SESSION日期，防止用户未退出登录，但其它WAR包提示未登录的情况
    		lastUpdate = System.currentTimeMillis();
			try {
				db.executeSqlUpdate("update "+SystemContext.dbObjectPrefix+"T_SESSION_INFO set dt_login=? where sso_id=?" ,new Object[]{new Date(),ssoid});
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
    	}
	}
    
	private String getSsoid(HttpServletRequest request){
		if(request.getParameter("SSO_ID")!=null){
			removeUserSession(request.getSession());
			return request.getParameter("SSO_ID");
		}
    	if(request.getCookies()!=null){
        	for(Cookie c:request.getCookies()){
        		if("SSO_ID".equals(c.getName())){//使用COOKIE中的UUID登录
        			return "".equals(c.getValue())?null:c.getValue();
        		}
        	}
    	}
    	return null;
    }

    private String getPagePath(HttpServletRequest request) {
		StringBuffer buff = new StringBuffer(request.getRequestURL().toString());
		@SuppressWarnings("unchecked")
		Enumeration<String> en = request.getParameterNames();
		String pname = null,pvalue = null;
		while(en.hasMoreElements()){
			pname = en.nextElement();
			pvalue = request.getParameter(pname);
			buff.append(pvalue.indexOf("?")>0?"&":"?").append(pname).append("=").append(pvalue);
		}
		return buff.toString();
	}

	/**
	 * 写COOKIE值
	 * @param name		名称
	 * @param value		COOKIE值
	 * @param expire	有效期，单位为天
	 * @param path		绑定路径
	 */
	protected static void setCookie(HttpServletResponse response,String name,String value,int expire,String path){
		try {
			value = URLEncoder.encode(value,"UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		Cookie c = new Cookie(name,value);
		c.setPath(path);
		if(expire>0){
			c.setMaxAge(expire*24*60*60);
		}
		response.addCookie(c);
	}

	/**
	 * 读取COOKIE的值
	 * @param name
	 * @return
	 */
	protected static String getCookie(HttpServletRequest request,String name){
		if(request==null||name==null||request.getCookies()==null){
			return null;
		}
		for(Cookie c:request.getCookies()){
			if(name.equals(c.getName())){
				try {
					return URLDecoder.decode(c.getValue(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					return c.getValue();
				}
			}
		}
		return null;
	}

	private static String sessionid="";
	public static String getUrlContent(String testUrl) throws MalformedURLException, IOException {
		System.out.println("LOAD_URL:"+testUrl);
		URL url = new java.net.URL(testUrl);
		HttpURLConnection con = (HttpURLConnection)url.openConnection(); 
		con.setRequestProperty("User-Agent","Mozilla/4.0");
		con.setRequestProperty("Cookie", sessionid);
		con.setConnectTimeout(3000);
		con.setReadTimeout(1000);
		InputStream in = con.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line = null;
		StringBuffer buff = new StringBuffer();
		while((line = reader.readLine())!=null){
			buff.append(line+"\n");
		}
		reader.close();
		String sid = con.getHeaderField("Set-Cookie");
		if(sid!=null){
			sid = sid.split(";")[0];
		}
		if(sid!=null&&!sid.equals(sessionid)){
			sessionid = sid;
		}
		con.disconnect();
		return buff.toString().trim();
	}
	protected String writeHTML(HttpServletResponse response,String html){
		try {
			response.setContentType("text/html; charset=UTF-8"); // 设置 content-type
			response.setCharacterEncoding("UTF-8");  // 设置响应数据编码格式 (输出)
			PrintWriter out = response.getWriter();
			out.print(html);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	public void destroy() {
        
        
    }

}

package com.dx.jwfm.framework.web.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.util.MD5;

public class FastMainAction extends FastBaseAction {

	public String execute(){
		HttpServletRequest request = RequestContext.getRequest();
		if(request.getSession().getAttribute("FAST_USER")==null){
			initAdminUser();
			return "login";
		}
		else{
			return main();
		}
	}
	
	public String main(){
		return "success";
	}
	
	private String adminUser = "admin",adminPwd = "admin";
	private void initAdminUser() {
		DbHelper db = new DbHelper();
		try {
			int cnt = db.getFirstIntSqlQuery("select count(*) from "+SystemContext.dbObjectPrefix+"T_USER");
			if(cnt==0){
				RequestContext.setRequestAttr("SYSTEM_FIRST_LOGIN",true);
				RequestContext.setRequestAttr("SYSTEM_USERNAME",adminUser);
				RequestContext.setRequestAttr("SYSTEM_PASSWORD",adminPwd);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
	}

	private String username,password;
	public String login(){
		DbHelper db = new DbHelper();
		//如果使用初始用户名和密码登录时，判断用户表中是否存在记录，如果不存在，初始化管理员账号密码
		if(adminUser.equals(username) && adminPwd.equals(password)){
			try {
				int cnt = db.getFirstIntSqlQuery("select count(*) from "+SystemContext.dbObjectPrefix+"T_USER");
				if(cnt==0){
					FastPo po = FastPo.getPo(SystemContext.dbObjectPrefix+"T_USER");
					po.put("VC_NAME", adminUser);
					po.put("VC_PWD", MD5.toMd5String(adminPwd));
					po.put("n_level", 0);
					po.put("dt_add", new Date());
					db.addPo(po);
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
		try {
			FastPo po = FastPo.getPo(SystemContext.dbObjectPrefix+"T_USER");
			List<FastPo> list = db.executeSqlQuery("select * from "+SystemContext.dbObjectPrefix+"T_USER where vc_name=?",
					po,new Object[]{username});
			FastPo user = null;
			if(list.size()>0){
				user = list.get(0);
				if(MD5.toMd5String(password).equals(user.getString("VC_PWD"))){//密码使用MD5加密
					RequestContext.getRequest().getSession().setAttribute("FAST_USER", user);
					return writeResult("ok", null);
				}
				else{
					return writeResult("failure", "您的用户名或密码不正确！");
				}
			}
			else{
				return writeResult("failure", "您的用户名不存在！");
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			return writeResult("error", FastUtil.getExceptionInfo(e));
		}
	}
	public String logout(){
		RequestContext.getRequest().getSession().removeAttribute("FAST_USER");
		return "loginsuccess";
	}
	
	private String[] test;

	public String[] getTest() {
		return test;
	}

	public void setTest(String[] test) {
		this.test = test;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}

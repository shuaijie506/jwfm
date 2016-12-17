﻿<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="com.dx.jwfm.framework.core.SystemContext"%>
<%@page import="java.io.File"%>
<%@page import="com.dx.jwfm.framework.core.contants.RequestContants"%>
<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String bpath = (String)request.getAttribute(RequestContants.REQUEST_URI_PRE);
String method = (String)request.getAttribute(RequestContants.REQUEST_URI_METHOD);
String actionExt = (String)request.getAttribute(RequestContants.REQUEST_URI_ACTIONEXT);
String addActionName = bpath+"_"+method+actionExt;
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/fast-tags" prefix="f" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<title>${REQUEST_FAST_MODEL.vcName }</title>
   <meta http-equiv="X-UA-Compatible" content="chrome=1">
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<jsp:include page="/common/common.jsp"></jsp:include>
<style type="text/css">
nobr{display:inline-block;width:150px;margin:2px 0px 2px 5px;height:22px;font-size:14px;}
i.fa{display:inline-block;width:20px;height:20px;font-size:18px;}
</style>
</HEAD>
<BODY >
<%
File f = new File(SystemContext.getAppPath(),"js/bootstrap/css/font-awesome.css");
//out.println(f.getAbsolutePath());
BufferedReader in = new BufferedReader(new FileReader(f));
String line = null;
while((line=in.readLine())!=null){
	int pos = line.indexOf(".fa-")+1;
	int pos2 = line.indexOf(":before");
	if(pos>0 && pos2>0){
		out.println("<nobr><i class='fa "+line.substring(pos,pos2)+"'></i>"+line.substring(pos,pos2)+"</nobr>");
	}
}
%>
</BODY>
</HTML>
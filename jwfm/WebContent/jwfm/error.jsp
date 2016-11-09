﻿<%@ page language="java" import="java.util.*,java.io.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = path + request.getRequestURI();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<title>出错啦！</title>
   <meta http-equiv="X-UA-Compatible" content="chrome=1">
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<jsp:include page="/common/common.jsp"></jsp:include>
<style type="text/css">
pre{margin:4px 50px;color:red;}
</style>
</HEAD>
<BODY >
<pre>
出错啦！，详细错误信息如下：
<%
Exception e = (Exception)request.getAttribute("exception");
e.printStackTrace(new PrintWriter(out));
%>
</pre>
</BODY>
</HTML>

﻿<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = path + request.getRequestURI();
%>
<!DOCTYPE HTML >
<HTML><HEAD>
<title>辉煌WEB程序快速开发平台</title>
   <meta http-equiv="X-UA-Compatible" content="chrome=1">
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<jsp:include page="/common/common.jsp"></jsp:include>
<SCRIPT type=text/javascript>
var isAlertShow = false;
    	$(function(){
    	    //点击提交按钮事件
            var treedata = [{id:'1',text:'功能列表',attributes:{url:'../core/menu.action'}},
                            {id:'2',text:'用户管理',attributes:{url:'../core/user.action'}},
                            {id:'test',text:'ACTION测试',attributes:{url:'../../mis/sys/HelloWorld_toJsp.action'}},
                            {id:'test',text:'Awesome图标',attributes:{url:'../core/faicon.jsp'}}
            ];
    	    $('#leftTree').tree({data:treedata,onClick:function(node){
    	    	$('#main').attr('src',node.attributes.url);
    	    }});
    	    $(window).resize(function(){
			    $('#mainDiv').height($(window).height());
			    $('#mainDiv').layout('resize');
    	    }).resize();
    	});
    </SCRIPT>
<style type="text/css">
html,body{overflow:hidden;height:100%;}
#titleDiv{background:rgb(79,129,189);color:white;display:block;}
#titleDiv a{color:white;}
#titleDiv .title{font-size:30px;padding:2px 14px;font-family:微软雅黑 黑体;font-weight:bold;text-shadow:3px 3px 3px #000;}
#contentDiv{overflow:hidden;}
</style>
</HEAD>

<BODY >
<div id="mainDiv" class="easyui-layout" style="height:100%;">
<div id="titleDiv" region="north" style="height:40px;position:relative;overflow:hidden;">
<span class=title >
辉煌WEB程序快速开发平台</span>
<span style="position:absolute;right:65px;bottom:3px;">当前登录：${FAST_USER.VC_NAME}</span>
<a style="position:absolute;right:25px;bottom:3px;" href="?op=logout">退出</a>
</div>
<div id="leftTree" region="west" title="菜单栏" split="true" style="width:140px;">
</div>
<div id="contentDiv" region="center">
<iframe name="main" id="main" frameborder="0" border="0" style="width:100%;height:100%;"></iframe>
</div>
</div>
</BODY>
</HTML>

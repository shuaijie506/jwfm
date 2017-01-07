﻿<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = path + request.getRequestURI();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<title>请登录Fast开发平台</title>
   <meta http-equiv="X-UA-Compatible" content="chrome=1">
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<jsp:include page="/common/common.jsp"></jsp:include>
<SCRIPT type=text/javascript>
    	$(function(){
    		if($.cookie('lastusername')){
    			$('#username').val($.cookie('lastusername'));
    		}
    	    //点击提交按钮事件
            $('#loginBtn').click(function(){
            	if($('#username').val()==''){
            		$('#username').focus();
            		return false;
            	}
            	if($('#password').val()==''){
            		$('#password').focus();
            		return false;
            	}
            	$.cookie('lastusername',$('#username').val());
            	$('form').submit();
            });
    	    $('form').form({url:'${REQUEST_URI_PRE}_login.action',
    	    		onSubmit: function(){
    		        	if($(this).form('validate')){
    		            	return true;
    		        	}else{
    		            	return false;
    		        	}
    		        },
    		        success:function(data){
    		    	    try{
    		    	        json = jQuery.parseJSON(data);  //获取从服务器得到的数据反馈信息
    		    	        if(json.result=="ok"){    //操作成功处理   
    		    	            $.util.showTip({content:'认证成功！'});
    		        			location.href = 'main.action';
    		    	        }else if(json.opState=="error"){//操作时发生错误
    		        			$('#loginError').text('登录时发生服务器错误：\n'+json.info);
    		    	        }
    		    	        else{//操作失败处理
    		        			$('#loginError').text(json.info);
    		    	        }
    		    	    }catch(e){
		        			$('#loginError').text('返回信息错误'+e+'\n'+data);
    		    	    }
    		        }
    	    });
    	    $('body').keyup(function(){
    	    	if($.event.fix(event).keyCode==13){
        	    	$('#loginBtn').click();
    	    	}
    	    });
    	    $(window).resize(function(){
    	    	$('.login').css('margin-top',parseInt(Math.max(($('body').height()-$('.login').height())*2/5,0))+'px');
    	    }).resize();
    	    <%if(request.getAttribute("SYSTEM_FIRST_LOGIN")!=null){//系统初始化后第一次登录 %>
	    		$('#username').val('<%=request.getAttribute("SYSTEM_USERNAME")%>');
	    		$('#password').val('<%=request.getAttribute("SYSTEM_PASSWORD")%>');
		    <%}%>
    	    <%if(request.getAttribute("LOGIN_ERROR")!=null){//登录不成功！ %>
	    		$('#loginError').text('<%=request.getAttribute("LOGIN_ERROR")%>');
    	    <%}%>
    	});
    </SCRIPT>
<style type="text/css">
html,body{background:#ccc;height:100%;overflow:hidden;}
.login{margin:40px auto;width:650px;height:421px;background:url(../images/login.jpg);position:relative;}
.login input{border:0px;padding:0px;height:15px;width:127px;background:transparent;}
#username{position:absolute;left:245px;top:163px;}
#password{position:absolute;left:245px;top:204px;}
#loginBtn{position:absolute;left:439px;top:160px;width:84px;height:84px;cursor:pointer;}
#loginError{position:absolute;left:221px;top:239px;width:150px;height:40px;color:red;}
.copyright{position:absolute;left:80px;top:312px;width:460px;height:30px;text-align:center;}
</style>
</HEAD>

<BODY >
<form action="" method=post>
<input type=hidden name=op value="login"/>
<center>
<div class=login >
<input type=text name=username id=username class="easyui-validatebox" data-options="required:true" />
<input type=password name=password id=password class="easyui-validatebox" data-options="required:true" />
<div title=点击登录 id=loginBtn></div>
<div id=loginError></div>
<div class=copyright><%=FastUtil.nvl(FastUtil.getRegVal("SYSTEM_COPYRIGHT"),"") %></div>
</div>
</center>
</form>
</BODY>
</HTML>

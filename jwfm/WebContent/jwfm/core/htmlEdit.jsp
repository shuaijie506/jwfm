﻿<%@page import="com.dx.jwfm.framework.core.contants.RequestContants"%>
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
<SCRIPT type=text/javascript>
	$(function(){
		//对字段进行非空验证和输入类型长度等限制
		initValid('#editFormTable');
		//点击添加按钮操作
	    $('#addBtn').click(function(){
			$('#editForm').submit();
		});
		//表单验证及提交处理操作
		$('#editForm').form({
			url:'<%=addActionName%>',
			onSubmit : function() {
				$(this).trigger('beforeSubmit');
				if(!$(this).data('stopSubmit') && $(this).form('validate')) {
					$.util.showLoading();
					$('#operateWindow').window('close');
					return true;
				} else {
					return false;
				}
			},
			success : function(data) {
				$.util.removeLoading();
				returnOptMsgEasyui(data);
			},
			onLoadError:function(){
				$.util.removeLoading();
				$.messager.alert('提示','操作失败!');
			}
		});
	});
</SCRIPT>
<form id="editForm" method="post">
<input type=hidden name=op value="save" >
<f:edittable class="fast-edit-table" />
</form>
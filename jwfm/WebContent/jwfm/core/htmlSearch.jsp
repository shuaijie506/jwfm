﻿﻿<%@page import="com.dx.jwfm.framework.core.contants.RequestContants"%>
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
<script type="text/javascript" src="<%=path %>/jwfm/js/jquery-fast-util-easyui.js"></script>  <!-- easyui通用工具类 -->
<SCRIPT type=text/javascript>
    	$(function(){
    		var fcols = <f:result type="easyui" frozen="true" hasChkCol="true"/>;
    		var cols = <f:result type="easyui" frozen="false"/>;
    		$('#searchTable').datagrid({
				height:$('.srhResultTd').height(),
				
				//title:'<div style="text-align:center;font-size:17">${REQUEST_FAST_MODEL.vcName }</div>',
				url:'${REQUEST_URI}?op=searchDataAjax&datagrid_type=easyui',
				queryParams:$.fn.datagrid.dealParam($('#searchDiv').formdata(),fcols,cols),  //用于查询的参数以及初始值
				//表头列，不建议改动
				frozenColumns:fcols,
				//数据列，根据显示的内容进行修改
				columns:cols,
				//功能按钮组
				toolbar:<f:toolbar type="easyui"/>,
				//分页信息
				pagination:true
			});
			//查询操作
			$('#search').click(function(){
			    $('#searchTable').datagrid('getPager').pageNumber=1;
				$('#searchTable').datagrid('getPager').pagination("options").pageNumber = 1;
				$('#searchTable').datagrid('reload',$.fn.datagrid.dealParam($('#searchDiv').formdata(),fcols,cols));
            });
    	});
    	function doSearch(){
    		$('#search').click();
    	}
    	function openAddPage(){
    		$.openWin({title:'添加数据',href:'<%=bpath%>_add<%=actionExt%>',width:600,height:400,
    			butParams:[{id:'addBtn',text:'保存',iconCls:'icon-save'}]});
    	}
    	function openModifyPage(){
    		var ids = getSelIds();					
    		if (ids.length==0){
 		       $.messager.alert('警告', '您选择要修改的记录！','warning');return;
    		}
    		else if (ids.length>1){
 		       $.messager.alert('警告', '您选择了 '+ids.length+' 条记录，请选择单条记录进行修改！','warning');return;
    		}
    		$.openWin({title:'修改数据',href:'<%=bpath%>_modify<%=actionExt%>?chkSelf='+ids[0],width:600,height:400,
    			butParams:[{id:'addBtn',text:'保存',iconCls:'icon-save'}]});
    	}
    	function deleteItems(){
    		
    	}
    </SCRIPT>
<style type="text/css">
html,body{overflow:hidden;}
</style>
</HEAD>
<BODY >111
<table class="searchTbl" fit="true" border="0" cellpadding="0" cellspacing="0" align="left" width="100%" height="100%">
    <!-- 查询条件行 --> 
   <tr height="20px"><td>
   <div id="searchDiv" class="searchDiv"><div class="searchDivContent">
<f:searchitem />
     </div></div>
   </td><td nowrap="nowrap"><a id="search" href="javascript:void(0)"  class="easyui-linkbutton" iconCls="icon-search">查询</a></td></tr>
   <!-- 数据结果列表 -->
   <tr><td colspan="2" class="srhResultTd"><f:result type="html"/></td></tr>
</table>
</BODY>
</HTML>
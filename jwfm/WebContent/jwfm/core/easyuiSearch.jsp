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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<title>${REQUEST_FAST_MODEL.vcName }</title>
   <meta http-equiv="X-UA-Compatible" content="chrome=1">
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<jsp:include page="/common/common.jsp"></jsp:include>
<script type="text/javascript" src="<%=path %>/jwfm/js/jquery-fast-util-easyui.js"></script>  <!-- easyui通用工具类 -->
${REQUEST_FAST_MODEL.modelStructure.search.headHTML }
<SCRIPT type=text/javascript>
    	$(function(){
    		var fcols = <f:result type="easyui" frozen="true" hasChkCol="true"/>;
    		var cols = <f:result type="easyui" frozen="false"/>;
    		$('#searchGrid').datagrid({
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
				$('#searchGrid').datagrid('load',$.fn.datagrid.dealParam($('#searchDiv').formdata(),fcols,cols));
            });
			$('.searchItem>:text').bind('keyup type',function(){
				if($.event.fix(event).keyCode==13){//输入后回车执行查询
					$('#search').click();
				}
			});
    	});
    	function doSearch(){
    		$('#search').click();
    	}
    	function getWinOption(){
    		var str = '${winOption}';
    		if(str){
    			try{return $.parseJSON(str);}catch(e){;}
    		}
    		return {};
    	}
    	function addItem(){
    		$.openWin($.extend({title:'添加数据',href:'<%=bpath%>_add<%=actionExt%>1',width:600,height:400,
    			butParams:[{id:'addBtn',text:'保存',iconCls:'icon-save'}]},getWinOption()));
    	}
    	function modifyItem(){
    		var rows = $('#searchGrid').datagrid('getSelections');					
    		if (rows.length==0){
 		       $.messager.alert('警告', '您选择要修改的记录！','warning');return;
    		}
    		else if (rows.length>1){
 		       $.messager.alert('警告', '您选择了 '+rows.length+' 条记录，请选择单条记录进行修改！','warning');return;
    		}
    		$.openWin({title:'修改数据',href:'<%=bpath%>_modify<%=actionExt%>?chkSelf='+rows[0]['VC_ID'],width:600,height:400,
    			butParams:[{id:'addBtn',text:'保存',iconCls:'icon-save'}]});
    	}
    	function delItem(){
    		dealMultiRow('searchGrid','<%=bpath%>_delete<%=actionExt%>','刪除');
    	}
    </SCRIPT>
<style type="text/css">
html,body{overflow:hidden;}
</style>
</HEAD>
<BODY >
<table class="searchTbl" fit="true" border="0" cellpadding="0" cellspacing="0" align="left" width="100%" height="100%">
    <!-- 查询条件行 --> 
   <tr height="20px"><td>
   <div id="searchDiv" class="searchDiv"><div class="searchDivContent">
<f:searchitem />
     </div></div>
   </td><td nowrap="nowrap"><a id="search" href="javascript:void(0)"  class="easyui-linkbutton" iconCls="icon-search">查询</a></td></tr>
   <!-- 数据结果列表 -->
   <tr><td colspan="2" class="srhResultTd"><div id="searchGrid"></div></td></tr>
</table>
</BODY>
</HTML>
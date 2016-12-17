<%@page import="com.dx.jwfm.framework.core.dao.model.FastColumnType"%>
<%@page import="com.dx.jwfm.framework.core.SystemContext"%>
<%@page import="com.dx.jwfm.framework.core.contants.RequestContants"%>
<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String bpath = (String)request.getAttribute(RequestContants.REQUEST_URI_PRE);
String method = (String)request.getAttribute(RequestContants.REQUEST_URI_METHOD);
String actionExt = (String)request.getAttribute(RequestContants.REQUEST_URI_ACTIONEXT);
String addActionName = bpath+"_"+method+actionExt;
long ltime = System.currentTimeMillis();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/fast-tags" prefix="f" %>

<%-- <script type="text/javascript" src="<%=path %>/js/ace/ace.js"></script> --%> <!-- 菜单功能编辑所用JS文件 -->

<script type="text/javascript" src="<%=path %>/jwfm/core/menuEdit.js"></script> <!-- 菜单功能编辑所用JS文件 -->
<SCRIPT type=text/javascript>
	$(function(){
		//最大化，不使用最大化是为了防止恢复窗口大小之后表单中的输入框变形
		$('#menuEditWin').window('resize',{width:$(window).width()-30,height:$(window).height()-20}).window('center').window('maximize').window('options').onResize = function(){
			$('#layoutbox').height($('#menuEditWin').height()-37).layout('resize');
		};
		//点击添加按钮操作
	    $('#addBtn').click(function(){
			$('#editForm').submit();
		});
		var model = {mainTable:{columns:[]}};
		$('#editForm').data('model',model);
		tableTrHover('.fast-edit-table');
		$('#layoutbox').layout();
		$.sysmenu.dbDataTypes = '<%=FastColumnType.types %>'.split(',');//数据类型列表
		loadMacrosMenu();
		loadDbTables();
		$('.tables-div a.option').click(function(){
			$('.tables-div a.selected').removeClass('selected');
			$(this).addClass('selected');
			$('.dbtbl-info input').val('');//清空表名等数据
			$('.dbtbl-body').empty();
			$.getJSON('menu_loadTable.action',{tblCode:$(this).attr('val')},function(tblModel){
				var model = {mainTable:tblModel};
				$('#editForm').data('model',model);
				$('.dbtbl-body').prevAll().remove();//清空表头，防止出现多个表头
				loadDbTables();
			});
		});
		//表单验证及提交处理操作
		$('#editForm').form({
			url:'<%=addActionName%>',
			onSubmit : function() {
				$(this).trigger('beforeSubmit');
				if(!$(this).data('stopSubmit') && $(this).form('validate')) {
					$.util.showLoading();
					$('#menuEditWin').window('close');
					$('.delRowBtn').click();
					return true;
				} else {
					return false;
				}
			},
			success : function(data) {
				$.util.removeLoading();
				returnOptMsgEasyui({data:data,winId:'#menuEditWin',callback:function(res,jsonRes){
					if(res){
						modifyItem(jsonRes.info);
					}
				}});
			},
			onLoadError:function(){
				$.util.removeLoading();
				$.messager.alert('提示','操作失败!');
			}
		});
	});
</SCRIPT>
<style>
input.index{text-align:center;}
.fast-child-table tr td input[type=text],.fast-child-table tr td textarea{width:97%;}
.fast-child-table tr td.selecttextarea{text-align:left;}
.fast-child-table tr td.selecttextarea textarea{height:37px;margin-top:2px;}
.fast-child-table tbody tr td.left{text-align:left;}
tr.hover td,tr.hover th,tr.hover td input,tr.hover td textarea,tr.hover td span.combo,tr.hover td select{background-color:#FFFFDD;}
.page-edit-container{overflow:auto;}
a.option{display:block;height:25px;line-height:25px;cursor:pointer;white-space:nowrap;}
a.option:hover{background:#FFFFDD;}
a.option.selected{background:#CCFF99;}
</style>
<form id="editForm" method="post">
<input type=hidden name=op value="save" >
<f:hidden name="po.VC_ID"/>
<div id="layoutbox" style="height:100%;">
	<div class="tables-div" data-options="region:'west',title:'数据库表',split:true" style="width:200px;">
	<a class=option val="NEWTABLE">=====创建新表=====</a>
	<c:forEach items="${tables}" var="tbl">
	<a class=option val="${tbl.VC_CODE}" title="${tbl.VC_NAME}">${tbl.VC_CODE}<font color=gray> [${tbl.VC_NAME}]</font></a>
	</c:forEach>
	</div>   
    <div data-options="region:'center'" >
<table class="fast-edit-table" >
<colgroup>
<col width="10%" />
<col width="30%" />
<col width="10%" />
<col width="30%" />
<col width="10%" />
<col width="30%" />
</colgroup>
<thead>
<tr style="display:none;">
<th colspan=6></th>
</tr>
</thead>
<tbody>
<%--业务表结构区域 --%>
<tr class=subtitle>
	<td colspan=6 id="dbtable-title">业务表结构  <a href="javascript:void(0)" id="addMtblColBtn">添加</a>
	<a href="javascript:void(0)" id="delMtblColBtn" class=delRowBtn>删除选中</a>
	<select id="dbtblselect" style="width:200px;display:none;"><option value="maintbl-tr">主表：</option><option value="createnewtbl">增加业务从表</option></select>
	<font color=red>注：修改英文表名后会创建为新表</font>
	</td>
</tr>
<tr class="dbtable-tr maintbl-tr">
	<td colspan=6>
	<div class=page-edit-container>
	<table class="dbtbl-info fast-child-table" cellpadding="0" cellspacing="0">
	<tbody>
	<tr>
	<td colspan=3 align=left>英文表名<input type=text name=model.mainTable.name style="width:85%" /></td>
	<td colspan=3 align=left>中文表名<input type=text name=model.mainTable.title style="width:85%" /></td>
	<td colspan=4 align=left>注释<input type=text name=model.mainTable.comment style="width:85%" /></td>
	</tr>
	</tbody></table>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody class="dbtbl-body"></tbody>
	</table>
	</div>
	</td>
</tr>
</tbody>
</table>
    </div> 
</div>
</form>
<div id="mtblColMenu" style="width:340px;"></div>
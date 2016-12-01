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

<script type="text/javascript" src="<%=path %>/jwfm/core/menuEdit.js"></script> <!-- JS工具方法 -->
<SCRIPT type=text/javascript>
	$(function(){
		//最大化，不使用最大化是为了防止恢复窗口大小之后表单中的输入框变形
		$('#operateWindow').window('resize',{width:$(window).width(),height:$(window).height()}).window('center');
		//对字段进行非空验证和输入类型长度等限制
		$('#editTbl<%=ltime%> .easyui-validatebox').validatebox();
		//点击添加按钮操作
	    $('#addBtn').click(function(){
			$('#editForm').submit();
		});
		var model = ${modeljson};
		$('#po_VC_URL').tooltip({content:'前缀不包含war包名称，后缀不含.action<br/>如访问URL为/jwfm/sys/org.action，则此处填写/sys/org即可'});
		$('#po_VC_GROUP').combobox({url:'<%=path%>/jwfm/core/main_comboData.action',
			width:$('#po_VC_GROUP').width(),
			queryParams:{sql:'select vc_group code,vc_group from (select distinct vc_group from <%=SystemContext.dbObjectPrefix %>t_menu_lib where n_del=0) order by vc_group'}
		});
		//=============业务主表区===============
			
			
			
		//将最后一列的输入框进行右边对齐，同时对.subtitle增加收起与展开功能
		$('.fast-edit-table').autoInputWidth();
		//表单验证及提交处理操作
		$('#editForm').data('model',model).form({
			url:'<%=addActionName%>',
			onSubmit : function() {
				$(this).trigger('beforeSubmit');
				if(!$(this).data('stopSubmit') && $(this).form('validate')) {
					$.util.showLoading();
					$('#operateWindow').window('close');
					$('.delRowBtn').click();
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
<style>
input.index{text-align:center;}
.fast-child-table tr td input[type=text],.fast-child-table tr td textarea{width:97%;}
.fast-child-table tr td.selecttextarea{text-align:left;}
.fast-child-table tr td.selecttextarea textarea{height:37px;margin-top:2px;}
.fast-child-table tbody tr td.left{text-align:left;}
</style>
<div id="editTbl<%=ltime%>">
<form id="editForm" method="post">
<input type=hidden name=op value="save" >
<f:hidden name="po.VC_ID"/>
<f:hidden name="po.VC_ADD"/>
<f:hidden name="po.DT_ADD"/>
<table class="fast-edit-table" >
<colgroup>
<col width="10%" />
<col width="30%" />
<col width="10%" />
<col width="30%" />
<col width="10%" />
<col width="30%" />
</colgroup>
<tr class=subtitle>
	<td colspan=6>基本信息</td>
</tr>
<tr>
	<th>菜单名称</th><td><f:textfield name="po.VC_NAME" id="po_VC_NAME"/></td>
	<th>菜单Url</th><td><f:textfield name="po.VC_URL" id="po_VC_URL"/></td>
	<th>所在分组</th><td><f:textfield name="po.VC_GROUP" id="po_VC_GROUP"/></td>
</tr>
<tr>
	<th>当前版本</th><td><f:textfield name="po.VC_VERSION" id="po_VC_VERSION"/></td>
	<th>添加人</th><td><f:textfield name="po.VC_ADD" id="po_VC_ADD"/></td>
	<th>添加时间</th><td><f:textfield name="po.DT_ADD" id="po_DT_ADD"/></td>
</tr>
<tr>
	<th>功能说明及更改历史</th><td colspan=5><f:textfield name="po.VC_NOTE" id="po_VC_NOTE" multiLine="true"/></td>
</tr>
<%--按钮的权限区域 --%>
<jsp:include page="menuEditBtns.jsp"></jsp:include>
<%--业务表结构区域 --%>
<jsp:include page="menuEditDbTable.jsp"></jsp:include>
<%--查询功能区域 --%>
<jsp:include page="menuEditSrhModel.jsp"></jsp:include>
<tr class=subtitle>
	<td colspan=6>查询结果</td>
</tr>
<tr class=subtitle>
	<td colspan=6>查询信息</td>
</tr>
<tr class=subtitle>
	<td colspan=6>字典数据</td>
</tr>
<tr class=subtitle>
	<td colspan=6>编辑页面</td>
</tr>
<tr class=subtitle>
	<td colspan=6>流程信息</td>
</tr>
<tr class=subtitle>
	<td colspan=6>更新日志</td>
</tr>
</table>
</form>

<div id="btnMenu" style="width:240px;">
<div data-options="names:'add,modify,del',iconCls:'icon-richeng'">添加 修改 删除</div>
<div data-options="names:'add,modify,del,exportExcel',iconCls:'icon-richeng'">添加 修改 删除 导出Excel</div>
<div data-options="names:'add,modify,del,submitItem,checkItem',iconCls:'icon-richeng'">添加 修改 删除 上报 审批</div>
<div data-options="names:'add,modify,del,submitItem,checkItem,exportExcel',iconCls:'icon-richeng'">添加 修改 删除 上报 审批 导出Excel</div>
<div class="menu-sep"></div>
</div>
</div>
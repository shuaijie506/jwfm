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
<link rel="stylesheet" type="text/css" href="<%=path %>/js/codemirror/lib/codemirror.css"><!-- 代码编辑器样式表 -->
<link rel="stylesheet" type="text/css" href="<%=path %>/js/codemirror/theme/eclipse.css"><!-- 代码编辑器样式表 -->
<link rel="stylesheet" type="text/css" href="<%=path %>/js/codemirror/addon/hint/show-hint.css"><!-- 代码编辑器样式表 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/lib/codemirror.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/mode/meta.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/mode/htmlmixed/htmlmixed.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/mode/javascript/javascript.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/mode/css/css.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/mode/xml/xml.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/addon/hint/show-hint.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/addon/hint/html-hint.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/addon/hint/css-hint.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/addon/hint/xml-hint.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror/addon/hint/javascript-hint.js"></script> <!-- 代码编辑器所用JS文件 -->


<SCRIPT type=text/javascript>
	$(function(){
		//最大化，不使用最大化是为了防止恢复窗口大小之后表单中的输入框变形
		$('#menuEditWin').window('resize',{width:$(window).width()-30,height:$(window).height()-20}).window('center').window('maximize');
		//对字段进行非空验证和输入类型长度等限制
		$('#editTbl<%=ltime%> .easyui-validatebox').validatebox();
		//点击添加按钮操作
	    $('#addBtn').click(function(){
			$('#editForm').submit();
		});
		var model = ${modeljson};
		$('#po_VC_URL').tooltip({content:'前缀不包含war包名称，后缀不含.action<br/>如访问URL为/jwfm/sys/org.action，则此处填写/sys/org即可'});
		$('#po_VC_GROUP').combobox({url:'<%=path%>/jwfm/tools_comboData.action',
			width:$('#po_VC_GROUP').width(),required:true,
			queryParams:{sql:'select vc_group code,vc_group from (select distinct vc_group from <%=SystemContext.dbObjectPrefix %>t_menu_lib where n_del=0) order by vc_group'}
		});
		tableTrHover('.fast-edit-table');
			
		//将最后一列的输入框进行右边对齐，同时对.subtitle增加收起与展开功能
		$('.fast-edit-table').initPage();
		//展开时判断信息是否显示完整，如果显示不完整，则滚动页面，此方法应该放在initPage之后
		$('tr.subtitle .expand-icon').click(function(){
			if($(this).hasClass('tree-expanded')){
				var tr = $(this).parent().parent(),pnl=$('#menuEditWin-main');
				var ntr = tr.nextUntil('tr.subtitle').filter('tr:last');
				if(ntr.position().top+ntr.height()>pnl.height()){
					pnl.scrollTop(pnl.scrollTop()+tr.position().top);
				}
			}
		});
		$(window).resize(function(){
			$('.page-edit-container').css('max-height',$(window).height()-115);
		}).resize();
		if('${po.VC_ID}'){//修改页面中默认只显示基本信息和按钮信息
			$('tr.subtitle:eq(1)').nextAll('.subtitle').find('.tree-expanded').click();
		}
		if($('#po_VC_URL').val()=='/module/'){
			$('#po_VC_URL').attr('readonly',false);
		}
		$('#editForm').data('model',model);
		$.sysmenu.dbDataTypes = '<%=FastColumnType.types %>'.split(',');//数据类型列表
		loadMacrosMenu();
		loadBtns();
		loadForwards();
		loadDbTables();
		loadSearchInfo();
		loadDicts();
		loadPageInfo();
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
				returnOptMsgEasyui({data:data,winId:'#menuEditWin'});
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
.page-toolbar{padding:2px 5px 0px;background-color:#efefef;height:28px;}
.page-toolbar a{display:inline-block;cursor:pointer;font-size:14px;padding:3px;border:1px solid transparent;min-width:22px;min-height:22px;vertical-align:middle;text-align:center;}
.page-toolbar a:hover{border:1px solid #ccc;}
.page-toolbar a:hover{border:1px solid #ccc;}
.page-toolbar a.disable{color:gray;}
.page-toolbar .char-icon{font-size:16px;font-weight:900;line-height:22px;top:-1px;}
.page-toolbar .fa-stack{width:22px;height:22px;}
#pageMapDiv{display:inline-block;margin-left:20px;}
#pageMapDiv span{display:none;}
#pageMapDiv span.delPage{display:inline-block;margin-left:3px;color:red;}
#pageMapDiv span.selected{display:inline-block;}
#pageMapDiv input{width:100px;}
#pageHTMLDiv{min-height:300px;}
#pageHTMLDiv .selected,#pageHTMLDiv .selected input,#pageHTMLDiv .selected select,#pageHTMLDiv .selected textarea{background-color:#CCFF99}
.edittbl-cols-body .selected td{background-color:#CCFF99}
#htmlEWBox{width:100%;height:100%;}
.dbtbl-editor-cols{position:relative;overflow:hidden;}
.editortype-tbl{overflow:auto;}
.editortype-div{position:absolute;width:100%;height:120px;bottom:0px;left:0px;background:#fff;border-top:solid 1px #ccc;}
.editortype-div a{display:inline-block;margin-left:4px;}
.editortype-div textarea{width:100%;height:100px;}
.CodeMirror{height:auto;}
.macroicon,.jsicon{color:#999933;margin-left:2px;cursor:pointer;}
i.opt-icon.fa{font-size:14px;margin-left:3px;cursor:pointer;}
.mode-sel-div{margin-top:7px;}
.mode-sel-div label{margin:0px 5px;display:inline-block;cursor:default;}
</style>
<div id="editTbl<%=ltime%>">
<form id="editForm" method="post">
<input type=hidden name=op value="save" >
<f:hidden name="po.VC_ID"/>
<f:hidden name="model.packageName"/>
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
<tr class=subtitle>
	<td colspan=6>基本信息</td>
</tr>
</thead>
<tr>
	<th>菜单名称</th><td><f:textfield name="po.VC_NAME" id="po_VC_NAME" notnull="true" validType="length[1,50]"/></td>
	<th>菜单Url</th><td><f:textfield name="po.VC_URL" id="po_VC_URL" readonly="true"/></td>
	<th>所在分组</th><td><f:textfield name="po.VC_GROUP" id="po_VC_GROUP" notnull="true" validType="length[1,100]"/></td>
</tr>
<tr>
	<th>当前版本</th><td><f:textfield name="po.VC_VERSION" id="po_VC_VERSION" notnull="true" validType="length[1,25]"/></td>
	<th>添加人</th><td><f:textfield name="po.VC_ADD" id="po_VC_ADD" notnull="true" validType="length[1,25]"/></td>
	<th>添加时间</th><td><f:textfield name="po.DT_ADD" id="po_DT_ADD" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})" notnull="true"/></td>
</tr>
<tr>
	<th>功能说明及更改历史</th><td colspan=5><f:textfield name="po.VC_NOTE" id="po_VC_NOTE" multiLine="true" validType="length[0,2000]"/></td>
</tr>
<%--按钮的权限区域 --%>
<tr class=subtitle>
	<td colspan=6>按钮和权限  <a href="javascript:void(0)" id="addBtnsBtn">添加</a>
	<a href="javascript:void(0)" id="delBtnsBtn" class=delRowBtn>删除选中</a></td>
</tr>
<tr>
	<td colspan=6>
	<div class=page-edit-container>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody id="btn-tbl-body"></tbody>
	</table>
	</div>
	</td>
</tr>
<%--控制类及相关信息 --%>
<tr class=subtitle>
	<td colspan=6>控制类及相关信息  <a href="javascript:void(0)" id="addForwardBtn">添加转向页面</a>
	<a href="javascript:void(0)" id="delForwardBtn" class=delRowBtn>删除选中</a></td>
</tr>
<tr>
	<td colspan=6>
	<div class=page-edit-container>
	<table class="classaction-info fast-child-table" cellpadding="0" cellspacing="0">
	<tbody>
	<tr>
	<td colspan=3 align=left>控制器类<input type=text name=model.actionName readonly="true" style="width:85%" /></td>
	</tr>
	<tr>
	<td colspan=3 align=left>查询类<input type=text name=model.searchClassName readonly="true" style="width:85%" /></td>
	</tr>
	</tbody></table>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody id="forward-tbl-body"></tbody>
	</table>
	</div>
	</td>
</tr>
<%--业务表结构区域 --%>
<tr class=subtitle>
	<td colspan=6 id="dbtable-title">业务表结构  <a href="javascript:void(0)" id="addMtblColBtn">添加列</a>
	<a href="javascript:void(0)" id="delMtblColBtn" class=delRowBtn>删除选中</a>
	<select id="dbtblselect" style="width:200px;"><option value="maintbl-tr">主表：</option><option value="createnewtbl">增加业务从表</option></select>
	</td>
</tr>
<tr class="dbtable-tr maintbl-tr">
	<td colspan=6>
	<div class=page-edit-container>
	<table class="dbtbl-info fast-child-table" cellpadding="0" cellspacing="0">
	<tbody>
	<tr>
	<td colspan=3 align=left>英文表名<input type=text name=model.mainTable.name readonly="true" style="width:85%" /></td>
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
<%--查询功能区域 --%>
<tr class=subtitle>
	<td colspan=6>查询信息设置  
	</td>
</tr>
<tr class="srhmodel-tr">
	<td colspan=6>
	<table class="srhmodel-info fast-child-table" cellpadding="0" cellspacing="0">
	<tbody>
	<tr>
	<th style="width:100px;">Select部分</th><td class="left selecttextarea"><textarea name=model.search.searchSelectSql style="width:85%" ></textarea>
	<span id=genSqlSelect class="hand glyphicon glyphicon-flash"></span></td>
	</tr>
	<tr>
	<th>OrderBy部分</th><td class="left"><input type=text name=model.search.searchOrderBySql style="width:50%" />无需输入order by，如： n_level,dt_add desc</td>
	</tr>
	<tr>
	<th title="Search页面中，添加到HEAD部分的HTML代码，可以此处引入相应的CSS和JS文件">结果页面HEAD部分的HTML</th><td class="left selecttextarea"><textarea name=model.search.headHTML style="width:85%" ></textarea>
	<span id="genScriptHTML" class="hand glyphicon glyphicon-flag"></span>
	<span id="genCssHTML" class="hand glyphicon glyphicon-link"></span></td>
	</tr>
	</tbody></table>
	</td>
</tr>
<tr class=subtitle>
	<td colspan=6>查询条件列表
	<a href="javascript:void(0)" id="addSrhCondBtn">添加查询条件</a>
	<a href="javascript:void(0)" id="delSrhCondBtn" class=delRowBtn>删除选中</a>
	</td>
</tr>
<tr class="srhmodel-tr">
	<td colspan=6>
	<div class=page-edit-container>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody class="srhmodel-body srhcond-body"></tbody>
	</table>
	</div>
	</td>
</tr>
<tr class=subtitle>
	<td colspan=6>查询结果列表 
	<a href="javascript:void(0)" id="addSrhResultBtn">添加结果列</a>
	<a href="javascript:void(0)" id="delSrhResultBtn" class=delRowBtn>删除选中</a>
	</td>
</tr>
<tr class="srhmodel-tr">
	<td colspan=6>
	<div class=page-edit-container>
	<table class="fast-child-table table table-striped table-bordered table-hover" cellpadding="0" cellspacing="0">
	<tbody class="srhmodel-body srhresult-body"></tbody>
	</table>
	</div>
	</td>
</tr>
<%--配置项及公共字典信息 --%>
<tr class=subtitle>
	<td colspan=6>配置项及公共字典信息  <a href="javascript:void(0)" id="addRegBtn">添加配置项</a>  <a href="javascript:void(0)" id="addDictBtn">添加字典项</a>
	<a href="javascript:void(0)" id="delDcitBtn" class=delRowBtn>删除选中</a></td>
</tr>
<tr>
	<td colspan=6>
	<div class=page-edit-container>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody id="dict-tbl-body"></tbody>
	</table>
	</div>
	</td>
</tr>
<%--独立页面设置（编辑、查看等页面） --%>
<tr class=subtitle>
	<td colspan=6 id="pageMapTitle">独立页面设置（编辑、查看等页面）
	</td>
</tr>
<tr>
	<!-- <td colspan=2>
	<div class=page-edit-container>
	<select id="dbtbl-editor-sel"></select>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody class="edittbl-cols-body"></tbody>
	</table>
	</div>
	</td> -->
	<td colspan=6 valign=top>
	<div class=page-toolbar>
	</div>
	<div class=page-edit-container>
	<div id="pageHTMLDiv"></div>
	<div style="display:none"><div id="pageHTMLHelp"><pre>单元格选中操作：
1.鼠标单击切换当前单元格的选中状态，其他被选中单元格会取消选中状态
2.按住键盘的Ctrl或Shift键，开启多选模式，此时鼠标点击选中时，原选中的单元格选中状态仍存在
3.按住键盘的Ctrl+Alt键，开启超级多选模式，此时鼠标经过的单元格都会被选中。
</pre></div></div>
	</div>
	</td>
</tr>
<!-- <tr class=subtitle>
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
</tr> -->
</table>
<div id="btnMenu" style="width:240px;"></div>
<div id="forwardMenu" style="width:240px;"></div>
<div id="mtblColMenu" style="width:340px;"></div>
</form>

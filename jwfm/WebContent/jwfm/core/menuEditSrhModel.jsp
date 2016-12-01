<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@page import="com.dx.jwfm.framework.core.dao.model.FastColumnType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

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
	<h3>查询条件列表</h3>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody class="srhmodel-body srhcond-body"></tbody>
	</table>
	<h3>查询结果列表</h3>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody class="srhmodel-body srhresult-body"></tbody>
	</table>
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
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody class="srhmodel-body srhcond-body"></tbody>
	</table>
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
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<tbody class="srhmodel-body srhresult-body"></tbody>
	</table>
	</td>
</tr>
<div id="srhCondMenu" style="width:340px;">
</div>

<SCRIPT type=text/javascript>
	$(function(){
		$(createThead([{width:'35px',text:'序号'},
				{width:'10%',text:'显示标题'},
				{width:'10%',text:'字段编码'},
				{width:'20%',text:'输入类型'},
				{width:'30%',text:'加载后执行的JS代码'},
				{width:'10%',text:'默认值'},
				{width:'10%',text:'查询过滤类型'},
				{width:'15%',text:'SQL语句片断'},
				{width:'35px',text:'删除'}
		])).insertBefore('.srhcond-body');
		$(createThead([{width:'35px',text:'序号'},
			{width:'20%',text:'显示标题'},
			{width:'20%',text:'字段编码'},
			{width:'60px',text:'显示宽度'},
			{width:'30%',text:'显示格式'},
			{width:'70px',text:'冻结此列'},
			{width:'70px',text:'能否排序'},
			{width:'70px',text:'是否隐藏'},
			{width:'20%',text:'对齐方式'},
			{width:'35px',text:'删除'}
		])).insertBefore('.srhresult-body');
		var model = $('#editForm').data('model')||{};
		$('#addSrhCondBtn').linkbutton({iconCls:'icon-add',plain:true}).click(function(){
			addTblCols([{}],'model.search.searchColumns[0]');
		});
		$('#addSrhResultBtn').linkbutton({iconCls:'icon-add',plain:true}).click(function(){
			addResultCols([{width:120,align:'center'}],'model.search.searchResultColumns[0]');
		});
		//删除查询条件事件
		$('#delSrhCondBtn').linkbutton({iconCls:'icon-remove',plain:true}).click(function(){
			$('.srhcond-body .delChk:checked').each(function(){
				$(this).parent().parent().remove();
			});
			resetBtnTblIndex('.srhcond-body');
		});
		//删除查询结果事件
		$('#delSrhResultBtn').linkbutton({iconCls:'icon-remove',plain:true}).click(function(){
			$('.srhresult-body .delChk:checked').each(function(){
				$(this).parent().parent().remove();
			});
			resetBtnTblIndex('.srhresult-body');
		});
		$('#genSqlSelect').tooltip({content:'点击后根据表名生成SQL语句'}).click(function(){
			$('textarea[name$=searchSelectSql]').val('select t.* from '+$('input[name=model\\.mainTable\\.code]').val()+' t');
		});
		$('#genScriptHTML').tooltip({content:'点击追加javascript文件引用HTML代码'}).click(function(){
			$('textarea[name$=headHTML]').val(($('textarea[name$=headHTML]').val()+'\n<'+'script type="text/javascript" src=""><'+'/script>').trim());
		});
		$('#genCssHTML').tooltip({content:'点击追加css文件引用HTML代码'}).click(function(){
			$('textarea[name$=headHTML]').val(($('textarea[name$=headHTML]').val()+'\n<'+'link rel="stylesheet" type="text/css" href=""/>').trim());
		});
		//生成输入类型下拉框
		function getEditorTypeSelect(name,val){
			val = val||'';
			var htm = [];
			var ary = $.sysmenu.editorType||[];
			htm.push(createSelectHTML({htmlAttrs:'class=editortype',value:val,data:ary}));
			$.editorTypeAry = ary;
			htm.push('<textarea name="'+name+'" class="seltextarea">'+val+'</textarea>');
			return htm.join('');
		}
		//输入类型下拉框选择事件
		function editorTypeChange(){
			var sel=$(this),txt=sel.next();
			if(!txt.data('srcval') && txt.val()){
				txt.data('srcval',txt.val());
				$('option:first',sel).val('').text('==恢复原始值==');
			}
			if(sel.val()==''){
				txt.val(txt.data('srcval')||'');
			}
			var item = $.editorTypeAry[sel.val()];
			txt.val((item&&(item.data||item.value))||'');
		}
		//生成查询语句类型的下拉框
		function getSearchTypeSelect(name,val){
			val = val||'';
			var htm = [];
			var ary = $.sysmenu.searchType||[];
			htm.push(createSelectHTML({htmlAttrs:'name='+name+' class=searchtype',value:val,data:ary}));
			$.searchTypeAry = ary;
			return htm.join('');
		}
		//输入类型下拉框选择事件
		function searchTypeChange(){
			var sel=$(this),tr=sel.parent().parent();
			var item = $.searchTypeAry[sel.val()];
			var vcCode = $('input[name$=vcCode]',tr).val();
			$('textarea[name$=sqlFragment]',tr).val(!item?'t.'+vcCode:item.func.call(this,vcCode));
		}
		//添加指定行
		function addTblCols(items,namepre){
			var htm = [];
			for(var i=0;i<items.length;i++){
				htm.push('<tr>');
				htm.push(createIndexTd());
				pushInputTds(htm,items[i],namepre,'vcTitle,vcCode'.split(','));
				htm.push('<td class=selecttextarea>'+getEditorTypeSelect(namepre+'.vcEditorType',items[i].vcEditorType||'')+'</td>');
				htm.push('<td><textarea name='+namepre+'.vcEditorJs >'+(items[i].vcEditorJs||'')+'</textarea></td>');
				pushInputTds(htm,items[i],namepre,'defaults'.split(','));
				htm.push('<td>'+getSearchTypeSelect(namepre+'.sqlSearchType',items[i].sqlSearchType||'')+'</td>');
				htm.push('<td><textarea name='+namepre+'.sqlFragment >'+(items[i].sqlFragment||'')+'</textarea></td>');
				htm.push(createDelTd());
				htm.push('</tr>');
			}
			var trs = $(htm.join('')).appendTo('.srhcond-body');
			$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
			$('select.editortype',trs).change(editorTypeChange);
			$('select.searchtype',trs).change(searchTypeChange).tooltip({content:'多选、模糊匹配、日期相关的过滤条件在后台查询时会自动根据数据库类型进行函数转换'});
			resetBtnTblIndex('.srhcond-body');
		}
		function frozenClick(){
			var tbody = $(this).parents('tbody')[0],tr=$(this).parent().parent();
			var trchked=$('>tr:has(input[name$=frozen]:checked)',tbody);
			var trunchk = $('>tr:has(input[name$=frozen]:not(:checked)):first',tbody);
			trchked.insertBefore(trunchk);
			resetBtnTblIndex(tbody);
		}
		//添加查询结果列
		function addResultCols(items,namepre){
			var htm = [];
			var alignAry = [{value:'left',text:'靠左对齐'},{value:'right',text:'靠右对齐'},{value:'center',text:'居中对齐'}];
			for(var i=0;i<items.length;i++){
				htm.push('<tr>');
				htm.push(createIndexTd());
				pushInputTds(htm,items[i],namepre,'vcTitle,vcCode,width,vcFormat'.split(','));
				pushCheckboxTds(htm,items[i],namepre,'frozen,canSort,hidden'.split(','));
				htm.push('<td>'+createSelectHTML({htmlAttrs:'name=align',value:items[i].align,data:alignAry})+'</td>');
				htm.push(createDelTd());
				htm.push('</tr>');
			}
			var trs = $(htm.join('')).appendTo('.srhresult-body');
			$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
			$(':checkbox[name$=frozen]',trs).bind('click',frozenClick);
			resetBtnTblIndex('.srhresult-body');
		}
		//供menuEditDbTable.jsp中的JS方法调用
		$('.srhmodel-tr').bind('addsrhcond',function(event,coltr){//添加查询条件
			function getVal(name){return $('input[name$=\\.'+name+']',coltr).val();}
			addTblCols([{vcTitle:getVal('name'),vcCode:getVal('code'),vcEditorType:'text',sqlSearchType:'like',sqlFragment:'t.'+getVal('code')}],'model.search.searchColumns[0]');
		}).bind('addsrhresult',function(event,coltr){//添加查询结果列
			
		});
		//数据库表下拉框选择事件
		$('#dbtblselect').change(function(){
		});
		//对表名等信息赋值
		setValueByName('.srhmodel-info');
		//将已有列信息显示
		addTblCols(model.search.searchColumns,'model.search.searchColumns[0]');
		addResultCols(model.search.searchResultColumns,'model.search.searchResultColumns[0]');
	});
</SCRIPT>
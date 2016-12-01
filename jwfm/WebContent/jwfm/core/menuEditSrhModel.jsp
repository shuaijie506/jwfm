<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@page import="com.dx.jwfm.framework.core.dao.model.FastColumnType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<tr class=subtitle>
	<td colspan=6>查询功能设置  
	<a href="javascript:void(0)" id="addSrhCondBtn">添加查询条件</a>
	<a href="javascript:void(0)" id="addSrhResultBtn">添加结果列</a>
	<a href="javascript:void(0)" id="delSrhCondBtn" class=delRowBtn>删除选中</a>
	</td>
</tr>
<tr class="srhmodel-tr">
	<td colspan=6>
	<table class="srhmodel-info fast-child-table" cellpadding="0" cellspacing="0">
	<tbody>
	<tr>
	<th style="width:100px;">Select部分</th><td class="left selecttextarea"><textarea name=model.search.searchSelectSql style="width:85%" ></textarea></td>
	</tr>
	<tr>
	<th>OrderBy部分</th><td class="left"><input type=text name=model.search.searchOrderBySql style="width:85%" /></td>
	</tr>
	</tbody></table>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<colgroup>
	<col width="35px" />
	<col width="10%" />
	<col width="10%" />
	<col width="20%" />
	<col width="30%" />
	<col width="10%" />
	<col width="10%" />
	<col width="15%" />
	<col width="35px" />
	</colgroup>
	<thead>
	<tr>
	<th>序号</th>
	<th>显示标题</th>
	<th>字段编码</th>
	<th>输入类型</th>
	<th>加载后执行的JS代码</th>
	<th>默认值</th>
	<th>查询过滤类型</th>
	<th>SQL语句片断</th>
	<th>删除</th>
	</tr>
	</thead>
	<tbody class="srhmodel-body srhcond-body"></tbody>
	</table>
	</td>
</tr>
<div id="srhCondMenu" style="width:340px;">
</div>

<SCRIPT type=text/javascript>
	$(function(){
		var model = $('#editForm').data('model')||{};
		$('#addSrhCondBtn').linkbutton({iconCls:'icon-add',plain:true}).click(function(){
			addTblCols([{}],'model.search.searchColumns[0]');
		});
		$('#addSrhResultBtn').linkbutton({iconCls:'icon-add',plain:true}).click(function(){
			addTblCols([{}],'model.search.searchColumns[0]');
		});
		//删除按钮事件
		$('#delSrhCondBtn').linkbutton({iconCls:'icon-remove',plain:true}).click(function(){
			$('.srhmodel-tr .delChk:checked').each(function(){
				$(this).parent().parent().remove();
			});
			$('.srhmodel-tr .srhmodel-body').each(function(){
				resetBtnTblIndex(this);
			});
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
			//var namepre = 'model.mainTable.columns[0]';
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
		//供menuEditDbTable.jsp中的JS方法调用
		$('.srhmodel-tr').bind('addsrhcond',function(event,coltr){//添加查询条件
			addTblCols([],'model.search.searchColumns[0]');
		}).bind('addsrhresult',function(event,coltr){//添加查询结果列
			
		});
		//数据库表下拉框选择事件
		$('#dbtblselect').change(function(){
		});
		//对表名等信息赋值
		setValueByName('.srhmodel-info');
		//将已有列信息显示
		addTblCols(model.search.searchColumns,'model.search.searchColumns[0]');
	});
</SCRIPT>
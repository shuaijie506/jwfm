<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@page import="com.dx.jwfm.framework.core.dao.model.FastColumnType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<tr class=subtitle>
	<td colspan=6>业务表结构  <a href="javascript:void(0)" id="addMtblColBtn">添加</a>
	<a href="javascript:void(0)" id="delMtblColBtn" class=delRowBtn>删除选中</a>
	<select id="dbtblselect" style="width:200px;"><option value="maintbl-tr">主表：</option><option value="createnewtbl">增加业务从表</option></select>
	</td>
</tr>
<tr class="dbtable-tr maintbl-tr">
	<td colspan=6>
	<table class="dbtbl-info fast-child-table" cellpadding="0" cellspacing="0">
	<tbody>
	<tr>
	<td colspan=3 align=left>英文表名<input type=text name=model.mainTable.code style="width:85%" /></td>
	<td colspan=3 align=left>中文表名<input type=text name=model.mainTable.name style="width:85%" /></td>
	<td colspan=4 align=left>注释<input type=text name=model.mainTable.comment style="width:85%" /></td>
	</tr>
	</tbody></table>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<colgroup>
	<col width="35px" />
	<col width="15%" />
	<col width="15%" />
	<col width="90px" />
	<col width="40px" />
	<col width="15%" />
	<col width="35px" />
	<col width="35px" />
	<col width="15%" />
	<col width="30%" />
	<col width="35px" />
	<col width="35px" />
	<col width="35px" />
	</colgroup>
	<thead>
	<tr>
	<th>序号</th>
	<th>显示名称</th>
	<th>英文编码</th>
	<th>数据类型</th>
	<th>数据长度</th>
	<th>默认值</th>
	<th>是否为空</th>
	<th>是否主键</th>
	<th>字典项名称</th>
	<th>备注</th>
	<th>查询条件</th>
	<th>查询结果</th>
	<th>删除</th>
	</tr>
	</thead>
	<tbody class="dbtbl-body"></tbody>
	</table>
	</td>
</tr>
<div id="mtblColMenu" style="width:340px;">
</div>

<SCRIPT type=text/javascript>
	$(function(){
		var model = $('#editForm').data('model')||{};
		//处理添加按钮的菜单
		var presetMenus = $.sysmenu.presetTblcolAry;
		$('#mtblColMenu').append(createMenuHTML(presetMenus)).menu({onClick:function(item){
			var ary = (item.names||'').split(',');
			var items = [];
			for(var i=0;i<ary.length;i++){
				if(presetMenus[ary[i].trim()]){
					items.push(presetMenus[ary[i]]);
				}
			}
			var namepre = 'model.mainTable.columns[0]',cls=$('#dbtblselect').val();
			if(cls.startWith('itemtbl-tr')){
				namepre = 'model.otherTables['+cls.replace('itemtbl-tr','')+'].columns[0]';
			}
			addTblCols(items,namepre,'.'+cls);
		}});
		$('#addMtblColBtn').splitbutton({iconCls:'icon-add',menu:'#mtblColMenu'}).click(function(){
			var namepre = 'model.mainTable.columns[0]',cls=$('#dbtblselect').val();
			if(cls.startWith('itemtbl-tr')){
				namepre = 'model.otherTables['+cls.replace('itemtbl-tr','')+'].columns[0]';
			}
			addTblCols([{}],namepre,'.'+cls);
		});
		//删除按钮事件
		$('#delMtblColBtn').linkbutton({iconCls:'icon-remove',plain:true}).click(function(){
			$('.dbtbl-body .delChk:checked').each(function(){
				$(this).parent().parent().remove();
			});
			$('.dbtbl-body').each(function(){
				resetBtnTblIndex(this);
			});
		});
		//生成数据类型下拉框
		function getDataTypeSelect(name,val){
			var htm = ['<select name="'+name+'" value="'+val+'">'];
			for(var i=0,ary='<%=FastColumnType.types %>'.split(',');i<ary.length;i++){
				htm.push('<option value="'+ary[i]+'"'+(val==ary[i]?' selected':'')+'>'+ary[i]+'</option>');
			}
			htm.push('</select>');
			return htm.join('');
		}
		//添加指定行
		function addTblCols(items,namepre,container){
			var htm = [];
			for(var i=0;i<items.length;i++){
				htm.push('<tr>');
				htm.push(createIndexTd());
				pushInputTds(htm,items[i],namepre,'name,code'.split(','));
				htm.push('<td>'+getDataTypeSelect(namepre+'.type',items[i].type||'')+'</td>');
				pushInputTds(htm,items[i],namepre,'typeLen,defaults'.split(','));
				pushCheckboxTds(htm,items[i],namepre,'canNull,primaryKey'.split(','));
				pushInputTds(htm,items[i],namepre,'dictName,comment'.split(','));
				pushCheckboxTds(htm,items[i],namepre,'searchCondition,serachResultCol'.split(','));
				htm.push(createDelTd());
				htm.push('</tr>');
			}
			var trs = $(htm.join('')).appendTo($('.dbtbl-body',container));
			$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
			$(':checkbox.searchCondition',trs).click(function(){//调用menuEditSrhModel.jsp中定义的事件
				if(this.checked){
					$('.srhmodel-tr').trigger('addsrhcond',[$(this).parent().parent()]);
				}
			});
			$(':checkbox.serachResultCol',trs).click(function(){//调用menuEditSrhModel.jsp中定义的事件
				if(this.checked){
					$('.srhmodel-tr').trigger('addsrhresult',[$(this).parent().parent()]);
				}
			});
			resetBtnTblIndex($('.dbtbl-body',container));
		}
		function refreshTblCode(){
			if($(this).attr('name').endWith('.code')){
				var opt = $('#dbtblselect option[value='+($(this).data('trcls')||'maintbl-tr')+']');
				opt.text(opt.text().replace(/：.+/g,'：')+$(this).val());
			}
		}
		model.otherTables = model.otherTables||[];
		if(model.otherTables.length>0){
			for(var i=0;i<model.otherTables.length;i++){
				var cls = 'itemtbl-tr'+i;
				$('<option value="'+cls+'">从表：'+model.otherTables[i].name+'</option>').insertBefore('#dbtblselect option[value=createnewtbl]');
				var tr = $('.maintbl-tr').clone().removeClass('maintbl-tr').addClass(cls).insertAfter('.maintbl-tr').hide();
				//对表名等信息赋值
				setValueByName('.'+cls+' .dbtbl-info');
				('.dbtbl-info input[type=text]',tr).data('trcls',cls).bind('keyup paste blur',refreshTblCode);
				//将已有列信息显示
				addTblCols(model.otherTables[i].columns,'model.otherTables['+i+'].columns[0]','.'+cls);
			}
		}
		//数据库表下拉框选择事件
		$('#dbtblselect').change(function(){
			if($(this).val()=='createnewtbl'){
				var cls = 'itemtbl-tr'+model.otherTables.length;
				var tr = $('.maintbl-tr').clone().removeClass('maintbl-tr').addClass(cls).insertAfter('.maintbl-tr').show();
				$('.dbtbl-info input[type=text]',tr).val('').data('trcls',cls).bind('keyup paste blur',refreshTblCode);
				$('.dbtbl-body',tr).empty();
				$('<option value="'+cls+'">从表：NEW_TABLE'+model.otherTables.length+'</option>').insertBefore('#dbtblselect option[value=createnewtbl]');
				$('#dbtblselect').val(cls);
				model.otherTables.push({});
			}
			$('.dbtable-tr:visible').hide();
			$('.'+$(this).val()).show();
		});
		$('#dbtblselect option[value=maintbl-tr]').text('主表：'+model.mainTable.code);
		//对表名等信息赋值
		setValueByName('.maintbl-tr .dbtbl-info');
		$('.maintbl-tr .dbtbl-info input[type=text]').data('trcls','maintbl-tr').bind('keyup paste blur',refreshTblCode);
		//将已有列信息显示
		addTblCols(model.mainTable.columns,'model.mainTable.columns[0]','.maintbl-tr');
	});
</SCRIPT>
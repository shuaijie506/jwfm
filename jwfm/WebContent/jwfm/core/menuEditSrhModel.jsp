<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@page import="com.dx.jwfm.framework.core.dao.model.FastColumnType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<tr class=subtitle>
	<td colspan=6>查询功能设置  <a href="javascript:void(0)" id="addSrhCondBtn">添加查询条件</a>
	<a href="javascript:void(0)" id="delSrhCondBtn" class=delRowBtn>删除选中</a>
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
	<th>删除</th>
	</tr>
	</thead>
	<tbody class="dbtbl-body"></tbody>
	</table>
	</td>
</tr>
<div id="srhCondMenu" style="width:340px;">
</div>

<SCRIPT type=text/javascript>
	$(function(){
		var model = $('#editForm').data('model')||{};
		//处理添加按钮的菜单
		var presetMenus = <%=FastUtil.getRegVal("SYSMENU_PRESET_TBLCOLS_ARY") %>;
		$('#srhCondMenu').append(createButtonMenu(presetMenus)).menu({onClick:function(item){
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
		$('#addSrhCondBtn').splitbutton({iconCls:'icon-add',menu:'#srhCondMenu'}).click(function(){
			var namepre = 'model.mainTable.columns[0]',cls=$('#dbtblselect').val();
			if(cls.startWith('itemtbl-tr')){
				namepre = 'model.otherTables['+cls.replace('itemtbl-tr','')+'].columns[0]';
			}
			addTblCols([{}],null,namepre,'.'+cls);
		});
		//删除按钮事件
		$('#delSrhCondBtn').linkbutton({iconCls:'icon-remove',plain:true}).click(function(){
			$('.maintbl-tbl-body .delChk:checked').each(function(){
				$(this).parent().parent().remove();
			});
			resetBtnTblIndex('.maintbl-tbl-body');
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
		function addTblCols(items,namepre,container){console.log(namepre,container)
			var htm = [];
			//var namepre = 'model.mainTable.columns[0]';
			for(var i=0;i<items.length;i++){
				htm.push('<tr>');
				htm.push(createIndexTd());
				pushInputTds(htm,items[i],namepre,'name,code'.split(','));
				htm.push('<td>'+getDataTypeSelect(namepre+'.type',items[i].type||'')+'</td>');
				pushInputTds(htm,items[i],namepre,'typeLen,defaults'.split(','));
				htm.push('<td><input type=checkbox value=true name='+namepre+'.canNull '+(items[i].canNull?'checked':'')+' /></td>');
				htm.push('<td><input type=checkbox value=true name='+namepre+'.primaryKey '+(items[i].primaryKey?'checked':'')+' /></td>');
				pushInputTds(htm,items[i],namepre,'dictName,comment'.split(','));
				htm.push(createDelTd());
				htm.push('</tr>');
			}
			var trs = $(htm.join('')).appendTo($('.dbtbl-body',container));
			$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
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
		//addTblCols(model.mainTable.columns,'model.mainTable.columns[0]','.maintbl-tr');
	});
</SCRIPT>
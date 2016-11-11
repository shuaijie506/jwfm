<%@page import="com.dx.jwfm.framework.util.FastUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<tr class=subtitle>
	<td colspan=6>按钮和权限  <a href="javascript:void(0)" id="addBtnsBtn">添加</a>
	<a href="javascript:void(0)" id="delBtnsBtn" class=delRowBtn>删除选中</a></td>
</tr>
<tr>
	<td colspan=6>
	<table class=fast-child-table cellpadding="0" cellspacing="0">
	<colgroup>
	<col width="35px" />
	<col width="15%" />
	<col width="15%" />
	<col width="25%" />
	<col width="15%" />
	<col width="15%" />
	<col width="35%" />
	<col width="35px" />
	</colgroup>
	<thead>
	<tr>
	<th>序号</th>
	<th>按钮名称</th>
	<th>英文简写</th>
	<th>按钮对应JS方法名</th>
	<th>按钮ID</th>
	<th>按钮图标样式</th>
	<th>按钮或权限的说明信息</th>
	<th>删除</th>
	</tr>
	</thead>
	<tbody id="btn-tbl-body"></tbody>
	</table>
	</td>
</tr>
<div id="btnMenu" style="width:240px;">
</div>

<SCRIPT type=text/javascript>
	$(function(){
		var model = $('#editForm').data('model')||{};
		var presetMenus = <%=FastUtil.getRegVal("SYSMENU_PRESET_BTN_ARY") %>;
		$('#btnMenu').append(createButtonMenu(presetMenus)).menu({onClick:function(item){
			var ary = (item.names||'').split(',');console.log(ary)
			var items = [];
			for(var i=0;i<ary.length;i++){
				if(presetMenus[ary[i].trim()]){
					items.push(presetMenus[ary[i]]);
				}
			}
			addBtnRows(items);
		}});
		$('#addBtnsBtn').splitbutton({iconCls:'icon-add',menu:'#btnMenu'}).click(function(){
			addBtnRows([{}]);
		});
		$('#delBtnsBtn').linkbutton({iconCls:'icon-remove',plain:true}).click(function(){
			$('#btn-tbl-body .delChk:checked').each(function(){
				$(this).parent().parent().remove();
			});
			resetBtnTblIndex('#btn-tbl-body');
		});
		function addBtnRows(items){
			var htm = [];
			for(var i=0;i<items.length;i++){
				htm.push('<tr>');
				htm.push(createIndexTd());
				pushInputTds(htm,items[i],'model.buttonAuths[0]','name,code,funName,btnId,iconCls,note'.split(','));
				htm.push(createDelTd());
				htm.push('</tr>');
			}
			var trs = $(htm.join('')).appendTo('#btn-tbl-body');
			$('.index',trs).bind('blur keyup',function(){
				if(event.type=='blur' || event.keyCode==13)
					adjustTrByIndex($(this));
			});
			resetBtnTblIndex('#btn-tbl-body');
		}
		//将已有按钮组显示
		addBtnRows(model.buttonAuths);
	});
</SCRIPT>
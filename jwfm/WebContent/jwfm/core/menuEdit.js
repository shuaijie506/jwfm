function MenuList(ary,keyPropt){
	this.keyPropt = keyPropt||'code';
	this.length=0;
	this.put=function(item){
		if(this[item[this.keyPropt]]){
			for(var i=0;i<this.length;i++){
				if(this[i][this.keyPropt]==item[this.keyPropt]){
					this[i] = item;
					break;
				}
			}
		}
		else{
			this[this.length++] = item;
		}
		this[item[this.keyPropt]] = item;
		return this;
	};
	this.putAll=function(ary){
		if(!ary || !ary.length)return this;
		for(var i=0;i<ary.length;i++){
			this.put(ary[i]);
		}
		return this;
	};
	this.remove=function(item){
		var itemCode = typeof(item)=='string'?item:item[this.keyPropt];
		for(var i=0;i<this.length;i++){
			if(this[i][this.keyPropt]==itemCode){
				for(var j=i;j<this.length-1;j++){
					this[j]=this[j+1];
				}
				break;
			}
		}
		return this;
	};
	this.removeAll=function(ary){
		if(!ary || !ary.length)return this;
		for(var i=0;i<ary.length;i++){
			this.remove(ary[i]);
		}
		return this;
	};
	this.clear=function(ary){
		this.length=0;
	};
	this.attr = function(name,val){
		if(arguments.length==2){
			this[name]=val;
			return this;
		}
		else{
			return this[name];
		}
	};
	this.putAll(ary);
}
$.sysmenu = {
		presetBtnAry:new MenuList([//预设添加按钮及相应组合
				{code:'add',name:'添加',iconCls:'icon-add',note:'点击按钮后打开添加界面'},
				{code:'modify',name:'修改',iconCls:'icon-edit',note:'点击按钮后打开修改界面'},
				{code:'del',name:'删除',iconCls:'icon-remove',note:'点击按钮后删除选中的数据'},
				{code:'submitItem',name:'上报',iconCls:'icon-up',note:'点击按钮后打开上报界面'},
				{code:'checkItem',name:'审批',iconCls:'icon-ok',note:'点击按钮后打开审批界面'},
				{code:'exportExcel',name:'导出Excel',iconCls:'icon-excel',note:'点击按钮后导出当前查询结果'},
				{code:'downItem',name:'下发',iconCls:'icon-down',note:'点击按钮后打开下发界面'},
				{code:'dealItem',name:'处理',iconCls:'icon-xinwen',note:'点击按钮后打开处理界面'},
				{code:'closeItem',name:'销号',iconCls:'icon-ok',note:'点击按钮后打开销号界面'},
				{code:'importItem',name:'导入',iconCls:'icon-back',note:'点击按钮后打开导入界面'}
				
		]).attr('groupMenus','add,modify,del;add,modify,del,exportExcel;add,modify,del,submitItem,checkItem;add,modify,del,submitItem,checkItem,exportExcel'),
		presetTblcolAry:new MenuList([//增加数据库表时的预设列信息及相应组合
			{code:'VC_ID',name:'主键',type:'String',typeLen:'50',defaults:'',canNull:false,primaryKey:true,comment:''},
			{code:'VC_MID',name:'主表主键',type:'String',typeLen:'50',defaults:'',canNull:false,primaryKey:true,comment:''},
			{code:'N_STAT',name:'流程状态值',type:'Integer',typeLen:'',defaults:'',canNull:false,primaryKey:false,comment:'0 暂存 1处理中 9已闭环'},
			{code:'VC_STAT',name:'流程状态',type:'String',typeLen:'50',defaults:'',canNull:false,primaryKey:false,comment:''},
			{code:'N_DEL',name:'删除标记',type:'Integer',typeLen:'',defaults:'0',canNull:false,primaryKey:false,comment:'0 未删除 1已删除'},
			{code:'VC_ADD',name:'添加人',type:'String',typeLen:'50',defaults:'',canNull:false,primaryKey:false,comment:''},
			{code:'DT_ADD',name:'添加时间',type:'Date',typeLen:'',defaults:'nowTime',canNull:false,primaryKey:false,comment:''},
			{code:'VC_MODIFY',name:'修改人',type:'String',typeLen:'50',defaults:'',canNull:false,primaryKey:false,comment:''},
			{code:'DT_MODIFY',name:'修改时间',type:'Date',typeLen:'',defaults:'nowTime',canNull:false,primaryKey:false,comment:''}
				
		]).attr('groupMenus','VC_ID,N_DEL,VC_ADD,DT_ADD,VC_MODIFY,DT_MODIFY;VC_ID,N_STAT,VC_STAT,N_DEL,VC_ADD,DT_ADD,VC_MODIFY,DT_MODIFY;VC_ID,VC_MID,N_DEL,VC_ADD,DT_ADD;'),
		editorType:new MenuList([/*如果类型值可修改并扩展，请将类型值写入data属性。禁止使用数字做为value值*/
			{value:'text',text:'单行文本框'},
			{value:'textarea',text:'多行文本框'},
			{value:'date:',text:'格式化日期',data:'date:yyyy-MM-dd'},
			{value:'select:dict:',text:'字典下拉框',data:'select:dict:字典名称'},
			{value:'select:sql:',text:'SQL结果下拉框',data:'select:sql:SQL语句'},
			{value:'combobox:',text:'JSON动态下拉框',data:'combobox:{url:"JSON结果URL",valueField:"id",textField:"text"}'},
			{value:'combotree:',text:'JSON动态下拉树',data:'combotree:{url:"JSON结果URL"}'},
			{value:'html:',text:'自定义输入控件',data:'html:自定义HTML代码'}
		],'value'),
		searchType:new MenuList([/*如果在生成SQL时需要进行JAVA运算，则请实现SQLConditionParser接口并将类名配置到参数searchSQLConditionParser中。禁止使用数字做为value值*/
			{value:'=',text:'相等',func:function(field){return 'and t.'+field+'=${'+field+'}';}},
			{value:'in',text:'多选匹配',func:function(field){return 't.'+field;}},
			{value:'like',text:'模糊匹配',func:function(field){return 't.'+field;}},
			{value:'date>=',text:'日期>=',func:function(field){return 't.'+field;}},
			{value:'date<=',text:'日期<=',func:function(field){return 't.'+field;}},
			{value:'dateRange',text:'日期范围限定',func:function(field){return 't.'+field;}}
		],'value')
};
//调整序号并对下标赋值
function resetBtnTblIndex(container){
	$('.index',container).each(function(idx){
		$(this).val(idx+1);
	});
	$('>tr',container).each(function(idx){
		$('*[name]',this).each(function(){
			$(this).attr('name',$(this).attr('name').replace(/\[\d+?\]\.([\w_]+)$/,'['+idx+'].$1'));
		});
	});
}
function adjustTrByIndexEvt(){
	if(event.type=='blur' || event.keyCode==13)
		adjustTrByIndex($(this));
}
//根据序号列的值重新排序
function adjustTrByIndex(idx){
	var tr = idx.parent().parent();
	var myidx = parseFloat(idx.val());
	while(tr.prev().find('.index').length>0 && myidx<parseFloat(tr.prev().find('.index').val())){
		tr.insertBefore(tr.prev());
	}
	while(tr.next().find('.index').length>0 && myidx>parseFloat(tr.next().find('.index').val())){
		tr.insertAfter(tr.next());
	}
}
//根据输入框的name自动赋值
function setValueByName(container){
	var model = $('#editForm').data('model')||{};
	$('*[name]',container).each(function(){
		try{
			$(this).val(eval($(this).attr('name')));
		}catch(e){;}
	});
}
function createMenuHTML(menuData){
	var btnMenuHtm = [];
	for(var i=0;i<menuData.length;i++){
		var item = menuData[i];
		btnMenuHtm.push('<div data-options="names:\''+item.code+'\',iconCls:\''+(item.iconCls||'')+'\'">'+item.name+'</div>');
	}
	var btnGroup = [];
	for(var i=0,ary = (menuData.groupMenus||'').split(';');i<ary.length;i++){
		var codeary=[],textary = [];
		for(var j=0,row = ary[i].split(',');j<row.length;j++){
			if(menuData[row[j]]){
				codeary.push(menuData[row[j]].code);
				textary.push(menuData[row[j]].name);
			}
		}
		if(codeary.length>0){
			btnGroup.push('<div data-options="names:\''+codeary.join(',')+'\'">'+textary.join(' ')+'</div>');
		}
	}
	if(btnGroup.length>0){
		btnGroup.push('<div class="menu-sep"></div>');
	}
	return btnGroup.join('')+btnMenuHtm.join('');
}
//获得字段的默认编辑类型，可通过重写此方法对机构ID等特定名称的字段进行设置默认编辑框
function getDefaultEditorType(item){
	if(item.type=='Date'){
		return 'date:yyyy-MM-dd HH:mm';
	}
	else if(item.typeLen>=500){
		return 'textarea';
	}
	else if(',VC_ID,VC_MID,N_DEL,VC_ADD,DT_ADD,VC_MODIFY,DT_MODIFY,'.indexOf(','+item.code+',')>=0){
		return 'hidden';
	}
	else{
		return 'text';
	}
}
//{htmlAttrs:'name=htmlName class=myselect',value:'2',blankOption:true,data:[{value:'1',text:'一'},{value:'2',text:'二'}],dataValueField:'value',dataTextField:'text'}
function createSelectHTML(selObj){
	selObj = $.extend({blankOption:true,dataValueField:'value',dataTextField:'text'},selObj);
	var ary = selObj.data,htm=[];
	htm.push('<select');
	if(selObj.htmlAttrs){
		htm.push(' '+selObj.htmlAttrs);
	}
	htm.push('>');
	if(selObj.blankOption){
		htm.push('<option value=""></option>');
	}
	if(ary){
		if(ary.length>0){
			for(var i=0;i<ary.length;i++){
				var val = ary[i][selObj.dataValueField],txt=ary[i][selObj.dataTextField];
				ary[val] = ary[i];
				htm.push('<option value="'+val+'"'+(val==selObj.value?' selected':'')+'>'+txt+'</option>');
			}
		}
		else{
			for(var p in ary){
				var val = p,txt=ary[p];
				htm.push('<option value="'+val+'"'+(val==selObj.value?' selected':'')+'>'+txt+'</option>');
			}
		}
	}
	htm.push('</select>');
	return htm.join('');
}
//生成输入类型下拉框
function getEditorTypeSelect(name,val,showHidden){
	val = val||'';
	var htm = [];
	var ary = $.sysmenu.editorType||[];
	if(showHidden){
		ary = new MenuList([{value:'hidden',text:'隐藏域'}],'value').putAll(ary);
	}
	for(var i=0;i<ary.length;i++){
		ary[ary[i].value] = ary[i];
		ary[ary[i].data] = ary[i];
	}
	var selVal = (val||'').toString();
	if(selVal && !ary[selVal]){//如果是选择类型后又对内容进行了编辑的类型，则对下拉框的值进行处理
		for(var i=0;i<ary.length;i++){
			if(selVal.startWith(ary[i].value)){
				selVal = ary[i].value;
				break;
			}
		}
	}
	htm.push(createSelectHTML({htmlAttrs:'class=editortype',value:selVal,data:ary}));
	$.sysmenu.editorType = ary;
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
	else if($.sysmenu.editorType[sel.val()]){
		var item = $.sysmenu.editorType[sel.val()];
		txt.val(item.data||item.value||'');
	}
}
//HTML编辑器
function openHTMLEditorWin(opt){
	opt = $.extend({html:'',saveFun:null,cancelFun:null,divId:'htmlEditorWin',parWin:'#operateWindow',_content:'<textarea id=htmlEWBox></textarea>',
			butParams:[{id:'btn-htmlEWSave',text:'保存',iconCls:'icon-save'},{id:'btn-htmlEWCancel',text:'取消',iconCls:'icon-cancel'}]},opt);
	opt._content = '<textarea id=htmlEWBox></textarea>';
	$.openWin(opt);
	$('#htmlEWBox').val(opt.html);
	$('#btn-htmlEWSave').click(function(){
		if(opt.saveFun){
			opt.saveFun($('#htmlEWBox').val());
		}
	});
	$('#btn-htmlEWCancel').click(function(){
		if(opt.cancelFun){
			opt.cancelFun($('#htmlEWBox').val());
		}
	});
}
//创建表头区
function createThead(ary){
	var htm=['<colgroup>'],headHtm=['<thead>'];
	if(!ary || ary.length==0)return '';
	for(var i=0;i<ary.length;i++){
		htm.push('<col width="'+ary[i].width+'" />');
		headHtm.push('<th');
		if(ary[i].thAttrs){
			headHtm.push(' '+ary[i].thAttrs);
		}
		headHtm.push('>'+ary[i].text+'</th>');
	}
	return htm.join('')+'</colgroup>\n'+headHtm.join('')+'</thead>'
}
function createIndexTd(){return '<td><input type=text class=index /></td>';}
function createDelTd(){return '<td><input type=checkbox class=delChk /></td>';}
function pushInputTds(htmary,object,namepre,nameary){
	for(var i=0;i<nameary.length;i++){
		htmary.push('<td><input type=text name='+namepre+'.'+nameary[i]+' value="'+(object[nameary[i]]||'')+'" /></td>');
	}
}
function pushCheckboxTds(htmary,object,namepre,nameary){
	for(var i=0;i<nameary.length;i++){
		htmary.push('<td><input type=checkbox name='+namepre+'.'+nameary[i]+' class="'+nameary[i]+'" value=true'+(object[nameary[i]]?' checked':'')+' /></td>');
	}
}



//=====================================以下为按钮和权限部分的JS========================================
$(function(){
	$(createThead([{width:'35px',text:'序号'},
			{width:'15%',text:'按钮名称'},
			{width:'15%',text:'英文简写'},
			{width:'25%',text:'按钮对应JS方法名'},
			{width:'15%',text:'按钮ID'},
			{width:'15%',text:'按钮图标样式'},
			{width:'35%',text:'按钮或权限的说明信息'},
			{width:'35px',text:'删除'}
	])).insertBefore('#btn-tbl-body');
	var model = $('#editForm').data('model')||{};
	var presetMenus = $.sysmenu.presetBtnAry;
	for(var i=0;i<presetMenus.length;i++){
		var item = presetMenus[i];
		var icode = item.code.length<7?item.code+'Item':item.code;
		$.extend(item,{funName:icode,btnId:icode+'Btn',});
	}
	$('#btnMenu').append(createMenuHTML(presetMenus)).menu({onClick:function(item){
		var ary = (item.names||'').split(',');
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
		$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
		resetBtnTblIndex('#btn-tbl-body');
	}
	//将已有按钮组显示
	addBtnRows(model.buttonAuths);
});
//=====================================以下为业务表结构部分的JS========================================
$(function(){
	$(createThead([{width:'35px',text:'序号'},
			{width:'15%',text:'显示名称'},
			{width:'15%',text:'英文编码'},
			{width:'90px',text:'数据类型'},
			{width:'40px',text:'数据长度'},
			{width:'15%',text:'默认值'},
			{width:'35px',text:'是否为空'},
			{width:'35px',text:'是否主键'},
			{width:'15%',text:'字典项名称'},
			{width:'30%',text:'备注'},
			{width:'35px',text:'查询条件'},
			{width:'35px',text:'查询结果'},
			{width:'35px',text:'删除'}
	])).insertBefore('.dbtbl-body');
	$('#dbtable-title').click(function(){
		if($('.expand-icon',this).hasClass('tree-expanded')){
			$('#dbtblselect').change();
		}
	});
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
		for(var i=0,ary=dbDataTypes.split(',');i<ary.length;i++){
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
		$('.expand-icon',$(this).parent()).addClass('tree-expanded').removeClass('tree-collapsed');
		if($(this).val()=='createnewtbl'){
			var cls = 'itemtbl-tr'+model.otherTables.length;
			var tr = $('.maintbl-tr').clone().removeClass('maintbl-tr').addClass(cls).insertAfter('.maintbl-tr').show();
			$('.dbtbl-info input[type=text]',tr).val('').data('trcls',cls).bind('keyup paste blur',refreshTblCode);
			$('.dbtbl-body',tr).empty();
			$('<option value="'+cls+'">从表：NEW_TABLE'+model.otherTables.length+'</option>').insertBefore('#dbtblselect option[value=createnewtbl]');
			$('#dbtblselect').val(cls);
			model.otherTables.push({columns:[]});
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
//=====================================以下为查询信息、查询条件、查询结果部分的JS========================================
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
//=====================================以下为独立页面设置（编辑、查看等页面）部分的JS========================================
$(function(){
	var model = $('#editForm').data('model')||{};
	$(createThead([{width:'35px',text:'序号'},
			{width:'20%',text:'显示标题'},
			{width:'20%',text:'字段编码'},
			{width:'60%',text:'输入类型'}
	])).insertBefore('.edittbl-cols-body');
	$(createSelectHTML({htmlAttrs:'id="pageMapSel" style="width:200px"',blankOption:false,data:model.pageHTMLAry,dataValueField:'id'})).appendTo('#pageMapTitle');
	$('<div id=pageMapDiv></div>').appendTo('#pageMapTitle');
	function updatePageHTMLSelect(){//更新下拉框中的内容事件
		var span = $(this).parent();
		$('#pageMapSel option:selected').val($('input[name$=id]',span).val()).text($('input[name$=name]',span).val());
		$('textarea',span).attr('code',$('input[name$=id]',span).val());
	}
	function removePage(){//删除页面的图标按钮事件
		$.messager.confirm('提示','删除后不可恢复，请慎重操作！确定要删除此页面吗？',function(r){
			if(r){
				$(this).parent().remove();
				$('#pageMapSel option:selected').remove();
			}
		});
	}
	function addPage(item){//添加新页面
		var span = $('<span>编码：<input type=text name=model.pageHTMLAry['+i+'].id />标题：<input type=text name=model.pageHTMLAry['+i+
				'].text /><span class="delPage icon icon-remove" title="点击后删除此页面"></span><textarea code="'+item.id+
				'" name=model.pageHTMLAry['+i+'].data style="display:none;" ></textarea></span>').appendTo('#pageMapDiv');
		$('input',span).bind('type keyup paste blur',updatePageHTMLSelect);
		$('span.delPage',span).bind('click',removePage);
	}
	for(var i=0;i<model.pageHTMLAry.length;i++){
		addPage(model.pageHTMLAry[i]);
	}
	//对页面隐藏域进行赋值
	setValueByName('#pageMapDiv');
	//页面选择框的选择事件
	$('#pageMapSel').append('<option value="==createNode==">==添加新页面==</option>').change(function(){
		var code = $('#pageMapSel').val();
		if(code=='==createNode=='){//添加新页面
			$.messager.confirm('提示','请输入新页面的编码和显示名称：<br/>页面编码：<input type=text id="newNodeCode" style="width:200px;" /><br/>页面名称：<input type=text id="newNodeName" style="width:200px;" /><br/>'+
					'克隆页面：<select id="newNodeSrc" style="width:200px;"></select>',function(r){
				if(r){
					code = $('#newNodeSrc').val();
					var txt = $('#pageMapDiv textarea[code='+code+']');
					$('<option>'+$('#newNodeName').val()+'</option>').insertBefore('#pageMapSel>option:last').attr('value',code);
					addPage({id:code,text:$('#newNodeName').val(),data:((txt&&txt.val())||'')});
					$('#pageMapSel').val(code).trigger('changeView');
				}
			}).find('.messager-icon').remove();
			$('#newNodeSrc').html($('#pageMapSel').html()).prepend('<option></option>').find('option:last').remove();
			return;
		}
		$(this).trigger('changeView');
	}).bind('changeView',function(){
		//切换已有页面
		var code = $('#pageMapSel').val();
		var txt = $('#pageMapDiv textarea[code='+code+']');
		$('#pageMapDiv>span.selected').removeClass('selected');
		txt.parent().addClass('selected');
		$('#pageHTMLDiv').data('textarea',txt).html(txt.val());
	}).change();
	//添加指定行
	function addEditTblCols(items,namepre){
		var htm = [];
		var len = $('.edittbl-cols-body>tr').length;
		for(var i=0;i<items.length;i++){
			htm.push('<tr code="'+items[i].code+'">');
			htm.push('<td>'+(len+i+1)+'</td>');
			htm.push('<td class=name>'+items[i].name+'</td>');
			htm.push('<td>'+items[i].code+'</td>');
			htm.push('<td class=selecttextarea>'+getEditorTypeSelect('model.mainTable.columns['+items[i].index+'].editorType',
					items[i].editorType||getDefaultEditorType(items[i]),true)+'</td>');
			htm.push('</tr>');
		}
		var trs = $(htm.join('')).appendTo('.edittbl-cols-body');
		$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);//序号修改后重新排序事件
		$('select.editortype',trs).change(editorTypeChange);//编辑类型下拉框选中事件
	}
	//根据业务表的内容刷新业务表对应输入控件列表
	function refreshDbEditorPanel(){
		if($('#pageMapTitle:visible').length==0){//修改页面关闭后，清除定时器
			clearInterval(editorHandel);
		}
		if($('#dbtbl-editor-sel:visible').length==0 && $('#dbtbl-editor-sel').val()){//页面初始化和页面展开可见时再处理
			return;
		}
		var nowsel = $('#dbtbl-editor-sel').val();
		$('#dbtbl-editor-sel').html($('#dbtblselect').html()).val(nowsel||$('#dbtbl-editor-sel option:first').val());
		$('#dbtbl-editor-sel option:last').remove();//刷新数据库表的下拉框选项
		var tblCls = $('#dbtbl-editor-sel').val();
		var oldary = $('#dbtbl-editor-sel').data('tblColsAry')[tblCls]||[];
		var ary = [],addedAry=[];
		var columns = tblCls=='maintbl-tr'?model.mainTable.columns:model.otherTables[tblCls.replace(/.*\D+/g,'')].columns;
		for(var i=0;i<columns.length;i++){
			columns[columns[i].code] = columns[i];
		}
		var changed = false;
		$('.'+tblCls+' .dbtbl-body input[name$=\\.code]').each(function(){
			var code = $(this).val();
			var item = columns[code]||{code:code,name:$(this).parent().parent().find('input[name$=\\.name]').val(),editorType:''};
			columns[code] = item;
			item.index = $(this).attr('name').replace(/.*\[(\d+)\].*/g,'$1');
			ary.push(item);
			ary[code] = item;
			if(!oldary[code]){
				if($('.edittbl-cols-body tr[code='+code+']').length>0){//如果原来已经有此行了，则去掉删除线
					$('.edittbl-cols-body tr[code='+code+']').removeClass('delete');
				}
				else{//列表中没有的时候添加到数组中，在后期统一添加行
					addedAry.push(item);
				}
			}
			if(oldary[code] && (oldary[code].name!=item.name || oldary[code].index!=item.index)){//更新名称和编辑类型的数组下标
				$('.edittbl-cols-body tr[code='+code+'] .name').text(item.name);
				var txt = $('.edittbl-cols-body tr[code='+code+'] textarea[name$=editorType]');
				txt.attr('name',txt.attr('name').replace(/\[\d+\]\.editorType/g,'['+item.index+'].editorType'));
			}
		});
		$('#dbtbl-editor-sel').data('tblColsAry')[tblCls] = ary;
		for(var i=0;i<oldary.length;i++){//将原来有但现在没有的列添加删除线
			if(!ary[oldary[i].code]){
				$('.edittbl-cols-body tr[code='+oldary[i].code+']').addClass('delete');
			}
		}
		if(addedAry.length>0){
			addEditTblCols(addedAry,'');
		}
	}
	$('#dbtbl-editor-sel').data('tblColsAry',{}).change(function(){
		$('.edittbl-cols-body').empty();
		refreshDbEditorPanel();
	});
	var editorHandel = setInterval(refreshDbEditorPanel,1000);
	//====================以下为表格编辑器的功能===================
	var btns = [
		{id:'tbltoolbar-recreate',icon:'fa-table',text:'重新设定表格列数',func:function(){
			
		}},
		{id:'tbltoolbar-undo',icon:'fa-reply',text:'撤消',func:function(){}},
		{id:'tbltoolbar-redo',icon:'fa-share',text:'重复',func:function(){}},
		{id:'tbltoolbar-editstyle',icon:'fa-chain',text:'设置所选单元格样式，不选任何单元格时设置表格样式',func:function(){}},
		{id:'tbltoolbar-htmledit',icon:'fa-edit',text:'以HTML格式编辑',func:function(){
			var tds = $('#pageHTMLDiv .selected');
			if(tds.length==0){//编辑整个表格
				
			}
			if(tds.length==1){//编辑整个表格
				
			}
			else{
				$.messager.alert('提示','每次仅能编辑一个单元格，如需编辑整个表格，请不要选中任何单元格！');
			}
		}},
		{id:'tbltoolbar-addcol',icon:'fa-plus-square',text:'单元格增加占用列数',func:function(){}},
		{id:'tbltoolbar-removecol',icon:'fa-minus-square',text:'单元格减少占用列数',func:function(){}},
		{id:'tbltoolbar-subtitle',icon:'fa-toggle-on',text:'切换当前行为副标题',func:function(){}},
		{id:'tbltoolbar-thtd',icon:'fa-retweet',text:'切换单元格类别（标签/显示值）',func:function(){
			$('#pageHTMLDiv .selected').toggleClass('th');
		}},
		{id:'tbltoolbar-thtd',icon:'fa-object-group',text:'绑定数据输入控件',func:function(){}},
		{id:'tbltoolbar-thtd',icon:'fa-object-ungroup',text:'绑定数据显示值',func:function(){}}
	];
	for(var i=0;i<btns.length;i++){
		$('<a id="'+btns[i].id+'" class="fa '+btns[i].icon+'" title="'+btns[i].text+'"></a>').appendTo('.page-toolbar').click(btns[i].func);
	}
	$('#pageHTMLDiv').mouseover(function(){
		//禁止日期弹出窗口
		top.$('div[lang]:visible').each(function(){
			if(top.$('>iframe',this).length==1){
				top.$(this).hide();
			}
		});
	}).click(function(){
		var evt = $.event.fix(event);
		var src = $(evt.target),td=src.filter('td,th').length>0?src:$(src.parents('th,td')[0]);
		if(td.length==1){//单元格点击后选中事件
			if(!evt.ctrlKey){
				$('#pageHTMLDiv .selected').not(td[0]).removeClass('selected')
			}
			td.toggleClass('selected');
		}
	});
	if($('#editTableDiv>table').length==0){
		$('#createEditTable').click();
	}
});
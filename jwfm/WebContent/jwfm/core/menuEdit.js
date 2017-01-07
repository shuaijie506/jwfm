//自定义菜单的数据模型
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
//初始化菜单管理中用到的各类数据
function initSysMenuPresetData(){
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
			presetForwardAry:new MenuList([//预设添加JSP转向页面菜单及相应组合
				{code:'success',name:'success',uri:'/jwfm/core/easyuiSearch.jsp'},
				{code:'openAddPage',name:'openAddPage',uri:'/jwfm/core/easyuiEdit.jsp'},
				{code:'openModifyPage',name:'openModifyPage',uri:'/jwfm/core/easyuiEdit.jsp'},
				{code:'openViewPage',name:'openViewPage',uri:'/jwfm/core/easyuiView.jsp'}
				]).attr('groupMenus','success,openAddPage,openModifyPage,openViewPage'),
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
				{value:'hidden',text:'隐藏域',handle:function(field){return '<input type=hidden id="'+field.replace(/\./g,'_')+'" name="'+field+'" value="${'+field+'}" />';}},
				{value:'text',text:'单行文本框',handle:function(field){return '<input type=text id="'+field.replace(/\./g,'_')+'" name="'+field+'" value="${'+field+'}" />';}},
				{value:'textarea',text:'多行文本框',handle:function(field){return '<textarea id="'+field.replace(/\./g,'_')+'" name="'+field+'" >${'+field+'}</textarea>';}},
				{value:'date:',text:'格式化日期',handle:function(field){return '<input type=text id="'+field.replace(/\./g,'_')+'" name="'+field+'" value="${'+field+':yyyy-MM-dd HH:mm}" class="Wdate" onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm\'})" />';}},
				{value:'select:dict:',text:'字典下拉框',handle:function(field){return '${$select$'+field+':dict:字典名称}';}},
				{value:'select:sql:',text:'SQL结果下拉框',handle:function(field){return '${$select$'+field+':sql:SQL语句}';}},
				{value:'combobox:',text:'JSON动态下拉框',handle:function(field){return '<input type=text id="'+field.replace(/\./g,'_')+'" name="'+field+'" value="${'+field+'}" />\n<script>$(\'#'+field.replace(/\./g,'_')+'\').combobox({url:"JSON结果URL",valueField:"id",textField:"text"});</script>';}},
				{value:'combotree:',text:'JSON动态下拉树',handle:function(field){return '<input type=text id="'+field.replace(/\./g,'_')+'" name="'+field+'" value="${'+field+'}" />\n<script>$(\'#'+field.replace(/\./g,'_')+'\').combotree({url:"JSON结果URL",valueField:"id",textField:"text"});</script>';}},
				{value:'file:',text:'文件上传',handle:function(field){return '<input type=text id="'+field.replace(/\./g,'_')+'" name="'+field+'" value="${'+field+'}" />\n<script>$(\'#'+field.replace(/\./g,'_')+'\').fileupload({fileType:"当前模块英文简称",multiple:true,maxFileSize:100*1024*1024,maxFileCount:10,allowExt:""});</script>';}},
				{value:'html:',text:'自定义输入控件',handle:function(field){return '<input type=text id="'+field.replace(/\./g,'_')+'" name="'+field+'" value="${'+field+'}" />\n<script>$(\'#'+field.replace(/\./g,'_')+'\');</script>';}}
			],'value'),
			searchType:new MenuList([/*如果在生成SQL时需要进行JAVA运算，则请实现SQLConditionParser接口并将类名配置到参数searchSQLConditionParser中。禁止使用数字做为value值*/
				{value:'=',text:'相等',handle:function(field){return 'and t.'+field+'=${'+field+'}';}},
				{value:'in',text:'多选匹配',handle:function(field){return 't.'+field;}},
				{value:'like',text:'模糊匹配',handle:function(field){return 't.'+field;}},
				{value:'date>=',text:'日期>=',handle:function(field){return 't.'+field;}},
				{value:'date<=',text:'日期<=',handle:function(field){return 't.'+field;}},
				{value:'dateRange',text:'日期范围限定',handle:function(field){return 't.'+field;}}
			],'value'),
			commonJsList:[
				{title:'下拉框变化时执行查询操作',handle:function(field){return '$("#'+field+'").change(function(){$("#search").click();});';}}
			],
			//独立页面中需要隐藏的列
			editPageHiddenCols:'VC_ID,VC_MID,N_DEL,VC_ADD,DT_ADD,VC_MODIFY,DT_MODIFY'.split(',')
	};
}
initSysMenuPresetData();
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
//根据指定的序号值调整行的显示顺序
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
function setValueByName(container,model){
	model = model||$('#editForm').data('model')||{};
	$('*[name]',container).each(function(){
		try{
			$(this).val(eval($(this).attr('name')));
		}catch(e){;}
	});
}
//根据菜单的数据模型创建菜单
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
function getDefaultEditor(item){
	var obj = $.sysmenu.editorType;
	if(item.type=='Date'){
		return obj['date'];
	}
	else if(item.typeLen>=500){
		return obj['textarea'];
	}
	else if($.sysmenu.editPageHiddenCols.indexOf(item.name)>=0){
		return obj['hidden'];
	}
	else{
		return obj['text'];
	}
}
//根据指定参数创建一个select的HTML对象
//{htmlAttrs:'name=htmlName class=myselect',value:'2',blankOption:true,data:[{value:'1',text:'一'},{value:'2',text:'二'}],dataValueField:'value',dataTextField:'text'}
function createSelectHTML(selObj){
	selObj = $.extend({blankOption:true,dataValueField:'value',dataTextField:'text',skipValue:{}},selObj);
	var ary = selObj.data,htm=[];
	htm.push('<select');
	if(selObj.htmlAttrs){
		htm.push(' '+selObj.htmlAttrs);
	}
	htm.push('>');
	if(selObj.blankOption){
		htm.push('<option value=""></option>');
	}
	function pushOption(htm,val,txt,selVal){
		if(selObj.skipValue && selObj.skipValue[val])return;
		htm.push('<option value="'+val+'"'+(val==selVal?' selected':'')+'>'+txt+'</option>');
	}
	if(ary){
		if(ary.length>0){
			for(var i=0;i<ary.length;i++){
				var val = typeof(ary[i])=='string'?ary[i]:ary[i][selObj.dataValueField],txt=typeof(ary[i])=='string'?ary[i]:ary[i][selObj.dataTextField];
				ary[val] = ary[i];
				pushOption(htm,val,txt,selObj.value)
			}
		}
		else{
			for(var p in ary){
				var val = p,txt=ary[p];
				pushOption(htm,val,txt,selObj.value)
			}
		}
	}
	htm.push('</select>');
	return htm.join('');
}
//生成输入类型下拉框
function getEditorTypeSelect(name,val,fieldName,showHidden){
	val = val||'';
	var htm = [];
	var ary = $.sysmenu.editorType||[];
	if(showHidden){
		ary = new MenuList([{value:'hidden',text:'隐藏域',handle:function(field){return '<input type=hidden id="'+field.replace(/\./g,'_')+'" name="'+field+'" value="${'+field+'}" />';}}],'value').putAll(ary);
	}
	for(var i=0;i<ary.length;i++){
		ary[ary[i].value] = ary[i];
	}
	var selVal = (val||'').toString();
	htm.push(createSelectHTML({htmlAttrs:'class=editortype fieldName='+(fieldName||''),value:selVal,data:ary,skipValue:{'hidden':showHidden}}));
	$.sysmenu.editorType = ary;
	htm.push('<textarea name="'+name+'" class="seltextarea" title="${}中如果要使用}请使用& # 125;">'+escapeValue(val)+'</textarea>');
	return htm.join('');
}
//输入类型下拉框选择事件
function editorTypeChange(){
	var sel=$(this),txt=sel.nextAll('textarea');
	if(!txt.data('srcval') && txt.val()){
		txt.data('srcval',txt.val());
		$('option:first',sel).val('').text('==恢复原始值==');
	}
	if(sel.val()==''){
		txt.val(txt.data('srcval')||'');
	}
	else if($.sysmenu.editorType[sel.val()]){
		var fieldName = sel.attr('fieldName')||'';
		var item = $.sysmenu.editorType[sel.val()];
		txt.val(item.handle.call(sel,fieldName)||'');
	}
}
//加载系统定义的宏名称
function loadMacrosMenu(){
	if($('#macrosMenuDiv').length==0){
		$('<div id=macrosMenuDiv></div>').appendTo('body').hide();
		$.getJSON(path+'/tools_loadMacroListJson.action',function(ary){
			var htm = [];
			for(var i=0;i<ary.length;i++){
				var code = '${'+ary[i].code+'}';
				htm.push('<div data-options="code:\''+code+'\'">'+code+' '+ary[i].name+'</div>');
			}
			$('#macrosMenuDiv').html(htm.join(''));
		});
	}
}
//显示系统定义的宏名称菜单
function showMacrosMenu(clickFun){
	if($('#macrosMenuDiv').length==0){
		$('<div id=macrosMenuDiv></div>').appendTo('body');
	}
	var evt = $.event.fix(event);
	$('#macrosMenuDiv').show().menu({onClick:clickFun}).menu('show',{left:evt.pageX,top:evt.pageY});
}
//显示系统定义的宏名称菜单
function showCommonJsMenu(clickFun){
	if($('#commonJsMenuDiv').length==0){
		$('<div id=commonJsMenuDiv></div>').appendTo('body');
		var htm = [],ary = $.sysmenu.commonJsList;
		for(var i=0;i<ary.length;i++){
			htm.push('<div data-options="idx:'+i+'">'+ary[i].title+'</div>');
		}
		$('#commonJsMenuDiv').html(htm.join(''));
	}
	var evt = $.event.fix(event);
	$('#commonJsMenuDiv').data('field',$(this).attr('field')).show().menu({onClick:function(item){
		$.extend(item,$.sysmenu.commonJsList[item.idx]);
		clickFun(item);
	}}).menu('show',{left:evt.pageX,top:evt.pageY});
}
//打开一个HTML编辑器窗口
function openHTMLEditorWin(opt){
	opt = $.extend({html:'',codetype:'htmlmixed',saveFun:null,cancelFun:null,divId:'htmlEditorWin',parWin:'#operateWindow',
			_content:'<textarea id=htmlEWBox></textarea>',title:'代码编辑窗口',
			butParams:[{id:'btn-htmlEWSave',text:'保存',iconCls:'icon-save'},{id:'btn-htmlEWCancel',text:'取消',iconCls:'icon-cancel'}]},opt);
	opt._content = '<div id=htmlEWBox></div>';
	$.openWin(opt);
	window.editor = CodeMirror($('#htmlEWBox')[0],{value:opt.html,lineNumbers:true,mode:opt.codetype,theme:'eclipse',
		tabSize:2,lineWrapping: true
	});
	if(opt.selectAll){
		editor.setSelection({line:0,ch:0},{line:editor.lastLine(),ch:9999});
	}
	$('#btn-htmlEWSave').click(function(){
		if(opt.saveFun){
			opt.saveFun(editor.getValue());
		}
		$('#htmlEditorWin').window('close');
	});
	$('#btn-htmlEWCancel').click(function(){
		if(opt.cancelFun){
			opt.cancelFun(editor.getValue());
		}
		$('#htmlEditorWin').window('close');
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
		htmary.push('<td><input type=text name='+namepre+'.'+nameary[i]+' value="'+escapeValue(object[nameary[i]]||'')+'" /></td>');
	}
}
function escapeValue(str){
	return (str||'').toString().replace(/"/g,'&#34;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}
function pushCheckboxTds(htmary,object,namepre,nameary){
	for(var i=0;i<nameary.length;i++){
		htmary.push('<td><input type=checkbox name='+namepre+'.'+nameary[i]+' class="chk-'+nameary[i]+'" value=true'+(object[nameary[i]]?' checked':'')+' /></td>');
	}
}
function tableTrHover(selecter){
	$(selecter).bind('mouseover',function(){//鼠标经过时整行变色
		var src=$(event.srcElement).closest('tr');
		if(src.find('tr').length==0){
			$('tr.hover').removeClass('hover');
			src.addClass('hover');
		}
	}).bind('mouseout',function(){
		$('tr.hover').removeClass('hover');
	});	
}


//=====================================以下为按钮和权限部分的JS========================================
function loadBtns(){
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
	if(model.newMenu){
		$('#btnMenu>.menu-item:first').click();
	}
}

//=====================================以下为控制类及相关信息部分的JS========================================
function loadForwards(){
	$(createThead([{width:'35px',text:'序号'},
		{width:'30%',text:'缩写'},
		{width:'70%',text:'JSP文件路径'},
		{width:'35px',text:'删除'}
		])).insertBefore('#forward-tbl-body');
	var model = $('#editForm').data('model')||{};
	var presetMenus = $.sysmenu.presetForwardAry;
	function transNode(item){
		return {key:item.code,value:item.uri};
	}
	$('#forwardMenu').append(createMenuHTML(presetMenus)).menu({onClick:function(item){
		var ary = (item.names||'').split(',');
		var items = [];
		for(var i=0;i<ary.length;i++){
			if(presetMenus[ary[i].trim()]){
				items.push(transNode(presetMenus[ary[i]]));
			}
		}
		addForwardRows(items);
	}});
	$('#addForwardBtn').splitbutton({iconCls:'icon-add',menu:'#forwardMenu'}).click(function(){
		addForwardRows([{}]);
	});
	$('#delForwardBtn').linkbutton({iconCls:'icon-remove',plain:true}).click(function(){
		$('#forward-tbl-body .delChk:checked').each(function(){
			$(this).parent().parent().remove();
		});
		resetBtnTblIndex('#forward-tbl-body');
	});
	function useDefaultPage(){
		var th = $(this),tr=th.parent().parent();
		var code = $('input[name$=key]',tr).val();
		var item = $.sysmenu.presetForwardAry[code];
		if(!item){
			$.util.showTip({content:'此转向没有默认页面'});return;
		}
		$(this).prevAll().val(item.uri);
	}
	function useUserPage(){
		var th = $(this),tr=th.parent().parent();
		var code = $('input[name$=key]',tr).val().replace(/^open/,'').replace(/Page$/,'');
		code = code=='success'?'Search':code;
		var url = $('#po_VC_URL').val().replace(/\.action/,'')+code.substr(0,1).toUpperCase()+code.substr(1)+'.jsp';
		$(this).prevAll().val(url);
	}
	function addForwardRows(items){
		var htm = [];
		for(var i=0;i<items.length;i++){
			htm.push('<tr>');
			htm.push(createIndexTd());
			pushInputTds(htm,items[i],'model.forwards[0]','key,value'.split(','));
			htm.push(createDelTd());
			htm.push('</tr>');
		}
		var trs = $(htm.join('')).appendTo('#forward-tbl-body');
		$('input[name$=value]',trs).css('width','90%').after('<i class="opt-icon fa fa-home" title="使用默认页面"></i><i class="opt-icon fa fa-plus-circle" title="使用自定义页面"></i>');
		$('.opt-icon.fa-home').click(useDefaultPage);
		$('.opt-icon.fa-plus-circle').click(useUserPage);
		$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
		resetBtnTblIndex('#forward-tbl-body');
	}
	//对类名信息赋值
	setValueByName('.classaction-info');
	$('<i class="opt-icon fa fa-plus-circle" title="创建新的控制类"></i>').insertAfter('.classaction-info input[name$=actionName]').click(function(){
		var model = $('#editForm').data('model')||{};
		var url = $('#po_VC_URL').val();
		var pos = url.lastIndexOf('/');
		url = url.substr(pos+1,1).toUpperCase()+url.substr(pos+2);
		$('.classaction-info input[name$=actionName]').val((model.packageName||'com.xxx')+'.action.'+url+'Action');
	});
	$('<i class="opt-icon fa fa-home" title="使用默认控制类"></i>').insertAfter('.classaction-info input[name$=actionName]').click(function(){
		$('.classaction-info input[name$=actionName]').val('com.dx.jwfm.framework.web.action.FastBaseAction');
	});
	$('<i class="opt-icon fa fa-plus-circle" title="创建新的查询类"></i>').insertAfter('.classaction-info input[name$=searchClassName]').click(function(){
		var model = $('#editForm').data('model')||{};
		var url = $('#po_VC_URL').val();
		var pos = url.lastIndexOf('/');
		url = url.substr(pos+1,1).toUpperCase()+url.substr(pos+2);
		$('.classaction-info input[name$=actionName]').val((model.packageName||'com.xxx')+'.search.'+url+'Search');
	});
	$('<i class="opt-icon fa fa-home" title="使用默认查询类"></i>').insertAfter('.classaction-info input[name$=searchClassName]').click(function(){
		$('.classaction-info input[name$=searchClassName]').val('');
	});
	//将已有按钮组显示
	function toArray(obj){
		var ary = [];
		for(var key in obj){
			ary.push({key:key,value:obj[key]});
		}
		return ary;
	}
	addForwardRows(toArray(model.forwards));
	if(model.newMenu){
		$('#forwardMenu>.menu-item:first').click();
	}
}
//=====================================以下为业务表结构部分的JS========================================
function loadDbTables(){
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
	//添加指定行
	function addTblCols(items,namepre,container){
		var htm = [];
		for(var i=0;i<items.length;i++){
			htm.push('<tr>');
			htm.push(createIndexTd());
			pushInputTds(htm,items[i],namepre,'title,name'.split(','));
			htm.push('<td>'+createSelectHTML({htmlAttrs:'name='+namepre+'.type',value:items[i].type||'',data:$.sysmenu.dbDataTypes,blankOption:false})+
					'<input type=hidden name='+namepre+'.editorType value="'+escapeValue(items[i].editorType||'')+'" /></td>');
			pushInputTds(htm,items[i],namepre,'typeLen,defaults'.split(','));
			pushCheckboxTds(htm,items[i],namepre,'canNull,primaryKey'.split(','));
			pushInputTds(htm,items[i],namepre,'dictName,comment'.split(','));
			pushCheckboxTds(htm,items[i],namepre,'searchCondition,serachResultCol'.split(','));
			htm.push(createDelTd());
			htm.push('</tr>');
		}
		var trs = $(htm.join('')).appendTo($('.dbtbl-body',container));
		$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
		$('.chk-searchCondition',trs).click(function(){//调用menuEditSrhModel.jsp中定义的事件
			if(this.checked){
				$('.srhmodel-tr').triggerHandler('addsrhcond',[$(this).parent().parent()]);
			}
		});
		$('.chk-serachResultCol',trs).click(function(){//调用menuEditSrhModel.jsp中定义的事件
			if(this.checked){
				$('.srhmodel-tr').triggerHandler('addsrhresult',[$(this).parent().parent()]);
			}
		});
		$('input[name$=defaults]',trs).css('width','92%').after('<i class="fa fa-map-marker macroicon"></i>');
		$('.macroicon',trs).click(function(){
			showMacrosMenu($.proxy(function(item){
				$(this).prev().val(item.code);
			},this));
		});
		resetBtnTblIndex($('.dbtbl-body',container));
	}
	function refreshTblCode(){
		if($(this).attr('name').endWith('.name')){
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
			$('.dbtbl-info input[name$=name]',tr).attr('readonly',false);
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
			$('.dbtbl-info input[name$=name]',tr).attr('readonly',false);
			$('.dbtbl-info input[type=text]',tr).val('').data('trcls',cls).bind('keyup paste blur',refreshTblCode);
			$('.dbtbl-body',tr).empty();
			$('<option value="'+cls+'">从表：NEW_TABLE'+model.otherTables.length+'</option>').insertBefore('#dbtblselect option[value=createnewtbl]');
			$('#dbtblselect').val(cls);
			model.otherTables.push({columns:[]});
		}
		$('.dbtable-tr:visible').hide();
		$('.'+$(this).val()).show();
	});
	$('#dbtblselect option[value=maintbl-tr]').text('主表：'+model.mainTable.name);
	//对表名等信息赋值
	setValueByName('.maintbl-tr .dbtbl-info');
	$('.maintbl-tr .dbtbl-info input[type=text]').data('trcls','maintbl-tr').bind('keyup paste blur',refreshTblCode);
	//将已有列信息显示
	addTblCols(model.mainTable.columns,'model.mainTable.columns[0]','.maintbl-tr');
}
//=====================================以下为查询信息、查询条件、查询结果部分的JS========================================
function loadSearchInfo(){
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
		$('textarea[name$=sqlFragment]',tr).val(item&&item.handle?item.handle.call(this,vcCode):'t.'+vcCode);
	}
	//添加指定行
	function addTblCols(items,namepre){
		var htm = [];
		for(var i=0;i<items.length;i++){
			htm.push('<tr>');
			htm.push(createIndexTd());
			pushInputTds(htm,items[i],namepre,'vcTitle,vcCode'.split(','));
			htm.push('<td class=selecttextarea>'+getEditorTypeSelect(namepre+'.vcEditorType',items[i].vcEditorType||'','search.'+items[i].vcCode)+'</td>');
			htm.push('<td><textarea style="width:90%;" name='+namepre+'.vcEditorJs >'+escapeValue(items[i].vcEditorJs||'')+'</textarea><i class="jsicon fa fa-map-marker"></i></td>');
			pushInputTds(htm,items[i],namepre,'defaults'.split(','));
			htm.push('<td>'+getSearchTypeSelect(namepre+'.sqlSearchType',items[i].sqlSearchType||'')+'</td>');
			htm.push('<td><textarea name='+namepre+'.sqlFragment >'+escapeValue(items[i].sqlFragment||'')+'</textarea></td>');
			htm.push(createDelTd());
			htm.push('</tr>');
		}
		var trs = $(htm.join('')).appendTo('.srhcond-body');
		$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
		$('input[name$=vcCode]',trs).bind('type keyup paste blur',function(){$(this).parent().parent().find('select.editortype').attr('fieldName','search.'+$(this).val());});
		$('select.editortype',trs).change(editorTypeChange);
		$('select.searchtype',trs).change(searchTypeChange).tooltip({content:'多选、模糊匹配、日期相关的过滤条件在后台查询时会自动根据数据库类型进行函数转换'});
		$('input[name$=defaults]',trs).css('width','90%').after('<i class="fa fa-map-marker macroicon"></i>');
		$('.macroicon',trs).click(function(){
			showMacrosMenu($.proxy(function(item){
				$(this).prev().val(item.code);
			},this));
		});
		$('.jsicon',trs).click(function(){
			showCommonJsMenu($.proxy(function(item){
				var th = $(this),tr=th.parent().parent();
				$(this).prev().val(item.handle('search_'+$('input[name$=vcCode]',tr).val()));
			},this));
		});
		resetBtnTblIndex('.srhcond-body');
	}
	function frozenClick(){
		var tbody = $(this).closest('tbody'),tr=$(this).parent().parent();
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
		$('.chk-frozen',trs).bind('click',frozenClick);
		resetBtnTblIndex('.srhresult-body');
	}
	//供menuEditDbTable.jsp中的JS方法调用
	$('.srhmodel-tr').unbind('addsrhcond').bind('addsrhcond',function(event,coltr){//添加查询条件
		function getVal(name){return $('input[name$=\\.'+name+']',coltr).val();}
		addTblCols([{vcTitle:getVal('title'),vcCode:getVal('name'),vcEditorType:'text',sqlSearchType:'like',sqlFragment:'t.'+getVal('name')}],'model.search.searchColumns[0]');
	}).unbind('addsrhresult').bind('addsrhresult',function(event,coltr){//添加查询结果列
		function getVal(name){return $('input[name$=\\.'+name+']',coltr).val();}
		addResultCols([{vcTitle:getVal('title'),vcCode:getVal('name'),width:120,vcFormat:getVal('type')=='Date'?'yyyy-MM-dd HH:mm':'',align:'center'}],'model.search.searchResultColumns[0]');
	});
	//数据库表下拉框选择事件
	$('#dbtblselect').change(function(){
	});
	//对表名等信息赋值
	setValueByName('.srhmodel-info');
	//将已有列信息显示
	addTblCols(model.search.searchColumns,'model.search.searchColumns[0]');
	addResultCols(model.search.searchResultColumns,'model.search.searchResultColumns[0]');
}
//=====================================以下为配置项及公共字典信息部分的JS========================================
function loadDicts(){
	$(createThead([
		{width:'20%',text:'分组名称'},
		{width:'30%',text:'配置名称/字典编码'},
		{width:'30%',text:'值/字典名称'},
		{width:'40%',text:'备注'},
		{width:'50px',text:'排序号'},
		{width:'35px',text:'删除'}
		])).insertBefore('#dict-tbl-body');
	var model = $('#editForm').data('model')||{};
	$('#addRegBtn').linkbutton({iconCls:'icon-add',plain:true}).click(function(){
		addDictRows([{group:'SYS_REGEDIT'}]);
	});
	$('#addDictBtn').linkbutton({iconCls:'icon-add',plain:true}).click(function(){
		addDictRows([{}]);
	});
	$('#delDcitBtn').linkbutton({iconCls:'icon-remove',plain:true}).click(function(){
		$('#dict-tbl-body .delChk:checked').each(function(){
			$(this).parent().parent().remove();
		});
		resetBtnTblIndex('#dict-tbl-body');
	});
	function addDictRows(items){
		var htm = [];
		for(var i=0;i<items.length;i++){
			htm.push('<tr>');
			pushInputTds(htm,items[i],'model.dictData[0]','group,code,text,note,seq'.split(','));
			htm.push(createDelTd());
			htm.push('</tr>');
		}
		var trs = $(htm.join('')).appendTo('#dict-tbl-body');
		$('.index',trs).bind('blur keyup',adjustTrByIndexEvt);
		$('input[name$=group]',trs).attr('readonly',function(){return this.value=='SYS_REGEDIT'});
		resetBtnTblIndex('#dict-tbl-body');
	}
	//将已有按钮组显示
	addDictRows(model.dictData);
}
//=====================================以下为独立页面设置（编辑、查看等页面）部分的JS========================================
function loadPageInfo(){
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
		var span = $('<span>编码：<input type=text name=model.pageHTMLAry[0].id />标题：<input type=text name=model.pageHTMLAry[0].text />'+
				'<span class="delPage fa fa-remove" title="点击后删除此页面"></span><textarea code="'+item.id+
				'" name=model.pageHTMLAry[0].data style="display:none;" ></textarea></span>').appendTo('#pageMapDiv');
		$('input',span).bind('type keyup paste blur',updatePageHTMLSelect);
		setValueByName(span,{pageHTMLAry:[item]});
		$('span.delPage',span).bind('click',removePage);
		$('#pageMapDiv>span').each(function(idx){
			$('*[name]',this).each(function(){
				$(this).attr('name',$(this).attr('name').replace(/\[\d+?\]\.([\w_]+)$/,'['+idx+'].$1'));
			});
		});
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
					code = $('#newNodeCode').val();
					var txt = $('#pageMapDiv textarea[code='+$('#newNodeSrc').val()+']');
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
		$('#pageHTMLDiv').data('textarea',txt).data('editorstack',[txt.val()])[0].innerHTML=(txt.val());
	}).change();
	//添加指定行
	function addEditTblCols(items,viewMode){
		var htm = [];
		var len = $('.edittbl-cols-body>tr').length;
		for(var i=0;i<items.length;i++){
			htm.push('<tr code="'+items[i].name+'">');
			htm.push('<td>'+(len+i+1)+'</td>');
			htm.push('<td class=name>'+items[i].name+'['+items[i].title+']</td>');
			htm.push('</tr>');
		}
		var trs = $(htm.join('')).appendTo('.edittbl-cols-body');
		trs.click(function(){
			$('.edittbl-cols-body>tr.selected').removeClass('selected');
			$(this).addClass('selected');
			var cols = $('#dbtbl-editor-sel').data('tblcols');
			var item = cols[$(this).attr('code')];
			if(viewMode){//查看模式
				replaceEditorSel('${po.'+item.code+(item.type==Date?':yyyy-MM-dd HH:mm':'')+'}');
			}
			else{//编辑模式
				$('.editortype-div').remove();
				var fieldName = ($('#dbtbl-editor-sel').val()=="maintbl-tr"?'po':'items[0]')+'.'+item.name;
				$('<div class="editortype-div">'+getEditorTypeSelect('editorType',item.editorType||getDefaultEditor(item).handle(fieldName),fieldName,true)+'</div>').appendTo('.dbtbl-editor-cols');
				$('<a href="javascript:void(0);">插入代码</a>').insertAfter('.editortype-div select').click(function(){
					replaceEditorSel(item.editorType=$('.editortype-div textarea').val());
					$(item.target).val(item.editorType);
				});
				$('.editortype-div select.editortype').change(editorTypeChange);
			}
		});
	}
	function replaceEditorSel(str){
		str = (str||'').toString();
		var start = editor.getCursor('from');
		editor.replaceSelection(str);
		var pos = editor.getCursor();
		editor.setSelection(start,pos);
	}
	//根据业务表的内容刷新业务表对应输入控件列表
	function refreshDbEditorPanel(viewMode){
		var tblCls = $('#dbtbl-editor-sel').val();
		var ary = [],addedAry=[];
		var columns = tblCls=='maintbl-tr'?model.mainTable.columns:model.otherTables[tblCls.replace(/.*\D+/g,'')].columns;
		for(var i=0;i<columns.length;i++){
			columns[columns[i].code] = columns[i];
		}
		var changed = false;
		$('.'+tblCls+' .dbtbl-body input[name$=\\.name]').each(function(){
			var th=$(this),name = th.val(),tr=th.parent().parent(),etTxt=$('input[name$=\\.editorType]',tr);
			var item = {name:name,title:$('input[name$=\\.title]',tr).val(),type:$('select[name$=\\.type]',tr).val(),typeLen:$('input[name$=\\.typeLen]',tr).val(),editorType:etTxt.val(),target:etTxt[0]};
			ary.push(item);
			ary[name] = item;
		});
		$('#dbtbl-editor-sel').data('tblcols',ary);
		if(ary.length>0){
			addEditTblCols(ary,viewMode);
		}
	}
	//====================以下为表格编辑器的功能===================
	//根据指定行，返回这些行所包围的全部行
	function getAllTrs(trs){
		var ftr = $(trs[0]),ltr=trs[trs.length-1],ntr=ftr.next('tr');
		while(ntr.length>0 && ntr[0]!=ltr){
			ftr = ftr.add(ntr);
			ntr = ntr.next('tr');
		}
		return ftr.add(ltr);
	}
	//根据指定单元格，返回这些单元格所包围的全部单元格
	function getAllTds(tds){
		var ftd = $(tds[0]),ltd=tds[tds.length-1],ntd=ftd.next('td,th');
		while(ntd.length>0 && ntd[0]!=ltd){
			ftd = ftd.add(ntd);
			ntd = ntd.next('td,th');
		}
		return ftd.add(ltd);
	}
	//得到指定列的列序号，下标从0开始
	function getColIndex(td){
		var tbody = td.parent().parent();
		bindRowColIndex(tbody);
		return td.data('col');
	}
	//得到合并单元格横跨的列数
	function getColspan(td){
		return parseInt($(td).attr('colspan')||1);
	}
	//得到合并单元格竖跨的行数
	function getRowspan(td){
		return parseInt($(td).attr('rowspan')||1);
	}
	//加载数据库业务表的信息
	function loadDbTblEditor(viewMode){
		$('#dbtbl-editor-sel').html($('#dbtblselect').html()).find('option:last').remove();
		$('#dbtbl-editor-sel').data('tblColsAry',{}).change(function(){
			$('.edittbl-cols-body').empty();
			refreshDbEditorPanel(viewMode);
		}).change();
	}
	//插入数据输入控件
	function insertDataInput(viewMode){
		var htm = '<select id="dbtbl-editor-sel"></select>'+
				'<table class=fast-child-table cellpadding="0" cellspacing="0"><tbody class="edittbl-cols-body"></tbody></table>';
		var tds = $('#pageHTMLDiv .selected');
		if(tds.length==0){//编辑整个表格
			$.messager.alert('提示','请选中一个要编辑的单元格！');
		}
		else if(tds.length>1){
			$.messager.alert('提示','每次仅能编辑一个单元格，如需编辑整个表格，请不要选中任何单元格！');
		}
		else{//编辑一个单元格
			openHTMLEditorWin({html:tds.html(),selectAll:true,saveFun:function(html){
				tds[0].innerHTML = (html);
				$('#pageHTMLDiv').trigger('change');
			}});
			var dbpnl = $('#htmlEditorWin>div.layout').layout('add',{region:'west',title:'业务表字段列表',split:true,width:200}).layout('panel','west');
			dbpnl.addClass('dbtbl-editor-cols').html('<select id="dbtbl-editor-sel"></select><div class=editortype-tbl>'+
					'<table class=fast-child-table cellpadding="0" cellspacing="0"><tbody class="edittbl-cols-body"></tbody></table></div>');
			(dbpnl.panel('options').onResize = function(){
				$('.editortype-tbl').height(dbpnl.height()-(viewMode?0:120)-$('#dbtbl-editor-sel').height());
			})();
			$(createThead([{width:'35px',text:'序号'},
				{width:'70%',text:'字段名称'}
			])).insertBefore('.edittbl-cols-body');
			loadDbTblEditor(viewMode);
		}
	}
	//上下移动行。有合并单元格时会出问题
	function moveRows(flag){
		var trs = $('#pageHTMLDiv .selected').parent(),ptr = $(trs[flag=='up'?0:trs.length-1])[flag=='up'?'prev':'next']('tr');
		if(ptr.length>0){//相邻有行时才处理
			var tbody = ptr.parent();
			bindRowColIndex(tbody);
			var cells = tbody.data('cells');
			var tds = $('>td,>th',ptr);
			for(var i=0;i<tds.length;i++){
				if($(tds[i]).data('rowspan')>1){
					$.messager.alert('提示','上下移动行的时候，相邻行不能有跨行的合并单元格！');
					return;
				}
			}
			trs = trs.length==1?trs:getAllTrs(trs);
			var row = $(trs[0]).attr('rowIndex');
			for(var i=0;i<cells[row].length;i++){
				var th = $(cells[row][i]);
				if(th.data('rowspan')>1 && th.data('row')<row){
					$.messager.alert('提示','上下移动行的时候，所选行不能有跨边界的合并单元格！');
					return;
				}
			}
			var row = $(trs[trs.length-1]).attr('rowIndex');
			for(var i=0;i<cells[row].length;i++){
				var th = $(cells[row][i]);
				if(th.data('rowspan')>1 && th.data('row-b')>row){
					$.messager.alert('提示','上下移动行的时候，所选行不能有跨边界的合并单元格！');
					return;
				}
			}
			trs.find('>td').addClass('selected');
			trs[flag=='up'?'insertBefore':'insertAfter'](ptr);
			$('#pageHTMLDiv').trigger('change');
		}
	}
	//根据指定列数对单元格进行重新排列，行不连续时自动扩展
	function resetCols(tbl,cols){
		if(!tbl || tbl.length==0 || cols<=0)return;
		bindRowColIndex(tbl.find('tbody:first'));
		var tbody = tbl.find('tbody'),colgroup = $('>colgroup',tbl),oldcols=$('>col',colgroup).length;
		if($('td[p-rowspan!=1],th[p-rowspan!=1]',tbody).length>0)return;//如果有跨行合并单元格时，不能重新排列
		if(oldcols!=cols){//列数变化时对colgroup和thead进行处理
			var td = $($('>thead',tbl).find('th,td')[0]);
			td.attr('colspan',cols).nextAll().remove();
			if(cols<oldcols){//减少列时
				$('>colgroup>col:eq('+(cols-1)+')',tbl).nextAll().remove();
			}
			else{//增加列时
				for(var i=0;i<cols-oldcols;i++){
					$('>colgroup>col:eq('+i+')',tbl).clone().appendTo(colgroup);
				}
				if(cols==2){
					$('>colgroup>col:eq(1)',tbl).attr('width','35%');
				}
			}
		}
		$('>tr>td.blank',tbody).remove();//移除空白单元格
		if($('>tr',tbody).length>1){
			$('>tr>td,>tr>th',tbody).appendTo($('>tr:first',tbody));//将所有单元格集中到第一行
			$('>tr:first',tbody).nextAll().remove();//其他行删除
		}
		var alltd = $('>tr:first',tbody).find('>td,>th');
		var dealidx = 0,tr=null;
		for(var i=0;i<alltd.length;i++){
			if(dealidx%cols==0){//新建行
				tr = $('<tr></tr>').appendTo(tbody);
			}
			var td = $(alltd[i]),colspan = parseInt(td.attr('colspan')||1);
			if(colspan>cols){//如果单元格占用的列数大于表格总列数，则修改colspan
				td.attr('colspan',colspan=cols);
			}
			if(colspan>1 && colspan>=oldcols-1){//如果单元格原来占用一行或差一格占用一行，此时应将列占用数自动增加
				td.attr('colspan',colspan=cols-(oldcols-colspan));
			}
			if((dealidx%cols+colspan)>cols){//如果当前单元格放置后超出了总列数，则将剩余单元格补为空白单元格，并将此单元格转到下一行处理
				if(cols-dealidx%cols>0){//将剩余单元格补为空白单元格
					$('<td class=blank></td>').appendTo(tr).attr('colspan',cols-dealidx%cols);
				}
				i--;
				dealidx = 0;
				continue;
			}
			td.appendTo(tr);
			dealidx+=colspan;
		}
		if(dealidx%cols>0 && dealidx%cols<cols){//将最后一行的剩余单元格补为空白单元格
			$('<td class=blank></td>').appendTo(tr).attr('colspan',cols-dealidx%cols);
		}
		$('>tr:empty',tbody).remove();
	}
	//左右移动选中的单元格，单元格不连续时自动扩展
	function moveCells(flag){
		var tds = $('#pageHTMLDiv .selected'),ftd=$(tds[0]),ltd=$(tds[tds.length-1]),ftr = ftd.parent(),ltr = ltd.parent();
		if(tds.length==0)return;
		var tbody = $(tds[0]).parent().parent();
		bindRowColIndex(tbody);
		if($('td[p-rowspan!=1],th[p-rowspan!=1]',tbody).length>0){
			$.messager.alert('提示','有跨行合并单元格时，不能重新排列表格，所以不能左右移动单元格，请使用其他功能');
			return;//如果有跨行合并单元格时，不能重新排列
		}
		//如果是向左，且当前已经是最左侧的单元格，则不处理
		if(flag=='left' && ftd.prev('td,th').length==0 && ftr.prev('tr').length==0)return;
		//如果是向右，且当前已经是最右侧的单元格，则不处理
		if(flag=='right' && ltd.next('td,th').length==0 && ltr.next('tr').length==0)return;
		var tbody = $(tds[0]).parent().parent(),tbl=tbody.parent();//TBODY
		var cols = $('>colgroup>col',tbl).length;//计算列数
		if($('>tr',tbody).length>1){
			$('>tr>td,>tr>th',tbody).appendTo($('>tr:first',tbody));//将所有单元格集中到第一行
			$('>tr:first',tbody).nextAll().remove();//其他行删除
		}
		tds = tds.length==1?tds:getAllTds(tds);//扩展选中的单元格
		tds.addClass('selected');//添加选中状态
		tds[flag=='left'?'insertBefore':'insertAfter']($(tds[flag=='left'?0:tds.length-1])[flag=='left'?'prev':'next']('td,th'));//移动单元格
		resetCols(tbl,cols);//重新按列数生成表格
		$('#pageHTMLDiv').trigger('change');
	}
	//撤消重做的功能函数
	function moveStackCursor(step){
		var th = $('#pageHTMLDiv'),stack = th.data('editorstack');//撤消恢复栈
		var newcursor = (stack.cursor||0)+step;
		if(stack && newcursor>=0 && newcursor<=stack.length-1){
			stack.cursor = newcursor;
			th.html(stack[stack.cursor]).find('td.selected,th.selected').removeClass('selected');
			$(th.data('textarea')).val(th.html());
			$('#tbltoolbar-undo').toggleClass('disable',!(stack&&stack.cursor>0));
			$('#tbltoolbar-redo').toggleClass('disable',!(stack&&stack.cursor<stack.length-1));
		}
	}
	//删除第一个选中的单元格所在行
	function delSelectRow(){
		var tds = $('#pageHTMLDiv .selected:first'),tbody=tds.parent().parent();
		if(tds.length==0)return;
		bindRowColIndex(tbody);
		if(tds.data('rowspan')>1){//如果是合并单元格，则先分解，再执行删除操作
			splitCells(tds);
			bindRowColIndex(tbody);
			tds = $('#pageHTMLDiv .selected:first');
		}
		var tbody = tds.parent().parent();
		var cells = tbody.data('cells'),row = tds.data('row');
		for(var i=0;i<cells[row].length;i++){
			var th = $(cells[row][i]);
			if(th.data('rowspan')>1){
				if(th.data('row')==row){
					splitCells(th);
				}
				else{
					th.attr('rowspan',th.data('rowspan')-1);
				}
			}
		}
		tds.parent().remove();
	}
	//删除第一个选中的单元格所在列
	function delSelectCol(){
		var tds = $('#pageHTMLDiv .selected:first'),tbody=tds.parent().parent();
		if(tds.length==0)return;
		bindRowColIndex(tbody);
		if(tds.data('colspan')>1){//如果是合并单元格，则先分解，再执行删除操作
			splitCells(tds);
			bindRowColIndex(tbody);
			tds = $('#pageHTMLDiv .selected:first');
		}
		var tbody = tds.parent().parent();
		var cells = tbody.data('cells'),col = tds.data('col');
		for(var i=0;i<cells.length;i++){
			var th = $(cells[i][col]);
			if(th.data('colspan')>1){
				th.attr('colspan',th.data('colspan')-1);
			}
			else{
				th.remove();
			}
		}
		var tbl=$('#pageHTMLDiv table:first');
		$('>colgroup>col:eq('+col+')',tbl).remove();
		$('>thead>tr>th:eq('+col+')',tbl).remove();
		if($('>tbody>tr:first>td',tbl).length==0){
			$('>tbody',tbl).empty();
		}
	}
	//增加减少单元格的合并列数
	function addColspan(num){
		var tds = $('#pageHTMLDiv .selected'),tbody=$(tds[0]).parent().parent(),tbl=tbody.parent();
		bindRowColIndex(tbody);
		if($('td[p-rowspan!=1],th[p-rowspan!=1]',tbody).length>0){
			$.messager.alert('提示','有跨行合并单元格时，不能重新排列表格，所以不能增减单元格合并列数，请使用其他功能。');
			return;//如果有跨行合并单元格时，不能重新排列
		}
		var cols = $('#pageHTMLDiv table:first>colgroup>col').length;//计算列数
		tds.each(function(){console.log(99)
			var td=$(this),colspan=parseInt(td.attr('colspan')||1),newspan=Math.min(cols,colspan+num);
			if(newspan!=cols && newspan>0 && newspan<=cols){
				td.attr('colspan',newspan);
			}
		});
		resetCols(tbl,cols);//重新按列数生成表格
		$('#pageHTMLDiv').trigger('change');
	}
	//将指定的单元格合并在一起
	function mergeCells(tds){
		var tbody = $(tds[0]).parent().parent();
		bindRowColIndex(tbody);
		var cleft=99999,ctop=99999,cright=0,cbottom=0;
		tds.each(function(){
			var th = $(this);
			cleft = Math.min(cleft,th.data('col'));
			ctop = Math.min(ctop,th.data('row'));
			cright = Math.max(cright,th.data('col-r'));
			cbottom = Math.max(cbottom,th.data('row-b'));
		});
		var mergeCells = $(),cells = tbody.data('cells');
		for(var i=ctop;i<=cbottom;i++){//查找范围内的合并单元格
			for(var j=cleft;j<=cright;j++){
				var th = $(cells[i][j]);
				if(th.data('rowspan')>1 || th.data('colspan')>1){
					mergeCells = mergeCells.add(cells[i][j]);
				}
			}
		}
		mergeCells.each(function(){//将范围内的合并单元格全部分解
			splitCells($(this));
		});
		bindRowColIndex(tbody);//重新绑定行列序号
		cells = tbody.data('cells');
		for(var i=ctop;i<=cbottom;i++){//查找范围内的单元格并删除
			for(var j=cleft;j<=cright;j++){
				var th = $(cells[i][j]);
				if(i!=ctop || j!=cleft){
					$(cells[i][j]).remove();
				}
			}
		}
		$('.selected',tbody).removeClass('selected');
		$(cells[ctop][cleft]).attr('rowspan',cbottom-ctop+1).attr('colspan',cright-cleft+1).addClass('selected hvcenter');
	}
	//分解第一个单元格
	function splitCells(td){
		td = $(td[0]);
		var rowspan = getRowspan(td),colspan=getColspan(td),tbody=td.parent().parent(),hasSelect = td.hasClass('selected');
		if(colspan==1 && rowspan==1)return;
		bindRowColIndex(tbody);
		var cells = tbody.data('cells');
		if(colspan>1){
			var ary = $(Array.buildAry('<td></td>',colspan-1).join('')).insertAfter(td);//第一行插入空白单元格
			if(hasSelect)ary.addClass('selected');
		}
		if(rowspan>1){//后续行插入相应的空白单元格
			for(var i=td.data('row')+1;i<=td.data('row-b');i++){
				var ary = $(Array.buildAry('<td></td>',colspan).join('')).insertAfter(cells[i][td.data('col')-1]);
				if(hasSelect)ary.addClass('selected');
			}
		}
		td.attr('rowspan',1).attr('colspan',1);
	}
	//对表格中的所有单元格进行扫描，形成单元格的二级数组并给单元格定义下标
	function bindRowColIndex(tbody){
		var cells = [];
		$('>tr',tbody).each(function(row){
			if(!cells[row])cells[row]=[];
			var colidx = 0;
			$(this).attr('rowIndex',row);
			$('>td,>th',this).each(function(){
				while(cells[row][colidx])colidx++;
				var th = $(this),rowspan=getRowspan(th),colspan=getColspan(th);
				th.attr('rowIdx',row).attr('colIdx',colidx).attr('p-rowspan',rowspan).attr('p-colspan',colspan)
				.data('row',row).data('col',colidx).data('rowspan',rowspan).data('colspan',colspan).data('row-b',row+rowspan-1).data('col-r',colidx+colspan-1);
				for(var i=row;i<row+rowspan;i++){
					if(!cells[i])cells[i]=[];
					for(var j=colidx;j<colidx+colspan;j++){
						cells[i][j] = th[0];
					}
				}
				colidx += colspan;
			});
		});
		tbody.data('cells',cells);
	}
	//用户输入列数，重新布局表格
	function resetTableCols(){
		var tbl=$('#pageHTMLDiv table:first'),cols = $('>colgroup>col',tbl).length;
		bindRowColIndex(tbl.find('tbody:first'));
		if($('td[p-rowspan!=1],th[p-rowspan!=1]',tbl.find('tbody:first')).length>0){
			$.messager.alert('提示','有跨行合并单元格时，不能重新排列表格');
			return;//如果有跨行合并单元格时，不能重新排列
		}
		var win = $.messager.prompt('提示','请输入您要设定的列数，建议使用偶数列',function(str){
			if(!isNaN(str) && parseInt(str)>0){
				var cols = parseInt(str);
				var mode = $('input[name=pageMode]').val();
				if(mode){//重新生成
					$('#pageHTMLDiv').html('<input type=hidden name=po.VC_ID value="${po.VC_ID}" /><div style="width:565px;margin:10px auto;">'+
							'<table id="editFormTable" class="fast-edit-table"><colgroup></colgroup><thead><tr><th colspan='+cols+'></th></tr></thead><tbody><tr></tr></tbody></table></div>');
					var tbl=$('#pageHTMLDiv table:first'),tr=$('tbody tr:first',tbl);
					$('>colgroup',tbl).html(Array.buildAry('<col width="15%" /><col width="35%" />',parseInt(cols/2)).join(''));
					var htm = [],hiddenhtm=[];
					$('.maintbl-tr .dbtbl-body input[name$=\\.name]').each(function(){
						var th=$(this),name = th.val(),tr=th.parent().parent(),etTxt=$('input[name$=\\.editorType]',tr);
						var item = {name:name,title:$('input[name$=\\.title]',tr).val(),type:$('select[name$=\\.type]',tr).val(),typeLen:$('input[name$=\\.typeLen]',tr).val(),editorType:etTxt.val()};
						if($.sysmenu.editPageHiddenCols.indexOf(item.name)>=0)return;
						htm.push('<td class=th>'+item.title+'</td><td>');
						if(mode=='input'){
							htm.push(getDefaultEditor(item).handle('po.'+item.name));
						}
						else{
							htm.push('${po.'+item.name+(item.type=='Date'?':yyyy-MM-dd HH:mm':'')+'}');
						}
						htm.push('</td>');
					});
					tr.html(htm.join(''));
				}
				resetCols($('#pageHTMLDiv table:first'),cols);
				$('#pageHTMLDiv').trigger('change');
			}
		});
		win.find('input:text').val(cols||4);
		if(tbl.length==0 || $('>tbody>tr',tbl).length==0){
			win.find('input:text').after('<div class="mode-sel-div"><label><input value="input" name="pageMode" checked type=radio>生成输入页面</label>'+
					'<label><input name="pageMode" value="view" type=radio>生成查看页面</label></div>');
		}
	}
	//以HTML格式编辑
	function editAsHTML(){
		var tds = $('#pageHTMLDiv .selected');
		if(tds.length==0){//编辑整个表格
			openHTMLEditorWin({html:$('#pageHTMLDiv').html(),saveFun:function(html){
				$('#pageHTMLDiv')[0].innerHTML = (html);
				$('#pageHTMLDiv').trigger('change');
			}});
		}
		else if(tds.length==1){//编辑一个单元格
			openHTMLEditorWin({html:tds.html(),saveFun:function(html){
				tds.html(html);
				$('#pageHTMLDiv').trigger('change');
			}});
		}
		else{
			$.messager.alert('提示','每次仅能编辑一个单元格，如需编辑整个表格，请不要选中任何单元格！');
		}
	}
	//编辑Javascript
	function editJavascript(){
		openHTMLEditorWin({html:$('#pageHTMLDiv>script:first').html(),codetype:'javascript',saveFun:function(html){
			if($('#pageHTMLDiv>script:first').length==0){
				$('#pageHTMLDiv').prepend('<script></script>');
			}
			$('#pageHTMLDiv>script:first').html(html);
			$('#pageHTMLDiv').trigger('change');
		}});
	}
	//插入空白行
	function insertBlankRow(){
		var tds = $('#pageHTMLDiv .selected:first');
		if(tds.length==0)return;
		var tr=tds.parent(),cols = $('#pageHTMLDiv table:first>colgroup>col').length;
		var htm = ['<tr>'];
		for(var i=0;i<cols;i++){
			htm.push('<td></td>');
		}
		$(htm.join('')+'</tr>').insertBefore(tr);
		$('#pageHTMLDiv').trigger('change');
	}
	//插入空白列
	function insertBlankCol(){
		var tds = $('#pageHTMLDiv .selected:first');
		if(tds.length==0)return;
		var tr=tds.parent(),tbody=tr.parent(),tbl=tbody.closest('table'),cols = $('>colgroup>col',tbl).length;
		var beginCol = getColIndex(tds);
		$('>tr',tbody).each(function(){
			var colIdx = 0;
			$('>td',this).each(function(){
				var th=$(this),colspan=parseInt(th.attr('colspan')||1);
				if(colspan+colIdx==beginCol){
					th.after('<td></td');
					return false;
				}
				else if(colspan+colIdx>beginCol){//单元格跨过新增线时，增加colspan
					th.attr('colspan',colspan+1);
					return false;
				}
				colIdx+=colspan;
			});
		});
		var tbl=$('#pageHTMLDiv table:first');
		var col = $('>colgroup>col:eq('+beginCol+')',tbl);
		col.before(col.clone());
		$('>thead>tr',tbl).append('<th></th>');
		$('#pageHTMLDiv').trigger('change');
	}
	//合并单元格并居中，如果选中的是一个合并单元格，则分解单元格
	function mergeSplitCells(){
		var tds = $('#pageHTMLDiv .selected'),tbody=$(tds[0]).parent().parent();
		if(tds.length==0)return;
		if(tds.length==1 && getRowspan(tds)==1 && getColspan(tds)==1)return;
		if(tds.length>1){//合并单元格
			mergeCells(tds);
		}
		else{//分解单元格
			splitCells(tds);
		}
		$('#pageHTMLDiv').trigger('change');
	}
	//删除选中单元格
	function deleteSelectCells(){
		var tbl=$('#pageHTMLDiv table:first'),tbody=tbl.find('tbody:first'),cols = $('>colgroup>col',tbl).length;
		bindRowColIndex(tbody);
		if($('td[p-rowspan!=1],th[p-rowspan!=1]',tbody).length>0){
			$.messager.alert('提示','有跨行合并单元格时，不能重新排列表格，所以不能单独删除单元格，请使用删除行或删除列');
			return;//如果有跨行合并单元格时，不能重新排列
		}
		$('#pageHTMLDiv .selected').remove();
		resetCols(tbl,cols);//重新按列数生成表格
		$('#pageHTMLDiv').trigger('change');
	}
	var btns = [
		{id:'tbltoolbar-recreate',icon:'fa-table',text:'重新设定表格列数',handle:resetTableCols},
		{id:'tbltoolbar-undo',icon:'fa-reply disable',text:'撤消',handle:function(){moveStackCursor(-1);}},
		{id:'tbltoolbar-redo',icon:'fa-share disable',text:'重复',handle:function(){moveStackCursor(1);}},
//		{id:'tbltoolbar-editstyle',icon:'fa-chain',text:'设置所选单元格样式，不选任何单元格时设置表格样式',handle:function(){}},
		{id:'tbltoolbar-htmledit',icon:'fa-edit',text:'以HTML格式编辑',handle:editAsHTML},
		{id:'tbltoolbar-jsedit',icon:'fa-stack"><i class="fa char-icon fa-stack-1x" style="font-size: 12px;line-height: 12px;top: 3px;">Js</i><i style="font-size: 20px;top:-3px" class="fa fa-stack-1x fa-square-o',text:'编辑Javascript',handle:editJavascript},
		{id:'tbltoolbar-insertrow',icon:'fa-ellipsis-h',text:'插入空白行',handle:insertBlankRow},
		{id:'tbltoolbar-insertcol',icon:'fa-ellipsis-v',text:'插入空白列',handle:insertBlankCol},
		{id:'tbltoolbar-mergecell',icon:'fa-life-ring',text:'合并单元格并居中',handle:mergeSplitCells},
		{id:'tbltoolbar-removecell',icon:'fa-remove',text:'删除选中单元格',handle:deleteSelectCells},
		{id:'tbltoolbar-removerow',icon:'fa-stack"><i class="fa delete char-icon fa-stack-1x">R</i><i class="fa-stack-1x',text:'删除选中单元格所在行',handle:function(){
			while($('#pageHTMLDiv .selected').length>0)delSelectRow();
			$('#pageHTMLDiv').trigger('change');
		}},
		{id:'tbltoolbar-removecol',icon:'fa-stack"><i class="fa delete char-icon fa-stack-1x">C</i><i class="fa-stack-1x',text:'删除选中单元格所在列',handle:function(){
			while($('#pageHTMLDiv .selected').length>0)delSelectCol();
			$('#pageHTMLDiv').trigger('change');
		}},
		{id:'tbltoolbar-rowup',icon:'fa-angle-double-up',text:'选中单元格所在行上移',handle:function(){moveRows('up');}},
		{id:'tbltoolbar-rowdown',icon:'fa-angle-double-down',text:'选中单元格所在行下移',handle:function(){moveRows('down');}},
		{id:'tbltoolbar-cellleft',icon:'fa-angle-left',text:'选中单元格左移',handle:function(){moveCells('left');}},
		{id:'tbltoolbar-cellright',icon:'fa-angle-right',text:'选中单元格右移',handle:function(){moveCells('right');}},
		{id:'tbltoolbar-addcol',icon:'fa-plus-square',text:'单元格增加占用列数',handle:function(){addColspan(1);}},
		{id:'tbltoolbar-removecol',icon:'fa-minus-square',text:'单元格减少占用列数',handle:function(){addColspan(-1);}},
		{id:'tbltoolbar-subtitle',icon:'fa-toggle-on',text:'切换当前行为副标题',handle:function(){
			$('#pageHTMLDiv .selected').parent().toggleClass('subtitle');
			$('#pageHTMLDiv').trigger('change');
		}},
		{id:'tbltoolbar-thtd',icon:'fa-retweet',text:'切换单元格类别（标签/显示值）',handle:function(){
			$('#pageHTMLDiv .selected').toggleClass('th');
			$('#pageHTMLDiv').trigger('change');
		}},
		{id:'tbltoolbar-dbcontrol',icon:'fa-object-group',text:'插入数据输入控件',handle:function(){insertDataInput(false);}},
		{id:'tbltoolbar-dbview',icon:'fa-object-ungroup',text:'插入数据显示值',handle:function(){insertDataInput(true);}},
		{id:'tbltoolbar-help',icon:'fa-question',text:'操作说明',handle:function(){$.openWin({divId:'pageHTMLHelpWin',_content:$('#pageHTMLHelp').html()})}}
	];
	for(var i=0;i<btns.length;i++){
		$('<a id="'+btns[i].id+'" title="'+btns[i].text+'" class="fa '+btns[i].icon+'"></a>').appendTo('.page-toolbar').click(btns[i].handle);
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
		var src = $(evt.target),td=src.closest('th,td');
		if(td.length==1){//单元格点击后选中事件
			if(!evt.ctrlKey && !evt.shiftKey){
				$('#pageHTMLDiv .selected').not(td[0]).removeClass('selected')
			}
			td.toggleClass('selected');
		}
	}).mousemove(function(){//超级多选模式
		var evt = $.event.fix(event);
		if(evt.ctrlKey && evt.altKey){
			var src = $(evt.target),td=src.closest('th,td');
			if(td.length==1 && !td.hasClass('selected')){
				td.addClass('selected');
			}
		}
	}).bind('change',function(){//变化时调用此事件
		var th = $(this);
		$('th,td',th).removeAttr('rowidx').removeAttr('colidx').removeAttr('p-rowspan').removeAttr('p-colspan');
		$(th.data('textarea')).val(th.html());
		var stack = th.data('editorstack')||[];//撤消恢复栈
		stack.push(th.html());
		stack.cursor = stack.length-1;
		th.data('editorstack',stack);
		$('#tbltoolbar-undo').removeClass('disable');
		$('#tbltoolbar-redo').addClass('disable');
	}).data('editorstack',[$('#pageHTMLDiv').html()]);//撤消恢复栈
}
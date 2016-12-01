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
		editorType:new MenuList([/*如果类型值过于复杂或有单引号与双引号，请将类型值写入data属性。禁止使用数字做为value值*/
			{value:'text',text:'单行文本框'},
			{value:'textarea',text:'多行文本框'},
			{value:'date:yyyy-MM-dd',text:'格式化日期'},
			{value:'select:dict:字典名称',text:'字典下拉框'},
			{value:'select:sql:SQL语句',text:'SQL结果下拉框'},
			{value:'combobox',text:'JSON动态下拉框',data:'combobox:{url:"JSON结果URL",valueField:"id",textField:"text"}'},
			{value:'combotree',text:'JSON动态下拉树',data:'combotree:{url:"JSON结果URL"}'},
			{value:'html:自定义HTML代码',text:'自定义输入控件'}
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
//{htmlAttrs:'name=htmlName class=myselect',value:'2',blankOption:true,data:[{value:'1',text:'一'},{value:'2',text:'二'}],dataValueField:'value',dataTextField:'text'}
function createSelectHTML(selObj){
	selObj = $.extend(selObj,{blankOption:true,dataValueField:'value',dataTextField:'text'});
	var ary = selObj.data,htm=[];
	htm.push('<select');
	if(selObj.htmlAttrs){
		htm.push(' '+selObj.htmlAttrs);
	}
	htm.push('>');
	if(selObj.blankOption){
		htm.push('<option value=""></option>');
	}
	for(var i=0;i<ary.length;i++){
		var val = ary[i][selObj.dataValueField],txt=ary[i][selObj.dataTextField];
		ary[val] = ary[i];
		htm.push('<option value="'+val+'"'+(val==selObj.value?' selected':'')+'>'+txt+'</option>');
	}
	htm.push('</select>');
	return htm.join('');
}
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
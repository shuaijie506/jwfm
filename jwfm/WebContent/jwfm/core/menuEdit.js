
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
function createButtonMenu(menuData){
	var btnMenuHtm = [];
	for(var i=1;i<menuData.length;i++){
		var item = menuData[i];
		var icode = item.code.length<7?item.code+'Item':item.code;
		$.extend(item,{funName:icode,btnId:icode+'Btn',});
		menuData[item.code] = item;
		btnMenuHtm.push('<div data-options="names:\''+item.code+'\',iconCls:\''+(item.iconCls||'')+'\'">'+item.name+'</div>');
	}
	var btnGroup = [];
	for(var i=0,ary = menuData[0].split(';');i<ary.length;i++){
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
function createIndexTd(){return '<td><input type=text class=index /></td>';}
function createDelTd(){return '<td><input type=checkbox class=delChk /></td>';}
function pushInputTds(htmary,object,namepre,nameary){
	for(var i=0;i<nameary.length;i++){
		htmary.push('<td><input type=text name='+namepre+'.'+nameary[i]+' value="'+(object[nameary[i]]||'')+'" /></td>');
	}
}
$(function(){
	if($('.searchTbl[fit=true]',this).length>0){//窗口大小发生变化时，datagrid也根随变化
		var par = this;
		$(window).resize(function(){
			var stbl = $('#searchGrid',par);
			if(stbl.length>0){
				var hei = $(window).height()-stbl.datagrid('getPanel').offset().top;
				stbl.datagrid('resize',{width:$(window).width(),height:hei});
			}
		});
	}
	$(window).resize(function(){//窗口大小变化时将弹出的window窗口的宽度和高度跟着变化，保证窗口在可显示范围内
		$('.window-body:visible').each(function(){
			var pnl = $(this).window('panel');
			var pos = pnl.offset();
			if(pos.left+pnl.outerWidth()>$(window).width()){
				pos.width = $(window).width()-pos.left;
			}
			if(pos.top+pnl.outerHeight()>$(window).height()){
				pos.height = $(window).height()-pos.top;
			}
			if(pos.width || pos.height){
				$(this).window('resize',pos);
			}
		});
	});
});
function dealMultiRow(tblId,url,info,param){
	var rows = $('#'+tblId).datagrid('getSelections');					
	if (rows.length==0){
	    $.messager.alert('警告', '请选择要删除的数据！','warning');return;
	}
	if(rows.length>20){
        $.messager.alert('警告', '您要'+info+'的数据过多，为保证数据安全，请控制在20条以内！','warning');
    }
	$.messager.confirm('警告', '您确定要'+info+'数据吗?', function(r){
		if(r){
	      	var ids = new Array();
	      	//当删除的数据大于行时，容易造成浏览器崩溃死掉
		  	for(var i=0;i<rows.length;i++){
				ids.push(rows[i]['VC_ID']);
		  	}
			param = $.extend({},param,{"chkSelf":ids});
			console.log(param)
		 	$.post(url,param,function(data){
		    	try{
		            jsonReStr = jQuery.parseJSON(data);  //获取从服务器得到的数据反馈信息
		            if(jsonReStr.successed){    //操作成功处理
		            	$('div.datagrid-header-check input[type="checkbox"]').attr('checked',false);//全选框复原
			            $.util.showTip({content:'数据'+info+'成功！'});
			            $('#searchGrid').datagrid('reload'); //刷新数据列
		            }else{  //操作失败处理
		                $.messager.alert('消息提示','操作处理失败！原因如下：<br><font color=red>'+jsonReStr.info+'</font>','error');
		            }
	            }catch(e){
	            	msg = ''+e;
	            	if(msg.length>280)msg = msg.substr(0,280)+'...';
	               $.messager.alert('消息提示','出现系统错误!可能原因如下：<br><font color=red>'+this.url+'页面地址未找到！或'+msg+'</font>','error');
	            }
		 	});//删除操作
		}
	});
}
//进行添加或编辑操作后，根据返回的json数据获取操作结果信息的通用方法
function returnOptMsgEasyui(opt){
	if(!opt){
		$.messager.alert('提示','参数不正确，请使用{data:data}传参');
		return;
	}
	if(typeof(opt)=='string'){
		opt = {data:opt};
	}
    try{
        jsonReStr = jQuery.parseJSON(opt.data);  //获取从服务器得到的数据反馈信息
        if(jsonReStr.successed){    //操作成功处理
            $.util.showTip({content:(opt.reMsg||jsonReStr.info||'保存成功！')});
            $(opt.gridId||'#searchGrid').datagrid('reload'); //刷新数据列
         	if(opt.callback && typeof(opt.callback)=='function'){
         		opt.callback(true);
         	}
           	return true;
        }else{  //操作失败处理
            try{$(opt.winId||'#operateWindow').window("open");}catch(e){;}
            $.messager.alert('消息提示','操作处理失败！原因如下：<br><font color=red>'+jsonReStr.info+'</font>','error',function(){
             	if(opt.callback && typeof(opt.callback)=='function'){
             		opt.callback(false);
             	}
            });
        }
    }catch(e){
        $.messager.alert('消息提示','出现系统错误!系统返回错误信息：<br><font color=red>'+opt.data+'</font>','error',function(){
         	if(opt.callback && typeof(opt.callback)=='function'){
         		opt.callback(false);
         	}
        });
    }
    return false;
}
$.extend($.fn.combobox.defaults,{valueField:"id",height:20,onLoadSuccess:function(){
	var th = $(this);
	$(this).combobox('textbox').unbind('click.showpanel').bind('click.showpanel',function(){
		th.combobox('showPanel');
	});
}});
$.extend($.fn.datagrid.defaults,{pageList:[10,20,50,200],rownumbers:true,
onBeforeLoad : function(param){//转换参数中的每页行数和页码参数名
	param['pager.rows']=param.rows;param.rows=undefined;
	param['pager.page']=param.page;param.page=undefined;
},
onLoadSuccess : function(data){
	var opt = $(this).datagrid('options');
	opt.pageSize = data.pageSize||'10';
	opt.pageNumber = data.pageNumber||'1';
	$('.datagrid-pager',$.data(this, 'datagrid').grid).pagination({total: data.total,pageSize:opt.pageSize,pageNumber:opt.pageNumber});
}});
$.fn.datagrid.dealParam = function(param,fcols,cols){
	cols = fcols.concat(cols);
	var formatCols = [];
	for(var i=0;i<cols.length;i++){
		for(var j=0;j<cols[i].length;j++){
			if(cols[i][j].field){
				formatCols.push(cols[i][j].field);
			}
		}
	}
	if(formatCols.length>0){
		param['search.formatCols'] = formatCols.join('<split>');
	}
	return param;
};



(function($){
	$.fn.htmldatagrid = function (option,data){
		if(typeof(option)=='string'){
			return $.fn.htmldatagrid.methods[option].call(this,data);
		}
		else{
			this.each(function(){
				if($(this).data('options')){//不重复加载
					return;
				}
				option = $.extend({},$.fn.htmldatagrid.defaults,option);
				$(this).data('options',option);
				if($('>.fast-table-container',this).length==0){
					$(this).html('<table class=fast-table-container border=0 cellpadding=0 cellspacing=0 width="100%"><tbody><tr><td>数据加载中</td></tr></tbody></table>');
				}
				if(option.pagination){//显示分页信息
					buildPageInfo(this,option);
				}
				if(option.toolbar && option.toolbar.length>0){//显示按钮区域
					buildToolbar(this,option);
				}
				//从远程加载数据
				$.fn.htmldatagrid.methods.load.call(this);
			});
			return this;
		}
	};
	$.fn.htmldatagrid.methods = {
			options:function(){
				return this.data('options');
			},
			resize:function(opt){//width,height
				
			},
			reload:function(param){//页数不变
				this.each(function(){
					loadData(this,param);
				});
			},
			load:function(param){//从第一页开始
				this.each(function(){
					loadData(this,param,1);
				});
			},
			fixRowHeight:function(){//自动设置行高
				
			},
			getData:function(){//获取当前加载的数据
				
			},
			getSelections:function(){//返回所有被选中的行，当没有记录被选中的时候将返回一个空数组。
				
			},
			clearSelections:function(){//清除所有选择的行。
				
			},
			scrollTo:function(idx){//滚动到指定的行。
				
			},
			selectAll:function(){//选择当前页中所有的行。
				
			},
			unselectAll:function(){//取消选择所有当前页中所有的行。
				
			},
			selectRow:function(idx){//选择一行，行索引从0开始。
				
			},
			unselectRow:function(idx){//取消选择一行。
				
			}
	};
	$.fn.htmldatagrid.defaults = {
			autoRowHeight:true,//定义设置行的高度，根据该行的内容。设置为false可以提高负载性能。
			maxRowHeight:100,//最大行高，超过最大行高时隐藏单元格内容
			idField:'VC_ID',//指明哪一个字段是标识字段。
			url:null,//一个URL从远程站点请求数据。
			loadMsg:'正在加载数据，请稍候...',//在从远程站点加载数据的时候显示提示消息。
			pagination:true,//如果为true，则在DataGrid控件底部显示分页工具栏。
			pageSize:20,//在设置分页属性的时候初始化页面大小。
			pageList:[10,20,50,100],//在设置分页属性的时候 初始化页面大小选择列表。
			queryParam:[],//请求URL时传递的参数
			toolbar:[]//操作按钮
	};
	//构建按钮信息
	function buildToolbar(target,opt){
		var htm = [];
		htm.push('<thead><tr><td colspan=2>');
		htm.push('<div class=toolbar></div>');
		htm.push('</td></tr></thead>');
		var tbl = $('>.fast-table-container',this);
		$('>thead',tbl).remove();
		tbl.prepend(htm.join(''));
		var tb = $('>thead .toolbar',tbl);
		for(var i=0;i<opt.toolbar.length;i++){
			var btn = $('<a></a>').appendTo(tb).linkbutton($.extend(opt.toolbar[i],{plain:true}));
		}
	}
	//构建分页信息
	function buildPageInfo(target,opt){
		var htm = [];
		htm.push('<tfoot><tr><td colspan=2>');
		htm.push('<nobr class=pageinfo>');
		htm.push('<a class="btn"><span class="pagination-load">&nbsp;</span></a>');
		htm.push('共 <span class=pagination-rowAmount></span> 条<span class="split">&nbsp;</span>');
		htm.push('<a class="btn"><span class="pagination-first">&nbsp;</span></a>');
		htm.push('<a class="btn"><span class="pagination-prev">&nbsp;</span></a>');
		htm.push('第<input type="text" class=pagination-page val="1" value="1"/>页,共<span class=pagination-pageAmount></span>页');
		htm.push('<a class="btn" ><span class="pagination-next">&nbsp;</span></a>');
		htm.push('<a class="btn" ><span class="pagination-last">&nbsp;</span></a>');
		htm.push('<select class=pagination-rows >');
		for(var i=0;i<opt.pageList.length;i++){
			htm.push('<option value="'+opt.pageList[i]+'">每页'+opt.pageList[i]+'条</option>');
		}
		htm.push('</select>');
		htm.push('</nobr>');
		htm.push('</td></tr></tfoot>');
		var tbl = $('>.fast-table-container',this);
		$('>tfoot',tbl).remove();
		var tf = $(htm.join('')).appendTo(tbl);
		$('.pagination-load',tf).click(function(){
			loadData(this);
		});
		$('.pagination-first',tf).click(function(){
			loadData(this,null,1);
		});
		$('.pagination-prev',tf).click(function(){
			loadData(this,null,Math.max(1,(opt.pageNumber||1)-1));
		});
		$('.pagination-page',tf).bind('blur keyup',function(){
			if((event.type=='blur' || event.keyCode==13) && $(this).attr('val')!=$(this).val()){
				$(this).attr('val',$(this).val());
				loadData(this,null,parseInt($(this).val()));
			}
		});
		$('.pagination-next',tf).click(function(){
			loadData(this,null,Math.max(1,(opt.pageNumber||1)-1));
		});
		$('.pagination-last',tf).click(function(){
			loadData(this,null,Math.max(1,(opt.pageAmount||1)));
		});
		$('.pagination-rows',tf).click(function(){
			opt.pageSize = parseInt($(this).val());
			loadData(this,null,1);
		});
	}
	//从远程加载数据
	function loadData(target,param,page){
		var tbl = $(target);
		var opt = tbl.data('options');
		if(!opt){
			return;
		}
		param = $.extend(opt.queryParam,param);
		param['pager.rows'] = opt.pageSize;
		param['pager.page'] = 1 || param['pager.page'] || page;
		$.ajax({
				url:opt.url,data:param,type:'post',dataType:'html',
				success:function(res){
					$('>tbody',tbl).html(res);
				},
				error:function(xmp,status,e){
					$.messager.alert('提示','数据加载失败。<div>'+status+e+'</div>');
				}
		});
		
	}
	function showLoading(target,opt){
		var loading = $('<div class=datagrid-mask></div>')
	}
})(jQuery);

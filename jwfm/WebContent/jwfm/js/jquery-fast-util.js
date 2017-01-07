window.path = document.location.href.replace(/.{8}[^\/]+(\/[^\/]+).*/,'$1');
(function($){
	if($('.searchDiv',this).length>0){//查询条件框自动隐藏
		$('.searchDiv',this).each(function(){
			var th = $(this);
			th.css('position','relative').data('old-height',th.height());
			if(th.height()<$('.searchDivContent',th).height()){
				$('<span class="searchDivFlex"></span>').attr('title','点击展开').appendTo(th).click(function(){
					$(this).toggleClass('up');
					if($(this).hasClass('up')){
						$(this).attr('title','点击收起');
						th.height($('.searchDivContent',th).height()+4);
					}
					else{
						$(this).attr('title','点击展开');
						th.height(th.data('old-height'));
					}
				});
			}
		});
	}
	//打开子窗口，默认大小800*600，窗口居中显示
	$.openWin = function(opt){
		var w = $(window);
		opt = $.extend({},$.openWin.defaults,opt);
		if($('#'+opt.divId).length==0){
			$('<div id='+opt.divId+' ></div>').appendTo('body');
		}
		var div = $('#'+opt.divId);
		var parOpt = null;
		if(opt.parWin){
			try{parOpt = $(opt.parWin).window('options');}catch(e){;}
		}
		opt.width = Math.min(w.width(),opt.width || (parOpt && parOpt.width) || parseInt(w.width()*2/3));
		opt.height = Math.min(w.height(),opt.height || (parOpt && parOpt.height) || parseInt(w.height()*2/3));
		opt.left = opt.left || (parOpt && parOpt.left) || parseInt(w.centerLeft()-opt.width/2);
		opt.top = opt.top || (parOpt && parOpt.top) || parseInt(w.centerTop()-opt.height/2);
		if(opt.href || opt.content){
			opt._href = opt.href;opt._content = opt.content;opt.href=null;opt.content=null;
		}
		var htm = '<div style="width:100%;height:100%;"><div id="'+opt.divId+'-main" region="center" border="false"></div><div id="'+
					opt.divId+'-btnArea" class=window-btnareas region="south" border="false"></div></div>';
		var frame = $(htm).appendTo(div.empty());
		var btnArea = $('#'+opt.divId+'-btnArea').height(33);
		if(opt.butParams && opt.butParams.length>0){
			for(var i=0;i<opt.butParams.length;i++){
				$('<a></a>').appendTo(btnArea).linkbutton(opt.butParams[i]);
			}
			$('a[id=btn-close]',btnArea).click(function(){
				$(this).closest('.window-body').window('close');
			});
		}
		else{
			btnArea.height(0);
		}
		frame.layout({fit:true});
		div.window(opt);
		var main = $('#'+opt.divId+'-main');
		if(opt._content){
			main.html(opt._content);
		}
		else if(opt._href){
			main.html('<table width=100% height=100%><tr><td align=center valign=middle><h1>正在加载...</h1></td></tr></table>');
			main.ajaxError(function(event,request, settings){
				if(settings && settings.dataType=='html'){
				     $(this).html("<table width=100% height=100%><tr><td align=center valign=middle><h1 style='color:red;'>加载页面时发生错误！</h1>"+
		    		 "<a style='cursor:pointer;color:blue;' class=errinfo>错误信息</a></td></tr></table>");
				     $('.errinfo',this).click(function(){
				    	 main.html(request.responseText);
				     });
				}
				else if(window.console){
					console.log(request.responseText);
				}
			});
			main.load(opt._href,opt.params);
		}
		return div;
	};
	$.openWin.defaults = {divId:'operateWindow',parWin:null,width:800,height:600,collapsible:false,minimizable:false,maximizable:true,closable:true,
			title:'操作窗口',url:null,params:{},butParams:[{id:'btn-close',text:'关闭',iconCls:'icon-close'}],onResize:function(){
				$('>div',this).layout('resize');
			}};
	//获取对象内所有可提交对象的键值对，返回键值对对象
	$.fn.formdata = function(){
		var params = {};
		$('input[name],select[name],textarea[name]',this).each(function(){
			var th = $(this);
			if(th.is('select,textarea,input')){
				params[$(this).attr('name')] = $(this).val();
			}
			if(th.is('input')){//如果是checkbox和radio控件，并且控件属于未选中状态，则将值置为空
				if(th.is(':checkbox,:radio') && !$(this).attr('checked')){
					params[$(this).attr('name')] = '';
				}
			}
		});
		return params;
	};
	//将最后一列的输入框进行右边对齐，同时对.subtitle增加收起与展开功能
	$.fn.initPage = function(){
		if(!$(this).is('table')){
			return;
		}
		var rightpos = 0,objs=$();
		$('input[type=text],select,textarea',this).each(function(){
			$(this).width($(this).width());
		});
		$('>*>tr',this).each(function(){
			$('>td:last',this).each(function(){
				var obj = $('>input[type=text],>select,>textarea,>span.combo',this).filter(':visible');
				if(obj.length==1 && obj.parent().find('>*:visible').length==1){
					rightpos = Math.max(rightpos,obj.offset().left+obj.outerWidth());
					objs = objs.add(obj);
				}
			});
		});
		objs.each(function(){
			var obj = $(this);
			var diff = rightpos-(obj.offset().left+obj.outerWidth());
			obj.width(obj.width()+diff);
			if(obj.is('span.combo')){
				$('.combo-text',obj).width($('.combo-text',obj).width()+diff);
			}
		});
		$('tr.subtitle',this).each(function(){//给标题行增加收起功能
			$('<span class="icon expand-icon tree-expanded"></span>').prependTo($('>td:first',this).attr('title','按Ctrl键展开栏目时可隐藏其他栏目')).click(function(){
				var collapsed = $(this).hasClass('tree-expanded');//是否收起操作
				var tr = $(this).parent().parent();
				tr.nextUntil('.subtitle')[collapsed?'hide':'show']();
				$(this).toggleClass('tree-expanded',!collapsed).toggleClass('tree-collapsed',collapsed);
				if(!collapsed){//展开时判断信息是否显示完整，如果显示不完整，则滚动页面
					if(event.ctrlKey){//按住Ctrl展开时，隐藏其他副标题内容
						tr.siblings().hide();
						$('.expand-icon',tr.siblings('.subtitle')).not(tr[0]).toggleClass('tree-expanded',false).toggleClass('tree-collapsed',true);
						tr.show();
						tr.nextUntil('.subtitle').show();
					}
				}
				else{
					tr.siblings('.subtitle:hidden').show();
				}
				return false;
			});
			$('>td:first',this).css('cursor','pointer').bind('click',function(){
				if($($.event.fix(event||{}).target).is('TD')){
					$('.expand-icon',this).click();
				}
			});
		});
		//增加必填标记和验证
		$('*[required],*[notnull],*[validType]',this).each(function(){
			var th = $(this);
			var required = (th.attr('notnull')||th.attr('required'))=='true';
			if(th.is(':visible')){
				var tip = th.attr('missingMessage')||(th.parent().prev().text().replace(/:|：/g,'')+'不能为空！');
				if(required)th.after('<rq/>');
				th.validatebox({required:required,missingMessage:tip,validType:th.attr('validType')});
			}
			else if(th.next().is('span.combo')){
				if(required)th.next().after('<rq/>');
			}
		});
		return this;
	};
	$.resultDealFun = function (data,showMsg,callback){
	    try{
	    	top.removeLoading=(top.removeLoading||$.util.removeLoading);
	    	top.showTip=(top.showTip||$.util.showTip);
            top.removeLoading();
	        json = jQuery.parseJSON(data);  //获取从服务器得到的数据反馈信息
	        function failureFun(){
             	if(callback && typeof(callback)=='function'){
             		callback(false,json);
             	}
            }
	        if(json.result=="ok"){    //操作成功处理   
	            top.showTip({content:showMsg||json.info||'操作成功！'});
	         	if(callback && typeof(callback)=='function'){
	         		callback(true,json);
	         	}
	        }else if(jsonReStr.opState=="error"){//操作时发生错误
	            $.messager.alert('消息提示','操作时发生服务器错误：<br><font color=red>'+json.info+'</font>','error',failureFun);
	        }
	        else{//操作失败处理
	            $.messager.alert('消息提示','操作处理失败！原因如下：<br><font color=red>'+json.info+'</font>','error',failureFun);
	        }
	    }catch(e){
	    	$.messager.alert('消息提示','出现系统错误!可能原因如下：<br><font color=red>'+e+'</font>','error',failureFun);
	    }
	};
	//进行添加或编辑操作后，根据返回的json数据获取操作结果信息的通用方法,用于列表页面时jsp生成的情况
	function returnOptMsg(data,reMsg,callback){
	    try{
	    	top.removeLoading=(top.removeLoading||$.util.removeLoading);
	    	top.showTip=(top.showTip||$.util.showTip);
	        jsonReStr = jQuery.parseJSON(data);  //获取从服务器得到的数据反馈信息
	        if(jsonReStr.opState=="success"){    //操作成功处理   
	         top.removeLoading();
	     	$('input[name="page"]').val($('input[name="page"]').attr('val'));
			$('form')[0].submit();
	         if(reMsg!=undefined||reMsg!=null){
	              top.showTip({content:reMsg});
	           }else if(jsonReStr.opInfo.length==0){  
	              top.showTip({content:'数据保存成功！'});
	           }else{
	              top.showTip({content:jsonReStr.opInfo});
	           }
	         	if(callback && typeof(callback)=='function'){
	         		callback(true);
	         	}
	        }else if(jsonReStr.opState=="failure"){  //操作失败处理
	            $('#operateWindow').window("open");
	            top.removeLoading();
	            $.messager.alert('消息提示','操作处理失败！原因如下：<br><font color=red>'+jsonReStr.opInfo+'</font>','error',function(){
	             	if(callback && typeof(callback)=='function'){
	             		callback(false);
	             	}
	            });
	        }
	       }catch(e){
	            $('#operateWindow').window("open");
	            top.removeLoading();
	            $.messager.alert('消息提示','出现系统错误!可能原因如下：<br><font color=red>'+e+'</font>','error',function(){
	             	if(callback && typeof(callback)=='function'){
	             		callback(false);
	             	}
	            });
	       }
	       return false;
	}

	$.fn.centerLeft = function(){//获取对象中心坐标的left坐标
		var th = $(this);
		var offset = th.offset()||{top:0,left:0};
		return th.scrollLeft()+th.width()/2+offset.left;
	};
	$.fn.centerTop = function(){//获取对象中心坐标的top坐标
		var th = $(this);
		var offset = th.offset()||{top:0,left:0};
		return th.scrollTop()+th.height()/2+offset.top;
	};
	$.util = {
		//显示一个提示信息
		showTip: function(options){
		   var opts = $.extend({
		   		title: '提示信息：',
				content: '内容',
				timeout:2500,
				width:150,
				height:35,
				showTitle:true,
				location:'center'
		   },options);
		   var style = {};  //样式
		   if('center'==opts.location)
		       style = {'width':opts.width,'height':opts.height,'top':parseInt($(window).centerLeft()-opts.height/2)+'px',
		       		'left':parseInt($(window).centerLeft()-opts.width/2)+'px'};
		   else if('top'==opts.location)
		       style = {width:opts.width,'height':opts.height,'top':5,left:parseInt($(window).centerLeft()-opts.width/2)+'px'};
		   var noticeBody = ''; //消息体
		   if(opts.showTitle)
		          noticeBody = '<ul class="fast-notice-title"><li>'+opts.title+'</li></ul>';
		    noticeBody = noticeBody + '<ul class="fast-notice-body"><li>'+opts.content+'</li></ul>';
		       
		   if($('.fast-notice').length==0){//不存在notice元素时创建
		       $('body').append('<div class=fast-notice></div>');
		   }
	       var th = $('.fast-notice:first');
	       th.empty().css(style).html(noticeBody); //添加消息体
           th.fadeIn("slow").delay(opts.timeout).fadeOut("slow");
		},
		//显示数据处理等待信息
		showLoading:function(msg){
			if($('.loading-mask-msg').length==0){
				$("<div class='loading-mask'></div><div class='loading-mask-msg'></div>").appendTo('body');
				$(window).scroll(function(){
					if($('.loading-mask').css('display')=='block'){
						var th = $(".loading-mask-msg");
			            th.css({left:parseInt($(window).centerLeft()-th.width()/2)+'px',top:parseInt($(window).centerTop()-th.height()/2)+'px'});
					}
				});
			}
			$('.loading-mask').css({display:"block",width:$(document).width(),height:$('body').height()}).appendTo("body");
            var th = $(".loading-mask-msg").html(msg||"正在处理，请稍候...").appendTo("body");
            th.css({display:"block",left:parseInt($(window).centerLeft()-th.width()/2)+'px',top:parseInt($(window).centerTop()-th.height()/2)+'px'});
		},
		
		//隐藏数据处理等待信息
		removeLoading:function(){
		    $('.loading-mask').remove();
			$('.loading-mask-msg').remove();
		},
		//自动缩放图片， 调用方法<img src='...' onload='$.util.scale({width:400,height:500})' />
		scale:function(options){
			var opt = $.extend({width:800,height:600,wheelCtrl:false,src:null,clickFullscreen:false},options);
			var img = $(opt.src||$.event.fix(event).target);
			if(img.length==0){
				return;
			}
			if(img.width()>opt.width||img.height()>opt.height){
				var ratio = Math.min(opt.width/img.width(),opt.height/img.height());
				var w=parseInt(img.width()*ratio),h=parseInt(img.height()*ratio);
				img.width(w);img.height(h);
			}
			if(opt.wheelCtrl){
				img.bind('mousewheel',function(){
					var e = $.event.fix(event);
					var img = $(this);
					if(img.length==0||(img.width()<20&&e.wheelDelta<0)){
						return;
					}
					var w=img.width()*(1+(e.wheelDelta>0?0.1:-0.1)),h=img.height()*(1+(e.wheelDelta>0?0.1:-0.1));
					var pos = img.position();pos.left-=((pos.width=w)-img.width())/2;pos.top-=((pos.height=h)-img.height())/2;
					pos.left+='px';pos.top+='px';
					img.css(pos);
					e.stopPropagation();
					return false;
				});
			}
			function fullscreen(){//双击在顶层窗口内全屏显示
				if(top.$){
					var _$=top.$,w=top;
					if(_$('#img_fullscreen_div').length==0){
						function hidefullscreen(){
							_$('#img_fullscreen_div').hide();
							if(_$('#img_fullscreen_div').data('angle')%4!=0){
								_$.messager.confirm('提示','您已经旋转了此图片，是否保存旋转后的图像？',function(r){
									if(r){
										var img=_$('#img_fullscreen_div>img');
										var url = img.attr('src');
										url = url + (url.indexOf('?')>0?'&':'?')+'save=true';
										_$.post(url);
									}
								});
							}
						}
						_$('<div id=img_fullscreen_div style="position:absolute;top:0px;left:0px;overflow:hidden;background:#eee;z-index:99999"></div>')
						.appendTo('body').bind('dblclick',hidefullscreen);
						_$('<div style="position:absolute;top:6px;right:0px;width:20px;height:20px;cursor:pointer;z-index:100000" class=icon-close title=关闭图片></div>')
						.appendTo('#img_fullscreen_div').click(hidefullscreen);
						_$('<div style="position:absolute;top:6px;right:20px;width:20px;height:20px;cursor:pointer;z-index:100000" class=icon-reload title=向右旋转></div>')
						.appendTo('#img_fullscreen_div').click(function(){
							var img=_$('#img_fullscreen_div>img');
							var url = img.attr('src');
							var angle = url.indexOf('angle=')>0?parseInt(url.replace(/.*angle=(\d+).*/g,'$1')||'0'):0;
							url = url.replace(/[\?|&]angle=(\d+)/i,'');
							url = url + (url.indexOf('?')>0?'&':'?')+'angle='+(angle+1);
							var w = img.width();
							img.attr('src',url);
							img.width(img.height()).height(w);
							_$('#img_fullscreen_div').data('angle',angle+1);
						});
						/*
						_$('<div style="position:absolute;top:6px;right:35px;width:30px;height:30px;cursor:pointer;z-index:100000" class=icon-redo></div>')
						.appendTo('#img_fullscreen_div').click(function(){_$('.full_screen_img').rotateRight();});*/
						_$(w).resize(function(){_$('#img_fullscreen_div').css({width:_$(w).width()+'px',height:_$(w).height()+'px'});});
						_$(w).scroll(function(){
							_$('#img_fullscreen_div').css('top',_$(w).scrollTop());
						});
					}
					_$('#img_fullscreen_div').data('angle',0);
					_$(w).scroll();
					_$(w).resize();
					_$('#img_fullscreen_div img').remove();
					_$('<img />').attr('src',$(this).attr('src').replace(/[\?|&](width|height)=\d+/i,'')).appendTo('#img_fullscreen_div').bind('load',function(){
						var img = $(this);
						var ww = $(window).width(),h=$(window).height();
						if(img.width()>ww||img.height()>h){
							var ratio = Math.min(ww/img.width(),h/img.height());
							var ww=parseInt(img.width()*ratio),h=parseInt(img.height()*ratio);
							img.width(ww);img.height(h);
						}
						img.css({position:'absolute',top:($(w).height()-img.height())/2,left:($(w).width()-img.width())/2}).show();
					}).bind('mousewheel',function(event){
						var e = $.event.fix(event);
						var img = $(this);
						if(img.length==0||(img.width()<20&&e.wheelDelta<0)){
							return;
						}
						var w=img.width()*(1+(e.wheelDelta>0?0.1:-0.1)),h=img.height()*(1+(e.wheelDelta>0?0.1:-0.1));
						var pos = img.position();pos.left-=((pos.width=w)-img.width())/2;pos.top-=((pos.height=h)-img.height())/2;
						pos.left+='px';pos.top+='px';
						img.css(pos);
						e.stopPropagation();
						return false;
					});
					if(_$.fn.draggable){
						_$('#img_fullscreen_div img').draggable();
					}
					_$('#img_fullscreen_div').show();
				}
			}
			img.bind('dblclick',fullscreen);
			if(opt.clickFullscreen){
				img.bind('click',fullscreen);
			}
		}
	};

	String.prototype.trim = String.prototype.trim||function () {return this.replace(/(^\s*)|(\s*$)/g,"");};
	String.prototype.cutString = String.prototype.cutString||function (bytesize) {var bytelen = 0;for(var i=0;i<this.length;i++){bytelen += this.charCodeAt(i)>=256?2:1;if(bytelen>=bytesize-3 && this.length-i>2){return this.substr(0,i+1)+'...';}}return this.toString();};
	String.prototype.startWith = String.prototype.startWith||function (arg) {return arg==null || arg.length==0?true:(arg.length>this.length?false:this.substr(0,arg.length)==arg);};
	String.prototype.endWith = String.prototype.endWith||function (arg) {return arg==null || arg.length==0?true:(arg.length>this.length?false:this.substr(this.length-arg.length)==arg);};

	Number.prototype.format = function (f) {pattern=(f||'0').toString();
		if(pattern.indexOf('%')==pattern.length-1)return Number(this*100).format(pattern.substr(0,pattern.length-1))+'%';//百分比
		var sign = this<0?'-':'',val = this<0?-this:this;
	    var str=val.toString();
	    var strInt,strFloat,formatInt=pattern,formatFloat=null;
	    if(/\./g.test(pattern)){
	        formatInt=pattern.split('.')[0];formatFloat=pattern.split('.')[1];
	    }
	    if(/\./g.test(str)){
	        if(formatFloat!=null){
	            var tempFloat = Math.round(parseFloat('0.'+str.split('.')[1])*Math.pow(10,formatFloat.length))/Math.pow(10,formatFloat.length);
	            strInt        = (Math.floor(val)+Math.floor(tempFloat)).toString();                
	            strFloat      = /\./g.test(tempFloat.toString())?tempFloat.toString().split('.')[1]:'0';            
	        }else{
	            strInt=Math.round(val).toString();strFloat='0';
	        }
	    }else{
	        strInt=str;strFloat='0';
	    }
	    if(formatInt!=null){
	        var outputInt= '',zero=formatInt.match(/0*$/)[0].length;
	        var comma         = null;
	        if(/,/g.test(formatInt)){
	            comma        = formatInt.match(/,[^,]*/)[0].length-1;
	        }
	        var newReg       = new RegExp('(\\d{'+comma+'})','g');
	        if(strInt.length<zero){
	            outputInt        = new Array(zero+1).join('0')+strInt;
	            outputInt        = outputInt.substr(outputInt.length-zero,zero);
	        }else{
	            outputInt        = strInt;
	        }
	        strInt=(outputInt.substr(0,outputInt.length%comma)+outputInt.substring(outputInt.length%comma).replace(newReg,(comma!=null?',':'')+'$1')).replace(/^,/,'');
	    }
	    if(formatFloat!=null){
	        var outputFloat = '';
	        var zero        = formatFloat.match(/^0*/)[0].length;
	        if(strFloat.length<zero){
	            outputFloat        = strFloat+new Array(zero+1).join('0');
	            var outputFloat1    = outputFloat.substring(0,zero);
	            var outputFloat2    = outputFloat.substring(zero,formatFloat.length);
	            outputFloat        = outputFloat1+outputFloat2.replace(/0*$/,'');
	        }else{
	            outputFloat        = strFloat.substring(0,formatFloat.length);
	        }
	        strFloat    = outputFloat;
	    }else{
	        if(pattern!='' || (pattern=='' && strFloat=='0')){
	            strFloat    = '';
	        }
	    }
	    return sign+strInt+(strFloat==''?'':'.'+strFloat);
	};
	Date.prototype.toString=function(){return this.format('yyyy-MM-dd HH:mm:ss');};
	Date.prototype.addYear=function(d){this.setFullYear(this.getFullYear()+d);return this;};
	Date.prototype.addMonth=function(d){this.setMonth(this.getMonth()+d);return this;};
	Date.prototype.addDay=function(d){this.setDate(this.getDate()+d);return this;};
	Date.prototype.addHour=function(d){this.setHours(this.getHours()+d);return this;};
	Date.prototype.addMinute=function(d){this.setMinutes(this.getMinutes()+d);return this;};
	Date.prototype.addSecond=function(d){this.setSeconds(this.getSeconds()+d);return this;};
	Date.prototype.format = function (f) {f=f||'yyyy-MM-dd';return f.replace('yyyy',this.getFullYear()).replace('MM',this.getMonth()+1)
	.replace('dd',this.getDate()).replace(/(HH|hh)/i,this.getHours()).replace('mm',this.getMinutes()).replace('ss',this.getSeconds())
	.replace(/(\D)(\d{1})(\D|$)/g,'$10$2$3').replace(/(\D)(\d{1})(\D|$)/g,'$10$2$3');};//两次相同的replace是为了能够匹配2012-3-3 2:9:4这种情况
	window.parseDate = function (d) {if(!d)return '';if(d&&d.type=='date')return d;if(/^\d{1,2}$/.test(d))d=d+'-1';if(/^\d{1,2}\D/.test(d))d=new Date().getFullYear()+'-'+d;d=(navigator.appVersion.toLowerCase().indexOf('msie')>=0)?d.replace(/(\d{4})(\D)(\d+\D\d+)/,'$3$2$1'):d;return new Date(Date.parse(d));};
	Array.prototype.indexOf=Array.prototype.indexOf||function(val,startIndex){for(var i=startIndex||0;i<this.length;i++){if(val==this[i])return i;}return -1;};
	Array.prototype.lastIndexOf=Array.prototype.lastIndexOf||function(val,startindex){for(var i=startindex||this.length-1;i>=0;i--){if(val==this[i])return i;}return -1;};
	jQuery.cookie = function (name, value, options) { if (typeof value != "undefined") { options = options || {}; if (value === null) { value = ""; options.expires = -1; } var expires = ""; if (options.expires && (typeof options.expires == "number" || options.expires.toUTCString)) { var date; if (typeof options.expires == "number") { date = new Date(); date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000)); } else { date = options.expires; } expires = "; expires=" + date.toUTCString(); } var path = options.path ? "; path=" + options.path : ""; var domain = options.domain ? "; domain=" + options.domain : ""; var secure = options.secure ? "; secure" : ""; document.cookie = [name, "=", encodeURIComponent(value), expires, path, domain, secure].join(""); } else { var cookieValue = null; if (document.cookie && document.cookie != "") { var cookies = document.cookie.split(";"); for (var i = 0; i < cookies.length; i++) { var cookie = jQuery.trim(cookies[i]); if (cookie.substring(0, name.length + 1) == (name + "=")) { cookieValue = decodeURIComponent(cookie.substring(name.length + 1)); break; } } } return cookieValue; } };

})(jQuery);

//文件上传组件
(function($){
	$.unsupportBreakpointUpload = typeof history.pushState == "function";//是否支持断点续传
	if($.unsupportBreakpointUpload && navigator.appVersion.match(/.*(Chrome)\/(\d+).*/g) && parseInt(RegExp.$2)<38){
		$.unsupportBreakpointUpload = false;
	}
	$.showFileInWindow = function (opt){//将指定的文件显示在新窗口中
		var opt = $.extend({},$.showFileInWindow.defaults,opt);
		if(!opt.ids){
			$.messager.alert('提示','请指定要显示的文件！');return;
		}
		if($('.show-file-info-div').length==0){
			$('<div class=show-file-info-div></div>').appendTo('body');
			$('.show-file-info-div').window(opt);
		}
		$('.show-file-info-div').window('open');
		$('.show-file-info-div').html('<div class=file-body></div>');
		var opt2 = $.extend({},$.fn.fileupload.defaults,{maxFileSize:10240*1024*1024,maxFileCount:999,titleMaxByte:44});
		loadExistFileInfo(opt2,opt.ids,function(success,data){
			if(success){
				var htm = [];
				var filebody = $('.show-file-info-div>.file-body')
				for(var i=0;data&&i<data.length;i++){
					var span = addFileHtmlEle(filebody,opt2,data[i].VC_NAME,data[i].N_BYTES);
					if(!span)return;
					span.attr('fileId',data[i].VC_ID).attr('state','complete');
					$('.upload-info',span).text('');
					$('.icon-del',span).hide();
					$('.file-progress-bar',span).width($('.file-progress',span).width());//进度条更新
					$('.file-name',span).width(270).html('<a href="'+opt2.loadFileUrl+data[i].VC_ID+'" target=_blank>'+data[i].VC_NAME.cutString(opt2.titleMaxByte)+'</a>');
				}
			}
			else{
				$('.show-file-info-div').text(data);
			}
		});
	};
	$.showFileInWindow.defaults = {ids:'',width:670,height:480,title:'查看文件',collapsible:false,minimizable:false};
	$.fn.fileupload = function(opt){//主方法
		if(!opt.fileType || opt.fileType=='当前模块英文简称'){
			alert('请指定fileType参数值！');return;
		}
		var th = $(this).hide();
		if(th.length==0){
			return;
		}
		if(!th.is('textarea,:text,:hidden')){
			alert('请在文本框上使用文件上传控件！');return;
		}
		if ($.unsupportBreakpointUpload) {//支持断点续传
			if(opt=='fileName'){//获取文件名称，返回数组对象
				var ary = [];
				var body = th.data('file-body');
				$('.file-span .file-name',body).each(function(){ary.push($(this).attr('title'));});
				return ary;
			}
			var opt = $.extend({target:this},$.fn.fileupload.defaults,opt);
			th.after('<input type=file /><span class=file-body></span>');
			var f = th.next(),body=f.next();
			$('<input type=button value=选择文件 />').insertBefore(f.hide()).click(function(){//隐藏选择文件按钮，使用按钮触发选择文件事件
				f.click();
			});
			th.data('file-body',body).bind('setValue',function(){
				var ary = [];
				$('.file-span',body).each(function(){ary.push($(this).attr('fileId'));});
				$(this).val(ary.join(','));
			})
			var errinfo = $('<div class=err-info></div>').appendTo(body);
			f.width(69).attr('multiple',opt.multiple);
			f[0].addEventListener("change",function(event){//上传文件按钮事件
				var evt = $.event.fix(event);
				var files = evt.target.files;
				for(var i=0;i<files.length;i++){
					if(!addFileInfo(files[i],body,opt)){
						break;
					}
				}
				$(this).val('');
				if(opt.autoStartUpload){
					$('.file-span[state=wait]:first',body).trigger('upload');
				}
			});
			var hisfile = $('<span class=history-file>历史上传</span>').insertAfter(f).toggle(!opt.hideHistory).click(function (){
				var wsize = {width:Math.min($(window).width(),600),height:Math.min($(window).height(),400)};
				if($('.history-file-div').length==0){
					$('<div class=history-file-div></div>').appendTo('body');
					$('.history-file-div').window($.extend({title:'请选择文件（此处列出您以前成功上传的文件，选中所需文件后关闭本窗口即可）',minimizable:false,collapsible:false},wsize));
				}
				$('.history-file-div .pagination-num').width(40);
				$('.history-file-div').empty().html('<div class=history-file-grid></div><a class="ok-btn" iconcls=icon-ok>确定</a>').window('open').window('resize',wsize);
				$('.history-file-div .ok-btn').linkbutton({plain:true}).click(function(){
					$('.history-file-div').window('close');
				});
				var cols = [[   
						{field:'VC_NAME',title:'文件名',width:240,sortable:true,editor:'text'},
						{field:'DT_UPLOAD:yyyy-MM-dd HH:mm',title:'上传时间',width:110,sortable:true,editor:'text',align:'center'},
						{field:'N_BYTES:filesize',title:'文件大小',width:60,sortable:true,editor:'text',align:'right'},
						{field:'VC_USER_NAME',title:'上传人',width:70,sortable:true,editor:'text',align:'center'},
						{title:'下载',width:48,field:'action',align:'center',formatter:function(value,row,index){
							return '<a href="'+opt.loadFileUrl+row.VC_ID+'" target=_blank>下载</a>';
						}}
				]];
				$('.history-file-grid').datagrid({pagination:true,rownumbers:true,fit:true,
					url:opt.historyFileUrl,
					queryParams:$.fn.datagrid.dealParam({"search.VC_NAME":"","search.DT_UPLOADBegin":"","search.DT_UPLOADEnd":""},[[]],cols),  //用于查询的参数以及初始值
					//数据列，根据显示的内容进行修改
					columns:cols,
					onSelect:function(idx,row){
						if(opt.maxFileCount==1){//如果只允许选择一个文件，则先清空已选择文件框
							body.empty();
						}
						var span = addFileHtmlEle(body,opt,row.VC_NAME,row.N_BYTES);
						if(!span)return;
						span.attr('fileId',row.vcId).attr('state','complete');
						$('.upload-info',span).text('上传成功');
						$('.file-progress-bar',span).width($('.file-progress',span).width());//进度条更新
						$('.file-name',span).html('<a href="'+opt.loadFileUrl+row.vcId+'" target=_blank>'+row.vcFilename.cutString(opt.titleMaxByte)+'</a>');
						$(opt.target).trigger('setValue');
						if(opt.maxFileCount==1){//如果只允许选择一个文件，选择后自动关闭窗口
							$('.history-file-div').window('close');
						}
					}
				});
			});
			//已上传文件显示
			if(th.val()!=''){
				var bodyspan = body;
				loadExistFileInfo(opt,th.val(),function(success,data){
					if(success){
						var htm = [];
						for(var i=0;data&&i<data.length;i++){
							var span = addFileHtmlEle(bodyspan,opt,data[i].VC_NAME,data[i].N_BYTES);
							if(!span)return;
							span.attr('fileId',data[i].fileId).attr('state','complete');
							$('.upload-info',span).text('上传成功');
							$('.file-progress-bar',span).width($('.file-progress',span).width());//进度条更新
							$('.file-name',span).html('<a href="'+opt.loadFileUrl+data[i].fileId+'" target=_blank>'+data[i].fileName.cutString(opt.titleMaxByte)+'</a>');
						}
					}
					else{
						errinfo.text(data);
					}
				});
			}
		}
		else{//不支持断点续传
			var ext = new Date().getTime()+'-'+($.fn.fileupload.defaults.fileBoxIndex++);
			if(!th.attr('id')){
				th.attr('id','fileId'+ext);
			}
			th.after('<a class=upload-btn>上传附件</a> <font color=red>您的浏览器不支持断点续传，请使用高版本谷歌浏览器(38以上)或其他推荐浏览器</font>'+
					'<div id="fileHtml'+ext+'" class=file-html-div></div>');
			var btn = th.next();
			btn.click(function(){
				fileupload(th.attr('id'),null,'fileHtml'+ext,opt.fileType,{param:{fileTypes:opt.allowExt,fileTypeDescription:opt.allowExt,
						maxFileCnt:opt.maxFileCount,urlSplit:'<br/>'}});
			});
			//已上传文件显示
			if(th.val()!=''){
				var opt = $.fn.fileupload.defaults;
				loadExistFileInfo(opt,th.val(),function(success,data){
					if(success){
						var htm = [];
						for(var i=0;data&&i<data.length;i++){
							htm.push('<a href="'+opt.loadFileUrl+data[i].fileId+'" target=_blank>'+data[i].fileName+'</a>');
						}
						$('#fileHtml'+ext).html(htm.join('<br/>'));
					}
					else{
						$('#fileHtml'+ext).html(data);
					}
				});
			}
		}
	};
	function loadExistFileInfo(opt,ids,callback){
		$.post(opt.loadFileInfoUrl,{ids:ids},function(res){
			try {
				var json = $.parseJSON(res);
			} catch (e) {
				callback(false,res.indexOf('重新登录系统')>0?'请先登录系统！':'加载已上传文件信息时发生错误：'+e+'');
				return;
			}
			callback(true,json);
		});
	}
	//根据文件信息在页面上添加显示控件
	function addFileInfo(file,body,opt){
		if($('.file-span',body).length>=opt.maxFileCount){
			$.messager.alert('提示','最多允许上传'+opt.maxFileCount+'个文件');
			return false;
		}
		if(file.size>opt.maxFileSize){
			$.messager.alert('提示','您选择的文件['+file.name+']'+fileSizeFormat(file.size)+'，超过了最大允许文件大小'+fileSizeFormat(opt.maxFileSize));
			return false;
		}
		var span = addFileHtmlEle(body,opt,file.name,file.size);
		if(!span)return false;
		span.data('file',file);
		return true;
	}
	function addFileHtmlEle(body,opt,fileName,fileSize){
		if($('.file-span',body).length>=opt.maxFileCount){
			$.messager.alert('提示','最多允许上传'+opt.maxFileCount+'个文件');
			return false;
		}
		if(!opt.testAllowExt(fileName)){
			return false;//文件名校验失败后返回 false
		}
		var htm = '<span class=file-span><span class=file-icon></span><span class=file-name></span>'+
				'<span class=btnarea><span class="btn icon-pause">暂停</span><span class="btn icon-del">删除</span></span>'+
				'<span class=file-progress><span class=file-progress-bar-box><span class=file-progress-bar></span></span><span class=file-progress-text></span></span><span class=upload-info>等待上传</span></span>';
		var span = $(htm).appendTo(body).data('opt',opt).attr('state','wait').attr('tid',new Date().getTime()).bind('upload',function(){
			var filespan = $(this);
			if(filespan.data('timeHandel')){
				clearInterval(filespan.data('timeHandel'));
				filespan.data('timeHandel',null);
			}
			startUpload($(this).data('pause',false));//上传事件
		}).bind('error',function(event,errInfo){
			var filespan = $(this);
			$('.upload-info',filespan).text(errInfo);
			filespan.attr('state','error').data('pause',true);
			$('.icon-pause',this).text('继续');
			if(opt.errTimeout>0){//指定时间内重新尝试上传
				var timeCount = parseInt(opt.errTimeout);
				$('.upload-info',filespan).text(errInfo+' ('+(timeCount)+')');
				var handel = setInterval(function(){//计时器
					$('.upload-info',filespan).text(errInfo+' ('+(--timeCount)+')');
					if(timeCount<=0){
						filespan.trigger('upload');
						clearInterval(handel);
					}
				},1000);
				filespan.data('timeHandel',handel);
			}
		});
		$('.file-name',span).attr('title',fileName).text(fileName.cutString(opt.titleMaxByte));
		$('.btn',span).mouseover(function(){
			$('.btn.hover').removeClass('hover');
			$(this).addClass('hover');
		}).mouseleave(function(){
			$(this).removeClass('hover');
		});
		$('.file-progress-text',span).append(fileSizeFormat(fileSize));
		$('.icon-pause',span).hide();//隐藏暂停按钮
		$('span.icon-pause',span).click(function(){//暂停，继续上传按钮
			if($(this).text()=='暂停'){
				span.data('pause',true);
				if(span.data('xhr')){
					span.data('xhr').abort();
				}
				$(this).text('继续');
				$('.upload-info',span).text('已暂停');
			}
			else{
				span.data('pause',false);
				$(this).text('暂停');
				span.trigger('upload');
			}
		});
		$('span.icon-del',span).click(function(){//删除按钮
			if(span.attr('fileId') && opt.delFileUrl){//如果是已上传的文件，需要到服务器上标记删除
				$.get(opt.delFileUrl,{delIds:span.attr('fileId')});
			}
			span.remove();
			$(opt.target).trigger('setValue');
		});
		return span;
	}
	//开始上传指定的文件，上传完成后自动上传后续文件
	function startUpload(filespan,container){
		if(typeof(filespan)=='string'){
			filespan = $(filespan,container);
		}
		var stat = filespan.data('filestate');
		if(stat==2){//已完成上传
			if(filespan.next().length>0){
				startUpload(filespan.next());return;
			}
		}
		else if(stat==1){//正在上传
			return;
		}
		else{//未开始上传
			uploadFile(filespan,function(){
				var span = $('.file-span[state=wait]:first',filespan.parent());
				if(span.length>0){
					setTimeout(function(){
						startUpload(span);
					},300);
				}
			});
		}
	}
	//执行文件上传操作(分段上传)
	function uploadFile(filespan,callback){
		if(filespan.data('pause')){
			return;//如果已暂停，则不再继续上传
		}
		var opt = filespan.data('opt');
		var file = filespan.data('file');
		var start = filespan.data('filePos')||0;
		if(!file)return;
		$('.file-progress-bar',filespan).width($('.file-progress',filespan).width()*start/file.size);//进度条更新
		if(start>=file.size){//如果文件已上传完毕，则调用回调方法
			$('.file-name',filespan).html('<a href="'+opt.loadFileUrl+filespan.attr('fileId')+'" target=_blank>'+$('.file-name',filespan).text()+'</a>');
			$('.upload-info',filespan).text('上传成功');
			filespan.attr('state','complete');
			$('.icon-pause',filespan).hide();//隐藏暂停按钮
			if(typeof(callback)=='function'){
				callback.call();
			}
			return;
		}
		if(start==0){
			$('.upload-info',filespan).text('准备上传');
		}
		var data = new FormData();
		data.append("fileType", opt.fileType);
		data.append("fileName", file.name);
		data.append("fileSize", file.size+'');
		data.append("lastModify", file.lastModified+'');
		data.append("start", start+'');
		//第一次先上传1K的内容，确认文件是否以前上传过，如果以前上传过，则使用断点续传
		var endPos = start + (start==0?1024:opt.splitSize);
		data.append("file", file.slice(start, endPos));
		// XMLHttpRequest 2.0 请求
		var xhr = new XMLHttpRequest();
		filespan.data('xhr',xhr);//绑定对象，供暂停按钮调用
		xhr.open("post", opt.uploadUrl, true);				
		//xhr.setRequestHeader("X_Requested_With", location.href.split("/")[5].replace(/[^a-z]+/g,"$"));
		// 上传进度中
		xhr.upload.addEventListener("progress", function(e) {
			var wid = $('.file-progress',filespan).width();
			$('.file-progress-bar',filespan).width(Math.min(wid,wid*(e.loaded + start) / file.size));
			showUploadInfo(filespan,file.size,e.loaded + start);
		}, false);
		// ajax成功后
		xhr.onreadystatechange = function(e) {
			if (xhr.readyState == 4) {
				filespan.data('xhr',null);//解绑对象，释放内存
				if (xhr.status == 200) {
					try {
						var json = $.parseJSON(xhr.responseText);
					} catch (e) {
						return filespan.trigger('error',['上传时发生错误：'+(e)]);
					} 
					if (!json || !json.success) {
						return filespan.trigger('error',['上传时发生错误：'+(json&&json.info)]);
					}
					filespan.data('filePos',json.length);
					if(json.length>endPos){//如果返回大小比上传的大，则是断点续传，要重置速度显示缓存，避免显示速度不准确
						$('.upload-info',filespan).text('开始续传');
						resetUploadInfo(filespan);
					}
					if(json.fileId){
						filespan.attr('fileId',json.fileId);//将服务器回传的文件ID写入filespan对象中
						var th = $(opt.target),ary=th.val()==''?[]:th.val().split(',');
						ary.push(json.fileId);
						th.val(ary.join(','));//将所有文件ID写入源文本框内
					}
					uploadFile(filespan,callback);//成功返回后递归调用，在调用后再判断是否上传完成
				} else if(!filespan.data('pause')){
					return filespan.trigger('error',['上传时发生错误：HTTP CODE '+xhr.status]);
				}
			}
		};
		try{
			xhr.send(data);
		}
		catch(e){
			filespan.trigger('error',['上传时发生错误：'+e]);
		}
		$('.icon-pause',filespan).show();//显示暂停按钮
	}
	//上传时显示上传进度、上传速度、剩余时间
	function showUploadInfo(filespan,fileSize,uploadSize){
		var tid = filespan.attr('tid');
		var timeary = showUploadInfo[tid+'time']||[];//时间队列
		var sizeary = showUploadInfo[tid+'size']||[];//已上传字节数队列
		var now = new Date().getTime();
		timeary.push(now);
		sizeary.push(uploadSize);
		showUploadInfo[tid+'time'] = timeary;
		showUploadInfo[tid+'size'] = sizeary;
		var remain = fileSize-uploadSize;//剩余字节大小 byte
		if(remain<=0){
			$('.upload-info',filespan).text('文件已上传，服务器正在处理，请稍候！');
		}
		if(now-(showUploadInfo[tid+'lastUpdate']||0)<1000){
			return;
		}
		showUploadInfo[tid+'lastUpdate'] = now;
		if(timeary.length>1 && timeary[0]!=timeary[timeary.length-1]){
			while(timeary[timeary.length-1]-timeary[0]>5000){//按5秒内数据统计速度
				timeary.shift();sizeary.shift();
			}
			var speed = 1000*(sizeary[sizeary.length-1]-sizeary[0])/(timeary[timeary.length-1]-timeary[0]);//上传速度 byte/s
			var time = parseInt(remain/speed);//剩余秒数 s
			$('.upload-info',filespan).text(addDot0(fileSizeFormat(uploadSize))+'/'+fileSizeFormat(fileSize)+' '+
					addDot0(fileSizeFormat(speed))+'/s '+timeFormat(time));
			var opt = filespan.data('opt');
			if(speed*10<opt.splitSize*2/3 || speed*10>opt.splitSize*1.5){//根据上传速度自动调整分块大小，提高上传效率
				opt.splitSize = speed*10;//分拆成10秒能传送1个包
			}
		}
	}
	//重置速度显示缓存，避免显示速度不准确
	function resetUploadInfo(filespan){
		var tid = filespan.attr('tid');
		showUploadInfo[tid+'time']=[];
		showUploadInfo[tid+'size']=[];
		showUploadInfo[tid+'lastUpdate']=0;
	}
	function fileSizeFormat(size){
		if(size<1024){
			return size+'B';
		}
		else if(size<1024*1024){
			return parseInt(size/102.4)/10+'K';
		}
		else if(size<1024*1024*1024){
			return parseInt(size/1024/102.4)/10+'M';
		}
		else{
			return parseInt(size/1024/1024/102.4)/10+'G';
		}
	}
	function addDot0(str){
		return str.indexOf('.')>0?str:str.replace(/(\d+)/g,'$1.0');
	}
	function add0(i){
		return i<10?'0'+i:i;
	}
	function timeFormat(time){
		if(time<60*60){
			return parseInt(time/60)+':'+add0(time%60);
		}
		else{
			return parseInt(time/60/60)+':'+add0(parseInt((time%(60*60))/60))+':'+add0(time%60);
		}
	}
	$.fn.fileupload.defaults = {multiple:true,maxFileSize:100*1024*1024,maxFileCount:10,splitSize:1024*1024,
			fileType:'other',autoStartUpload:true,fileBoxIndex:1,titleMaxByte:34,allowExt:'',hideHistory:false,errTimeout:0,
			testAllowExt:function(fileName){
				if(!this.allowExt || this.allowExt=='*.*')return true;
				var ary = this.allowExt.split(',');
				fileName = fileName.toLowerCase();
				for(var i=0;i<ary.length;i++){
					if(fileName.length>ary[i].length-1 && fileName.substr(fileName.length-ary[i].length+1).toLowerCase()==ary[i].substr(1).toLowerCase()){
						return true;
					}
				}
				$.messager.alert('提示','您选择的文件不符合要求('+this.allowExt+')');
				return false;
			},
			uploadUrl:path+'/jwfm/userFileUpload_uploadFile.action',
			loadFileInfoUrl:path+'/jwfm/userFileUpload_loadFileInfo.action',delFileUrl:path+'/jwfm/userFileUpload_delFiles.action',
			loadFileUrl:path+'/jwfm/userFileUpload_loadFile.action?vcId=',historyFileUrl:path+'/jwfm/userFileUpload.action?op=searchDataAjax&datagrid_type=easyui'};
	$('head').append('<style>'+
			'.history-file{cursor:pointer;color:#6666CC;margin-left:10px;vertical-align:bottom;line-height:14px;}'+
			'.history-file-div{overflow:hidden;}'+
			'.history-file-grid{width:100%;height:100%;}'+
			'.file-body{display:block;padding-top:4px;}'+
			'.file-body span{display:inline-block;font-size:12px;}'+
			'.file-body .err-info{color:red;}'+
			'.file-body span.file-span{border:0px solid #ccc;margin:0px 8px 8px 0px;width:310px;height:44px;position:relative;background:#efefef;}'+
			'.file-span span.file-icon{background:url(/mis/images/docs-32.png);position:absolute;left:2px;top:6px;width:32px;height:32px;}'+
			'.file-span span.file-name{width:210px;height:14px;position:absolute;left:36px;top:7px;text-overflow:ellipsis;cursor:default;}'+
			'.file-span span.btnarea{width:70px;height:16px;position:absolute;right:5px;top:7px;text-align:right;}'+
			'.file-span span.btn{padding:0px 2px;cursor:pointer;}'+
			'.file-span span.btn.hover{background:#0066CC;color:#fff;}'+
			'.file-span .file-progress{width:50px;height:12px;position:absolute;left:36px;bottom:7px;text-align:center;font-size:8px;line-height:12px;border:1px solid #99CCFF;overflow:hidden;}'+
			'.file-span .file-progress-bar-box{width:100%;height:100%;position:relative;}'+
			'.file-span .file-progress-bar{width:0px;height:12px;background:#99CCFF;position:absolute;left:0px;bottom:0px;}'+
			'.file-span .file-progress-text{width:100%;height:100%;position:absolute;left:0px;bottom:0px;}'+
			'.file-span .upload-info{position:absolute;left:90px;height:14px;line-height:12px;bottom:6px;color:gray;}'+
			'.upload-btn{color:blue;cursor:pointer;}'+
			'.file-html-div{padding-top:5px;line-height:18px;}'+
			'.history-file-div .pagination-num{width:25px;}'+
			'.history-file-div .pagination-info{padding-right:60px;}'+
			'.history-file-div{position:relative;}'+
			'.history-file-div .ok-btn{position:absolute;right:2px;bottom:3px;}'+ 
			'.show-file-info-div .file-body{padding:7px;}'+ 
			'</style>');
})(jQuery);
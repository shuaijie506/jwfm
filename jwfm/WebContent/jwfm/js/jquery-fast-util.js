
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
	};
	$.openWin.defaults = {divId:'operateWindow',parWin:null,width:800,height:600,collapsible:false,minimizable:false,maximizable:true,closable:true,
			title:'操作窗口',url:null,params:{},butParams:[{id:'btn-close',text:'关闭',iconCls:'icon-close'}],onResize:function(){
				$('>div',this).layout('resize');
			}};
	//获取对象内所有可提交对象的键值对，返回键值对对象
	$.fn.formdata = function(){
		var params = {};
		$('input[name],select[name],textarea[name]',this).each(function(){
			var tagName = ($(this).attr('tagName')||'').toLowerCase();
			if(tagName=='select' || tagName=='textarea' || tagName=='input'){
				params[$(this).attr('name')] = $(this).val();
			}
			if(tagName=='input'){//如果是checkbox和radio控件，并且控件属于未选中状态，则将值置为空
				var type = $(this).attr('type');
				if((type=='checkbox' || type=='radio') && !$(this).attr('checked')){
					params[$(this).attr('name')] = '';
				}
			}
		});
		return params;
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
	window.parseDate = function (d) {if(!d)return '';if(d&&d.type=='date')return d;if(/^\d{1,2}$/.test(d))d=d+'-1';if(/^\d{1,2}\D/.test(d))d=new Date().getFullYear()+'-'+d;d=$.browser.msie?d.replace(/(\d{4})(\D)(\d+\D\d+)/,'$3$2$1'):d;return new Date(Date.parse(d));};
	jQuery.cookie = function (name, value, options) { if (typeof value != "undefined") { options = options || {}; if (value === null) { value = ""; options.expires = -1; } var expires = ""; if (options.expires && (typeof options.expires == "number" || options.expires.toUTCString)) { var date; if (typeof options.expires == "number") { date = new Date(); date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000)); } else { date = options.expires; } expires = "; expires=" + date.toUTCString(); } var path = options.path ? "; path=" + options.path : ""; var domain = options.domain ? "; domain=" + options.domain : ""; var secure = options.secure ? "; secure" : ""; document.cookie = [name, "=", encodeURIComponent(value), expires, path, domain, secure].join(""); } else { var cookieValue = null; if (document.cookie && document.cookie != "") { var cookies = document.cookie.split(";"); for (var i = 0; i < cookies.length; i++) { var cookie = jQuery.trim(cookies[i]); if (cookie.substring(0, name.length + 1) == (name + "=")) { cookieValue = decodeURIComponent(cookie.substring(name.length + 1)); break; } } } return cookieValue; } };

})(jQuery);

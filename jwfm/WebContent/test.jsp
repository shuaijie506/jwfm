﻿<%@page import="com.dx.jwfm.framework.web.tag.HtmlUtil"%>
<%@ page language="java" import="java.util.*,java.io.*" pageEncoding="UTF-8"%>
<%if(request.getParameter("type")==null || !request.getParameter("type").endsWith("data")){ %>
<script type="text/javascript" src="/jwfm/js/easyui/jquery.min.js"></script> <!-- jquery框架 -->
<script type="text/javascript">
var t = new Date().getTime();
document.write(navigator.appVersion.toLowerCase());
</script>
<div id=timediv>begin test</div>
<%} %>
<%
long l = System.currentTimeMillis();
response.getWriter().flush();
String path = request.getContextPath();
String basePath = path + request.getRequestURI();
if("ajax".equals(request.getParameter("type"))){
%>
<div id=main></div>
<script type="text/javascript">
var t = new Date().getTime();
$.post('test.jsp?type=ajaxdata&t='+new Date().getTime(),function(res){
	$('#main').html(res);
	timediv.innerText = (new Date().getTime()-t)+'   '+(new Date().getTime()-<%=l%>)+'  '+navigator.appVersion;
});
</script>
<%}else if("ajaxdata".equals(request.getParameter("type"))){ %>
<%=HtmlUtil.genTestHtml(5000, 10) %>
<%}else if("json".equals(request.getParameter("type"))){
%>
<div id=main></div>
<script type="text/javascript">
var t = new Date().getTime();
$.post('test.jsp?type=jsondata&t='+new Date().getTime(),function(res){
	var ary = $.parseJSON(res);
	var htm = ['<table>'];
	for(var i=0;i<ary.length;i++){
		htm.push('<tr>');
		for(var j=0;j<ary[i].length;j++){
			htm.push('<td>');
			htm.push(ary[i][j]);
			htm.push('</td>');
		}
		htm.push('</tr>');
	}
	htm.push('</table>');
	$('#main').html(htm.join(''));
	timediv.innerText = (new Date().getTime()-t)+'   '+(new Date().getTime()-<%=l%>)+'  '+navigator.appVersion;
});
</script>
<%}else if("jsonadd".equals(request.getParameter("type"))){
	%>
	<div id=main></div>
	<script type="text/javascript">
	var t = new Date().getTime();
	$.post('test.jsp?type=jsondata&t='+new Date().getTime(),function(res){
		var ary = $.parseJSON(res);
		var htm = '<table>';
		for(var i=0;i<ary.length;i++){
			htm+='<tr>';
			for(var j=0;j<ary[i].length;j++){
				htm+='<td>';
				htm+=ary[i][j];
				htm+='</td>';
			}
			htm+='</tr>';
		}
		htm+='</table>';
		$('#main').html(htm);
		timediv.innerText = (new Date().getTime()-t)+'   '+(new Date().getTime()-<%=l%>)+'  '+navigator.appVersion;
	});
	</script>
	<%}else if("jsondata".equals(request.getParameter("type"))){ %>
<%=HtmlUtil.genTestJson(5000, 10) %>
<%}else{ %>
<%=HtmlUtil.genTestHtml(5000, 10) %>
<%-- <table>
<tr><td><div style="width:100px;height:100px;overflow:hidden;">我是标题栏我是标题栏我是标题栏我是标题栏我是标题栏我是标题栏我是标题栏我是标题栏我是标题栏</div></td></tr>
<tr><td><table><tbody id=tb></tbody></table></td></tr>
</table> 
$('#tb').html('<head><title>sssss</title></head><body>有错误发生<div>详细错误</div></body>');
$('table').append('<tfoot><tr><td>分布</td></tr></tfoot>');
--%>

<script type="text/javascript">
timediv.innerText = (new Date().getTime()-t)+'   '+(new Date().getTime()-<%=l%>)+'  '+navigator.appVersion;
</script>
<%} %>
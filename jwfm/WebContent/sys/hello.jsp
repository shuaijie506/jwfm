<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
%>
My name is ${name}

My name is <%=request.getAttribute("name") %>

<jsp:include page="/common/common.jsp"></jsp:include>
<script type="text/javascript" src="<%=path %>/jwfm/js/jquery-fast-util-easyui.js"></script>  <!-- easyui通用工具类 -->
<link rel="stylesheet" type="text/css" href="<%=path %>/js/codemirror-5.21.0/lib/codemirror.css"><!-- 代码编辑器样式表 -->
<link rel="stylesheet" type="text/css" href="<%=path %>/js/codemirror-5.21.0/theme/eclipse.css"><!-- 代码编辑器样式表 -->
<link rel="stylesheet" type="text/css" href="<%=path %>/js/codemirror-5.21.0/addon/hint/show-hint.css"><!-- 代码编辑器样式表 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/lib/codemirror.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/mode/meta.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/mode/javascript/javascript.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/mode/htmlmixed/htmlmixed.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/mode/css/css.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/mode/xml/xml.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/addon/hint/show-hint.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/addon/hint/css-hint.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/addon/hint/html-hint.js"></script> <!-- 代码编辑器所用JS文件 -->
<script type="text/javascript" src="<%=path %>/js/codemirror-5.21.0/addon/hint/javascript-hint.js"></script> <!-- 代码编辑器所用JS文件 -->

<div id=htmlEWBox></div>


<script>
window.editor = CodeMirror($('#htmlEWBox')[0],{value:'	<div id=htmlEWBox></div>',lineNumbers:true,mode:'htmlmixed',theme:'eclipse',tabSize:2});
</script>
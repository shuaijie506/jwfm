<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

My name is ${name}

My name is <%=request.getAttribute("name") %>

${REQUEST_FAST_MODEL.modelStructure.search.headHTML }
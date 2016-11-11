package com.hhkj.model.mis.sys;

import com.dx.jwfm.framework.web.action.FastBaseAction;
import com.dx.jwfm.framework.web.view.Node;

public class HelloWorld extends FastBaseAction {

	@Override
	public String execute() {
		return writeHTML("hello world213!");
	}
	
	public String toJsp(){
		setAttribute("name", "张三");
		return "hello.jsp";
	}
	
	public String toJsp2(){
		setAttribute("name", "张三");
		return "/sys/hello.jsp";
	}
	
	public String json(){
		Node n = new Node("1","张三丰");
		return writeJson(n);
	}

}

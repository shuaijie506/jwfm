package com.dx.jwfm.framework.core.servlet;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class VirtualServletConfig implements ServletConfig {
	
	ServletContext context;
	
	@SuppressWarnings("unchecked")
	public VirtualServletConfig(ServletContext context) {
		super();
		this.context = context;
		params = context.getInitParameterNames();
	}

	Enumeration<String> params;

	
	public String getInitParameter(String arg0) {
		return null;
	}

	
	public Enumeration<String> getInitParameterNames() {
		return params;
	}

	
	public ServletContext getServletContext() {
		return context;
	}

	
	public String getServletName() {
		return "FastVirtualServlet";
	}

}

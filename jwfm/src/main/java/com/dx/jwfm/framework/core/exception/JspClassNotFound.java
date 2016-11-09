package com.dx.jwfm.framework.core.exception;

public class JspClassNotFound extends Exception {

	private static final long serialVersionUID = 1L;
	public JspClassNotFound(String msg){
		super(msg);
	}
	public JspClassNotFound(Throwable e){
		super(e);
	}
	public JspClassNotFound(String msg,Throwable e){
		super(msg,e);
	}
}

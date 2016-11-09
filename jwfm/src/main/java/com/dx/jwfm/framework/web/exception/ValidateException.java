package com.dx.jwfm.framework.web.exception;

public class ValidateException extends Exception {

	private static final long serialVersionUID = 1L;
	public ValidateException(String msg){
		super(msg);
	}
	public ValidateException(Throwable e){
		super(e);
	}
	public ValidateException(String msg,Throwable e){
		super(msg,e);
	}
}

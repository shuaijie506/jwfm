package com.dx.jwfm.framework.core.exception;

public class ForwardException extends Exception {

	private static final long serialVersionUID = 1L;
	public ForwardException(String msg){
		super(msg);
	}
	public ForwardException(Throwable e){
		super(e);
	}
	public ForwardException(String msg,Throwable e){
		super(msg,e);
	}
}

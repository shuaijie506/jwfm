package com.dx.jwfm.framework.web.exception;

public class DatagridBuilderNotFound extends Exception {

	private static final long serialVersionUID = 1L;
	public DatagridBuilderNotFound(String msg){
		super(msg);
	}
	public DatagridBuilderNotFound(Throwable e){
		super(e);
	}
	public DatagridBuilderNotFound(String msg,Throwable e){
		super(msg,e);
	}
}

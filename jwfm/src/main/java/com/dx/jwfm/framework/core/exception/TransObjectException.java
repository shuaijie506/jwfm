package com.dx.jwfm.framework.core.exception;

public class TransObjectException extends RuntimeException {
	private static final long serialVersionUID = 6079858063902362868L;
	public TransObjectException(){
		super();
	}
	public TransObjectException(String msg){
		super(msg);
	}
	public TransObjectException(Throwable e){
		super(e);
	}
	public TransObjectException(String msg,Throwable e){
		super(msg,e);
	}
}

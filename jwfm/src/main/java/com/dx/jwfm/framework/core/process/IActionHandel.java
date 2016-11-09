package com.dx.jwfm.framework.core.process;

public interface IActionHandel {
	
	/**
	 * 在调用action中方法处理用户请求之前的调用方法
	 * @return 返回true时跳过action的处理代码,返回false时正常执行action
	 */
	public boolean beforeExecute(Object action,String method);
	
	/**
	 * 在调用action中方法处理用户请求之后的调用方法
	 */
	public void afterExecute(Object action,String method);

}

package com.dx.jwfm.framework.core.contants;

/**
 * @author Administrator
 * 用来存储HttpServletRequest中attribute中的键值
 */
public class RequestContants {
	/** 请求的URI。从/fast/fast/user/user_add.action?op=save中解析出的/fast/fast/user/user.action */
	public final static String REQUEST_URI = "REQUEST_URI";
	/** 请求URI的前缀。从/fast/fast/user/user_add.action?op=save中解析出的/fast/fast/user/user */
	public final static String REQUEST_URI_PRE = "REQUEST_URI_PRE";
	/** 请求URI的方法。从/fast/fast/user/user_add.action?op=save中解析出的add */
	public final static String REQUEST_URI_METHOD = "REQUEST_URI_METHOD";
	/** 请求URI的后缀。从/fast/fast/user/user_add.action?op=save中解析出的.action */
	public static final String REQUEST_URI_ACTIONEXT = "REQUEST_URI_ACTIONEXT";

	/** 请求处理过程中抛出的异常对象 */
	public final static String REQUEST_EXCEPTION = "exception";
	
	/** 请求对应的URL模型，存储对象是FastModel */
	public final static String REQUEST_FAST_MODEL = "REQUEST_FAST_MODEL";
	/** 请求对应的控制器action，存储对象是action对象 */
	public final static String REQUEST_FAST_ACTION = "REQUEST_FAST_ACTION";

}

package com.dx.jwfm.framework.web.filter;

/**
 * 根据用户ID创建用户对象，以保存在session中
 */
public interface LoginUserCreator {
	
	public Object createUser(String userId);

}

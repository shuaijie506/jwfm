package com.dx.jwfm.framework.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//在运行时可以获取  
@Target({ ElementType.TYPE })//作用到类的域上面  
public @interface FastModelInfo {
	/** 开发人员姓名 */
	public String author();
	/** 开发日期，同时也做版本号 */
	public String devDate();
	/*** 菜单对应分组名 */
	public String group();
	/*** 菜单名 */
	public String name();
	/*** 对应请求地址 */
	public String url();
	/** 更新信息 */
	public String updateInfo();
}

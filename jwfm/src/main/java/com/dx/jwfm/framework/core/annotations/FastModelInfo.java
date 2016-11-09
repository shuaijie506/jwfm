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
	/** 开发日期 */
	public String devDate();
	/** 更新信息 */
	public String updateInfo();
}

package com.dx.jwfm.framework.core.parser;

public interface IDefaultValueParser {
	
	/**
	 * 判断是否能够解析指定的变量
	 * @param variable
	 * @return
	 */
	public boolean hasDefaultValue(String variable);

	/**
	 * 根据指定的变量返回相应的值
	 * @param variable
	 * @return
	 */
	public Object getDefaultValue(String variable);
}

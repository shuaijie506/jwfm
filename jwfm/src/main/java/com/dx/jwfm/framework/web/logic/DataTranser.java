package com.dx.jwfm.framework.web.logic;

import java.util.Map;

/**
 * 将编码转换成文本，用于查看页面和列表页面
 * @author 宋帅杰
 */
public interface DataTranser {

	/**
	 * 对编码进行转换，转换成功后返回非null的字符串，不需转换的直接返回null
	 * @param colName	列名 
	 * @param code		编码值
	 * @return
	 */
	public String transData(String colName, Map<String,Object> row);
}

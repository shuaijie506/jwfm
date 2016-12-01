package com.dx.jwfm.framework.web.logic;

import java.util.Map;

import com.dx.jwfm.framework.core.model.search.SearchColumn;

public interface SQLConditionParser {

	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月26日 下午4:40:00
	 * 功能描述: 根据查询条件的设定和参数值生成SQL语句的过滤条件 如: and t.n_del=${N_DEL}
	 * 返回非null值时表示已被处理，后续处理类不能再处理
	 * 方法的参数和返回值: 
	 * @param srhcol
	 * @param paramMap
	 * @return
	 */
	public String createSqlFragment(SearchColumn srhcol,Map<String, Object> paramMap);
}

package com.dx.jwfm.framework.web.builder;

import java.util.List;

import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.ButtonAuth;
import com.dx.jwfm.framework.core.model.search.SearchResultColumn;
import com.dx.jwfm.framework.web.search.Pager;
import com.dx.jwfm.framework.web.search.Search;

/**
 * @author Administrator
 * 根据数据构建相应JS框架的JS内容文本
 */
public interface IDatagridBuilder {
	
	/**根据查询结果集构建JSON字符串
	 * @param list	查询结果集
	 * @param pager	分页信息
	 * @param search 
	 * @return
	 */
	public String buildResultJson(List<FastPo> list,Pager pager, Search search);
	
	/**
	 * 构建查询结果列的文本
	 * @param columns
	 * @param frozen
	 * @param hasChkCol
	 * @return
	 */
	public String buildResultColumn(List<SearchResultColumn> columns,boolean frozen,boolean hasChkCol);
	
	/**
	 * 构建工具栏的文本
	 * @param btns
	 * @return
	 */
	public String buildToolbar(List<ButtonAuth> btns);
	
	/**返回该模板对应的默认查询界面
	 * @return
	 */
	public String getDefaultSearchPage();
	/**返回该模板对应的默认编辑界面
	 * @return
	 */
	public String getDefaultEditPage();
}

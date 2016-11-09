package com.dx.jwfm.framework.core.model.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchModel {

	/**查询条件列*/
	private List<SearchColumn> searchColumns = new ArrayList<SearchColumn>();
	private HashMap<String,SearchColumn> searchColumnMap;
	/**查询结果展示列*/
	private List<SearchResultColumn> searchResultColumns = new ArrayList<SearchResultColumn>();
	/**查询SQL的select部分，包含select,from,where部分如：
	 * select * from tbl where n_del=0 */
	private String searchSelectSql;
	/**查询SQL的默认排序部分，不含order by关键字，只列出要排序的字段*/
	private String searchOrderBySql;
	/**查询页面的附加css样式，不含< style >< /style>标签部分*/
	private String customCss;
	/**查询页面的附加js代码，不含< script >< /script>标签部分*/
	private String customJs;
	/**表格数据行的样式和css*/
	private String vcRowStyle,vcRowCss;
	
	public SearchColumn getSearchColumn(String code) {
		if(searchColumnMap==null){
			searchColumnMap = new HashMap<String, SearchColumn>();
			for(SearchColumn col:searchColumns){
				searchColumnMap.put(col.getVcCode(), col);
			}
		}
		return searchColumnMap.get(code);
	}
	public List<SearchColumn> getSearchColumns() {
		return searchColumns;
	}
	public void setSearchColumns(List<SearchColumn> searchColumns) {
		this.searchColumns = searchColumns;
	}
	public List<SearchResultColumn> getSearchResultColumns() {
		return searchResultColumns;
	}
	public void setSearchResultColumns(List<SearchResultColumn> searchResultColumns) {
		this.searchResultColumns = searchResultColumns;
	}
	public String getSearchSelectSql() {
		return searchSelectSql;
	}
	public void setSearchSelectSql(String searchSelectSql) {
		this.searchSelectSql = searchSelectSql;
	}
	public String getSearchOrderBySql() {
		return searchOrderBySql;
	}
	public void setSearchOrderBySql(String searchOrderBySql) {
		this.searchOrderBySql = searchOrderBySql;
	}
	public String getCustomCss() {
		return customCss;
	}
	public void setCustomCss(String customCss) {
		this.customCss = customCss;
	}
	public String getCustomJs() {
		return customJs;
	}
	public void setCustomJs(String customJs) {
		this.customJs = customJs;
	}
	public String getVcRowStyle() {
		return vcRowStyle;
	}
	public void setVcRowStyle(String vcRowStyle) {
		this.vcRowStyle = vcRowStyle;
	}
	public String getVcRowCss() {
		return vcRowCss;
	}
	public void setVcRowCss(String vcRowCss) {
		this.vcRowCss = vcRowCss;
	}
}

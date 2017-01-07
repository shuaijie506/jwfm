package com.dx.jwfm.framework.web.search;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.MapObject;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.util.FastUtil;

public class Search extends MapObject  {

	/**用户指定的排序字段*/
	protected String orderby;
	private String delField = SystemContext.getDbDelFlagField();
	
	public Search(){
		this.put(delField , 0);
	}
	
	protected SearchModel getSearchModel(){
		FastModel fmodel = RequestContext.getFastModel();
		if(fmodel==null)return null;
		return fmodel.getModelStructure().getSearch();
	}

	public String getSearchSql(){
		SearchModel model = getSearchModel();
		StringBuffer sql = new StringBuffer(model.getSearchSelectSql());
		if(sql.toString().toLowerCase().indexOf("where")<0){
			sql.append(" where ").append(delField).append("=${").append(delField).append("}");
		}
		for(String key:map.keySet()){
			SearchColumn col = model.getSearchColumn(key);
			String sqlFrag = null;
			if(col==null || isBlank(col))continue;
			String val = (String)map.get(key);
			if(FastUtil.isNotBlank(val)){
				sqlFrag = getSqlFragment(col);
				sql.append(" ").append(sqlFrag);
			}
		}
		sql.append(" order by ");
		if(FastUtil.isNotBlank(orderby)){
			sql.append(orderby);
			if(orderby.endsWith(",")){
				sql.append(",");
			}
		}
		sql.append(model.getSearchOrderBySql());
		return replaceMacro(sql.toString());
	}

	protected String getSqlFragment(SearchColumn col) {
		return col.getSqlFragmentFinal(map);
	}


	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年12月29日 上午9:29:12
	 * 功能描述: 判断字段的内容是否为空
	 * 方法的参数和返回值: 
	 * @param fieldName
	 * @return
	 */
	protected boolean isBlank(SearchColumn col) {
		if(col==null || col.getVcCode()==null)return true;
		String fieldName = col.getVcCode();
		if(col.getSqlSearchType()!=null && col.getSqlSearchType().endsWith("Range")){//范围
			return FastUtil.isBlank(getString(fieldName)) && 
					FastUtil.isBlank(getString(fieldName+"Begin")) && FastUtil.isBlank(getString(fieldName+"End"));
		}
		return FastUtil.isBlank(getString(fieldName));
	}


	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月9日 下午3:09:18
	 * 功能描述: 将#{变量名}替换为宏
	 * 方法的参数和返回值: 
	 * @param sql
	 * @return
	 */
	private String replaceMacro(String sql) {
		if(sql==null || sql.length()==0){
			return "";
		}
		StringBuffer buff = new StringBuffer();
		int pos = sql.indexOf("#{");
		int lastpos = 0;
		if(pos>=0){
			while(pos>=0){
				buff.append(sql.substring(lastpos, pos));
				int nextpos = sql.indexOf("}",pos);
				String key = sql.substring(pos+2, nextpos);
				Object val = get(key);
				if(val!=null){
					buff.append(val);
				}
				lastpos = nextpos+1;
				pos = sql.indexOf("#{",lastpos);
			}
			if(lastpos<sql.length()){
				buff.append(sql.substring(lastpos));
			}
		}
		else{
			return sql;
		}
		return buff.toString();
	}

	public String getSearchCntSql(String sql){
		String lsql = sql.toLowerCase();
		int pos = lsql.indexOf(" from ");
		if(pos>0){
			int pos2 = lsql.lastIndexOf("select",pos);
			if(pos2<6){
				return "select count(*) "+sql.substring(pos);
			}
		}
		return "select count(*) from ("+sql+")";
	}

	public String getOrderby() {
		if(orderby==null){
			orderby = getString("ORDERBY");
		}
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

}

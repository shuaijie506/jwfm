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
	

	public String getSearchSql(){
		FastModel fmodel = RequestContext.getFastModel();
		if(fmodel==null)return null;
		SearchModel model = fmodel.getModelStructure().getSearch();
		StringBuffer sql = new StringBuffer(model.getSearchSelectSql());
		if(sql.toString().toLowerCase().indexOf("where")<0){
			sql.append(" where ").append(delField).append("=${").append(delField).append("}");
		}
		for(String key:map.keySet()){
			SearchColumn col = model.getSearchColumn(key);
			String sqlFrag = null;
			if(col==null || FastUtil.isBlank(sqlFrag=col.getSqlFragmentFinal(map)))continue;
			String val = (String)map.get(key);
			if(FastUtil.isNotBlank(val)){
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
		int pos = sql.toLowerCase().indexOf(" from ");
		if(pos>0){
			return "select count(*) "+sql.substring(pos);
		}
		else{
			return "select count(*) from ("+sql+")";
		}
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

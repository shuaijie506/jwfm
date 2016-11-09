package com.dx.jwfm.framework.web.search;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.MapObject;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.util.FastUtil;

public class Search extends MapObject  {

	/**用户指定的排序字段*/
	protected String orderby;
	
	public Search(){
		this.put("N_DEL", 0);
	}
	

	public String getSearchSql(){
		FastModel fmodel = RequestContext.getFastModel();
		if(fmodel==null)return null;
		SearchModel model = fmodel.getModelStructure().getSearch();
		StringBuffer sql = new StringBuffer(model.getSearchSelectSql());
		if(sql.toString().toLowerCase().indexOf("where")<0){
			sql.append(" where N_DEL=${N_DEL}");
		}
		for(String key:map.keySet()){
			SearchColumn col = model.getSearchColumn(key);
			if(col==null || col.getSqlFragment()==null)continue;
			String val = (String)map.get(key);
			if(FastUtil.isNotBlank(val)){
				sql.append(" ").append(col.getSqlFragment());
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
		return sql.toString();
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

package com.dx.jwfm.framework.web.logic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.web.search.Pager;
import com.dx.jwfm.framework.web.search.Search;

public class FastBaseLogic {
	
	static Logger logger = Logger.getLogger(FastBaseLogic.class);

	/**
	 * 查询数据
	 * @param searchModel 
	 * @param tblModel 
	 * @param search 
	 * @param pager 
	 */
	public List<FastPo> searchData(SearchModel searchModel, FastTable tblModel, Search search, Pager pager) {
		String sql = search.getSearchSql();
		String sqlCnt = search.getSearchCntSql(sql);
		DbHelper db = new DbHelper();
		List<FastPo> list = null;
		try {
			String pageSql = db.getDatabaseDialect().getPagedSql(sql, pager.getBegRowNo(), pager.getEndRowNo());
			list = db.executeSqlQuery(pageSql,FastPo.getPo(tblModel.getName()),search);
			if(list.size()<pager.getRows()){
				pager.setRowAmount(list.size());
			}
			else{
				int amount = db.getFirstIntSqlQuery(sqlCnt,search);
				pager.setRowAmount(amount);
			}
		} catch (SQLException e) {
			logger.error(e);
		}
		return list;
	}

	public void addPo(FastPo po, List<FastPo> adds) throws SQLException {
		addPo(po, adds, null, null);
	}
	public void addPo(FastPo po, List<FastPo> adds, List<FastPo> updates) throws SQLException {
		addPo(po, adds, updates, null);
	}
	public void addPo(FastPo po, List<FastPo> adds, List<FastPo> updates, List<FastPo> dels) throws SQLException {
		DbHelper db = new DbHelper();
		db.setAutoCommit(false);
		if(po!=null){
			db.addPo(po);
		}
		if(adds!=null && adds.size()>0){
			db.addPo(adds);
		}
		if(updates!=null && updates.size()>0){
			db.updatePo(adds);
		}
		if(dels!=null && dels.size()>0){
			db.deletePo(dels);
		}
		db.commit();
	}

	public void updatePo(FastPo po, List<FastPo> items) throws SQLException {
		ArrayList<FastPo> adds = new ArrayList<FastPo>(),updates = new ArrayList<FastPo>();
		if(items!=null){
			DbHelper db = new DbHelper();
			try {
				db.setAutoCommit(false);
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
			for(FastPo p:items){
				if(p.getTblModel()==null)continue;
				Object[] pk = p.getPkParams();
				if(pk==null || pk.length==0 || pk[0]==null){
					adds.add(p);
				}
				else{
					String sql = p.getTblModel().searchCntByIdSql();
					try {
						int cnt = db.getFirstIntSqlQuery(sql,pk);
						if(cnt>0){
							updates.add(p);
						}
						else{
							adds.add(p);
						}
					} catch (SQLException e) {
						logger.error(e.getMessage(),e);
					}
					continue;
				}
			}
		}
		updatePo(po, adds, updates, null);
	}
	public void updatePo(FastPo po, List<FastPo> adds, List<FastPo> updates) throws SQLException {
		updatePo(po, adds, updates, null);
	}
	public void updatePo(FastPo po, List<FastPo> adds, List<FastPo> updates, List<FastPo> dels) throws SQLException {
		DbHelper db = new DbHelper();
		db.setAutoCommit(false);
		if(po!=null){
			db.updatePo(po);
		}
		if(adds!=null && adds.size()>0){
			db.addPo(adds);
		}
		if(updates!=null && updates.size()>0){
			db.updatePo(adds);
		}
		if(dels!=null && dels.size()>0){
			db.deletePo(dels);
		}
		db.commit();
	}

}

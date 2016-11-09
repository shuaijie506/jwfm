package com.dx.jwfm.framework.core.dao.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.dx.jwfm.framework.core.dao.dialect.impl.MySqlDialect;
import com.dx.jwfm.framework.core.dao.dialect.impl.OracleDialect;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastTable;

public interface DatabaseDialect {
	
	public static DatabaseDialect getDialect(Connection con){
		String clsName = con.toString().toLowerCase();
		if(clsName.indexOf("mysql")>=0){
			return new MySqlDialect();
		}
		else if(clsName.indexOf("oracle")>=0){
			return new OracleDialect();
		}
		else{
			return null;
		}
	}

	/**
	 * 根据表结构构造建表语句，如果相同表名已存在，则构建更新语句
	 * @param tbl
	 * @return
	 */
	public List<String> getTableCreateOrUpdateSql(FastTable tbl);

	/**
	 * 得到更改表结构的SQL语句列表
	 * @param tbl
	 * @return
	 */
	public List<String> getTableUpdateSql(FastTable tbl);
	/**
	 * 得到创建表的SQL语句列表
	 * @param tbl
	 * @return
	 */
	public List<String> getTableCreateSql(FastTable tbl);
	/**
	 * 获取SQL语句的分页语句，如1-20行，21到40行
	 * @param sql
	 * @param beginRow	开始行数，行标从1开始
	 * @param endRow	结束行数
	 * @return
	 */
	public String getPagedSql(String sql,int beginRow,int endRow);

	/**
	 * 根据数据库结构中的列说明返回数据库类型
	 * @param col
	 * @return
	 */
	public String getDbType(FastColumn col);
	
	/**
	 * 判断一个表是否存在
	 * @param tblName
	 * @return
	 * @throws SQLException 
	 */
	public boolean isTableExist(String tblName) throws SQLException;
	
}

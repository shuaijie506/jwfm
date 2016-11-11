package com.dx.jwfm.framework.core.dao.model;

import java.util.ArrayList;
import java.util.List;

import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;

/**
 * 数据库对象
 * @author 宋帅杰
 *
 */
public class FastDbObject implements DatabaseObject {
	
	/**
	 * 创建对象所用SQL
	 */
	protected String sql;

	/**
	 * 测试对象是否存在所用SQL
	 */
	protected String testSql;

	
	public List<String> getObjectCreateSql(DatabaseDialect dialect) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(sql);
		return list;
	}

	
	public List<String> getObjectUpdateSql(DatabaseDialect dialect) {
		return getObjectCreateSql(dialect);
	}


	public String getSql() {
		return sql;
	}


	public void setSql(String sql) {
		this.sql = sql;
	}


	public String getTestSql() {
		return testSql;
	}


	public void setTestSql(String testSql) {
		this.testSql = testSql;
	}

}

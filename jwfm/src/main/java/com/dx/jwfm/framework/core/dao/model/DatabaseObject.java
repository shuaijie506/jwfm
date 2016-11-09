package com.dx.jwfm.framework.core.dao.model;

import java.util.List;

import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;

public interface DatabaseObject {

	/**
	 * 创建对象的SQL语句
	 * @return
	 */
	List<String> getObjectCreateSql(DatabaseDialect dialect);
	
	/**
	 * 更新对象的SQL语句
	 * @return
	 */
	List<String> getObjectUpdateSql(DatabaseDialect dialect);
}

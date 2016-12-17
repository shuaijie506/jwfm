package com.dx.jwfm.framework.core.dao.model;

import java.util.ArrayList;
import java.util.List;

import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;

public class FastTableIndex implements DatabaseObject {

	/** 索引说明，一般为汉字 */
	protected String title;

	/** 索引所对应的表名 */
	protected String tableCode;

	/** 索引编码，一般为大写英文字母、数字和下划线组成 */
	protected String name;

	/** 索引列，多列时使用,分隔 */
	protected String columns;

	
	public List<String> getObjectCreateSql(DatabaseDialect dialect) {
		ArrayList<String> list = new ArrayList<String>();
		list.add("create index "+name+" on "+tableCode+" ("+columns+")");
		return list;
	}

	
	public List<String> getObjectUpdateSql(DatabaseDialect dialect) {
		ArrayList<String> list = new ArrayList<String>();
		list.add("drop index "+name+" on "+tableCode+" ("+columns+")");
		list.add("create index "+name+" on "+tableCode+" ("+columns+")");
		return list;
	}

}

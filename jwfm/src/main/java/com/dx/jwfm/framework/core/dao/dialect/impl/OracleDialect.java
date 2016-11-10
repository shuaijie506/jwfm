package com.dx.jwfm.framework.core.dao.dialect.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastColumnType;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;

public class OracleDialect implements DatabaseDialect {

	static Logger logger = Logger.getLogger(DbHelper.class.getName());

	protected static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	
	public boolean isTableExist(String tblName) throws SQLException {
		DbHelper db = new DbHelper();
		int cnt = db.getFirstIntSqlQuery("select count(*) from user_objects where object_name='"+tblName+"'");
		return cnt>0;
	}

	
	public List<String> getTableCreateOrUpdateSql(FastTable tbl) {
		DbHelper db = new DbHelper();
		int cnt = 0;
		try {
			cnt = db.getFirstIntSqlQuery("select count(*) from user_objects where object_name='"+tbl.getCode()+"'");
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		List<String> list = null;
		if(cnt==0){
			list = getTableCreateSql(tbl);
		}
		else{
			list = getTableUpdateSql(tbl);
		}
		return list;
	}
	
	public List<String> getTableUpdateSql(FastTable tbl) {
		DbHelper db = new DbHelper();
		List<String> list = new ArrayList<String>();
		String name = tbl.getCode();
		try {
			StringBuffer buff = new StringBuffer();
			String comment = db.getFirstStringSqlQuery("select comments from user_tab_comments where table_name='"+name+"'");
			if(!equals(tbl.getComment(),comment)){//注释不一样
				buff.append("comment on table ").append(tbl.getCode()).append(LINE_SEPARATOR);
				buff.append("  is '").append(tbl.getComment().replaceAll("'", "''")).append("'");
				list.add(buff.toString());
				buff.setLength(0);
			}
//			String pkName = db.getFirstStringSqlQuery("select constraint_name from user_CONSTRAINTS where table_name='"+name+"' and CONSTRAINT_TYPE= 'P'");
			String sql = "select t.COLUMN_NAME,t.DATA_TYPE||" +
					"(case when t.data_type like '%CHAR%' then '('||t.DATA_LENGTH||')' " +
					"when t.data_type='NUMBER' and t.DATA_PRECISION is not null and t.DATA_SCALE>0 then '('||t.DATA_PRECISION||','||t.DATA_SCALE||')' " +
					"when t.data_type='NUMBER' and t.DATA_PRECISION is not null and t.DATA_SCALE=0 then '('||t.DATA_PRECISION||')' end) DATA_TYPE," +
					"t.DATA_LENGTH,t2.COMMENTS,t.NULLABLE," +
					"(SELECT count(*) from user_CONS_COLUMNS A,user_CONSTRAINTS B WHERE A.CONSTRAINT_NAME=B.CONSTRAINT_NAME(+) " +
					"AND B.CONSTRAINT_TYPE= 'P' and t.TABLE_NAME=a.table_name and a.column_name=t.COLUMN_NAME) IS_PK " +
					"from user_tab_columns t,user_col_comments t2 where t.TABLE_NAME=t2.table_name(+) " +
					"and t.COLUMN_NAME=t2.column_name(+) and t.table_name='"+name+"' order by t.COLUMN_ID";
			List<FastPo> cols = db.executeSqlQuery(sql);
			HashSet<Object> oldCols = new HashSet<Object>();
			for(FastPo po:cols){
				String colname = po.getString("COLUMN_NAME");
				oldCols.add(colname);
				FastColumn col = tbl.getColumn(colname);
				if(col==null)continue;//被删除的列不处理，暂时保留
				String oldType = getDbType(col);
				//如果字段类型或is null发生改变，则生成alter语句
				if(!oldType.equals(po.get("DATA_TYPE")) || col.isCanNull()!="Y".equals(po.get("NULLABLE"))){
					buff.append("alter table ").append(name).append(" modify ").append(col.getCode()).append(" ").append(getDbType(col));
					if(col.isCanNull()=="Y".equals(po.get("NULLABLE"))){
						buff.append(col.isCanNull()?" not null":" null");
					}
					list.add(buff.toString());
					buff.setLength(0);
				}
				if(!equals(col.getComment(),po.getString("COMMENTS"))){
					buff.append("comment on column ").append(name).append(".").append(col.getCode()).append(LINE_SEPARATOR);
					buff.append("  is '").append(col.getComment().replaceAll("'", "''")).append("'");
					list.add(buff.toString());
					buff.setLength(0);//清空SQL语句
				}
			}
			for(FastColumn col:tbl.getColumns()){
				if(!oldCols.contains(col.getCode())){//如果在旧表中不存在列，则新添加列
					buff.append("alter table ").append(name).append(" add ").append(col.getCode()).append(" ").append(getDbType(col));
					if(!col.isCanNull()){//不能为空时，要加not null
						buff.append(" not null");
					}
					list.add(buff.toString());
					buff.setLength(0);
					buff.append("comment on column ").append(name).append(".").append(col.getCode()).append(LINE_SEPARATOR);
					buff.append("  is '").append(col.getComment().replaceAll("'", "''")).append("'");
					list.add(buff.toString());
					buff.setLength(0);//清空SQL语句
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 得到创建表的SQL语句列表
	 * @param tbl
	 * @return
	 */
	public List<String> getTableCreateSql(FastTable tbl) {
		List<String> list = new ArrayList<String>();
		StringBuffer buff = new StringBuffer();
		buff.append("create table "+tbl.getCode()+LINE_SEPARATOR);
		buff.append("("+LINE_SEPARATOR);
		int nameMax=0,typeMax=0,nullMax=0;
		for(FastColumn col:tbl.getColumns()){
			nameMax = Math.max(nameMax, col.getCode().length());
			typeMax = Math.max(typeMax, getDbType(col).length());
			nullMax = Math.max(nullMax, col.isCanNull()?0:"not null".length());
		}
		for(FastColumn col:tbl.getColumns()){
			buff.append(col.getCode()).append(getBlank(nameMax+1-col.getCode().length()))
			.append(getDbType(col)).append(getBlank(typeMax+1-getDbType(col).length()));
			if(nullMax>0){
				if(!col.isCanNull()){
					buff.append("not null ");
				}
				else{
					buff.append(getBlank(nullMax+1));
				}
			}
			buff.append(",").append(LINE_SEPARATOR);
		}
		buff.deleteCharAt(buff.length()-LINE_SEPARATOR.length()-1).append(")");
		list.add(buff.toString());
		if(tbl.getPkColumns().size()==1){
			FastColumn keyCol = tbl.getPkColumns().get(0);
			//数字格式的使用自增数
			if(keyCol.getType()==FastColumnType.Integer || keyCol.getType()==FastColumnType.Long){
				list.add("CREATE SEQUENCE SEQ_"+tbl.getCode()+" INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE CACHE 10");
				list.add("CREATE OR REPLACE TRIGGER tri_seq_"+tbl.getCode()+" BEFORE INSERT ON "+tbl.getCode()+"\n  FOR EACH ROW\n  BEGIN\n    "
						+ "SELECT SEQ_"+tbl.getCode()+".nextval INTO :new."+keyCol.getCode()+" FROM dual;\n  END;");
			}
		}
		buff.setLength(0);//清空SQL语句
		if(tbl.getComment()!=null){
			buff.append("comment on table ").append(tbl.getCode()).append(LINE_SEPARATOR);
			buff.append("  is '").append(tbl.getComment().replaceAll("'", "''")).append("'");
			list.add(buff.toString());
			buff.setLength(0);//清空SQL语句
		}
		StringBuffer key = new StringBuffer();
		for(FastColumn col:tbl.getColumns()){
			buff.append("comment on column ").append(tbl.getCode()).append(".").append(col.getCode()).append(LINE_SEPARATOR);
			buff.append("  is '").append(col.getComment().replaceAll("'", "''")).append("'");
			list.add(buff.toString());
			buff.setLength(0);//清空SQL语句
			if(col.isPrimaryKey()){
				key.append(",").append(col.getCode());
			}
		}
		if(key.length()>0){
			buff.append("alter table ").append(tbl.getCode()).append(LINE_SEPARATOR);
			buff.append("  add constraint ").append("PK_"+tbl.getCode()).append(" primary key (").append(key.substring(1)).append(")"+LINE_SEPARATOR);
			buff.append("  using index");
			list.add(buff.toString());
		}
		return list;
	}

	
	public String getPagedSql(String sql, int beginRow, int endRow) {
		if(beginRow<=1){
			return "select * from (" + sql + ") sql_$t where rownum<="+endRow;
		}
		else{
			return "select * from (select sql_$t.*,rownum sql_$r from (" + sql + ") sql_$t where rownum<="+endRow + 
	        		") where sql_$r>"+beginRow;
		}
	}

	
	public String getDbType(FastColumn col) {
		switch(col.getType()){
		case String:
			if(col.getTypeLen()<0 || col.getTypeLen()>4000){
				return "CLOB";
			}
			return "VARCHAR2("+col.getTypeLen()+")";
		case Integer:
		case Long:
		case Float:
		case Double:
			return "NUMBER";
		case Date:
			return "TIMESTAMP";
		}
		return "VARCHAR2(500)";
	}

	public static String getBlank(int len){
		StringBuffer buff = new StringBuffer();
		while(len-->0){
			buff.append(" ");
		}
		return buff.toString();
	}
	
	public static boolean equals(String s1,String s2){
		if(s1==null)s1="";
		if(s2==null)s2="";
		return s1.trim().equals(s2.trim());
	}

}

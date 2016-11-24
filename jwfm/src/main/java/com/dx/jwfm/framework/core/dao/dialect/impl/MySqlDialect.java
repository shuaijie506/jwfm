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
import com.dx.jwfm.framework.util.FastUtil;

public class MySqlDialect implements DatabaseDialect {

	static Logger logger = Logger.getLogger(DbHelper.class.getName());

	protected static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private String vcSid;
	
	private String filterSid(String fieldName){
		return vcSid==null || vcSid.trim().length()==0?"":" and "+fieldName+"='"+vcSid+"'";
	}
	
	
	public boolean isTableExist(String tblName) throws SQLException {
		DbHelper db = new DbHelper();
		int cnt = db.getFirstIntSqlQuery("select count(*) from information_schema.tables t where t.table_name='"+tblName+"'"+filterSid("t.table_schema"));
		return cnt>0;
	}

	
	public List<String> getTableCreateOrUpdateSql(FastTable tbl) {
		DbHelper db = new DbHelper();
		int cnt = 0;
		try {
			cnt = db.getFirstIntSqlQuery("select count(*) from information_schema.tables t where t.table_name='"+tbl.getCode()+"'"+filterSid("t.table_schema"));
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
			String comment = getSafeComment(db.getFirstStringSqlQuery("select t.table_comment from information_schema.tables t " +
					"where t.table_type='BASE TABLE' and t.table_name='"+name+"'"+filterSid("t.table_schema")));
			if(!equals(tbl.getComment(),comment)){//注释不一样
				buff.append("alter table ").append(tbl.getCode()).append(LINE_SEPARATOR);
				buff.append("  comment= '").append(tbl.getComment().replaceAll("'", "''")).append("'");
				list.add(buff.toString());
				buff.setLength(0);
			}
			String sql = "select t.COLUMN_NAME,t.COLUMN_TYPE DATA_TYPE," +
					"floor(t.character_maximum_length) DATA_LENGTH,t.column_comment COMMENTS,t.column_DEFAULT,t.is_NULLABLE NULLABLE," +
					"(case when t.column_key='PRI' then 'y' else 'n' end) IS_PK " +
					"from information_schema.columns t where t.table_name='"+name+"'"+filterSid("t.table_schema")+" order by t.ordinal_position";
			List<FastPo> cols = db.executeSqlQuery(sql);
			HashSet<Object> oldCols = new HashSet<Object>();
			for(FastPo po:cols){
				String colname = po.getString("COLUMN_NAME");
				oldCols.add(colname);
				FastColumn col = tbl.getColumn(colname);
				if(col==null)continue;//被删除的列不处理，暂时保留
				String oldType = getDbType(col);
				//如果字段类型或is null发生改变，则生成alter语句
				if(!oldType.equals(po.get("DATA_TYPE")) || col.isCanNull()!="Y".equals(po.get("NULLABLE")) || 
						!equals(col.getComment(),po.getString("COMMENTS"))){
					buff.append("alter table ").append(name).append(" modify ").append(col.getCode()).append(" ").append(getDbType(col));
					if(col.isCanNull()=="Y".equals(po.get("NULLABLE"))){
						buff.append(col.isCanNull()?" not null":" null");
					}
					buff.append(" comment '").append(col.getComment().replaceAll("'", "''")).append("'");
					list.add(buff.toString());
					buff.setLength(0);
				}
			}
			for(FastColumn col:tbl.getColumns()){
				if(!oldCols.contains(col.getCode())){//如果在旧表中不存在列，则新添加列
					buff.append("alter table ").append(name).append(" add ").append(col.getCode()).append(" ").append(getDbType(col));
					if(!col.isCanNull()){//不能为空时，要加not null
						buff.append(" not null");
					}
					buff.append(" comment '").append(col.getComment().replaceAll("'", "''")).append("'");
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
		StringBuffer key = new StringBuffer();
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
			if(col.isPrimaryKey()){
				if(tbl.pkColumns().size()==1){
					if(FastColumnType.Integer.equals(col.getType()) || FastColumnType.Long.equals(col.getType())){
						buff.append(" PRIMARY KEY AUTO_INCREMENT");
					}
				}
				else{
					key.append(",").append(col.getCode());
				}
			}
			buff.append(" comment '").append(FastUtil.nvl(col.getComment(),col.getName()).replaceAll("'", "''")).append("'");
			buff.append(",").append(LINE_SEPARATOR);
		}
		if(key.length()>1){
			buff.append("primary key (").append(key.substring(1)).append(")");
		}
		else{
			buff.deleteCharAt(buff.length()-LINE_SEPARATOR.length()-1);
		}
		buff.append(") Comment='").append(FastUtil.nvl(tbl.getComment(),tbl.getName()).replaceAll("'", "''")).append("'");
		list.add(buff.toString());
		buff.setLength(0);//清空SQL语句
		for(FastColumn col:tbl.getColumns()){
			if(col.isPrimaryKey()){
				key.append(",").append(col.getCode());
			}
		}
		return list;
	}

	
	public String getPagedSql(String sql, int beginRow, int endRow) {
//		if(sql.toLowerCase().matches("^select\\s+count\\(.+")){
//			return sql;
//		}
		return sql + " limit "+(beginRow)+","+(endRow-beginRow);
	}

	
	public String getDbType(FastColumn col) {
		switch(col.getType()){
		case FastColumnType.String:
			if(col.getTypeLen()<0 || col.getTypeLen()>4000){
				return "TEXT";
			}
			return "VARCHAR("+col.getTypeLen()+")";
		case FastColumnType.Integer:
		case FastColumnType.Long:
			return "INT";
		case FastColumnType.Float:
			return "FLOAT";
		case FastColumnType.Double:
			return "DOUBLE";
		case FastColumnType.Date:
			return "DATETIME";
		}
		return "VARCHAR(500)";
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

	protected String getSafeComment(String s){
		if(s==null){
			return "";
		}
		return s.replaceAll("\\s*;?\\s*InnoDB free: \\d+ kB\\s*;?\\s*", "");
	}

	@Override
	public String getDate2StringFun(String fieldName, String format) {
		return "date_format("+fieldName+",'"+tranFormat(format)+"')";
	}

	@Override
	public String getString2DateFun(String fieldName, String format) {
		return "str_to_date("+fieldName+",'"+tranFormat(format)+"')";
	}
	private String tranFormat(String format) {
		if(format==null || format.trim().length()==0){
			format = "yyyy-MM-dd";
		}
		return format.replaceAll("yyyy", "%Y").replaceAll("MM", "%m").replaceAll("dd", "%d").replaceAll("HH", "%H").replaceAll("mm", "%i").replaceAll("ss", "%s");
	}

}

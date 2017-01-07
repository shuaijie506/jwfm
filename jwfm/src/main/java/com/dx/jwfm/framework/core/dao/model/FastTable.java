package com.dx.jwfm.framework.core.dao.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.exception.UnsupportNameException;
import com.dx.jwfm.framework.web.logic.DataTranser;

public class FastTable implements DatabaseObject,Serializable {

	static Logger logger = Logger.getLogger(FastTable.class.getName());

	/**  */
	private static final long serialVersionUID = 1L;

	/** 名称，一般为汉字 */
	protected String title;

	/** 编码，一般为大写英文字母、数字和下划线组成 */
	protected String name;

	/** 备注说明，此列为空时备注取name中的值 */
	protected String comment;
	/** 删除标记字段名 */
	protected String delCol = SystemContext.getDbDelFlagField();

	ArrayList<FastColumn> columns = new ArrayList<FastColumn>();

	ArrayList<FastTableIndex> indexs = new ArrayList<FastTableIndex>();

	ArrayList<FastColumn> pkCols = new ArrayList<FastColumn>();

	HashMap<String,FastColumn> colMap = new HashMap<String,FastColumn>();
	
	/** 结编码类的数据进行转换，可以转换成相应的文本，或者虚构某个列名，显示组合信息 */
	protected DataTranser dataTanser;

	/**
	 * ID为数字时，此字段暂存ID的最大值
	 */
	private Long maxIdVal;
	
	public FastColumn getColumn(String name){
		if(name==null){
			return null;
		}
		name = name.toUpperCase();
		if(colMap.size()==0){
			setColumns(columns);
		}
		return colMap.get(name);
	}
	
	public void setColumns(ArrayList<FastColumn> columns){
		pkCols.clear();
		colMap.clear();
		for(FastColumn col:columns){
			if(col.isPrimaryKey()){
				pkCols.add(col);
			}
			colMap.put(col.getName(), col);
		}
		this.columns = columns;
	}
	
	public String keyColCode(){
		for(FastColumn col:columns){
			if(col.isPrimaryKey()){
				return col.getName();
			}
		}
		return null;
	}
	
	
	public List<String> getObjectCreateSql(DatabaseDialect dialect) {
		return dialect.getTableCreateSql(this);
	}

	
	public List<String> getObjectUpdateSql(DatabaseDialect dialect) {
		return dialect.getTableUpdateSql(this);
	}

	public String insertSql(){
		StringBuilder buff = new StringBuilder("insert into ").append(name).append("(");
		StringBuilder valbuff = new StringBuilder();
		for(FastColumn col:columns){
			buff.append(col.getName()).append(",");
			valbuff.append(",?");
		}
		buff.deleteCharAt(buff.length()-1).append(") values(").append(valbuff.substring(1)).append(")");
		return buff.toString();
	}

	public String updateSql(){
		StringBuilder buff = new StringBuilder("update ").append(name).append(" set ");
		for(FastColumn col:columns){
			if(!col.primaryKey){
				buff.append(col.getName()).append("=?,");
			}
		}
		buff.deleteCharAt(buff.length()-1).append(" where ");
		for(FastColumn col:pkCols){
			buff.append(col.getName()).append("=? and");
		}
		return buff.toString().substring(0,buff.length()-4);
	}

	public String deleteSql(){
		StringBuilder buff = new StringBuilder("delete from ").append(name).append(" where ");
		for(FastColumn col:pkCols){
			buff.append(col.getName()).append("=? and");
		}
		return buff.toString().substring(0,buff.length()-4);
	}

	public String searchByIdSql(){
		StringBuilder buff = new StringBuilder("select * from ").append(name).append(" where ");
		for(FastColumn col:pkCols){
			buff.append(col.getName()).append("=? and");
		}
		if(pkCols.isEmpty()){
			return buff.toString();
		}
		return buff.toString().substring(0,buff.length()-4);
	}

	public String searchCntByIdSql(){
		StringBuilder buff = new StringBuilder("select count(*) from ").append(name).append(" where ");
		for(FastColumn col:pkCols){
			buff.append(col.getName()).append("=? and");
		}
		return buff.toString().substring(0,buff.length()-4);
	}
	
	public long nextIdVal(){
		if(maxIdVal==null){
			DbHelper db = new DbHelper();
			try {
				maxIdVal = db.getFirstLongSqlQuery("select max("+keyColCode()+") from "+name);
			} catch (SQLException e) {
				logger.error(e);
				maxIdVal = 0L;
			}
		}
		return ++maxIdVal;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title==null?null:title.toUpperCase();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(!Pattern.matches("\\w+", name)){
			throw new UnsupportNameException("please use a-zA-Z_0-9单词字符");
		}
		this.name = name==null?null:name.toUpperCase();
	}

	public String getComment() {
		return comment==null?title:comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ArrayList<FastColumn> getColumns() {
		return columns;
	}

	public ArrayList<FastColumn> pkColumns() {
		return pkCols;
	}

	public ArrayList<FastTableIndex> getIndexs() {
		return indexs;
	}

	public void setIndexs(ArrayList<FastTableIndex> indexs) {
		this.indexs = indexs;
	}

	public String getDelCol() {
		return delCol;
	}

	public void setDelCol(String delCol) {
		this.delCol = delCol;
	}

	public DataTranser getDataTanser() {
		return dataTanser;
	}

	public void setDataTanser(DataTranser dataTanser) {
		this.dataTanser = dataTanser;
	}

}

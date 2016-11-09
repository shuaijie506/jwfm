package com.dx.jwfm.framework.core.dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.web.logic.DataTranser;

public class FastTable implements DatabaseObject,Serializable {

	/**  */
	private static final long serialVersionUID = 1L;

	/** 名称，一般为汉字 */
	protected String name;

	/** 编码，一般为大写英文字母、数字和下划线组成 */
	protected String code;

	/** 备注说明，此列为空时备注取name中的值 */
	protected String comment;
	/** 删除标记字段名 */
	protected String delCol = "N_DEL";

	ArrayList<FastColumn> columns = new ArrayList<FastColumn>();

	ArrayList<FastTableIndex> indexs = new ArrayList<FastTableIndex>();

	ArrayList<FastColumn> pkCols = new ArrayList<FastColumn>();

	HashMap<String,FastColumn> colMap = new HashMap<String,FastColumn>();
	
	/** 结编码类的数据进行转换，可以转换成相应的文本，或者虚构某个列名，显示组合信息 */
	protected DataTranser dataTanser;

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
		ArrayList<FastColumn> pkCols = new ArrayList<FastColumn>();
		HashMap<String,FastColumn> colMap = new HashMap<String,FastColumn>();
		for(FastColumn col:columns){
			if(col.isPrimaryKey()){
				pkCols.add(col);
			}
			colMap.put(col.getCode(), col);
		}
		this.columns = columns;
		this.colMap = colMap;
		this.pkCols = pkCols;
	}
	
	public String getKeyColCode(){
		for(FastColumn col:columns){
			if(col.isPrimaryKey()){
				return col.getCode();
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

	public String getInsertSql(){
		StringBuilder buff = new StringBuilder("insert into ").append(code).append("(");
		StringBuilder valbuff = new StringBuilder();
		for(FastColumn col:columns){
			buff.append(col.getCode()).append(",");
			valbuff.append(",?");
		}
		buff.deleteCharAt(buff.length()-1).append(") values(").append(valbuff.substring(1)).append(")");
		return buff.toString();
	}

	public String getUpdateSql(){
		StringBuilder buff = new StringBuilder("update ").append(code).append(" set ");
		for(FastColumn col:columns){
			if(!col.isPrimaryKey){
				buff.append(col.getCode()).append("=?,");
			}
		}
		buff.deleteCharAt(buff.length()-1).append(" where ");
		for(FastColumn col:pkCols){
			buff.append(col.getCode()).append("=? and");
		}
		return buff.toString().substring(0,buff.length()-4);
	}

	public String getDeleteSql(){
		StringBuilder buff = new StringBuilder("delete from ").append(code).append(" where ");
		for(FastColumn col:pkCols){
			buff.append(col.getCode()).append("=? and");
		}
		return buff.toString().substring(0,buff.length()-4);
	}

	public String getSearchByIdSql(){
		StringBuilder buff = new StringBuilder("select * from ").append(code).append(" where ");
		for(FastColumn col:pkCols){
			buff.append(col.getCode()).append("=? and");
		}
		if(pkCols.isEmpty()){
			return buff.toString();
		}
		return buff.toString().substring(0,buff.length()-4);
	}

	public String getSearchCntByIdSql(){
		StringBuilder buff = new StringBuilder("select count(*) from ").append(code).append(" where ");
		for(FastColumn col:pkCols){
			buff.append(col.getCode()).append("=? and");
		}
		return buff.toString().substring(0,buff.length()-4);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name==null?null:name.toUpperCase();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code==null?null:code.toUpperCase();
	}

	public String getComment() {
		return comment==null?name:comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ArrayList<FastColumn> getColumns() {
		return columns;
	}

	public ArrayList<FastColumn> getPkColumns() {
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

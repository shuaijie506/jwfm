package com.dx.jwfm.framework.core.dao.po;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastColumnType;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.model.MapObject;
import com.dx.jwfm.framework.util.FastUtil;

public class FastPo extends MapObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 为true时表示此对象为基础模板对象，不能对此对象进行任何数据写入操作，
	 * 以防止开发人员误操作，出现多线程安全共同读写一个对象的情况
	 */
	private boolean isBasePo;
	
	/**
	 * 用来存储基础模板对象，可以通过FastPo.getPo("TABLE_NAME")来获取PO对象
	 */
	private static HashMap<String,FastPo> basePoMap = new HashMap<String, FastPo>();
	
	private FastTable tblModel;

	public static FastPo getPo(String tableName){
		FastPo po = getBasePo(tableName);
		FastPo p = po==null?null:po.clone();
		if(p!=null)p.isBasePo = false;
		return p;
	}

	private static FastPo getBasePo(String tableName){
		if(tableName==null)return null;
		tableName = tableName.toUpperCase();
		return basePoMap.get(tableName);
	}

	public FastPo() {
		super();
		isBasePo = false;
	}
	
	public FastPo(FastTable tblModel) {
		super();
		if(tblModel==null || FastUtil.isBlank(tblModel.getName())){
			return;
		}
		if(!basePoMap.containsKey(tblModel.getName())){
			isBasePo = true;
			this.tblModel = tblModel;
			basePoMap.put(tblModel.getName().toUpperCase(), this);
		}
	}
	
	public String getVcId(){
		return getString("VC_ID");
	}

	@Override
	public Object get(Object key) {
		if(tblModel!=null && tblModel.getDataTanser()!=null){
			String val = tblModel.getDataTanser().transData((String)key,map);
			if(val!=null){
				return val;
			}
		}
		return super.get(key);
	}

	private static Pattern varPat = Pattern.compile("\\$\\{(.+?)\\}");
	public void initDefaults() {
		if(tblModel!=null){
			for(FastColumn col:tblModel.getColumns()){
				initDefaults(col);
			}
		}
	}
	public void initDefaults(String colName) {
		if(tblModel!=null){
			colName = colName.toUpperCase();
			FastColumn col = tblModel.getColumn(colName);
			initDefaults(col);
		}
	}

	private void initDefaults(FastColumn col) {
		if(FastUtil.isBlank(getString(col.getName())) && FastUtil.isNotBlank(col.getDefaults())){
			String defaults = col.getDefaults();
			if(defaults.indexOf("$")>=0){
				Matcher mat = varPat.matcher(defaults);
				if(mat.matches()){//完整匹配时，可以是任意类型对象
					put(col.getName(),RequestContext.getBeanValue(mat.group(1)));
				}
				else{
					while(mat.find()){//组合匹配时，按字符串处理
						Object obj = RequestContext.getBeanValue(mat.group(1));
						defaults = defaults.replace(mat.group(), obj==null?"":obj.toString());
					}
					put(col.getName(),defaults);
				}
			}
			else{
				put(col.getName(),defaults);
			}
		}
	}

	public void setTableModelName(String tblName) {
		FastPo basepo = basePoMap.get(tblName);
		if(basepo==null){
			throw new RuntimeException("can't find table ["+tblName+"] in table cache!");
		}
		tblModel = basepo.tblModel;
	}

	public FastPo clone(){
		FastPo po = new FastPo();
		po.tblModel = this.tblModel;
		return po;
	}

	public FastPo element(String key,Object value){
		this.put(key, value);
		return this;
	}

	public FastPo loadDataFromResultSet(ResultSet rs,int row) throws SQLException{
		ResultSetMetaData md = rs.getMetaData();
		int cols = md.getColumnCount();
		FastPo po = new FastPo();
		for(int i=1;i<=cols;i++){
			String name = md.getColumnName(i);
			FastColumn col = getColumn(name);
			if(col!=null){//如果有数据结构列说明，则使用指定类型
				po.put(name, col.getRsObject(rs, i+1));
			}
			else{//否则由JDBC指定返回类型
				po.put(name, DbHelper.getObject(rs,md.getColumnType(i+1),i+1));
			}
		}
		return po;
	}
	
	public FastColumn getColumn(String name){
		return tblModel==null?null:tblModel.getColumn(name);
	}

	public List<FastPo> fromResultSet(ResultSet rs) throws SQLException{
		List<FastPo> list = new ArrayList<FastPo>();
		ResultSetMetaData md = rs.getMetaData();
		int cols = md.getColumnCount();
		String[] names = new String[cols];
		for(int i=1;i<=cols;i++){
			names[i-1] = md.getColumnName(i);
		}
		while(rs.next()){
			FastPo po = this.clone();
			for(int i=0;i<cols;i++){
				FastColumn col = getColumn(names[i]);
				if(col!=null){//如果有数据结构列说明，则使用指定类型
					po.put(names[i], col.getRsObject(rs, i+1));
				}
				else{//否则由JDBC指定返回类型
					po.put(names[i], DbHelper.getObject(rs,md.getColumnType(i+1),i+1));
				}
			}
			list.add(po);
		}
		return list;
	}
	
	public Object[] getPkParams(String pk){
		if(pk==null){
			return new Object[]{pk};
		}
		if(tblModel!=null && tblModel.pkColumns().size()>1){
			String[] ary = pk.split(",");
			if(ary.length==tblModel.pkColumns().size()){
				return ary;
			}
		}
		return new Object[]{pk};
	}

	public Object[] getPkParams() {
		ArrayList<Object> list = new ArrayList<Object>();
		if(tblModel!=null){
			for(FastColumn col:tblModel.pkColumns()){
				list.add(get(col.getName()));
			}
		}
		return list.toArray();
	}

	public Object[] getInsertParams(){
		ArrayList<Object> list = new ArrayList<Object>();
		if(tblModel!=null){
			for(FastColumn col:tblModel.getColumns()){
				Object val = get(col.getName());
				if(val instanceof String){
					val = tranJdbcValue(col.getType(),(String)val);
				}
				list.add(val);
			}
		}
		return list.toArray();
	}

	public Object[] getUpdateParams(){
		ArrayList<Object> list = new ArrayList<Object>();
		ArrayList<Object> pklist = new ArrayList<Object>();
		if(tblModel!=null){
			for(FastColumn col:tblModel.getColumns()){
				if(col.isPrimaryKey()){
					pklist.add(get(col.getName()));
				}
				else{
					Object val = get(col.getName());
					if(val instanceof String){
						val = tranJdbcValue(col.getType(),(String)val);
					}
					list.add(val);
				}
			}
		}
		list.addAll(pklist);
		return list.toArray();
	}

	public FastTable getTblModel() {
		return tblModel;
	}

	public String toString(){
		StringBuilder buff = new StringBuilder();
		for(String key:map.keySet()){
			buff.append(",").append(key).append(" : ").append(map.get(key)).append("\n");
		}
		buff.setCharAt(0, '{');
		buff.append("}");
		return buff.toString();
	}

	public boolean isBasePo() {
		return isBasePo;
	}

	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月7日 下午4:25:31
	 * 功能描述: 将字符串的值模式转换为JDBC支持的数据类型，如日期转换等
	 * 方法的参数和返回值: 
	 * @param type
	 * @param strValue
	 * @return
	 */
	private Object tranJdbcValue(String type,String strValue) {
		if(FastColumnType.Date.equals(type)){
			return FastUtil.parseDate(strValue);
		}
		return strValue;
	}

	public void initIdDelValue() {
		if(FastUtil.isBlank(getString(SystemContext.getDbIdField()))){
			FastTable tm = tblModel;
			if(tm.pkColumns().size()==1){
				FastColumn keyCol = tm.pkColumns().get(0);
				if(FastColumnType.String.equals(keyCol.getType())){//字符串格式的ID，使用UUID
					put(keyCol.getName(), FastUtil.getUuid());
				}
			}
		}
		Object delFlag = get(SystemContext.getDbDelFlagField());
		if(delFlag ==null || "".equals(delFlag)){
			put(SystemContext.getDbDelFlagField(), 0);
		}
	}

}

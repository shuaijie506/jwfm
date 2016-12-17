package com.dx.jwfm.framework.core.dao.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.dx.jwfm.framework.core.dao.dialect.impl.MySqlDialect;
import com.dx.jwfm.framework.core.dao.dialect.impl.OracleDialect;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;

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
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月24日 下午4:26:00
	 * 功能描述: 根据字段名和日期格式将字段转换为字符串，
	 * 如oracle数据库中传入参数 t.dt_date, yyyy-MM-dd HH:mm:ss，返回字符串 to_char(t.dt_date,'yyyy-mm-dd hh24:mi:ss')
	 * 方法的参数和返回值: 
	 * @param fieldName
	 * @param format
	 * @return
	 */
	public String getDate2StringFun(String fieldName,String format);
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月24日 下午4:26:00
	 * 功能描述: 根据字段名和日期格式将字段转换为日期型，
	 * 如oracle数据库中传入参数 t.vc_date, yyyy-MM-dd HH:mm:ss，返回字符串 to_date(t.vc_date,'yyyy-mm-dd hh24:mi:ss')
	 * 方法的参数和返回值: 
	 * @param fieldName
	 * @param format
	 * @return
	 */
	public String getString2DateFun(String fieldName,String format);
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月28日 上午11:13:07
	 * 功能描述: 将多个字段或常量连接为一列
	 * 方法的参数和返回值: 
	 * @param strary
	 * @return
	 */
	public String concatString(String[] strary);
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年12月15日 上午11:08:31
	 * 功能描述: 列出所有表和视图，此列表中的对象有以下属性 VC_CODE,VC_NAME,VC_TYPE，分别对应表名，表注释，类型（TABLE、VIEW）
	 * 方法的参数和返回值: 
	 * @return
	 */
	public List<FastPo> listTables();
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年12月15日 上午11:09:37
	 * 功能描述: 根据表名获取整个表的全部信息，包含所有列信息
	 * 方法的参数和返回值: 
	 * @param tableCode
	 * @return
	 */
	public FastTable loadTableInfo(String tableCode);
	
}

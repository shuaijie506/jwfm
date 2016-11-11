package com.dx.jwfm.framework.core.dao;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.parser.IDefaultValueParser;
import com.dx.jwfm.framework.util.FastUtil;

public class DbHelper {

	static Logger logger = Logger.getLogger(DbHelper.class.getName());

	/**
	 * 数据源名称，使用Spring时为BeanId，否则为JNDI名称
	 */
	private String dataSourceName;

	/**
	 * 当前保持的数据库连接
	 */
	private Connection con = null;
	
//	private DatabaseDialect dialect;
	
	/**
	 * 是否自动提交事务，默认为true
	 */
	private boolean autoCommit = true;
	
	private static Object[] emptyParam=new Object[0];

	public DbHelper() {
	}

	public DbHelper(String datasourceName) {
		this.dataSourceName = datasourceName;
	}

	public int execute(JdbcConnExecuter executer) throws SQLException{
		int res = -1;
		try {
			con = getConnection();
			con.setAutoCommit(false);
			res = executer.execute(con);
			if(autoCommit){
				con.commit();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw e1;
			}
			throw e;
		}
		finally{
			if(autoCommit){
				close();
			}
		}
		return res;
	}

	public int execute(JdbcStatementExecuter executer) throws SQLException{
		int res = -1;
		Statement st = null;
		try {
			con = getConnection();
			con.setAutoCommit(false);
			st = con.createStatement();
			res = executer.execute(st);
			if(autoCommit){
				con.commit();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			if(autoCommit){
				try {
					con.rollback();
				} catch (SQLException e1) {
					logger.error(e1.getMessage(),e1);
					throw e1;
				}
			}
			throw e;
		}
		finally{
			if(st!=null){
				try {
					st.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
			}
			if(autoCommit){
				close();
			}
		}
		return res;
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月4日 上午10:34:26
	 * 功能描述: 事务提交之后，无论成功或失败，都会释放数据库连接
	 * 方法的参数和返回值: 
	 */
	public void commit(){
		if(con!=null){
			try {
				con.commit();
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				try {
					con.rollback();
				} catch (SQLException e1) {
					logger.error(e1.getMessage(),e1);
				}
			}
			finally{
				close();
			}
		}
	}

	public void close(){
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
			finally{
				con = null;
			}
		}
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:31:28
	* 功能描述: 执行SQL语句查询数据库内容
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号
	* @return			以整型数字格式返回数据集中的第一行第一列中的数据
	 * @throws SQLException 
	*/
	public int getFirstIntSqlQuery(String sql) throws SQLException{
		return getFirstIntSqlQuery(sql,emptyParam);
	}

	public int getFirstIntSqlQuery(String sql,Map<String,Object> params) throws SQLException{
		return getFirstIntSqlQuery(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),getParams(sql,params));
	}
	public int getFirstIntSqlQuery(String sql,Object[] params) throws SQLException{
		String res = getFirstStringSqlQuery(sql,params);
		int val = 0;
		try {
			if(res!=null){
				val = Integer.parseInt(res);
			}
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(),e);
		}
		return val;
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:31:28
	* 功能描述: 执行SQL语句查询数据库内容
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号
	* @return			以整型数字格式返回数据集中的第一行第一列中的数据
	 * @throws SQLException 
	*/
	public long getFirstLongSqlQuery(String sql) throws SQLException{
		return getFirstLongSqlQuery(sql,emptyParam);
	}

	public long getFirstLongSqlQuery(String sql,Map<String,Object> params) throws SQLException{
		return getFirstLongSqlQuery(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),getParams(sql,params));
	}
	public long getFirstLongSqlQuery(String sql,Object[] params) throws SQLException{
		String res = getFirstStringSqlQuery(sql,params);
		long val = 0;
		try {
			if(res!=null){
				val = Long.parseLong(res);
			}
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(),e);
		}
		return val;
	}
	
	
	/**
	* 开发人：王涛
	* 开发日期: 2014-11-27  下午04:31:28
	* 功能描述: 执行SQL语句查询数据库内容
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号
	* @return			以整型数字格式返回数据集中的第一行第一列中的数据
	 * @throws SQLException 
	*/
	public double getFirstDoubleSqlQuery(String sql) throws SQLException{
		return getFirstDoubleSqlQuery(sql,emptyParam);
	}

	public double getFirstDoubleSqlQuery(String sql,Map<String,Object> params) throws SQLException{
		return getFirstDoubleSqlQuery(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),getParams(sql,params));
	}
	public double getFirstDoubleSqlQuery(String sql,Object[] params) throws SQLException{
		String res = getFirstStringSqlQuery(sql,params);
		double val = 0;
		try {
			if(res!=null){
				val = Double.parseDouble(res);
			}
		}catch (NumberFormatException e) {
			logger.error(e.getMessage(),e);
		}
		return val;
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:30:51
	* 功能描述: 执行SQL语句查询数据库内容
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号
	* @return			以字符串格式返回数据集中的第一行第一列中的数据
	 * @throws SQLException 
	*/
	public String getFirstStringSqlQuery(String sql) throws SQLException{
		return getFirstStringSqlQuery(sql,emptyParam);
	}
	public String getFirstStringSqlQuery(String sql,Map<String,Object> params) throws SQLException{
		return getFirstStringSqlQuery(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),getParams(sql,params));
	}
	private static Object[] getParams(String sql,Map<String,?> params){
		Pattern pat = Pattern.compile("\\$\\{([^\\}]+)\\}");
		Matcher mat = pat.matcher(sql);
		List<Object> paraml = new ArrayList<Object>();
		int index = 0;
		while(mat.find()){
			String pa = mat.group(1);
			Object value = null;
			try {
				if(params.containsKey(pa)){
					value = params.get(pa);
				}
				else{//如果指定参数中未找到对应名称的解析器，则从模型和系统的解析器中进行解析
					List<IDefaultValueParser> list = RequestContext.getDefaultValueParser();;
					for(int i=0;list!=null && i<list.size();i++){
						if(list.get(i).hasDefaultValue(pa)){
							value = list.get(i).getDefaultValue(pa);
							break;
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			paraml.add(index++,value);
		}
		Object[] param = new Object[index];
		for(int i=0;i<index;i++){
			param[i] = paraml.get(i);
		}
		return param;
	}
	public String getFirstStringSqlQuery(String sql,Object[] params) throws SQLException{
		List<String> list = executeStringSqlQuery(getDatabaseDialect().getPagedSql(sql, 0, 1),params);
		if(list.isEmpty()){
			return null;
		}
		return list.get(0);
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-11-2  下午05:10:47
	* 功能描述: 查询只有一列字符串的数据集
	* 方法的参数和返回值: 
	* @param sql
	* @return
	 * @throws SQLException 
	*/
	public List<String> executeStringSqlQuery(String sql) throws SQLException{
		return executeStringSqlQuery(sql,emptyParam);
	}
	/**
	 * 开发人： 宋帅杰
	 * 开发时间： 2011-6-13 下午01:45:26
	 * 功能描述：查询只有一列字符串的数据集
	 * 方法的参数和返回值
	 * @param sql
	 * @param params
	 * @return
	 * List 
	 * ==================================
	 * 修改历史
	 * 修改人        修改时间      修改原因及内容
	 *
	 * ==================================
	 * @throws SQLException 
	 */
	public List<String> executeStringSqlQuery(final String sql,final Object[] params) throws SQLException{
		final List<String> list = new ArrayList<String>();
		execute(new JdbcConnExecuter() {
			public int execute(Connection con) throws SQLException {
				logger.info(sql);
				if(params!=null && params.length>0){
					logger.info(format(params));
				}
				PreparedStatement st = con.prepareStatement(sql);
				if(params!=null){
					for(int i=0;i<params.length;i++){
						st.setObject(i+1, transDbType(params[i]));
					}
				}
				st.execute();
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					list.add(rs.getString(1));
				}
				rs.close();
				st.close();
				return 0;
			}
		});
		return list;
	}
	protected Object format(Object[] params) {
		StringBuilder buff = new StringBuilder("[");
		for(Object o:params){
			buff.append(o).append(",");
		}
		if(buff.length()>1){
			buff.deleteCharAt(buff.length()-1);
		}
		buff.append("]");
		return buff.toString();
	}

	public List<String> executeStringSqlQuery(String sql,Map<String,Object> params) throws SQLException{
		return executeStringSqlQuery(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),getParams(sql,params));
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-11-6  下午02:53:11
	* 功能描述: 查询有一列或两列的数据，并将每行数据封闭成为一个HashMap，第一列做为key,第二列做为value
	* 方法的参数和返回值: 
	* @param sql
	* @return
	 * @throws SQLException 
	*/
	public HashMap<String, String> getMapSqlQuery(String sql) throws SQLException {
		return getMapSqlQuery(sql,emptyParam);
	}
	/**
	 * 开发人： 宋帅杰
	 * 开发时间： 2011-6-13 下午01:44:29
	 * 功能描述：查询有一列或两列的数据，并将每行数据封闭成为一个HashMap，第一列做为key,第二列做为value
	 * 方法的参数和返回值
	 * @param sql
	 * @param params
	 * @return
	 * HashMap 
	 * ==================================
	 * 修改历史
	 * 修改人        修改时间      修改原因及内容
	 *
	 * ==================================
	 * @throws SQLException 
	 */
	public HashMap<String, String> getMapSqlQuery(final String sql,final Object[] params) throws SQLException {
		final HashMap<String, String> map = new LinkedHashMap<String, String>();
		execute(new JdbcConnExecuter() {
			public int execute(Connection con) throws SQLException {
				logger.info(sql);
				if(params!=null && params.length>0){
					logger.info(format(params));
				}
				PreparedStatement st = con.prepareStatement(sql);
				if(params!=null){
					for(int i=0;i<params.length;i++){
						st.setObject(i+1, transDbType(params[i]));
					}
				}
				st.execute();
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					map.put(rs.getString(1), rs.getString(2));
				}
				rs.close();
				st.close();
				return 0;
			}
		});
		return map;
	}
	public HashMap<String, String> getMapSqlQuery(String sql,Map<String,Object> params) throws SQLException{
		return getMapSqlQuery(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),getParams(sql,params));
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:30:37
	* 功能描述: 执行SQL语句查询数据库内容
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号
	* @param mapper		数据库行的映射器，将数据库中的行转换为一个对象
	* @return			查询后得到的数据集，集合中的每个对象表示数据库中的一行
	 * @throws SQLException 
	*/
	public List<FastPo> executeSqlQuery(String sql) throws SQLException{
		return executeSqlQuery(sql,emptyParam);
	}

	public List<FastPo> executeSqlQuery(final String sql,final Object[] params) throws SQLException{
		return executeSqlQuery(sql, null, params);
	}
	public List<FastPo> executeSqlQuery(String sql,Map<String,Object> params) throws SQLException{
		return executeSqlQuery(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),getParams(sql,params));
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  13:38:32
	* 功能描述: 执行SQL语句查询数据库内容
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号
	* @param mapper		数据库行的映射器，将数据库中的行转换为一个对象
	* @param params		SQL语句的参数列表
	* @return			查询后得到的数据集，集合中的每个对象表示数据库中的一行
	 * @throws SQLException 
	*/
	public List<FastPo> executeSqlQuery(final String sql,final FastPo po,final Object[] params) throws SQLException{
		final List<FastPo> list = new ArrayList<FastPo>();
		execute(new JdbcConnExecuter() {
			public int execute(Connection con) throws SQLException {
				logger.info(sql);
				if(params!=null && params.length>0){
					logger.info(format(params));
				}
				PreparedStatement st = con.prepareStatement(sql);
				if(params!=null){
					for(int i=0;i<params.length;i++){
						st.setObject(i+1, transDbType(params[i]));
					}
				}
				st.execute();
				ResultSet rs = st.getResultSet();
				list.addAll((po==null?new FastPo():po).fromResultSet(rs));
				rs.close();
				st.close();
				return list.size();
			}
		});
		return list;
	}
	public List<FastPo> executeSqlQuery(String sql,FastPo po,Map<String,?> params) throws SQLException{
		return executeSqlQuery(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),po,getParams(sql,params));
	}


	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:29:23
	* 功能描述: 执行SQL语句更新数据库内容
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号
	* @return			SQL语句执行成功后影响到的行数，如果返回-1表示SQL语句执行失败
	 * @throws SQLException 
	*/
	public int executeSqlUpdate(String sql) throws SQLException{
		return executeSqlUpdate(sql,emptyParam);
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:27:44
	* 功能描述: 执行SQL语句更新数据库内容
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号，参数以?代替
	* @param params		SQL语句中的参数，可以为null
	* @return			SQL语句执行成功后影响到的行数，如果返回-1表示SQL语句执行失败
	 * @throws SQLException 
	*/
	public int executeSqlUpdate(final String sql,final Object[] params) throws SQLException{
		return execute(new JdbcConnExecuter() {
			public int execute(Connection con) throws SQLException {
				logger.info(sql);
				if(params!=null && params.length>0){
					logger.info(format(params));
				}
				int result = -1;
				if(params==null||params.length==0){//如果SQL语句对应的参数矩阵为空，直接执行SQL语句
					Statement st = con.createStatement();
					result = st.executeUpdate(sql);
					st.close();
				}
				else{//否则对参数列表进行遍历，顺序执行参数矩阵中的行
					PreparedStatement pst = con.prepareStatement(sql);
					for(int i=0;i<params.length;i++){
						pst.setObject((i+1), transDbType(params[i]));
					}
					result = pst.executeUpdate();
					pst.close();
				}
				return result;
			}
		});
	}

	public int executeSqlUpdate(String sql,Map<String,Object> params) throws SQLException{
		return executeSqlUpdate(sql.replaceAll("'?\\$\\{([^\\}]+)\\}'?", "?"),getParams(sql,params));
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27 下午01:11:56
	* 功能描述: 对同一条SQL语句添加多行数据，用于insert语句，可一次添加多条数据
	* 方法的参数和返回值: 
	* @param sql		可执行SQL语句，语句末尾不能带有分号，参数以?代替
	* @param params		SQL语句中的参数，可以为null
	* @return			SQL语句执行成功后影响到的行数，如果返回-1表示SQL语句执行失败
	 * @throws SQLException 
	*/
	public int executeSqlUpdateMultiRow(String sql,Collection<Object[]> paramsList) throws SQLException{
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		List<Collection<Object[]>> paramsLists = new ArrayList<Collection<Object[]>>();
		paramsLists.add(paramsList);
		return executeSqlUpdatePrivate(sqls,paramsLists);
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27 下午04:24:57
	* 功能描述: 执行SQL语句更新数据库内容
	* 方法的参数和返回值: 
	* @param sqlList	可执行SQL语句的集合，语句末尾不能带有分号
	* @return			SQL语句执行成功后影响到的行数，如果返回-1表示SQL语句执行失败
	 * @throws SQLException 
	*/
	public int executeSqlUpdate(Collection<String> sqlList) throws SQLException{
		List<Collection<Object[]>> paramsLists = new ArrayList<Collection<Object[]>>();
		for(int i=0;i<sqlList.size();i++){
			paramsLists.add(new ArrayList<Object[]>());
		}
		return executeSqlUpdatePrivate(sqlList, paramsLists);
	}

	public int executeSqlUpdate(Collection<String> sqlList,Collection<Object[]> paramsList) throws SQLException{
		List<Collection<Object[]>> paramsLists = new ArrayList<Collection<Object[]>>();
		Iterator<Object[]> it = paramsList.iterator();
		while(it.hasNext()){
			List<Object[]> params = new ArrayList<Object[]>();
			params.add(it.next());
			paramsLists.add(params);
		}
		return executeSqlUpdatePrivate(sqlList, paramsLists);
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:24:57
	* 功能描述: 执行SQL语句更新数据库内容
	* 方法的参数和返回值: 
	* @param sqlList	可执行SQL语句的集合，语句末尾不能带有分号
	* @param paramsListsSQL语句中的参数矩阵集合，可以为null
	* @return			SQL语句执行成功后影响到的行数，如果返回-1表示SQL语句执行失败
	 * @throws SQLException 
	*/
	private int executeSqlUpdatePrivate(final Collection<String> sqlList,final Collection<Collection<Object[]>> paramsLists) throws SQLException{
		return execute(new JdbcConnExecuter() {
			public int execute(Connection con) throws SQLException {
				int result = 0;
				Iterator<String> it = sqlList.iterator();
				Iterator<Collection<Object[]>>it2 = paramsLists.iterator();
				while(it.hasNext()&&it2.hasNext()){
					String sql = it.next();
					logger.info(sql);
					Collection<Object[]> paramsList = it2.next();
					if(paramsList==null||paramsList.size()==0){//如果SQL语句对应的参数矩阵为空，直接执行SQL语句
						Statement st = con.createStatement();
						result += st.executeUpdate(sql);
						st.close();
					}
					else{//否则对参数列表进行遍历，顺序执行参数矩阵中的行
						Iterator<Object[]> it3 = paramsList.iterator();
						while(it3.hasNext()){
							Object[] params = it3.next();
							if(params!=null && params.length>0){
								logger.info(format(params));
							}
							PreparedStatement pst = con.prepareStatement(sql);
							for(int i=0;i<params.length;i++){
								pst.setObject((i+1), transDbType(params[i]));
							}
							result += pst.executeUpdate();
							pst.close();
						}
					}
				}
				return result;
			}
		});
	}
	
	public static Map<String,Object> obj2map(Object obj){
		Map<String,Object> map = new HashMap<String,Object>();
		if(obj!=null){
			for(PropertyDescriptor p:PropertyUtils.getPropertyDescriptors(obj)){
				try {
					map.put(p.getName(), PropertyUtils.getProperty(obj, p.getName()));
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return map;
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午03:21:00
	* 功能描述: 对参数类型进行转换，目前只转换日期型java.util.Date为java.sql.Timestamp
	* 方法的参数和返回值: 
	* @param o
	* @return
	*/
	private static Object transDbType(Object o){
		if(o instanceof java.util.Date){
			java.util.Date d = (java.util.Date)o;
			return new java.sql.Timestamp(d.getTime());
		}
		return o;
	}

	public static Connection _CONNECTION;

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:24:40
	* 功能描述: 获取系统数据库连接池中的连接
	* 方法的参数和返回值: 
	* @return
	 * @throws SQLException 
	*/
	public Connection getConnection() throws SQLException {
		if(_CONNECTION!=null){//方便不起工程直接使用本工具类执行SQL语句
			con = _CONNECTION;
		}
		if(con==null){
			con = SystemContext.getConnection(dataSourceName);
		}
		if(con==null){
			throw new SQLException("can't get connection!");
		}
		con.setAutoCommit(autoCommit);
		return con;
	}

	public static HashMap<String,DatabaseDialect> dialectMap = new HashMap<String,DatabaseDialect>();
	private DatabaseDialect dialect;
	public DatabaseDialect getDatabaseDialect() {
		if(dialect!=null){
			return dialect;
		}
		dialect = dialectMap.get(dataSourceName);
		if(dialect!=null){
			return dialect;
		}
		if(con!=null){
			dialect = DatabaseDialect.getDialect(con);
		}
		else{
			Connection con = null;
			try {
				con = getConnection();
				dialect = DatabaseDialect.getDialect(con);
			} catch (SQLException e1) {
				logger.error(e1.getMessage(),e1);
			}
			close();
		}
		if(dialect==null){
			String clsName = SystemContext.getSysParam("databaseDialect");
			if(clsName==null){
				System.err.println("不能正确识别数据库类型，请添加配置项databaseDialect，其值要实现接口com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect");
				System.exit(0);
			}
			try {
				dialect = (DatabaseDialect) FastUtil.newInstance(clsName);
			} catch (Exception e) {
				System.err.println("配置项databaseDialect对应的值["+clsName+"]必须实现接口com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect");
				logger.error(e.getMessage(),e);
				System.exit(0);
			}
		}
		dialectMap.put(dataSourceName,dialect);
		return dialect;
	}

	public static Connection getConnection(Class<?> driver,String dbUrl,String userName,String userPwd) throws Exception{
		Driver d = (Driver) driver.newInstance();
        Properties p = new Properties();
        p.setProperty("user", userName);
        p.setProperty("password", userPwd);
        Connection con = d.connect(dbUrl, p);
        return con;
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:57:38
	* 功能描述: 通过PO类型名和主键获取PO对象
	* 方法的参数和返回值: 
	* @param name		PO类名
	* @param pk			主键
	* @return
	 * @throws SQLException 
	*/
	public FastPo loadFastPo(final FastPo po, final String pk) throws SQLException {
		String sql = po.getTblModel().searchByIdSql();
		List<FastPo> list = executeSqlQuery(sql,po,po.getPkParams(pk));
		FastPo p = list.size()>0?list.get(0):null;
		return p;
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:57:02
	* 功能描述: INSERT数据
	* 方法的参数和返回值: 
	* @param obj
	* @return
	 * @throws SQLException 
	*/
	public boolean addPo(FastPo po) throws SQLException{
		if(po.getTblModel()==null){
			throw new SQLException("object has not tblModel!");
		}
		po.initIdDelValue();
		String sql = po.getTblModel().insertSql();
		int cnt = executeSqlUpdate(sql,po.getInsertParams());
		return cnt>0;
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:57:43
	* 功能描述: UPDATE数据
	* 方法的参数和返回值: 
	* @param obj
	* @return
	 * @throws SQLException 
	*/
	public boolean updatePo(FastPo po) throws SQLException{
		if(po.getTblModel()==null){
			throw new SQLException("object has not tblModel!");
		}
		String sql = po.getTblModel().updateSql();
		int cnt = executeSqlUpdate(sql,po.getUpdateParams());
		return cnt>0;
	}

	/**
	 * 如果记录存在，则更新之，否则插入新记录
	 * @param po
	 * @return
	 * @throws SQLException
	 */
	public boolean insertOrUpdatePo(FastPo po) throws SQLException{
		String sql = po.getTblModel().searchCntByIdSql();
		int cnt = getFirstIntSqlQuery(sql,po.getPkParams());
		if(cnt>0){//如果已经存在，则更新之
			return updatePo(po);
		}
		else{
			return addPo(po);
		}
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:57:43
	* 功能描述: DELETE数据
	* 方法的参数和返回值: 
	* @param obj
	* @return
	 * @throws SQLException 
	*/
	public boolean deletePo(FastPo po) throws SQLException{
		String sql = po.getTblModel().deleteSql();
		int cnt = executeSqlUpdate(sql,po.getPkParams());
		return cnt>0;
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:57:02
	* 功能描述: INSERT数据
	* 方法的参数和返回值: 
	* @param obj
	* @return
	 * @throws SQLException 
	*/
	public int addPo(List<FastPo> pos) throws SQLException{
		if(pos==null){
			return 0;
		}
		int cnt = 0;
		boolean autoCommit = isAutoCommit();
		if(autoCommit){//如果是自动提交的，暂时设置为不自动提交
			setAutoCommit(false);
		}
		for(FastPo po:pos){//批量插入
			cnt += addPo(po)?1:0;
		}
		if(autoCommit){//如果是自动提交的，提交事务，同时关闭连接
			commit();
			close();
		}
		return cnt;
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:57:43
	* 功能描述: UPDATE数据
	* 方法的参数和返回值: 
	* @param obj
	* @return
	 * @throws SQLException 
	*/
	public int updatePo(List<FastPo> pos) throws SQLException{
		if(pos==null){
			return 0;
		}
		int cnt = 0;
		boolean autoCommit = isAutoCommit();
		if(autoCommit){//如果是自动提交的，暂时设置为不自动提交
			setAutoCommit(false);
		}
		for(FastPo po:pos){//批量更新
			cnt += updatePo(po)?1:0;
		}
		if(autoCommit){//如果是自动提交的，提交事务，同时关闭连接
			commit();
			close();
		}
		return cnt;
	}

	/**
	 * 如果记录存在，则更新之，否则插入新记录
	 * @param po
	 * @return
	 * @throws SQLException
	 */
	public int insertOrUpdatePo(List<FastPo> pos) throws SQLException{
		if(pos==null){
			return 0;
		}
		int cnt = 0;
		boolean autoCommit = isAutoCommit();
		if(autoCommit){//如果是自动提交的，暂时设置为不自动提交
			setAutoCommit(false);
		}
		for(FastPo po:pos){//批量处理
			cnt += insertOrUpdatePo(po)?1:0;
		}
		if(autoCommit){//如果是自动提交的，提交事务，同时关闭连接
			commit();
			close();
		}
		return cnt;
	}

	/**
	* 开发人：宋帅杰
	* 开发日期: 2014-11-27  下午04:57:43
	* 功能描述: DELETE数据
	* 方法的参数和返回值: 
	* @param obj
	* @return
	 * @throws SQLException 
	*/
	public int deletePo(List<FastPo> pos) throws SQLException{
		if(pos==null){
			return 0;
		}
		int cnt = 0;
		boolean autoCommit = isAutoCommit();
		if(autoCommit){//如果是自动提交的，暂时设置为不自动提交
			setAutoCommit(false);
		}
		for(FastPo po:pos){//批量删除
			cnt += deletePo(po)?1:0;
		}
		if(autoCommit){//如果是自动提交的，提交事务，同时关闭连接
			commit();
			close();
		}
		return cnt;
	}
	
	public int doUpdate(List<FastPo> adds,List<FastPo> updates,List<FastPo> dels) throws SQLException{
		int cnt = 0;
		boolean autoCommit = isAutoCommit();
		if(autoCommit){//如果是自动提交的，暂时设置为不自动提交
			setAutoCommit(false);
		}
		if(adds!=null){
			for(FastPo po:adds){//批量插入
				cnt += addPo(po)?1:0;
			}
		}
		if(updates!=null){
			for(FastPo po:updates){//批量更新
				cnt += updatePo(po)?1:0;
			}
		}
		if(dels!=null){
			for(FastPo po:dels){//批量删除
				cnt += deletePo(po)?1:0;
			}
		}
		if(autoCommit){//如果是自动提交的，提交事务，同时关闭连接
			commit();
			close();
		}
		return cnt;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.autoCommit = autoCommit;
		getConnection().setAutoCommit(autoCommit);
	}

	public static Object getObject(ResultSet rs, int jdbcType, int col) throws SQLException{
		switch(jdbcType){
		case Types.NULL:
			return null;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return rs.getTimestamp(col);
		case Types.VARCHAR:
		case Types.CHAR:
		case Types.CLOB:
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.NCHAR:
		case Types.NCLOB:
		case Types.NVARCHAR:
		case Types.ROWID:
			return rs.getString(col);
		default:
			return rs.getObject(col);
		}
	}
}

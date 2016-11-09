package com.dx.jwfm.framework.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.builder.IDatagridBuilder;

public class SystemContext {

	static Logger logger = Logger.getLogger(SystemContext.class);
	
	/**
	 * 系统配置参数列表
	 */
	static Map<String,String> systemParam;

	/**
	 * Spring的上下文对象
	 */
	static ApplicationContext appContext;
	/**
	 * 系统在文件系统中的路径
	 */
	static String appPath;
	
	/**
	 * 工程发布时的URL路径前缀
	 */
	public static String path;
	
	public static String dbObjectPrefix = "FAST_";

	static boolean useSpring;//使用Spring

	/**
	 * Servlet配置参数列表
	 */
	static Map<String,String> filterParam;

	/**
	 * 如果系统使用Spring，则根据BEAN的ID，从SPRING中得到注册的BEAN对象。
	 * 否则按beanId变量中指定的内容做为类名进行加载
	 * @param beanId 在SPRING配置文件中注册的BEANID。
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object getBeanOrClassInstance(String beanId) {
		if(beanId==null){
			return null;
		}
		if(!useSpring && appContext!=null){
			return appContext.getBean(beanId);
		}
		else if(beanId.indexOf(".")>0){
			try {
				Class cls = RequestContext.class.getClassLoader().loadClass(beanId);
				return cls.newInstance();
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return null;
	}

	/**
	 * 获得当前工程在服务器上的根目录
	 * @return
	 */
	public static String getAppPath() {
		return appPath;
	}
	/**
	 * 获得当前工程的URL根路径
	 * @return
	 */
	public static String getPath() {
		return path;
	}
	private static HashMap<String,DataSource> dsmap = new HashMap<String,DataSource>();
	/**
	 * 根据指定名称获取数据连接池对象，如果使用Spring，可从Spring中获取Bean，否则从JNDI中获取连接池
	 * @return
	 */
	public static DataSource getDataSource(String dataSourceName) throws SQLException{
		if(dsmap.containsKey(dataSourceName)){
			return dsmap.get(dataSourceName);
		}
		DataSource ds = null;
		try {
			if(dataSourceName==null || dataSourceName.trim().length()==0){
				dataSourceName = filterParam.get("dataSource");
			}
			if(dataSourceName==null || dataSourceName.trim().length()==0){
				dataSourceName = FastUtil.nvl(SystemContext.systemParam.get("dataSource"),"fastDataSource");
			}
			if(FastFilter.useSpring){//使用Spring
				ds = (DataSource)SystemContext.getBeanOrClassInstance(dataSourceName);
			}
			if(ds==null){
				Context ctx = new InitialContext();
				ds = (DataSource)ctx.lookup(dataSourceName);
			}
		} catch (Exception e) {
			throw new SQLException(FastFilter.useSpring?"can't find datasource["+dataSourceName+"] in JNDI\n在JNDI数据源中未找到名称为["+dataSourceName+"]的连接池！":
				"can't find bean["+dataSourceName+"] in Spring Context!\n在Spring容器中找不到id为["+dataSourceName+"]的Bean，可能是未配置此Bean或程序未正常启动",e);
		}
		if(ds==null){
			throw new SQLException(FastFilter.useSpring?"can't find datasource["+dataSourceName+"] in JNDI\n在JNDI数据源中未找到名称为["+dataSourceName+"]的连接池！":
				"can't find bean["+dataSourceName+"] in Spring Context!\n在Spring容器中找不到id为["+dataSourceName+"]的Bean，可能是未配置此Bean或程序未正常启动");
		}
		dsmap.put(dataSourceName, ds);
		return ds;
	}
	public static Connection getConnection(String dataSourceName) throws SQLException {
		DataSource ds = getDataSource(dataSourceName);
		Connection conn = ds.getConnection();
		if(conn==null){
			throw new SQLException("create connection failed!");
		}
		return conn;
	}

	static Map<String,IDatagridBuilder> datagridBuilderMap = new Hashtable<String, IDatagridBuilder>();
	public static IDatagridBuilder getDatagridBuilder(String type) throws ClassNotFoundException{
		if(datagridBuilderMap.containsKey(type)){
			return datagridBuilderMap.get(type);
		}
		if(systemParam.containsKey("datagridBuilder."+type)){
			String clsName = systemParam.get("datagridBuilder."+type);
			Object obj = getBeanOrClassInstance(clsName);
			if(obj==null){
				throw new ClassNotFoundException(clsName);
			}
			if(!(obj instanceof IDatagridBuilder)){
				throw new ClassCastException("The class "+clsName+" should be implement IDatagridBuilder!");
			}
			datagridBuilderMap.put(type, (IDatagridBuilder) obj);
		}
		return datagridBuilderMap.get(type);
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月8日 上午10:06:44
	 * 功能描述: 获取系统配置参数
	 * 方法的参数和返回值: 
	 * @param name
	 * @return
	 */
	public static String getSysParam(String name){
		return systemParam.get(name);
	}
}

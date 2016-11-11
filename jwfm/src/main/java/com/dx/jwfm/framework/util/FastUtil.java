package com.dx.jwfm.framework.util;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.util.json.DateJsonValueProcessor;

import net.sf.json.JsonConfig;



public class FastUtil {

	static Logger logger = Logger.getLogger(FastUtil.class);
	/**
	 * 返回第一个非null变量中的值
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String nvl(String s1,String s2){
		return s1!=null?s1:s2;
	}
	public static String nvl(String s1,String s2,String s3){
		String val=nvl(s1,s2);
		return val!=null?val:s3;
	}
	/**
	 * 根据类名创建一个对象
	 * @param clsName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static Object newInstance(String clsName) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<Object> cls = (Class<Object>) Thread.currentThread().getContextClassLoader().loadClass(clsName);
		return cls.newInstance();
	}
	/**
	 * 判断字符串非空
	 * @param val
	 * @return
	 */
	public static boolean isNotBlank(String val) {
		return val!=null && val.trim().length()>0;
	}
	public static boolean isBlank(String val) {
		return val==null || val.trim().length()==0;
	}
	/**
	 * 获取框架中字典项的值
	 * @param groupName
	 * @param code
	 * @return
	 */
	public static String getDictVal(String groupName,String code){
		DbHelper db = new DbHelper();
		try {
			return db.getFirstStringSqlQuery("select vc_text from "+SystemContext.dbObjectPrefix+"T_DICT where n_del=0 and VC_GROUP=? and vc_code=?",
					new Object[]{groupName,code});
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}
	/**
	 * 获取框架中字典项的所有值列表
	 * @param groupName
	 * @return
	 */
	public static List<FastPo> getDicts(String groupName){
		DbHelper db = new DbHelper();
		try {
			return db.executeSqlQuery("select * from "+SystemContext.dbObjectPrefix+"T_DICT where n_del=0 and VC_GROUP=? order by n_seq",
					FastPo.getPo(""+SystemContext.dbObjectPrefix+"T_DICT"), new Object[]{groupName});
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}
	public static Map<String,String> getDictsMap(String groupName){
		DbHelper db = new DbHelper();
		try {
			return db.getMapSqlQuery("select vc_code,vc_text from "+SystemContext.dbObjectPrefix+"T_DICT where n_del=0 and VC_GROUP=? order by n_seq",
					new Object[]{groupName});
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}
	/**
	 * 获取框架中用户配置项的值
	 * @param name
	 * @return
	 */
	public static String getRegVal(String name){
		return getDictVal("SYS_REGEDIT",name);
	}
	/**
	 * 组合字符串数组
	 * @param ary
	 * @return
	 */
	public static String join(String[] ary){
		return join(ary,",");
	}
	/**
	 * 组合字符串数组
	 * @param ary
	 * @return
	 */
	public static String join(String[] ary, String split) {
		if(ary==null||ary.length==0){
			return "";
		}
		if(split==null){
			split = ",";
		}
		StringBuffer buff = new StringBuffer();
		for(String str:ary){
			buff.append(split).append(str);
		}
		return buff.substring(split.length());
	}
	
	private static Boolean debugModel;
	public static boolean isDebugModel(){
		if(debugModel==null){
			URL url = FastUtil.class.getClassLoader().getResource("fast.debug");
			debugModel = url!=null;
		}
		return debugModel;
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月3日 上午11:11:08
	 * 功能描述: 格式化日期
	 * 方法的参数和返回值: 
	 * @param d
	 * @param format
	 * @return
	 */
	public static String format(Date d,String format){
		if(d==null){
			return null;
		}
		return new SimpleDateFormat(format).format(d);
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月7日 下午4:30:01
	 * 功能描述: 将字符串转换为Date对象
	 * 以非数字字符对字符串进行分隔，如不足6个数字，则后边进行补0。
     * 可匹配以下任意格式：2011-3-28，2011-03-28，2011－03－28，2011年03月28日，
     * 	2011-3-28 14:29:8，2011-03-28 14:29:08，2011－03－28 14：29：08，2011年03月28日14时29分8秒，
     * 	2011-3-28 14:2，2011-03-28 14:02，2011－03－28 14：02，2011年03月28日14时2分，等
	 * 方法的参数和返回值: 
	 * @param date
	 * @return
	 */
	public static Date parseDate(String dateStr){
		if(isBlank((String)dateStr)){
			return null;
		}
		Date result = null;
		try {
			String[] ds = ((String)dateStr).split("\\D+");
			int[] dds = new int[]{0,1,1,0,0,0,0};
			for(int i=0;i<dds.length;i++){
				try {
					dds[i] = Integer.parseInt(ds[i]);
				} catch (Exception e) {
				}
			}
			Calendar c = Calendar.getInstance();
			c.set(dds[0],dds[1]-1,dds[2],dds[3],dds[4],dds[5]);
			result = c.getTime();
		} catch (Exception e) {
			System.out.println("dateconverter is failed!");
		}
    	return result;
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月3日 上午11:11:47
	 * 功能描述: 格式化数字
	 * 方法的参数和返回值: 
	 * @param num
	 * @param format
	 * @return
	 */
	public static String format(Number num,String format){
		if(num==null){
			return null;
		}
		return new DecimalFormat(format).format(num);
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月3日 上午11:12:26
	 * 功能描述: 格式化异常信息，以供在页面上显示
	 * 方法的参数和返回值: 
	 * @param e
	 * @return
	 */
	public static String getExceptionInfo(Throwable e){
		StringBuffer buff = new StringBuffer();
		while(e!=null){
			buff.append(e.getClass().getSimpleName()).append("[").append(e.getMessage()).append("]").append("\n");
			e = e.getCause();
		}
		buff.append("时间：").append(format(new Date(),"yyyy-MM-dd HH:mm:ss"));
		return buff.toString().trim();
	}
	
	public static JsonConfig getJsonConfigDefault(){
		JsonConfig conf = new JsonConfig();
		conf.registerJsonValueProcessor(java.util.Date.class,new DateJsonValueProcessor("yyyy-MM-dd HH:mm"));
		conf.registerJsonValueProcessor(java.sql.Date.class,new DateJsonValueProcessor("yyyy-MM-dd HH:mm"));
		return conf;
	}
	public static boolean isInteger(String p) {
		try {
			Integer.parseInt(p);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	public static boolean isLong(String p) {
		try {
			Long.parseLong(p);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	public static boolean isFloat(String p) {
		try {
			Float.parseFloat(p);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	public static boolean isDouble(String p) {
		try {
			Double.parseDouble(p);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

}

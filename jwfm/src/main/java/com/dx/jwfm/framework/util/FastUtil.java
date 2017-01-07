package com.dx.jwfm.framework.util;

import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.util.json.DateJsonValueProcessor;

import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;



public class FastUtil {

	static Logger logger = Logger.getLogger(FastUtil.class);
	/**
	 * 返回第一个非null变量中的值
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String nvl(String s1,String s2){
		return s1!=null&&s1.length()>0?s1:s2;
	}
	public static String nvl(String s1,String s2,String s3){
		String val=nvl(s1,s2);
		return val!=null&&val.length()>0?val:s3;
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
		conf.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);//防止自包含
		conf.registerJsonValueProcessor(java.util.Date.class,new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
		conf.registerJsonValueProcessor(java.sql.Date.class,new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
		conf.registerJsonValueProcessor(java.sql.Timestamp.class,new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
		conf.registerJsonValueProcessor(java.sql.Time.class,new DateJsonValueProcessor("HH:mm:ss"));
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
		if(cls==null)return null;
		return cls.newInstance();
	}
	public static void copyBeanPropts(Object dest,Object orig){
		PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(orig);
		for (int i = 0; i < origDescriptors.length; i++) {
			String name = origDescriptors[i].getName();
			if (!"class".equals(name) && PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {
				try {
					Object val = PropertyUtils.getProperty(orig, name);
					PropertyUtils.setProperty(dest, name, val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 获取框架中字典项的值
	 * @param groupName
	 * @param code
	 * @return
	 */
	public static String getDictVal(String groupName,String code){
		return getUserDictVal("sys",groupName, code);
	}
	/**
	 * 获取指定用户字典项的值
	 * @param groupName
	 * @param code
	 * @return
	 */
	public static String getUserDictVal(String userId,String groupName,String code){
		DbHelper db = new DbHelper();
		try {
			return db.getFirstStringSqlQuery("select vc_text from "+SystemContext.dbObjectPrefix+"T_DICT where n_del=0 and vc_user_id=? and VC_GROUP=? and vc_code=?",
					new Object[]{userId,groupName,code});
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}
	/**
	 * 获取框架中字典项的所有值列表
	 * 字段：VC_ID,VC_GROUP,VC_CODE,VC_TEXT,VC_NOTE,N_SEQ
	 * @param groupName
	 * @return
	 */
	public static List<FastPo> getDicts(String groupName){
		return getUserDicts("sys", groupName);
	}
	/**
	 * 获取指定用户字典项的所有值列表
	 * 字段：VC_ID,VC_GROUP,VC_CODE,VC_TEXT,VC_NOTE,N_SEQ
	 * @param groupName
	 * @return
	 */
	public static List<FastPo> getUserDicts(String userId,String groupName){
		DbHelper db = new DbHelper();
		try {
			return db.executeSqlQuery("select * from "+SystemContext.dbObjectPrefix+"T_DICT where n_del=0 and vc_user_id=? and VC_GROUP=? order by n_seq",
					FastPo.getPo(""+SystemContext.dbObjectPrefix+"T_DICT"), new Object[]{userId,groupName});
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}
	public static Map<String,String> getDictsMap(String groupName){
		return getUserDictsMap("sys", groupName);
	}
	public static Map<String,String> getUserDictsMap(String userId,String groupName){
		DbHelper db = new DbHelper();
		try {
			return db.getMapSqlQuery("select vc_code,vc_text from "+SystemContext.dbObjectPrefix+"T_DICT where n_del=0 and vc_user_id=? and VC_GROUP=? order by n_seq",
					new Object[]{userId,groupName});
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}
	/**
	 * 获取框架中配置项的值
	 * @param name
	 * @return
	 */
	public static String getRegVal(String name){
		return getUserRegVal("sys", name);
	}
	/**
	 * 获取指定用户配置项的值
	 * @param name
	 * @return
	 */
	public static String getUserRegVal(String userId,String name){
//		UtilPrepareClass
		return getRegVal(name,UtilPrepareClass.regDefaultMap.get(name));
	}
	public static String getRegVal(String name, String defaults){
		return getUserRegVal("sys", name, defaults);
	}
	public static String getUserRegVal(String userId,String name, String defaults){
		String val = getDictVal("SYS_REGEDIT",name);
		return isBlank(val)?defaults:val;
	}

	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月21日 上午10:50:51
	 * 功能描述: 返回MD5特殊码
	 * 方法的参数和返回值: 
	 * @param s
	 * @return
	 */
	public final static String toMd5String(String s) {
		return toMd5String(s.getBytes());
	}
	public final static String toMd5String(File f) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		BufferedInputStream in = null;
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			in = new BufferedInputStream(new FileInputStream(f));
			byte[] buff = new byte[8192];
			int len = 0;
			while((len=in.read(buff))>=0){
				mdTemp.update(buff, 0, len);
			}
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		finally{
			try {
				in.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	public final static String toMd5String(byte[] bytes) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(bytes);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月21日 上午10:51:14
	 * 功能描述: 得到随机UUID值
	 * 方法的参数和返回值: 
	 * @return
	 */
	public static String getUuid(){
		UUID uuid = UUID.randomUUID();
		String strUUID = uuid.toString();
		strUUID = strUUID.replaceAll("-","");
		return strUUID;
	}

	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年12月14日 上午8:59:34
	 * 功能描述: 获取指定对象的指定属性值，属性名支持.取多级属性
	 * 方法的参数和返回值: 
	 * @param bean
	 * @param name
	 * @return
	 */
	public static Object getProptValue(Object bean, String name) {
		int pos = name.indexOf(".");
		if(pos>0){
			Object proptbean = getProptValue(bean,name.substring(0,pos));
			Object val = getProptValue(proptbean,name.substring(pos+1));
			return val;
		}
		else{
			Object val = null;
			if(bean instanceof Map){
				Map<?,?> map = (Map<?,?>) bean;
				val = map.get(name);
			}
			try {
				val = PropertyUtils.getProperty(bean, name);
			} catch (Exception e) {
				logger.debug(e.getMessage(),e);
			}
			return val;
		}
	}
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年12月14日 上午9:00:51
	 * 功能描述: 按指定格式将对象格式化并返回格式化后的内容
	 * 方法的参数和返回值: 
	 * @param val
	 * @param format
	 * @return
	 */
	public static String format(Object val,String format) {
		if(val==null || "".equals(val)){
			return "";
		}
		else if(val instanceof Date){
			Date d = (Date) val;
			if(FastUtil.isBlank(format)){
				format = "yyyy-MM-dd HH:mm";
			}
			return new SimpleDateFormat(format).format(d);
		}
		else if(val instanceof java.sql.Date){
			java.sql.Date d = (java.sql.Date) val;
			if(FastUtil.isBlank(format)){
				format = "yyyy-MM-dd HH:mm";
			}
			return new SimpleDateFormat(format).format(d);
		}
		else if(val instanceof Number){
			Number n = (Number) val;
			if(FastUtil.isBlank(format)){
				return new DecimalFormat().format(n);
			}
			else{
				if("filesize".equals(format)){
					return formatFileSize(n.longValue());
				}
				else{
					return new DecimalFormat(format).format(n);
				}
			}
		}
		return val.toString();
	}
	/**
	 * 开发人： 宋帅杰
	 * 开发时间： 2011-6-23 上午11:30:18
	 * 功能描述：将文件大小格式化成易读的字符串
	 * 方法的参数和返回值
	 * @param fileSize
	 * @return
	 * String 
	 * ==================================
	 * 修改历史
	 * 修改人        修改时间      修改原因及内容
	 *
	 * ==================================
	 */
	public static String formatFileSize(long fileSize){
        DecimalFormat formater = new DecimalFormat();
        formater.applyPattern("###.##");
        if(fileSize < 1024){
            return fileSize + " B";
        }
        else if(fileSize < 1024*1024){
            return formater.format(fileSize/1024f) + " KB";
        }
        else if(fileSize < 1024*1024*1024){
            return formater.format(fileSize/(1024*1024f)) + " MB";
        }
        else{
            return formater.format(fileSize/(1024*1024*1024f)) + " GB";
        }
	}
	private static String sessionid;
	public static String getUrlContent(String testUrl) throws IOException {
		System.out.println("LOAD_URL:"+testUrl);
		URL url = new java.net.URL(testUrl);
		HttpURLConnection con = (HttpURLConnection)url.openConnection(); 
		con.setRequestProperty("User-Agent","Mozilla/4.0");
		con.setRequestProperty("Cookie", sessionid);
		con.setConnectTimeout(5*1000);
		con.setReadTimeout(3*1000);
		InputStream in = con.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line = null;
		StringBuffer buff = new StringBuffer();
		while((line = reader.readLine())!=null){
			buff.append(line+"\n");
		}
		reader.close();
		String sid = con.getHeaderField("Set-Cookie");
		if(sid!=null){
			sid = sid.split(";")[0];
		}
		if(sid!=null&&!sid.equals(sessionid)){
			sessionid = sid;
		}
		con.disconnect();
		return buff.toString().trim();
	}
}

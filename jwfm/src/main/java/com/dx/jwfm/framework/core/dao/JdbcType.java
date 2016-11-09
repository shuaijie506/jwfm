package com.dx.jwfm.framework.core.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.exception.TransObjectException;

public class JdbcType {

	static Logger logger = Logger.getLogger(JdbcType.class.getName());

	protected int type;
	
	protected int maxLength;

	public JdbcType(int type){
		this.type = type;
	}

	public JdbcType(int type,int maxLength){
		this.type = type;
		this.maxLength = maxLength;
	}
	
	public Object tansValue(Object obj) throws Exception{
		return tansValue(obj,type);
	}
	
	public static Object tansValue(Object obj,int type) throws TransObjectException{
		if(obj==null||"".equals(obj))
			return null;
		String msg = null;
		try {
			switch(type){
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				msg = "���ڸ�ʽΪyyyy-MM-dd HH:mm:ss";
				if(obj instanceof Date){
					return new java.sql.Timestamp(((Date)obj).getTime());
				}
				else{
					String val = obj.toString();
					return new java.sql.Timestamp(parse(val).getTime());
				}
			case Types.BIGINT:
				msg = "["+obj+"]����ת��������";
				return new BigInteger(obj.toString(),10);
			case Types.BOOLEAN:
				return new Boolean("true".equals(obj.toString().toLowerCase()));
			case Types.CHAR:
				msg = "["+obj+"]����ת�����ַ�";
				return new Character(obj.toString().charAt(0));
			case Types.DECIMAL:
				msg = "["+obj+"]����ת��������";
				return Float.parseFloat(obj.toString());
			case Types.DOUBLE:
				msg = "["+obj+"]����ת��������";
				return Double.parseDouble(obj.toString());
			case Types.FLOAT:
				msg = "["+obj+"]����ת��������";
				return Float.parseFloat(obj.toString());
			case Types.NUMERIC:
				msg = "["+obj+"]����ת��������";
				return new BigDecimal(obj.toString());
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.INTEGER:
				msg = "["+obj+"]����ת��������";
				return Integer.parseInt(obj.toString());
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				return obj.toString();
			case Types.CLOB:
				return null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			if(e.getMessage()!=null)
				throw new TransObjectException(msg+e.getMessage());
			else
				throw new TransObjectException(msg);
		}
		return obj;
	}

    public static Date parse(String dateStr) throws ParseException{
		if(dateStr==null || dateStr.trim().length()==0){
			return null;
		}
		Date result = null;
		try {
			String[] ds = ((String)dateStr).split("\\D");
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
			throw new ParseException("dateconverter is failed!",-1);
		}
    	return result;
    }
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
}
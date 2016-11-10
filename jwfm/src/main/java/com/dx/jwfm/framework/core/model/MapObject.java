package com.dx.jwfm.framework.core.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.dx.jwfm.framework.core.SystemContext;

public class MapObject implements Map<String,Object> {

	protected LinkedHashMap<String,Object> map = new LinkedHashMap<String, Object>();

	public String getString(String key){
		Object obj = get(key);
		if(obj instanceof String){
			return (String)obj;
		}
		return toString(obj);
	}

	public Integer getInteger(String key){
		Object obj = get(key);
		if(obj instanceof Integer){
			return (Integer)obj;
		}
		return Integer.parseInt(toString(obj));
	}

	public Long getLong(String key){
		Object obj = get(key);
		if(obj instanceof Long){
			return (Long)obj;
		}
		return Long.parseLong(toString(get(key)));
	}

	public Float getFloat(String key){
		Object obj = get(key);
		if(obj instanceof Float){
			return (Float)obj;
		}
		return Float.parseFloat(toString(get(key)));
	}

	public Double getDouble(String key){
		Object obj = get(key);
		if(obj instanceof Double){
			return (Double)obj;
		}
		return Double.parseDouble(toString(get(key)));
	}

	public Date getDate(String key){
		return (Date) get(key);
	}

	private String toString(Object obj){
		return obj==null?"":obj.toString();
	}

	
	public int size() {
		return map.size();
	}

	
	public boolean isEmpty() {
		return map.isEmpty();
	}

	
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	
	public Object get(Object key) {
		if(key==null)return null;
		String k = key.toString();
		if(k.indexOf(":")>0){//如果有冒号分隔，则后面部分自动识别为输出格式，目前支持数字和日期格式化输出
			int pos = k.indexOf(":");
			String propt = k.substring(0,pos).toUpperCase();
			String fmt = k.substring(pos+1);
			Object val = map.get(propt);
			if(val==null){//如果冒号前部分为键值取值为null，则输出原始键值对应的值
				return map.get(k.toUpperCase());
			}
			if(fmt!=null && fmt.length()>0 && fmt.charAt(0)==':'){
				return val;
			}
			if(val instanceof Date){
				Date d = (Date)val;
				return new SimpleDateFormat(fmt).format(d);
			}
			else if(val instanceof Number){
				return new DecimalFormat(fmt).format(val);
			}
			else{
				return val.toString();
			}
		}
		if(SystemContext.getDbDelFlagField().equals(k) && !map.containsKey(k)){
			map.put(k, "0");
		}
		return map.get(k.toUpperCase());
	}
	
	public MapObject element(String key,Object value){
		this.put(key, value);
		return this;
	}

	
	public Object remove(Object key) {
		return map.remove(key);
	}

	
	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll(m);
	}

	
	public void clear() {
		map.clear();
	}

	
	public Set<String> keySet() {
		return map.keySet();
	}

	
	public Collection<Object> values() {
		return map.values();
	}

	
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	
	public Object put(String key, Object value) {
		return map.put(key.toUpperCase(), value);
	}
	public Object setPropt(String key, Object value) {
		return map.put(key, value);
	}

}

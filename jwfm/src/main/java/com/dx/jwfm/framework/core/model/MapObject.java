package com.dx.jwfm.framework.core.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.util.FastUtil;

public class MapObject implements Map<String,Object> {


	public String getString(String key){
		Object obj = get(key);
		if(obj instanceof String){
			return (String)obj;
		}
		return toString(obj);
	}

	public Integer getInteger(String key){
		Object obj = get(key);
		if(obj==null){
			return null;
		}
		else if(obj instanceof Integer){
			return (Integer)obj;
		}
		return Integer.parseInt(toString(obj));
	}

	public Long getLong(String key){
		Object obj = get(key);
		if(obj==null){
			return null;
		}
		else if(obj instanceof Long){
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
		if(obj==null){
			return null;
		}
		else if(obj instanceof Double){
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

	public MapObject element(String key,Object value){
		this.put(key, value);
		return this;
	}
	
	public Object setPropt(String key, Object value) {
		return put(key, value);
	}

	protected LinkedHashMap<String,Object> map = new LinkedHashMap<String, Object>();
	protected LinkedHashMap<String,Object> mapUpper = new LinkedHashMap<String, Object>();
	
	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Object get(Object key) {
		if(key==null)return null;
		String k = key.toString();
		if(k.indexOf(":")>0){//如果有冒号分隔，则后面部分自动识别为输出格式，目前支持数字和日期格式化输出
			int pos = k.indexOf(":");
			String propt = k.substring(0,pos);
			String fmt = k.substring(pos+1);
			Object val = get(propt);
			if(val==null){//如果冒号前部分为键值取值为null，则输出null值
				return null;
			}
			if(fmt!=null && fmt.length()>0){
				if(fmt.charAt(0)==':'){
					return val;
				}
				else{
					return FastUtil.format(val, fmt);
				}
			}
		}
		if(SystemContext.getDbDelFlagField().equals(k) && !map.containsKey(k)){
			put(k, "0");
		}
		Object obj = map.get(k);
		if(obj==null && k!=null){
			obj = mapUpper.get(k.toUpperCase());
		}
		return obj;
	}
	
	public Object remove(Object key) {
		if(key!=null){
			mapUpper.remove(key.toString().toUpperCase());
		}
		return map.remove(key);
	}

	
	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll(m);
		for(String key:m.keySet()){
			if(key==null)continue;
			String ku = key.toUpperCase();
			if(!ku.equals(key)){
				mapUpper.put(ku, m.get(key));
			}
		}
	}

	public boolean containsKey(Object key) {
		if(key==null){
			return map.containsKey(key);
		}
		return map.containsKey(key) || mapUpper.containsKey(key.toString().toUpperCase());
	}

	
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public void clear() {
		map.clear();
		mapUpper.clear();
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
		if(key!=null){
			String ku = key.toUpperCase();
			if(!ku.equals(key)){
				mapUpper.put(ku, value);
			}
		}
		return map.put(key, value);
	}

}

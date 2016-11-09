package com.dx.jwfm.framework.core.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.dx.jwfm.framework.util.Uuid;

public class CommonDefaultValueParser implements IDefaultValueParser {
	
	protected HashMap<String,IDefaultValueGenerator> map = new HashMap<String,IDefaultValueGenerator>();
	
	public CommonDefaultValueParser(){
		map.put("uuid", new IDefaultValueGenerator() {
			public Object getValue(String name) {
				return Uuid.getUuid();
			}
		});
		map.put("nowDate", new IDefaultValueGenerator() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			public Object getValue(String name) {
				try {
					return sdf.parse(sdf.format(new Date()));
				} catch (ParseException e) {
				}
				return new Date();
			}
		});
		map.put("nowTime", new IDefaultValueGenerator() {
			public Object getValue(String name) {
				return new Date();
			}
		});
	}

	
	public boolean hasDefaultValue(String variable) {
		return map.containsKey(variable);
	}

	
	public Object getDefaultValue(String variable) {
		IDefaultValueGenerator gen = map.get(variable);
		return gen==null?null:gen.getValue(variable);
	}

}
interface IDefaultValueGenerator {
	
	public Object getValue(String name);

}
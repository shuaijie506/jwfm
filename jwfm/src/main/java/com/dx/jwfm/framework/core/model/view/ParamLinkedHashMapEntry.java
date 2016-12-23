package com.dx.jwfm.framework.core.model.view;

public class ParamLinkedHashMapEntry {
	
	private String key;
	private Object value;
	
	private ParamLinkedHashMap map;
	
	public ParamLinkedHashMapEntry() {
	}
	
	public ParamLinkedHashMapEntry(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	void setMap(ParamLinkedHashMap map){
		this.map = map;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		if(map!=null){
			map.put(key, value);
		}
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		if(map!=null && key!=null){
			map.put(key, value);
		}
	}

}

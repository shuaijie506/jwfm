package com.dx.jwfm.framework.core.model.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParamLinkedHashMap extends LinkedHashMap<String,Object> {

	private static final long serialVersionUID = 1L;

	public ParamLinkedHashMap(){
		super();
	}
	
	public ParamLinkedHashMap(int initialCapacity){
		super(initialCapacity);
	}
	
	public ParamLinkedHashMap(int initialCapacity, float loadFactor){
		super(initialCapacity, loadFactor);
	}
	
	public ParamLinkedHashMap(Map<? extends String,? extends Object> m){
		super(m);
		for(String key:m.keySet()){
			entrys.add(new ParamLinkedHashMapEntry(key,m.get(key)));
		}
	}
	
	private ArrayList<ParamLinkedHashMapEntry> entrys = new ArrayList<ParamLinkedHashMapEntry>();
	
	public int size(){
		return entrys.size();
	}
	
	public ParamLinkedHashMapEntry get(int idx){
		return entrys.get(idx);
	}
	
	public void set(int idx,ParamLinkedHashMapEntry entry){
		while(entrys.size()<=idx)entrys.add(null);
		entrys.set(idx, entry);
		entry.setMap(this);
	}
}

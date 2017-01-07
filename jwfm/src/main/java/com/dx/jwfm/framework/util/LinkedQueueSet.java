package com.dx.jwfm.framework.util;

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class LinkedQueueSet {
	private LinkedHashSet<String> set;
	private LinkedList<String> list;
	private int len;
	
	public LinkedQueueSet(int len){
		this.len = len;
		set = new LinkedHashSet<String>((int) (len/0.75));
		list = new LinkedList<String>();
	}
	
	public void add(String str){
		if(list.size()>=len){
			String val = list.removeFirst();
			set.remove(val);
		}
		set.add(str);
		list.add(str);
	}
	
	public boolean contains(String str){
		return set.contains(str);
	}
}

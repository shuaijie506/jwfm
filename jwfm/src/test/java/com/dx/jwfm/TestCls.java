package com.dx.jwfm;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCls {

	public static void main(String[] args) {
		System.out.println(new DecimalFormat().format(1.1111111));
		
//		System.out.println(str);
	}

	private static String replaceVars(String str, HashMap<String, String> map) {
		StringBuffer buff = new StringBuffer();
		int pos = str.indexOf("${");
		int lastpos = 0;
		if(pos>=0){
			while(pos>=0){
				buff.append(str.substring(lastpos, pos));
				int nextpos = str.indexOf("}",pos);
				String key = str.substring(pos+2, nextpos);
				String val = map.get(key);
				if(val!=null){
					buff.append(val);
				}
				lastpos = nextpos+1;
				pos = str.indexOf("${",lastpos);
			}
			if(lastpos<str.length()){
				buff.append(str.substring(lastpos));
			}
		}
		else{
			return str;
		}
		return buff.toString();
	}

}

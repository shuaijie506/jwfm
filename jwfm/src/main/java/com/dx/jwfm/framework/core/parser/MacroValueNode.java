package com.dx.jwfm.framework.core.parser;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.util.FastUtil;

public class MacroValueNode {

	static Logger logger = Logger.getLogger(FastFilter.class);
	
	private String code,name;
	
	private IMacroValueGenerator valueHandel;
	
	public static MacroValueNode genNode(String code,String name,String clsName){
		try {
			Object obj = FastUtil.newInstance(clsName);
			if(obj instanceof IMacroValueGenerator){
				MacroValueNode n = new MacroValueNode();
				n.code = code;
				n.name = name;
				n.valueHandel = (IMacroValueGenerator) obj;
				return n;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
	private MacroValueNode(){
		
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public IMacroValueGenerator getValueHandel() {
		return valueHandel;
	}

	
}

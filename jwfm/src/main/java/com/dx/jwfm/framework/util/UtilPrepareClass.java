package com.dx.jwfm.framework.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class UtilPrepareClass {

	static Logger logger = Logger.getLogger(UtilPrepareClass.class);
	
	public static HashMap<String,String> regDefaultMap = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static void loadFastXml(Element root){
		try {
			List<Element> list = root.selectNodes("regedit-default/item");
			for(Element e:list){
				regDefaultMap.put(e.attributeValue("name"), e.getText());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

}

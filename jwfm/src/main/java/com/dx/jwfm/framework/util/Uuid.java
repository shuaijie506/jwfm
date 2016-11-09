package com.dx.jwfm.framework.util;

import java.util.UUID;

public class Uuid {

	public static String getUuid(){
		UUID uuid = UUID.randomUUID();
		String strUUID = uuid.toString();
		strUUID = strUUID.replaceAll("-","");
		return strUUID;
	}
	
	
}

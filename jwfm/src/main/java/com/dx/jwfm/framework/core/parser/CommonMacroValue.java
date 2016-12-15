package com.dx.jwfm.framework.core.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dx.jwfm.framework.util.FastUtil;

public class CommonMacroValue implements IMacroValueGenerator {

	@Override
	public Object getValue(String name) {
		if("uuid".equals(name)){
			return FastUtil.getUuid();
		}
		else if("nowDate".equals(name)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return sdf.parse(sdf.format(new Date()));
			} catch (ParseException e) {
			}
			return new Date();
		}
		else if("nowTime".equals(name)){
			return new Date();
		}
		return null;
	}

}
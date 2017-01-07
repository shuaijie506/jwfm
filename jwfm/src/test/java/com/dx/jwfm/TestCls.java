package com.dx.jwfm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.util.EncryptUtil;

import net.sf.json.JSONObject;

public class TestCls {

	public static void main(String[] args) throws UnsupportedEncodingException {
		FastPo po = new FastPo();
		po.put("date", new Date());
		JSONObject obj = JSONObject.fromObject(po);
		System.out.println(obj);
	}

	private static String getSsoKey(String userId) {
		long l = System.currentTimeMillis();
		String time = String.valueOf(l^((long)(Math.random()*1000000000))).substring(0,8);
		String userId2 = userId==null||userId.length()==0?"":EncryptUtil.encryptDes(userId, time);
		return EncryptUtil.encryptDes(time+"|"+l+"|"+userId2,"hh123~!@");
	}
}

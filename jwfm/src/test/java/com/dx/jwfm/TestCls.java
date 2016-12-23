package com.dx.jwfm;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.dx.jwfm.framework.util.EncryptUtil;

public class TestCls {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String str = "{\"forwards\":{\"success\":\"/jwfm/core/easyuiSearch.jsp\",\"openAddPage\":\"/jwfm/core/easyuiEdit.jsp\",\"openModifyPage\":\"/jwfm/core/easyuiEdit.jsp\",\"openViewPage\":\"/jwfm/core/easyuiView.jsp\"},\"updateLogs\":[],\"buttonAuths\":[],\"flowModel\":{},\"vcModify\":\"\",\"dtAdd\":{\"date\":3,\"hours\":0,\"seconds\":0,\"month\":11,\"timezoneOffset\":-480,\"year\":115,\"minutes\":0,\"time\":1449072000625,\"day\":4},\"vcNote\":\"\",\"defaultSearchData\":false,\"dtModify\":null,\"search\":{\"headHTML\":\"\",\"searchColumns\":[],\"searchOrderBySql\":\"\",\"searchSelectSql\":\"\",\"searchResultColumns\":[]},\"dictData\":[],\"otherDbObjects\":[],\"vcAdd\":\"宋帅杰\",\"packageName\":\"com.dx.jwfm.framework.web\",\"vcAuth\":\"宋帅杰\",\"mainTableName\":\"\",\"useAjaxOperator\":false,\"vcId\":\"com.dx.jwfm.framework.web.action.ToolsAction\",\"otherTables\":[],\"vcVersion\":\"2015-12-03\",\"newMenu\":false,\"vcName\":\"工具Action\",\"initDataSqlList\":[],\"vcUrl\":\"/jwfm/tools\",\"mainTable\":null,\"pageHTMLAry\":[{\"data\":\"\",\"pid\":\"\",\"id\":\"edit\",\"text\":\"编辑页面\"},{\"data\":\"\",\"pid\":\"\",\"id\":\"view\",\"text\":\"查看页面\"}],\"actionName\":\"com.dx.jwfm.framework.web.action.ToolsAction\",\"vcGroup\":\"Fast开发平台\"}";
		String strEncrKey = "hhkjcsmis";
		String charset = "UTF-8";
		byte[] dest = EncryptUtil.encryptDes(str.getBytes(charset), strEncrKey);
		byte[] bytes = new byte[]{-21,59,-113,-85,55,-30,80,126};
		int idx=0;
//		System.out.println((int)bytes[0]&256);
//		System.out.println(byteToBit((byte) (bytes[idx+2]>>6&0b00000011)));
//		System.out.println((byte)((bytes[idx+1]<<2)|(bytes[idx+2]>>6)));
//		for(int i=0;i<dest.length;i++){
//			System.out.print(dest[i]+",");
//		}
//		System.out.println();
		byte[] src = EncryptUtil.decryptDes(dest, strEncrKey);
		System.out.println(str);
		System.out.println(str.length());
//		System.out.println(0b00111111);
		String strdest = EncryptUtil.encryptDes(str, strEncrKey);
		System.out.println(strdest);
		System.out.println(strdest.length());
		System.out.println(EncryptUtil.decryptDes(strdest, strEncrKey));
		strdest = EncryptUtil.encryptMix(str, strEncrKey);
		System.out.println(strdest);
		System.out.println(strdest.length());
		System.out.println(EncryptUtil.decryptMix(strdest, strEncrKey));
//		System.out.println(new String(src,charset));
//		for(int i=32;i<127;i++){
//			System.out.print(new String(new byte[]{(byte) i}));
//		}
		
	}
	public static String byteToBit(byte b) {  
        return ""  
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)  
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)  
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)  
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);  
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

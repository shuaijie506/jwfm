package com.dx.jwfm.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.Logger;

public class EncryptUtil {
	static Logger logger = Logger.getLogger(EncryptUtil.class);

	public static String encryptMix(String src, String strEncrKey){
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream out = new GZIPOutputStream(bos);
			out.write(src.getBytes("GBK"));
			out.close();
			byte[] bytes = bos.toByteArray();
			byte[] dest = encryptDes(bytes,strEncrKey);
			String str = desBytes2String(dest);
			return str;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	public static String decryptMix(String encryptString, String strEncrKey){
		try {
			byte[] bytes = string2DesBytes(encryptString);
			byte[] src = decryptDes(bytes, strEncrKey);
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(src));
			int len = 0;
			byte[] buff = new byte[8192];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while((len=in.read(buff))>=0){
				out.write(buff,0,len);
			}
			in.close();
			String str = new String(out.toByteArray(),"GBK");
			return str;
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	public static String encryptDes(String src, String strEncrKey){
		return encryptDes(src, strEncrKey, "GBK");
	}
	public static String encryptDes(String src, String strEncrKey, String charset){
		if(FastUtil.isBlank(src)){
			return null;
		}
		try {
			byte[] dest = encryptDes(src.getBytes("GBK"),strEncrKey);
			String str = desBytes2String(dest);
			return str;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	public static byte[] encryptDes(byte[] bytes, String strEncrKey){
		if(bytes==null || bytes.length==0){
			return null;
		}
		try{
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(strEncrKey.getBytes());
			//创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			//Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			//用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			//现在，获取数据并加密
			//正式执行加密操作
			return cipher.doFinal(bytes);
		}catch(Throwable e){
			logger.error(e.getMessage(),e);
		}
		return null;
    }
	public static String decryptDes(String encryptString, String strEncrKey) throws UnsupportedEncodingException{
		return decryptDes(encryptString, strEncrKey, "GBK");
	}
	public static String decryptDes(String encryptString, String strEncrKey, String charset) throws UnsupportedEncodingException{
		byte[] bytes = string2DesBytes(encryptString);
		byte[] src = decryptDes(bytes, strEncrKey);
		String str = new String(src,charset);
		return str;
	}
	public static byte[] decryptDes(byte[] bytes, String strEncrKey){
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom random = new SecureRandom();
			// 创建一个DESKeySpec对象
			DESKeySpec desKey = new DESKeySpec(strEncrKey.getBytes());
			// 创建一个密匙工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// 将DESKeySpec对象转换成SecretKey对象
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey, random);
			// 真正开始解密操作
			return cipher.doFinal(bytes);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	private static String desBytes2String(byte[] bytes) throws UnsupportedEncodingException {
		if(bytes==null || bytes.length==0){
			return null;
		}
		if(bytes.length%8!=0){
			throw new UnsupportedEncodingException("please use DES encrypt results bytes!");
		}
		int len = bytes.length/8;
		StringBuilder buff = new StringBuilder(len*11);
		for(int i=0;i<len;i++){
			int idx = i<<3;
			buff.append(tochar(0b00111111&(bytes[idx]>>2&0b00111111)));
			buff.append(tochar(0b00111111&((bytes[idx]<<4)|(bytes[idx+1]>>4&0b00001111))));
			buff.append(tochar(0b00111111&((bytes[idx+1]<<2)|(bytes[idx+2]>>6&0b00000011))));
			buff.append(tochar(0b00111111&(bytes[idx+2])));
			buff.append(tochar(0b00111111&(bytes[idx+3]>>2&0b00111111)));
			buff.append(tochar(0b00111111&((bytes[idx+3]<<4)|(bytes[idx+4]>>4&0b00001111))));
			buff.append(tochar(0b00111111&((bytes[idx+4]<<2)|(bytes[idx+5]>>6&0b00000011))));
			buff.append(tochar(0b00111111&(bytes[idx+5])));
			buff.append(tochar(0b00111111&(bytes[idx+6]>>2&0b00111111)));
			buff.append(tochar(0b00111111&((bytes[idx+6]<<4)|(bytes[idx+7]>>4&0b00001111))));
			buff.append(tochar(0b00111111&(bytes[idx+7]<<2)));
		}
		return buff.toString();
	}
	private static byte[] string2DesBytes(String encryptString) throws UnsupportedEncodingException {
		if(encryptString==null || encryptString.length()==0){
			return null;
		}
		int len = encryptString.length();
		if(len%11!=0){
			throw new UnsupportedEncodingException("please use DES encrypt results String!");
		}
		len = len/11;
		byte[] buff = new byte[len*8];
		byte[] charbuff = new byte[11];
		for(int i=0;i<len;i++){
			int idx = i*11;
			int desidx = i<<3;
			for(int j=0;j<11;j++){
				charbuff[j] = (byte) (encryptString.charAt(idx+j)-33);
			}
			buff[desidx] = (byte) (charbuff[0]<<2|charbuff[1]>>4);
			buff[desidx+1] = (byte) (charbuff[1]<<4|charbuff[2]>>2);
			buff[desidx+2] = (byte) (charbuff[2]<<6|charbuff[3]);
			buff[desidx+3] = (byte) (charbuff[4]<<2|charbuff[5]>>4);
			buff[desidx+4] = (byte) (charbuff[5]<<4|charbuff[6]>>2);
			buff[desidx+5] = (byte) (charbuff[6]<<6|charbuff[7]);
			buff[desidx+6] = (byte) (charbuff[8]<<2|charbuff[9]>>4);
			buff[desidx+7] = (byte) (charbuff[9]<<4|charbuff[10]>>2);
		}
		return buff;
	}
	public static String getSsoKey(String userId) {
		long l = System.currentTimeMillis();
		String time = String.valueOf(l^((long)(Math.random()*1000000000))).substring(0,8);
		String userId2 = userId==null||userId.length()==0?"defaultUser":EncryptUtil.encryptDes(userId, time);
		return EncryptUtil.encryptDes(time+"|"+l+"|"+userId2,"hh123~!@");
	}
	private static char tochar(int i){
		return (char)(i+33);
	}
}

package com.dx.jwfm.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;



public class NetUtil {
	
	static Logger logger = Logger.getLogger(NetUtil.class);
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2010-10-26  下午07:36:10
	 * 功能描述: 
	 * 方法的参数和返回值: 
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws IOException {
		String url = "http://localhost:8080/bzgl/shcjgl/oa/shOaWorkKhMonth.action";
		List<NameValueEntry> data = new ArrayList<NameValueEntry>();
		data.add(new NameValueEntry("user", "111"));
		data.add(new NameValueEntry("user", "222"));
		data.add(new NameValueEntry("user", "好人一生平安"));
//		System.out.println(postData(url, data));
		url = "http://localhost:8080/jwfm/mis/sys/HelloWorld_toJsp.action?SSO_ID=0b5b113b1b1c45b98e46223f5f6fe7fd";
		System.out.println(httpGet(url));
		System.out.println(httpGet(url));
	}
	
	/**
	* 开发人：宋帅杰
	* 开发日期: 2010-10-27  上午08:05:03
	* 功能描述: 获取URL的内容
	* 方法的参数和返回值: 
	* @param testUrl
	* @return
	* @throws MalformedURLException
	* @throws IOException
	*/
	public static String httpGet(String url) throws IOException {
		return httpRequest(url,false,null,null,null);
	}

	public static String httpPost(String url,List<NameValueEntry> data) throws IOException {
		return httpRequest(url,true,data,null,null);
	}

	public static String httpRequest(String url,boolean post,List<NameValueEntry> params,Map<String,String> header,Map<String,String> cookie) throws IOException {
		System.out.println("LOAD_URL:"+url);
		String requestCharset = "utf-8";
		StringBuffer buff = null;
		if(!post){
			buff = new StringBuffer();
			for(NameValueEntry nv:params){
				buff.append(nv.getName()).append("=").append(URLEncoder.encode(nv.getValue(), requestCharset)).append("&");
			}
			buff.deleteCharAt(buff.length()-1);
			url += (url.indexOf("?")>0?"&":"?")+buff.toString();
		}
		URL neturl = new java.net.URL(url);
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection)neturl.openConnection();
			Map<String,String> map = mergeHeader(header);
			map.put("Cookie", getCookie(url,cookie));
			con.setRequestMethod(post?"POST":"GET");
			for(String key:map.keySet()){
				con.setRequestProperty(key, map.get(key));
			}
			if(post){
				if(params!=null && params.size()>0){
					con.setDoOutput(true);
					buff = new StringBuffer();
					for(NameValueEntry nv:params){
						buff.append(nv.getName()).append("=").append(URLEncoder.encode(nv.getValue(), requestCharset)).append("&");
					}
					buff.deleteCharAt(buff.length()-1);
					con.getOutputStream().write(buff.toString().getBytes(requestCharset));
				}
			}
			InputStream in = con.getInputStream();
			List<String> list = con.getHeaderFields().get("Set-Cookie");
			mergeCookie(url,list);
			String ct = con.getHeaderField("Content-Type");
			String charset = "utf-8";
			int pos = 0;
			if(ct!=null && (pos=ct.indexOf("charset="))>=0){
				int pos2 = ct.indexOf(";",pos+8);
				charset = ct.substring(pos+8,pos2);
			}
			String ce = con.getHeaderField("Content-Encoding");
			if(ce!=null && "gzip".equals(ce.trim())){
				in = new GZIPInputStream(in);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(in,charset));
			try {
				String line = null;
				buff = new StringBuffer();
				while((line = reader.readLine())!=null){
					buff.append(line+"\n");
				}
			} catch (Exception e) {
				throw e;
			}
			finally{
				if(reader!=null){
					reader.close();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		finally{
			if(con!=null){
				con.disconnect();
			}
		}
		return buff.toString().trim();
	}
	
	public static InputStream downloadFile(String url,Map<String,String> cookie) throws IOException{
		URL neturl = new java.net.URL(url);
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection)neturl.openConnection();
			Map<String,String> map = mergeHeader(null);
			map.put("Cookie", getCookie(url,cookie));
			con.setRequestMethod("GET");
			for(String key:map.keySet()){
				con.setRequestProperty(key, map.get(key));
			}
			InputStream in = con.getInputStream();
			List<String> list = con.getHeaderFields().get("Set-Cookie");
			mergeCookie(url,list);
			String ct = con.getHeaderField("Content-Type");
			if(ct.indexOf("text/html")>=0){
				if(in!=null){
					in.close();
				}
				throw new MalformedURLException("The url is not a file!");
			}
			return in;
		} catch (IOException e) {
			throw e;
		}
	}

	private static Map<String, String> defaultHeader;
	private static Map<String, String> mergeHeader(Map<String, String> map) {
		if(map==null){
			map = new HashMap<String, String>();
		}
		if(defaultHeader==null){
			defaultHeader = new HashMap<String, String>();
			defaultHeader.put("User-Agent", "Mozilla/4.0");
			defaultHeader.put("Accept","text/html,application/xhtml+xml,application/xml,image/webp");
		}
		map.putAll(defaultHeader);
		return map;
	}

	private static void mergeCookie(String testUrl, List<String> cookieList) {
		if(cookieList==null || cookieList.size()==0){
			return;
		}
		int pos = testUrl.indexOf("/",9);
		String uri = pos>0?testUrl.substring(0,pos):testUrl;
		Hashtable<String,String> map = cookieMap.get(uri);
		if(map==null){
			map = new Hashtable<String,String>();
			cookieMap.put(uri,map);
		}
		for(String str:cookieList){
			if(str==null || str.length()==0){
				continue;
			}
			pos = str.indexOf(";");
			str = pos>0?str.substring(0,pos):str;
			pos = str.indexOf("=");
			map.put(str.substring(0, pos), str.substring(pos+1));
		}
	}

	private static Hashtable<String,Hashtable<String,String>> cookieMap = new Hashtable<String,Hashtable<String,String>>();
	private static String getCookie(String testUrl, Map<String, String> cookie) {
		int pos = testUrl.indexOf("/",9);
		String uri = pos>0?testUrl.substring(0,pos):testUrl;
		Hashtable<String,String> map = cookieMap.get(uri);
		StringBuffer buff = new StringBuffer();
		for(String key:map.keySet()){
			buff.append(key).append("=").append(encode(map.get(key))).append(";");
		}
		if(cookie!=null){
			for(String key:cookie.keySet()){
				buff.append(key).append("=").append(encode(cookie.get(key))).append(";");
			}
		}
		return buff.toString();
	}

	private static Object encode(String str) {
		try {
			return URLEncoder.encode(str,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			return str;
		}
	}
	
	

}

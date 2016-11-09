package com.dx.jwfm.framework.core.handle;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.process.IActionHandel;

public class ParameterActionHandle implements IActionHandel {

	static Logger logger = Logger.getLogger(RequestContext.class);
	@SuppressWarnings("unchecked")
	
	public boolean beforeExecute(Object action, String method) {
		HttpServletRequest request = RequestContext.getRequest();
		String ctype = request.getHeader("content-type");
		if(ctype!=null && ctype.indexOf("multipart/form-data")>=0){
			dealMultiPartForm(request,action);
		}
		else{
			Enumeration<String> en = request.getParameterNames();
			while(en.hasMoreElements()){
				String key = en.nextElement();
				setObjectPropt(action,key,request,request.getParameter(key));
			}
		}
		return false;
	}
	
	private Pattern listPat = Pattern.compile("(.*)\\[(\\d+)\\]$");
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setObjectPropt(Object obj,String fieldName,HttpServletRequest request,Object value){
		int pos = fieldName.indexOf(".");
		if(pos<0){//参数名中没有带. 做为普通参数
			if(obj instanceof Map){
				((Map) obj).put(fieldName, value);
			}
			else{
				try {//如果没有指定属性时，cls==null
					Class cls = PropertyUtils.getPropertyType(obj, fieldName);
					if(cls!=null && PropertyUtils.isWriteable(obj, fieldName)){
						Object val = cls.isArray()?(value==null?null:value.toString().split("\\s*,\\s*")):value;
						BeanUtils.setProperty(obj, fieldName, val);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else{
			String namePre = fieldName.substring(0,pos);
			Matcher m = listPat.matcher(namePre);
			if(m.matches()){//items[0]格式，做为List填充，如果类型不是List或其子类时，不处理此参数
				String proptName = m.group(1);
				int idx = Integer.parseInt(m.group(2));
				try {
					Class cls = PropertyUtils.getPropertyType(obj, proptName);
					if(cls!=null && cls.isAssignableFrom(List.class)){//只针对实现List类型的对象
						Object propt = PropertyUtils.getProperty(obj, proptName);
						if(propt==null){
							propt = new ArrayList();
							PropertyUtils.setProperty(obj,proptName,propt);
						}
						List list = (List) propt;
						Object row = null;
						if(list.size()<=idx || list.get(idx)==null){//如果列表中没有指定下标的对象，则创建之
							Field f = getField(obj.getClass(),proptName);
							Type fc = f.getGenericType();//如果是List类型，得到其Generic的类型
							Class rowCls = HashMap.class;//如果没有找到泛型参数，则使用FastPo
							if(fc!=null && fc instanceof ParameterizedType){//是泛型参数的类型
								ParameterizedType pt = (ParameterizedType) fc;
								rowCls = (Class)pt.getActualTypeArguments()[0];//得到泛型里的class类型对象。
							}
							while(list.size()<=idx){
								list.add(rowCls.newInstance());
							}
							if(list.get(idx)==null){
								list.add(idx,rowCls.newInstance());
							}
						}
						row = list.get(idx);
						//得到对象后，将参数值赋给对象的属性
						setObjectPropt(row,fieldName.substring(pos+1),request,value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{//简单的对象
				try {
					Object propt = PropertyUtils.getProperty(obj, namePre);
					if(propt==null){
						propt = PropertyUtils.getPropertyType(obj, namePre).newInstance();
						PropertyUtils.setProperty(obj,namePre,propt);
					}
					setObjectPropt(propt,fieldName.substring(pos+1),request,value);
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}
		}
	}

	static File tempFileDir;
	/**
	 * 上传附件时的表单参数处理
	 * @param request
	 * @param action
	 */
	private void dealMultiPartForm(HttpServletRequest request, Object action) {
		try {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// Set factory constraints
			factory.setSizeThreshold(2000000000);
			if(tempFileDir==null){
				String dir = request.getSession().getServletContext().getInitParameter("uploadFileDir");
				if(dir!=null && new File(dir).exists()){
					tempFileDir = new File(dir);
				}
				else{
					File[] fs = File.listRoots();
					for(File f:fs){
						try {
							tempFileDir = new File(f.getAbsolutePath(),"tempfile");
							tempFileDir.mkdirs();
							break;
						} catch (Exception e) {
							tempFileDir = null;
						}
					}
				}
			}
			factory.setRepository(tempFileDir);
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			// Set overall request size constraint
			upload.setSizeMax(2000000000);
			upload.setHeaderEncoding(request.getCharacterEncoding());
			// Parse the request
			List<FileItem> items = upload.parseRequest(request);
			HashMap<String,String> params = new HashMap<String, String>();
			for(FileItem item:items){
				if (item.isFormField()) {
					String val = item.getString(request.getCharacterEncoding());
					params.put(item.getFieldName(), val);
					setObjectPropt(action,item.getFieldName(),request,val);
			    } else if(item.getSize()>0){
			    	File tmpfile = File.createTempFile(item.getName(), "tmp",tempFileDir);
			    	String fieldName = item.getFieldName();
			    	item.write(tmpfile);
					setObjectPropt(action,fieldName,request,tmpfile);
					setObjectPropt(action,fieldName+"FileName",request,item.getName());
			    }
			}
			RequestContext.addRequestParamMap(params);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@SuppressWarnings("rawtypes")
	private Field getField(Class cls,String fieldName){
		if(fieldName==null){
			return null;
		}
		for(Field f:cls.getDeclaredFields()){
			if(fieldName.equals(f.getName())){
				return f;
			}
		}
		Class sc = cls.getSuperclass();
		if(sc!=null){
			return getField(sc,fieldName);
		}
		return null;
	}

	
	public void afterExecute(Object action, String method) {
		
	}

}

package com.dx.jwfm.framework.web.tag;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.util.FastUtil;

public class SelectTag extends HiddenTag {
	/**  */
	private static final long serialVersionUID = 1L;
	/** 支持4种类别：
	 * 1.#{1:选项1;2:选项2;3:选项3}，此时系统按分号和冒号将字符串解析为列表列表中如果用到英文分号和冒号时，以&#58;和&#59;代替
	 * 2.指定从page, request, session, application中获取对象，对象可以是实现Collection接口和Map接口
	 * 3.以dict:开头，则使用dict:后面指定的分组名从公共字典中加载项目
	 * 3.以sql:开头，则使用sql:后面指定的SQL语句从查询结果中加载项目
	 *  */
	protected String list;
	/** list属性中指定的对象为List时，从List中每个元素对象的哪个属性做为value和text，默认为id和text */
	protected String valueField="id",textField="text";
	/** 是否在最前页显示空白选项 */
	protected boolean emptyOption;

	@Override
	public int doEndTag() throws JspException {
	    JspWriter out = pageContext.getOut();
	    try{
	    	writeHtml(out);
		} catch (Exception e) {
			throw new JspException(e);
		}
	    name = null;list=null;value=null;valueField="id";textField="text";emptyOption=false;
		return SKIP_BODY;
	}
	
	public void writeHtml(Writer out){
    	try {
			out.write("<select name=\"");
			out.write(name);
			out.write("\"");
			for(String k:attr.keySet()){
				out.write(" ");
				out.write(k);
				if(attr.get(k)!=null){
			    	out.write("=\"");
			    	out.write(attr.get(k).replace("\"", "\\\"").replaceAll("\\r|\\n", ""));
			    	out.write("\"");
				}
			}
			if(FastUtil.isNotBlank(id)){
				out.write(" id=\"");
				out.write(id);
				out.write("\"");
			}
			if(FastUtil.isNotBlank(getValue())){//将value值写入value属性中
				out.write(" value=\"");
				out.write(value);
				out.write("\"");
			}
			out.write(" >\n");
			if(emptyOption){
				addOption(out,"","");
			}
			printOptions(out);
			out.write("</select>");
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void printOptions(Writer out) throws IOException{
		if(list.startsWith("#{") && list.endsWith("}")){//自定义选项列表
			String str = list.substring(2, list.length()-1);
			String[] ary = str.split(";");
			for(String row:ary){
				String[] tmp = row.split(":");
				addOption(out,tmp[0].replaceAll("&#58;", ":"),tmp[1].replaceAll("&#58;", ":"));
			}
		}
		else if(list.toLowerCase().startsWith("dict:")){
			Map<String, String> map = FastUtil.getDictsMap(list.substring(5));
			for(String key:map.keySet()){
				addOption(out,key,map.get(key));
			}
		}
		else if(list.toLowerCase().startsWith("sql:")){
			DbHelper db = new DbHelper();
			Map<String, String> map = null;
			try {
				map = db.getMapSqlQuery(list.substring(4));
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				map = new HashMap<String, String>();
			}
			for(String key:map.keySet()){
				addOption(out,key,map.get(key));
			}
		}
		else{
			Object bean = getBean(list);
			if(bean instanceof Collection){
				Collection list = (Collection) bean;
				for(Object row:list){
					String val = (String) getProptValue(row, valueField);
					String text = (String) getProptValue(row, textField);
					addOption(out,val,text);
				}
			}
			else if(bean instanceof Map){
				Map map = (Map) bean;
				for(Object key:map.keySet()){
					addOption(out,(String)key,(String)map.get(key));
				}
			}
		}
	}

	private void addOption(Writer out, String val, String text) throws IOException {
		out.write("<option value=\"");
		if(val!=null){
			out.write(val);
		}
		out.write("\"");
		if(value!=null && value.equals(val)){
			out.write(" selected=\"true\"");
		}
		out.write(">");
		if(text!=null){
			out.write(text);
		}
		out.write("</option>\n");
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public boolean isEmptyOption() {
		return emptyOption;
	}

	public void setEmptyOption(boolean emptyOption) {
		this.emptyOption = emptyOption;
	}
	
}

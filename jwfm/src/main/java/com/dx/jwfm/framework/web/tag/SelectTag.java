package com.dx.jwfm.framework.web.tag;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.dx.jwfm.framework.util.FastUtil;

public class SelectTag extends HiddenTag {
	/**  */
	private static final long serialVersionUID = 1L;
	/** 支持3种类别：
	 * 1.#{1:选项1;2:选项2;3:选项3}，此时系统按分号和冒号将字符串解析为列表列表中如果用到英文分号和冒号时，以&#58;和&#59;代替
	 * 2.指定从page, request, session, application中获取对象，对象可以是实现Collection接口和Map接口
	 * 3.以dict:开头，则使用dict:后面指定的分组名从公共字典中加载项目 */
	protected String list;
	/** list属性中指定的对象为List时，从List中每个元素对象的哪个属性做为value和text，默认为id和text */
	protected String valueField="id",textField="text";
	/** 是否在最前页显示空白选项 */
	protected boolean emptyOption;

	@Override
	public int doEndTag() throws JspException {
	    JspWriter out = pageContext.getOut();
	    try{
	    	out.print("<select name=\"");
	    	out.print(name);
	    	out.print("\"");
			for(String k:attr.keySet()){
		    	out.print(" ");
		    	out.print(k);
				if(attr.get(k)!=null){
			    	out.print("=\"");
			    	out.print(attr.get(k).replace("\"", "\\\"").replaceAll("\\r|\\n", ""));
			    	out.print("\"");
				}
			}
			out.println(" >");
			getValue();//将value值写入value属性中
			if(emptyOption){
				addOption(out,"","");
			}
			printOptions(out);
	    	out.print("</select>");
		} catch (IOException ex) {
			logger.error(ex);
		} catch (Exception e) {
			throw new JspException(e);
		}
	    name = null;list=null;value=null;valueField="id";textField="text";emptyOption=false;
		return SKIP_BODY;
	}
	
	@SuppressWarnings("rawtypes")
	protected void printOptions(JspWriter out) throws IOException{
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

	private void addOption(JspWriter out, String val, String text) throws IOException {
		out.print("<option value=\"");
		if(val!=null){
			out.print(val);
		}
		out.print("\"");
		if(value!=null && value.equals(val)){
			out.print(" selected");
		}
		out.print(">");
		if(text!=null){
			out.print(text);
		}
		out.println("</option>");
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

package com.dx.jwfm.framework.web.tag;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.dx.jwfm.framework.util.FastUtil;

public class HtmlTableTag extends BaseViewTag {
	private static final long serialVersionUID = 1L;
	private String value;
	private int offset;
	private int length;
	private String trattrs;
	private boolean noSortCol;//禁用自定义顺序
	public List<Column> cols = new ArrayList<Column>();
	public List<ColSpan> colspans = new ArrayList<ColSpan>();
	public HashMap<String,String> fieldMap = new HashMap<String, String>();
	
	private Iterator<Object> it;//数据循环器
	
	/** 初始化数据 */
	@SuppressWarnings("unchecked")
	protected void initData(){
	    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Object obj = pageContext.getAttribute(value);
		if(obj==null){
			obj = request.getAttribute(value);
		}
		if(obj==null){
			obj = request.getSession().getAttribute(value);
		}
		if(obj instanceof Collection){
			it = ((Collection<Object>)obj).iterator();
		}
		if(it!=null){
			for(int i=0;i<offset;i++){//定位至offset的指定位置
				it.next();
			}
		}
	}

	@Override
	public int doStartTag() throws JspException {
		cols.clear();
		colspans.clear();
		return super.doStartTag();
	}

	public int doEndTag() throws JspException {
		initData();
	    JspWriter out = pageContext.getOut();
		try {
			String path = (String)pageContext.getRequest().getAttribute("REQUEST_PATH");
			path = path==null?null:path.replaceAll("\\.action.*", "").replaceAll("_.*", "");
			pageContext.getServletContext().setAttribute("table.tag."+path, this);
		} catch (Exception e1) {
			logger.error(e1.getMessage(),e1);
		}
		try {
			out.print("<table");
			out.print(getHtmlAttrString());
			out.println(">");
			out.println("<colgroup>");
			for (Column c : cols) {
				if(c.hide)continue;
				out.println(c.getColHtml());
				
			}
			out.println("</colgroup>");
			out.println("<thead>");
			for (Column c : cols) {
				if(c.hide)continue;
				if("#chk".equals(c.getText())){
					c.setText("<input type=checkbox name=chkAll />");
				}
				out.println(c.getThHtml());
			}
			out.println("</thead>");
			out.println("<tbody>");
			if(it!=null){
				Object o=null;
				int i=0;
				while(it.hasNext()&&(length<=0||i<length)){
					o = it.next();i++;
					out.println("<tr "+getCalcVal(trattrs,o,null)+">");
					for (Column c : cols) {
						if(c.hide)continue;
						out.println(c.getTdHtml(o,i));
					}
					out.println("</tr>");
				}
			}
			out.println("</tbody>");
	    	out.println(getBodyContent().getString().trim());
			out.println("</table>");
			out.println("<script src=\"/mis/js/htmltable.js?v=2\"></script>");
			out.println("<script>");
			HashMap<String, String> colspanmap = new HashMap<String, String>();
			for(ColSpan cs:colspans){
				String to = colspanmap.get(cs.to);
				if(to!=null){//将多层合并单元格的结束列修改为真正的结束列field值
					cs.to = to;
				}
				colspanmap.put(cs.from, cs.to);
				out.println(new StringBuffer("spanTblCol('").append(cs.from).append("','").append(cs.to).append("','").append(cs.text)
						.append("','").append(cs.attrs.replaceAll("'", "\\'").replaceAll("\n", "\\n").replaceAll("\r", "\\r")).append("');").toString());
			}
			out.println("</script>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.doEndTag();
	}
	Pattern pat1 = Pattern.compile("\\#\\{\\s*([^\\}]+)\\s*\\}");
	Pattern isnum = Pattern.compile("[\\#0\\.,]+");
	DecimalFormat df = new DecimalFormat();
	public String getCalcVal(String exp,Object o,String format){
		if(FastUtil.isBlank(exp)){
			return "";
		}
		String val = exp;
		Matcher mat = pat1.matcher(val);
		while(mat.find()){//表中需要显示多个属性合并的内容
			String name = mat.group(1);
			String value = getObjVal(o, name, format);
			val = val.replace("#{"+name+"}", value);
		}
		return val;
	}
	public String getObjVal(Object o,String field,String format){
		if(o==null){
			return "";
		}
		String val=null;
		Object v = null;
		try {
			v = PropertyUtils.getProperty(o,field);
		} catch (Exception e) {
		}
		if("html".equals(format)){
			val = v==null?"":v.toString();
			val = escapeBr(StringEscapeUtils.escapeHtml(val));
		}
		else{
			val = FastUtil.format(v, format);
		}
		return FastUtil.nvl(val, "");
	}

    private String escapeBr(String str){
    	return str==null?null:str.replaceAll("\\r*\\n\\r*", "<br/>");
    }
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	public String getTrattrs() {
		return trattrs;
	}

	public void setTrattrs(String trattrs) {
		this.trattrs = trattrs;
	}
	public boolean isNoSortCol() {
		return noSortCol;
	}

	public void setNoSortCol(boolean noSortCol) {
		this.noSortCol = noSortCol;
	}
	public class Column{

		private String width,text,field,format,attrs,sort;
//		private String srcwidth,srctext,srcformat;
		
		private boolean hide;//是否隐藏
		
//		private int nseq;//显示顺序
		
		public Column() {
			super();
		}
		public Column(String width, String text, String field, String format,String sort,
				String attrs, boolean disabled) {
			super();
			this.width = width;
			this.text = text;
			this.field = field;
			this.format = format;
			this.sort = sort;
			this.attrs = attrs==null?"":attrs;
			if(format!=null && format.indexOf("MM-dd HH:mm")>=0){
				if(attrs !=null && attrs.indexOf("class=\"")>=0){
					this.attrs = attrs.replace("class=\"", "class=\"datetime ");
				}
				else{
					this.attrs += " class=\"datetime\"";
				}
			}
		}
		public String getColHtml(){
			return new StringBuffer("<col width=\"").append(width).append("\"/>").toString();
		}
		public String getThHtml(){
			return new StringBuffer("<th field=\"").append(field).append("\"").append(FastUtil.isNotBlank(sort)?" sort=\""+sort+"\"":"").append(attrs.replaceAll("#\\{.+\\}", "")).append(">").append(text).append("</th>").toString();
		}
		public String getTdHtml(Object o,int index){
			String val = null;
			if(field.indexOf("#")>=0){
				if(field.startsWith("#index")){
					val = ""+index + getCalcVal(field.replace("#index", ""),o,format);
				}
				else if(field.startsWith("#chk")){
					String name = field.length()>5?field.substring(5):"vcId";
					val = "<input type=checkbox name=chkSelf value=\""+getObjVal(o, name, null)+"\" />";
				}
				else{
					val = getCalcVal(field,o,format);
				}
			}
			else{
				val = getObjVal(o, field, format);
			}
			boolean leftAlign = attrs!=null && attrs.indexOf("class")>=0 && attrs.matches(".*class=['|\"](\\S+\\s+)?left(\\s+\\S+)?['|\"].*");
			StringBuffer buff = new StringBuffer("<td");
			buff.append(getCalcVal(attrs,o,format)).append(">");
			if(leftAlign){
				buff.append("<span>").append(val).append("</span>");
			}
			else{
				buff.append(val);
			}
			buff.append("</td>");
			return buff.toString();
		}
		
		public String getWidth() {
			return width;
		}
		public void setWidth(String width) {
			this.width = width;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public String getFormat() {
			return format;
		}
		public void setFormat(String format) {
			this.format = format;
		}
		public String getAttrs() {
			return attrs;
		}
		public void setAttrs(String attrs) {
			this.attrs = attrs;
		}
		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}

	}
	class ColSpan{

		private String text,from,to,attrs;
		
//		private int fromCol,toCol,rowLayer;//起始列号，终止列号，行的层数

		public ColSpan() {
			super();
		}
		public ColSpan(String text, String from, String to,String attrs) {
			super();
			this.text = text;
			this.from = from;
			this.to = to;
			this.attrs = attrs==null?"":attrs;
		}
		public String getHtml(){
			return new StringBuffer("<col width=\"").append(text).append("\"/>").toString();
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getTo() {
			return to;
		}
		public void setTo(String to) {
			this.to = to;
		}
	}
}

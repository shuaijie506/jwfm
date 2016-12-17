package com.dx.jwfm.framework.web.tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.MapObject;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.util.FastUtil;


/**
 * @author 宋帅杰
 * 查询条件项目标签，显示所有查询条件
 */
public class SearchItemTag extends BaseViewTag {
	/**  */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(SearchItemTag.class);
	
	/**
	 * 菜单地址，非必要属性。用于在页面中获取某个菜单项的功能按钮权限
	 * 使用举例（如字典表的权限操作，字典子表的操作权限需要与字典主表的操作权限一致，因此需要传入字典主表的菜单地址，以获取和主表相同的操作功能）
	 */
	private String menuUrl;
	
	@Override
	public int doEndTag() throws JspException {
	    FastModel model = RequestContext.getFastModel();
	    JspWriter out = pageContext.getOut();
	    if(model==null){
			return super.doEndTag();
	    }
	    System.out.println(this);
	    try{
	    	StringWriter buff = new StringWriter();
	    	PrintWriter jsOut = new PrintWriter(buff);
	    	List<SearchColumn> items = model.getModelStructure().getSearch().getSearchColumns();
	    	for(SearchColumn col:items){
	    		out.print("<span class=\"searchItem\"> ");
	    		out.print(col.getVcTitle());
	    		out.print(replaceVars(getEditorHtml(col, "search", getValue(col))));
	    		out.println("</span>");
	    		if(FastUtil.isNotBlank(col.getVcEditorJs())){
	    			jsOut.println(col.getVcEditorJs());
	    		}
	    	}
	    	jsOut.flush();
	    	if(buff.getBuffer().length()>0){
	    		out.println("<script>");
	    		out.println(buff.toString());
	    		out.println("</script>");
	    	}
		} catch (IOException ex) {
			logger.error(ex);
		}
		return super.doEndTag();
	}

	public String getEditorHtml(SearchColumn col, String prefix,String value){
		//日期型的需要特殊处理
		if("dateRange".equals(col.getSqlSearchType())){//日期范围
			String format = null;
			if(col.getVcEditorType()!=null && col.getVcEditorType().startsWith("date:")){
				format = col.getVcEditorType().substring(5);
			}
			if(FastUtil.isBlank(format)){
				format = "yyyy-MM-dd";
			}
			int width = format.length()*6+25;
			StringBuffer buff = new StringBuffer();
			buff.append("<input type=hidden").append(HtmlUtil.createIdAndName(prefix, col.getVcCode()))
			.append(" value=1 /><input type=text").append(HtmlUtil.createIdAndName(prefix, col.getVcCode()+"Begin"))
			.append(" class=\"Wdate\" onfocus=\"WdatePicker({dateFmt:'").append(format).append("'})\" value=\"")
			.append(FastUtil.nvl((String)RequestContext.getBeanValue(prefix+"."+col.getVcCode()+"Begin"),""))
			.append("\" style=\"width:").append(width).append("px;\" /> 至 <input type=text").append(HtmlUtil.createIdAndName(prefix, col.getVcCode()+"End"))
			.append(" class=\"Wdate\" onfocus=\"WdatePicker({dateFmt:'").append(format).append("'})\" value=\"")
			.append(FastUtil.nvl((String)RequestContext.getBeanValue(prefix+"."+col.getVcCode()+"End"),"")).append("\" style=\"width:").append(width).append("px;\" />");
			return buff.toString();
		}
		return HtmlUtil.createEditorHtml(prefix, col.getVcCode(), col.getVcEditorType(), value);
	}
	private String getValue(SearchColumn col){
		Object action = RequestContext.getRequestAction();
		String val = null;
		if(action!=null){//先从Action的search属性中取值
			try {
				Object srh = PropertyUtils.getProperty(action, "search");
				if(srh instanceof MapObject){//如果search属性是MAP对象，则按MAP对象取值
					MapObject map = (MapObject) srh;
					if(map.containsKey(col.getVcCode())){
						val = FastUtil.nvl(FastUtil.format(map.get(col.getVcCode()),null), "");
					}
				}//否则按一般对象取属性值
				else if(srh!=null && PropertyUtils.isReadable(srh, col.getVcCode())){
					val = FastUtil.nvl(BeanUtils.getProperty(srh, col.getVcCode()), "");
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		if(val==null){//如果未取到值，从用户请求的参数中取值
			val = RequestContext.getParameter("search."+col.getVcCode());
		}
		if(val==null && col.getDefaults()!=null && col.getDefaults().indexOf("${")>=0){//如果请求参数中不包含相应的值，则取默认值
			val = SystemContext.replaceMacroString(col.getDefaults());
		}
		return val;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}
	
}

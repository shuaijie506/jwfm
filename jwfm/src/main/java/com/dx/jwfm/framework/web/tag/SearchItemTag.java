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
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.MapObject;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.core.parser.IDefaultValueParser;
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
	    try{
	    	StringWriter buff = new StringWriter();
	    	PrintWriter jsOut = new PrintWriter(buff);
	    	List<SearchColumn> items = model.getModelStructure().getSearch().getSearchColumns();
	    	for(SearchColumn col:items){
	    		out.print("<span class=\"searchItem\"> ");
	    		out.print(col.getVcTitle());
	    		out.print(HtmlUtil.createEditorHtml("search", col.getVcCode(), col.getVcEditorType(), getValue(col)));
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
	
	private String getValue(SearchColumn col){
		Object action = RequestContext.getRequestAction();
		String val = null;
		if(action!=null){//先从Action的search属性中取值
			try {
				Object srh = PropertyUtils.getProperty(action, "search");
				if(srh instanceof MapObject){//如果search属性是MAP对象，则按MAP对象取值
					MapObject map = (MapObject) srh;
					if(map.containsKey(col.getVcCode())){
						val = FastUtil.nvl(getFormatValue(map.get(col.getVcCode()),null), "");
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
		if(val==null && FastUtil.isNotBlank(col.getDefaults())){//如果请求参数中不包含相应的值，则取默认值
			List<IDefaultValueParser> list = RequestContext.getDefaultValueParser();
			String defaults = col.getDefaults();
			for(int i=0;i<list.size();i++){
				if(list.get(i).hasDefaultValue(defaults)){
					Object obj = list.get(i).getDefaultValue(defaults);
					val = obj==null?null:obj.toString();
					break;
				}
			}
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

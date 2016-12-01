package com.dx.jwfm.framework.web.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.core.model.search.SearchResultColumn;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.builder.IDatagridBuilder;
import com.dx.jwfm.framework.web.exception.DatagridBuilderNotFound;


/**
 * @author 宋帅杰
 * 结果展示标签
 */
public class ResultTag extends BaseViewTag {
	/**  */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(ResultTag.class);
	
	/**
	 * 菜单地址，非必要属性。用于在页面中获取某个菜单项的功能按钮权限
	 * 使用举例（如字典表的权限操作，字典子表的操作权限需要与字典主表的操作权限一致，因此需要传入字典主表的菜单地址，以获取和主表相同的操作功能）
	 */
	private String menuUrl;
	/**
	 * 显示模式，可选值为easyui,html
	 */
	private String type;
	/*** type=easyui时此属性区分显示冻结列和一般列，可选值为true/false，默认值为false */
	private String frozen;
	/*** 是否显示复选框列，可选值为true/false，默认值为false */
	private String hasChkCol;
	
	@Override
	public int doEndTag() throws JspException {
	    FastModel model = RequestContext.getFastModel();
	    JspWriter out = pageContext.getOut();
	    if(model==null){
			return super.doEndTag();
	    }
	    try{
	    	SearchModel search = model.getModelStructure().getSearch();
	    	List<SearchResultColumn> list = search.getSearchResultColumns();
    		List<FastPo> data = (List<FastPo>) RequestContext.getRequest().getAttribute("searchResultData");
	    	if(list!=null){
	    		if("html".equals(type)){//HTML显示格式
		    		out.println("<table class=fast-table-container border=0 cellpadding=0 cellspacing=0 width='100%'><tbody><tr><td>");
		    		out.println(buildTable(search, list,data));
		    		out.println("</td></tr></tbody></table>");
	    		}
	    		else{//其他JS框架显示模式
	    			try {
						IDatagridBuilder builder = SystemContext.getDatagridBuilder(type);
						if(builder!=null){
		    				out.print(builder.buildResultColumn(list, "true".equals(frozen), "true".equals(hasChkCol)));
						}
						else{
							throw new DatagridBuilderNotFound("The type ["+type+"] is not found! please set [datagridBuilder."+type+
									"] in fast.properties,the value should be implements IDatagridBuilder");
						}
					} catch (Exception e) {
						throw new DatagridBuilderNotFound("The datagridBuilder."+type+
								"'s value in fast.properties can't be load as Class!",e);
					}
	    		}
	    	}
		} catch (IOException ex) {
			logger.error(ex);
		} catch (DatagridBuilderNotFound e) {
			throw new JspException(e);
		}
		return super.doEndTag();
	}
	private String buildTable(SearchModel search,List<SearchResultColumn> list, List<FastPo> data) {
		StringBuffer buff = new StringBuffer();
		StringBuffer colbuff = new StringBuffer(),thbuff = new StringBuffer();
		buff.append("<table class=\"fast-table\"");
		for(String k:attr.keySet()){
	    	buff.append(" ");
	    	buff.append(k);
			if(attr.get(k)!=null){
		    	buff.append("=\"");
		    	buff.append(attr.get(k).replace("\"", "\\\"").replaceAll("\\r|\\n", ""));
		    	buff.append("\"");
			}
		}
		buff.append(" ><colgroup>");
		for(SearchResultColumn col:list){
    		if(col.isHidden())continue;
			colbuff.append("<col width=\"").append(col.getWidth()).append("px\" />");
			thbuff.append("<th ");
			if(FastUtil.isNotBlank(col.getCanSort())){
				thbuff.append(" sort=\"").append(col.getVcCode()).append("\" order=\"").append(col.getCanSort()).append("\"");
			}
			if(FastUtil.isNotBlank(col.getVcThCss())){
				thbuff.append(" class=\"").append(col.getVcThCss()).append("\"");
			}
			if(FastUtil.isNotBlank(col.getVcThStyle())){
				thbuff.append(" style=\"").append(col.getVcThStyle()).append("\"");
			}
			if(col.isFrozen()){
				thbuff.append(" frozen=\"true\"");
			}
			thbuff.append(">").append(col.getVcTitle()).append("</th>");
		}
		buff.append(colbuff.toString()).append("</colgroup><thead>").append(thbuff.toString()).append("</thead>");
		buff.append("<tbody>");
		if (data == null || data.isEmpty()) {
			buff.append("<tr><td colspan=").append(list.size()).append(">未查到任何内容</td></tr>");
		} else {
			String rowAttr = "";
//			if (FastUtil.isNotBlank(search.getVcRowCss())) {
//				rowAttr += " class=\"" + search.getVcRowCss() + "\"";
//			}
//			if (FastUtil.isNotBlank(search.getVcRowStyle())) {
//				rowAttr += " style=\"" + search.getVcRowStyle() + "\"";
//			}
			for (FastPo row : data) {
				buff.append("<tr").append(rowAttr).append(">");
				for (SearchResultColumn col : list) {
		    		if(col.isHidden())continue;
					buff.append("<td");
					if (FastUtil.isNotBlank(col.getVcTdCss())) {
						buff.append(" class=\"").append(col.getVcTdCss()).append("\"");
					}
					if (FastUtil.isNotBlank(col.getVcTdStyle())) {
						buff.append(" style=\"").append(col.getVcTdStyle()).append("\"");
					}
					buff.append(">");
					if (col.getVcFormat()!=null && col.getVcFormat().startsWith("dict:")) {
						String val = row.getString(col.getVcCode());
						val = getDictText(col.getVcFormat().substring(5), val);
						buff.append(val);
					} else {
						buff.append(row.get(FastUtil.isNotBlank(col.getVcFormat())
								? col.getVcCode() + ":" + col.getVcFormat() : col.getVcCode()));
					}
					buff.append("</td>");
				}
				buff.append("</tr>");
			}
		}
		buff.append("</tbody></table>");
		return buff.toString();
	}
	private HashMap<String,Map<String,String>> dictMap = new HashMap<String, Map<String,String>>();
	private String getDictText(String groupName, String code) {
		Map<String,String> map = dictMap.get(groupName);
		if(map==null){
			map = FastUtil.getDictsMap(groupName);
			dictMap.put(groupName, map);
		}
		return map.get(code);
	}

	public String getHasChkCol() {
		return hasChkCol;
	}

	public void setHasChkCol(String hasChkCol) {
		this.hasChkCol = hasChkCol;
	}

	public String getFrozen() {
		return frozen;
	}

	public void setFrozen(String frozen) {
		this.frozen = frozen;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}

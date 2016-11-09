package com.dx.jwfm.framework.web.tag;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.util.FastUtil;

public class HtmlUtil {

	static Logger logger = Logger.getLogger(HtmlUtil.class);

	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月7日 下午2:23:58
	 * 功能描述: 生成固定格式的编辑框
	 * 方法的参数和返回值: 
	 * @param prefix
	 * @param fieldName
	 * @param editorType
	 * @param value
	 * @return
	 */
	public static String createEditorHtml(String prefix,String fieldName,String editorType,String value){
		StringWriter buff = new StringWriter();
    	PrintWriter out = new PrintWriter(buff);
    	if(editorType==null){
    		editorType = "text";
    	}
		if(editorType.startsWith("html:")){
			out.println(editorType.substring(5));
		}
		else if(editorType.startsWith("sqlDict:")){//解析SQL语句，第一列为值，第二列为显示文本
			DbHelper db = new DbHelper();
			try {
				Map<String, String> map = db.getMapSqlQuery(editorType.substring("sqlDict:".length()));
				createSelectHtml(map, out, prefix, fieldName, value);
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		else if(editorType.startsWith("dict:")){//字典项
			Map<String, String> map = FastUtil.getDictsMap(editorType.substring("dict:".length()));
			createSelectHtml(map, out, prefix, fieldName, value);
		}
		else{//文本或日期
    		out.print("<input type=text id=\"");
    		if(FastUtil.isNotBlank(prefix)){
    			out.print(prefix);
    			out.print("_");
    		}
    		out.print(fieldName);
    		out.print("\" name=\"");
    		if(FastUtil.isNotBlank(prefix)){
    			out.print(prefix);
    			out.print(".");
    		}
    		out.print(fieldName);
    		out.print("\"");
    		if(value!=null){
    			out.print(" value=\""+value.replaceAll("\"", "\\\"")+"\"");
    		}
    		if("date".equals(editorType)){
    			out.print("class=\"Wdate\" onfocus=\"WdatePicker()\"");
    		}
    		else if(editorType.startsWith("date:")){
    			out.print("class=\"Wdate\" onfocus=\"WdatePicker({dateFmt:'"+editorType.substring(5)+"'})\"");
    		}
    		out.print(" />");
		}
		out.flush();
		return buff.toString();
	}
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月7日 下午2:24:18
	 * 功能描述: 生成固定格式的编辑页面代码
	 * 方法的参数和返回值: 
	 * @param colList
	 * @param hiddenCols
	 * @param cols
	 * @param editColCnt
	 * @return
	 */
	public static String createSimpleEditTableHtml(List<FastColumn> colList,String[] hiddenCols,String[] cols,int editColCnt){
		StringBuffer buff = new StringBuffer();
		if(colList==null || colList.isEmpty()){
			return null;
		}
		HashMap<String,FastColumn> colmap = new HashMap<String,FastColumn>();
		for(FastColumn col:colList){
			colmap.put(col.getCode(), col);
		}
		if(hiddenCols!=null){
			for(String colCode:hiddenCols){
				buff.append("<input type=hidden name=\"po.").append(colCode).append("\" id=\"po_").append(colCode).append("\" value=\"${po.").append(colCode).append("}\" />\n");
			}
		}
		buff.append("<div style=\"width:565px;margin:10px auto;\">\n<table id=\"editFormTable\" class=\"fast-edit-table\">\n");
		buff.append("<colgroup>\n");
		for(int i=0;i<editColCnt;i++){
			buff.append("<col width=\"15%\" />\n");
			buff.append("<col width=\"35%\" />\n");
		}
		buff.append("</colgroup>\n");
		buff.append("<thead>\n<tr style='display:none;'>\n");
		for(int i=0;i<editColCnt*2;i++){
			buff.append("<th></th>\n");
		}
		buff.append("</tr>\n</thead>\n");
		buff.append("<tbody>\n");
		int cells = 0;
		if(hiddenCols!=null){
			for(String colCode:cols){
				FastColumn col = colmap.get(colCode);
				if(col==null){
					continue;
				}
				if(cells%editColCnt==0){
					buff.append("<tr>\n");
				}
				if(cells%editColCnt>0 && "textarea".equals(col.getEditorType())){//textarea编辑器独占一行
					buff.append("<td colspan=").append((editColCnt-cells%editColCnt)*2).append("></td>\n</tr>\n<tr>");
					cells+=editColCnt-cells%editColCnt;
				}
				buff.append("<th>").append(col.getName()).append("</th><td");
				if("textarea".equals(col.getEditorType())){
					buff.append(" colspan=").append(editColCnt*2-1);
					cells++;
				}
				String ext = "";
				if(col.getEditorType()!=null && col.getEditorType().startsWith("date:")){//如果是日期类型，要在值后面加上日期格式
					ext = col.getEditorType().substring(4);
				}
				buff.append(">").append(HtmlUtil.createEditorHtml("po", col.getCode(), col.getEditorType(), "${po."+col.getCode()+ext+"}"));
				buff.append("</td>\n");
				cells++;
				if(cells%editColCnt==0){
					buff.append("</tr>\n");
				}
			}
		}
		buff.append("</tbody>\n</table>\n</div>");
		return buff.toString();
	}
	private static void createSelectHtml(Map<String, String> map, PrintWriter out, String prefix,String fieldName,String value){
		out.print("<select id=\"");
		if(FastUtil.isNotBlank(prefix)){
			out.print(prefix);
			out.print("_");
		}
		out.print(fieldName);
		out.print("\" name=\"");
		if(FastUtil.isNotBlank(prefix)){
			out.print(prefix);
			out.print(".");
		}
		out.print(fieldName);
		out.print("\" val=\"");
		out.print(value);
		out.print("\">");
		out.println("<option value=\"\"></option>\n");
		for(String key:map.keySet()){
			out.print("<option value=\"");
			out.print(key);
			if(value!=null && value.equals(key)){
				out.print("\" selected=\"true");
			}
			out.print("\">");
			out.print(map.get(key));
			out.println("</option>");
		}
		out.println("</select>");
	}
	
}

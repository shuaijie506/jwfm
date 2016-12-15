package com.dx.jwfm.framework.web.tag;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.util.FastUtil;

import net.sf.json.JSONArray;

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
		else if(editorType.startsWith("select:")){//解析SQL语句，第一列为值，第二列为显示文本
			if(value!=null && value.indexOf("${")==0){//宏定义规则，用于编辑页面
				out.println("${$select$"+prefix+"."+fieldName+editorType.substring(6)+"}");
			}
			else{
				SelectTag sel = new SelectTag();
				sel.setName(prefix+"."+fieldName);
				sel.setValue(value==null?"":value.toString());
				sel.setEmptyOption(true);
				sel.setList(editorType.substring(7));
				sel.setId(prefix+"_"+fieldName);
				sel.writeHtml(out);
			}
//			DbHelper db = new DbHelper();
//			Map<String, String> map = null;
//			try {
//				map = db.getMapSqlQuery(editorType.substring("sqlDict:".length()));
//			} catch (SQLException e) {
//				logger.error(e);
//				map = new HashMap<String, String>();
//			}
//			createSelectHtml(map, out, prefix, fieldName, value);
		}
//		else if(editorType.startsWith("dict:")){//字典项
//			Map<String, String> map = FastUtil.getDictsMap(editorType.substring("dict:".length()));
//			createSelectHtml(map, out, prefix, fieldName, value);
//		}
		else if("textarea".equals(editorType)){//多行文本
    		out.print("<textarea");
    		out.print(createIdAndName(prefix,fieldName));
			out.print(">");
    		if(value!=null){
    			out.print(value.replaceAll("\"", "\\\""));
    		}
    		out.print("</textarea>");
		}
		else if("text".equals(editorType) || editorType.startsWith("date")){//文本或日期
    		out.print("<input type=text");
    		out.print(createIdAndName(prefix,fieldName));
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
		else{
			out.print(editorType);
		}
		out.flush();
		return buff.toString();
	}
	public static String createIdAndName(String prefix, String fieldName) {
		StringBuffer buff = new StringBuffer(" id=\"");
		if(FastUtil.isNotBlank(prefix)){
			buff.append(prefix);
			buff.append("_");
		}
		buff.append(fieldName);
		buff.append("\" name=\"");
		if(FastUtil.isNotBlank(prefix)){
			buff.append(prefix);
			buff.append(".");
		}
		buff.append(fieldName);
		buff.append("\"");
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
				buff.append("<td class=th>").append(col.getName()).append("</th><td");
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
//	private static void createSelectHtml(Map<String, String> map, PrintWriter out, String prefix,String fieldName,String value){
//		out.print("<select");
//    		out.print(createIdAndName(prefix,fieldName));
//		out.print(" val=\"");
//		out.print(value);
//		out.print("\">");
//		out.println("<option value=\"\"></option>\n");
//		for(String key:map.keySet()){
//			out.print("<option value=\"");
//			out.print(key);
//			if(value!=null && value.equals(key)){
//				out.print("\" selected=\"true");
//			}
//			out.print("\">");
//			out.print(map.get(key));
//			out.println("</option>");
//		}
//		out.println("</select>");
//	}
	
	public static String genTestHtml(int rows,int cols){
		StringBuffer buff = new StringBuffer("<table>");
		for(int i=0;i<rows;i++){
			buff.append("<tr>");
			for(int j=0;j<cols;j++){
				buff.append("<td>").append(i).append("*").append(j).append("=").append(i*j);
				buff.append("</td>");
			}
			buff.append("</tr>");
		}
		buff.append("</table>");
		return buff.toString();
	}
	
	public static String genTestJson(int rows,int cols){
		JSONArray ary = new JSONArray();
		for(int i=0;i<rows;i++){
			JSONArray row = new JSONArray();
			for(int j=0;j<cols;j++){
				row.add(i+"*"+j+"="+(i*j));
			}
			ary.add(row);
		}
		return ary.toString();
	}
	
}

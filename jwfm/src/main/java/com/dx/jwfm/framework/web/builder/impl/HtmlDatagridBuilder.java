package com.dx.jwfm.framework.web.builder.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.ButtonAuth;
import com.dx.jwfm.framework.core.model.search.SearchResultColumn;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.builder.IDatagridBuilder;
import com.dx.jwfm.framework.web.search.Pager;
import com.dx.jwfm.framework.web.search.Search;

public class HtmlDatagridBuilder implements IDatagridBuilder {

	/* (non-Javadoc)
	 * @see com.dx.jwfm.framework.web.builder.IDatagridBuilder#buildResultJson(java.util.List, com.dx.jwfm.framework.web.search.Pager, com.dx.jwfm.framework.web.search.Search)
	 */
	public String buildResultJson(List<FastPo> list, Pager pager, Search search) {
		JSONObject obj = new JSONObject();
		obj.put("total", pager.getRowAmount());
		obj.put("pageSize", pager.getRows());
		obj.put("pageNumber", pager.getPage());
		String formatCols = search.getString("formatCols");//执行查询时，会将有冒号的字段通过参数formatCols传到服务器
		if(list.size()>0 && FastUtil.isNotBlank(formatCols)){
			String[] ary = formatCols.split("<split>");
			for(int i=0;i<ary.length;i++){
				if(list.get(0).containsKey(ary[i])){//如果已经包含指定字段，则无需进行特殊处理
					continue;
				}
				int pos = ary[i].indexOf(":dict");
				String field = ary[i];
				Map<String,String> dictMap = null;
				if(pos>0){//公共字典
					field = ary[i].substring(0,pos);
					String groupName = ary[i].substring(pos+5);
					dictMap = FastUtil.getDictsMap(groupName);
				}
				for(int j=0;j<list.size();j++){
					FastPo row = list.get(j);
					if(pos>0){//公共字典
						if(dictMap!=null){
							row.setPropt(ary[i], dictMap.get(row.getString(field)));
						}
						else{
							row.setPropt(ary[i], row.get(field));
						}
					}
					else{//一般的格式化内容
						row.setPropt(ary[i], row.get(ary[i]));
					}
				}
			}
		}
		JSONArray ary = new JSONArray();
		ary.addAll(list,FastUtil.getJsonConfigDefault());
		obj.put("rows", ary);
		return obj.toString();
	}

	public String buildResultColumn(List<SearchResultColumn> columns, boolean frozen, boolean hasChkCol) {
		JSONArray ary = new JSONArray();
		JSONArray row = new JSONArray();
		
		if(hasChkCol){
    		JSONObject obj = new JSONObject();
			obj.put("field", "ck");
			obj.put("checkbox", true);
			row.add(obj);
		}
    	for(SearchResultColumn col:columns){
    		if(frozen == col.isFrozen()){//是否为冻结列
	    		JSONObject obj = new JSONObject();
    			if(col.getVcType().startsWith("dict:")){
	    			obj.put("field", col.getVcCode()+":dict"+col.getVcType().substring(5));
    			}
    			else if(FastUtil.isNotBlank(col.getVcFormat())){
	    			obj.put("field", col.getVcCode()+":"+col.getVcFormat());
	    		}
	    		else{
	    			obj.put("field", col.getVcCode());
	    		}
    			obj.put("title", col.getVcTitle());
    			obj.put("width", col.getWidth());
    			obj.put("sortable", FastUtil.isNotBlank(col.getCanSort()));
    			row.add(obj);
    		}
    	}
		ary.add(row);
    	return ary.toString();
	}

	public String buildToolbar(List<ButtonAuth> btns) {
		JSONArray ary = new JSONArray();
    	for(ButtonAuth btn:btns){
    		JSONObject obj = new JSONObject();
    		obj.put("id", btn.getBtnId());
    		obj.put("text", btn.getName());
    		obj.put("iconCls", btn.getIconCls());
    		obj.put("handler", btn.getFunName());
    		ary.add(obj);
    	}
    	return ary.toString();
	}

	public String getDefaultSearchPage() {
		return "/fast/template/easyui/search.jsp";
	}

	public String getDefaultEditPage() {
		return "/fast/template/easyui/editPage.jsp";
	}

}

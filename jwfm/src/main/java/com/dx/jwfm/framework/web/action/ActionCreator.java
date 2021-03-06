package com.dx.jwfm.framework.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dx.jwfm.framework.core.annotations.FastModelInfo;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.model.ButtonAuth;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.FastModelStructure;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.tag.HtmlUtil;

public abstract class ActionCreator extends FastBaseAction {

//	protected HashMap<String,String> getForwards(){
//		HashMap<String,String> map = new HashMap<String, String>();
//		map.put("success", 			"/jwfm/core/easyuiSearch.jsp");
//		map.put("openAddPage",		"/jwfm/core/easyuiEdit.jsp");
//		map.put("openModifyPage",	"/jwfm/core/easyuiEdit.jsp");
//		map.put("openViewPage",		"/jwfm/core/easyuiView.jsp");
//		return map;
//	}
	/*** 主表信息 */
	abstract protected FastTable getMainTable();
	/*** 查询条件信息 */
	protected SearchModel getSearchModel(){
		return new SearchModel();
	}
	/*** 查询界面的按钮 */
	protected List<ButtonAuth> getButtonList(){
		return new ArrayList<ButtonAuth>();
	}
	
	protected void initModel(FastModel model){
		
	}
	
	private static HashMap<String,FastModel> modelMap = new HashMap<String, FastModel>();
	public FastModel getFastModel(){
		String clsName = this.getClass().getName();
		FastModel model = modelMap.get(clsName);
		if(model!=null){
			return model;
		}
		model = new FastModel();
		modelMap.put(clsName, model);
		model.setVcId(clsName.length()>50?clsName.substring(clsName.length()-50):clsName);
//		model.setDtAdd(new Date());
		FastModelInfo info = this.getClass().getAnnotation(FastModelInfo.class);
		if(info!=null){
			model.setVcAuth(info.author());
			model.setVcAdd(info.author());
			model.setVcModify(info.updateInfo());
			model.setDtAdd(FastUtil.parseDate(info.devDate()));
			model.setVcGroup(info.group());
			model.setVcName(info.name());
			model.vcVersion = info.devDate();
			model.setVcUrl(info.url());
		}
		FastModelStructure struct = new FastModelStructure();
		FastUtil.copyBeanPropts(struct, model);
		model.setModelStructure(struct);
		struct.setMainTable(getMainTable());
//		struct.setMainTableName(struct.getMainTable().getName());
//		struct.setPackageName("fast.main");
		int pos = clsName.lastIndexOf(".");
		String pn = clsName.substring(0, pos);
		if(pn.endsWith(".action")){
			pn = pn.substring(0, pn.length()-7);
		}
		struct.setPackageName(pn);
		struct.setActionName("com.dx.jwfm.framework.web.action.FastBaseAction");
		struct.setDefaultSearchData(false);
		struct.setForward("success", 			"/jwfm/core/easyuiSearch.jsp");
		struct.setForward("openAddPage",		"/jwfm/core/easyuiEdit.jsp");
		struct.setForward("openModifyPage",		"/jwfm/core/easyuiEdit.jsp");
		struct.setForward("openViewPage",		"/jwfm/core/easyuiView.jsp");
		struct.setSearch(getSearchModel());
//		struct.setPageHTML("edit", "编辑页面", getEditTable());
		struct.setButtonAuths(getButtonList());
		
		initModel(model);
		return model;
	}
	protected String getSimpleEditTableHtml(String[] hiddenCols,String[] cols,int editColCnt){
		FastTable mt = getMainTable();
		if(mt==null){
			return null;
		}
		return HtmlUtil.createSimpleEditTableHtml(mt.getColumns(), hiddenCols, cols, editColCnt);
	}
}

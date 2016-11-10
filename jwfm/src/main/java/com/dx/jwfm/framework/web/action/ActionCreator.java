package com.dx.jwfm.framework.web.action;

import java.util.HashMap;
import java.util.List;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.annotations.FastModelInfo;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.ButtonAuth;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.FastModelStructure;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.tag.HtmlUtil;

public abstract class ActionCreator extends FastBaseAction {

	/*** 菜单对应分组名 */
	abstract protected String getGroup();
	/*** 菜单名 */
	abstract protected String getName();
	/*** 版本，建议以日期做为版本号 */
	abstract protected String getVersion();
	/*** 对应请求地址 */
	abstract protected String getUrl();
	/*** 开发人员姓名 */
	abstract protected String getAuthor();
	protected HashMap<String,String> getForwards(){
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("success", 			"/jwfm/core/htmlSearch.jsp");
		map.put("openAddPage",		"/jwfm/core/htmlEdit.jsp");
		map.put("openModifyPage",	"/jwfm/core/htmlEdit.jsp");
		map.put("openViewPage",		"/jwfm/core/htmlView.jsp");
		return map;
	}
	/*** 主表信息 */
	abstract protected FastTable getMainTable();
	/*** 查询条件信息 */
	abstract protected SearchModel getSearchModel();
	/*** 查询界面的按钮 */
	abstract protected List<ButtonAuth> getButtonList();
	/*** 编辑界面的HTML */
	abstract protected String getEditTable();
	
	protected void initModel(FastModel model){
		
	}
	
	private static HashMap<String,FastModel> modelMap = new HashMap<String, FastModel>();
	public FastModel getFastModel(){
		String clsName = this.getClass().getName();
		FastModel model = modelMap.get(clsName);
		if(!FastUtil.isDebugModel() && model!=null){
			return model;
		}
		model = new FastModel();
		modelMap.put(clsName, model);
		model.setVcId(clsName);
		model.setVcGroup(getGroup());
		model.setVcName(getName());
		model.version = getVersion();
		model.setVcUrl(getUrl());
		model.setVcAuth(getAuthor());
		model.setVcAdd(getAuthor());
//		model.setDtAdd(new Date());
		FastModelInfo info = this.getClass().getAnnotation(FastModelInfo.class);
		if(info!=null){
			model.setVcAuth(info.author());
			model.setVcAdd(info.author());
			model.setVcModify(info.updateInfo());
		}
		FastModelStructure struct = new FastModelStructure();
		model.setModelStructure(struct);
		struct.setName(model.getVcName());
		struct.setMainTable(getMainTable());
		struct.setMainTableName(struct.getMainTable().getCode());
//		struct.setOtherTables(getOtherTables());
//		struct.setVcUri(model.getVcUrl());
		struct.setVcGroup(model.getVcGroup());
//		struct.setPackageName("fast.main");
		struct.setActionName("com.dx.jwfm.framework.web.action.FastBaseAction");
//		struct.setActionHandleName(null);
		struct.setDefaultSearchData(true);
//		struct.setActionDefaultValueParser(null);
		struct.getForwards().putAll(getForwards());
		struct.setSearch(getSearchModel());
		struct.setEditTable(getEditTable());
		model.setButtonAuths(getButtonList());
		
		initModel(model);
		model.init();
		return model;
	}
	
	protected FastPo getDictData(String code,String text,String groupName,int nseq){
		return getDictData(code, text, null, groupName, nseq);
	}
	protected FastPo getDictData(String code,String text,String note,String groupName,int nseq){
		FastPo p = FastPo.getPo(SystemContext.dbObjectPrefix+"T_DICT");
		p.element("VC_CODE", code).element("VC_TEXT", text).element("VC_NOTE", note).element("VC_GROUP", groupName).element("N_SEQ", nseq);
		return p;
	}
	protected String getSimpleEditTableHtml(String[] hiddenCols,String[] cols,int editColCnt){
		FastTable mt = getMainTable();
		if(mt==null){
			return null;
		}
		return HtmlUtil.createSimpleEditTableHtml(mt.getColumns(), hiddenCols, cols, editColCnt);
	}
}

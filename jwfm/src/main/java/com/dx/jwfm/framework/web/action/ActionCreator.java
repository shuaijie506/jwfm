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
		map.put("success", 			"/jwfm/core/easyuiSearch.jsp");
		map.put("openAddPage",		"/jwfm/core/easyuiEdit.jsp");
		map.put("openModifyPage",	"/jwfm/core/easyuiEdit.jsp");
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
		if(model!=null){
			return model;
		}
		model = new FastModel();
		modelMap.put(clsName, model);
		model.setVcId(clsName);
		model.setVcGroup(getGroup());
		model.setVcName(getName());
		model.vcVersion = getVersion();
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
		FastUtil.copyBeanPropts(struct, model);
		model.setModelStructure(struct);
		struct.setMainTable(getMainTable());
		struct.setMainTableName(struct.getMainTable().getCode());
//		struct.setPackageName("fast.main");
		struct.setActionName("com.dx.jwfm.framework.web.action.FastBaseAction");
		struct.setDefaultSearchData(false);
		struct.setForward("success", 			"/jwfm/core/easyuiSearch.jsp");
		struct.setForward("openAddPage",		"/jwfm/core/easyuiEdit.jsp");
		struct.setForward("openModifyPage",	"/jwfm/core/easyuiEdit.jsp");
		struct.setForward("openViewPage",		"/jwfm/core/htmlView.jsp");
		struct.setSearch(getSearchModel());
		struct.setPageHTML("edit", "编辑页面", getEditTable());
		struct.setButtonAuths(getButtonList());
		
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

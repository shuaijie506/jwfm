package com.dx.jwfm.framework.web.action;

import java.util.ArrayList;
import java.util.List;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.annotations.FastModelInfo;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastColumnType;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.ButtonAuth;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.core.model.search.SearchResultColumn;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.exception.ValidateException;

@FastModelInfo(author="宋帅杰", devDate = "2015-12-03", updateInfo = "")
public class UserActionCreator extends ActionCreator {

	@Override
	protected String addItem() throws ValidateException {
		if(po.getString("VC_PWD").length()<32){
			po.put("VC_PWD", FastUtil.toMd5String(po.getString("VC_PWD")));
		}
		return super.addItem();
	}

	@Override
	protected String modifyItem() throws ValidateException {
		if(po.getString("VC_PWD").length()<32){
			po.put("VC_PWD", FastUtil.toMd5String(po.getString("VC_PWD")));
		}
		return super.modifyItem();
	}

	static FastModel model;
	@Override
	protected String getGroup() {
		return "Fast开发平台";
	}

	@Override
	protected String getName() {
		return "用户管理";
	}

	@Override
	protected String getVersion() {
		return "2016-12-01v2";
	}
	@Override
	protected String getUrl() {
		return "/jwfm/core/user";
	}
	
	@Override
	protected String getAuthor() {
		return "宋帅杰";
	}

	protected String getEditTable() {
		return getSimpleEditTableHtml("VC_ID".split(","), "VC_NAME,VC_PWD,N_LEVEL,DT_ADD".split(","),1);
	}


	@Override
	protected FastTable getMainTable() {
		FastTable tbl = new FastTable();
		tbl.setName("Fast开发用户表");
		tbl.setCode(SystemContext.dbObjectPrefix+"T_USER");
		List<FastColumn> cols = tbl.getColumns();
		tbl.getColumns().add(new FastColumn("主键", "VC_ID", null, FastColumnType.String, 50, null, null, false, true));
		tbl.getColumns().add(new FastColumn("姓名", "VC_NAME", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("密码", "VC_PWD", null, FastColumnType.String, 50, "", null, false, false));
		tbl.getColumns().add(new FastColumn("权限级别", "N_LEVEL", "权限级别 0管理员 1开发人员 2用户维护人员 3作废", FastColumnType.Integer, 0, "1", lvlDictName, true, false));
		cols.get(cols.size()-1).setEditorType("select:dict:"+lvlDictName);
		tbl.getColumns().add(new FastColumn("创建时间", "DT_ADD", null, FastColumnType.Date, 0, "${nowTime}", null, false, false));
		cols.get(cols.size()-1).setEditorType("date:yyyy-MM-dd HH:mm");
		tbl.getColumns().add(new FastColumn("删除标记", "N_DEL", null, FastColumnType.Integer, 0, "0", null, false, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}

	@Override
	protected SearchModel getSearchModel() {
		SearchModel search = new SearchModel();
		List<SearchColumn> cols = search.getSearchColumns();
		cols.add(new SearchColumn("级别", "N_LEVEL", "select:dict:"+lvlDictName, 120, null, "=", "and t.N_LEVEL=${N_LEVEL} "));
		cols.get(cols.size()-1).setVcEditorJs("$('#search_N_LEVEL').change(function(){doSearch();});");
		cols.add(new SearchColumn("用户名", "VC_NAME", "text", 120, null, "like", "t.vc_name"));
		cols.add(new SearchColumn("创建时间", "DT_ADD", "date:yyyy-MM-dd", 120, null, "dateRange", "t.dt_add"));
		search.setSearchSelectSql("select t.* from "+SystemContext.dbObjectPrefix+"T_USER t ");
		search.setSearchOrderBySql("n_level,dt_add desc");
		search.getSearchResultColumns().add(new SearchResultColumn("姓名", "VC_NAME", 85, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("权限级别", "N_LEVEL", 75, "dict:"+lvlDictName, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", "desc"));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", ""));
		return search;
	}

	@Override
	protected List<ButtonAuth> getButtonList() {
		List<ButtonAuth> buttonAuths = new ArrayList<ButtonAuth>();
		buttonAuths.add(new ButtonAuth("添加", "add", "addItem", "addItemBtn", "icon-add", "添加按钮"));
		buttonAuths.add(new ButtonAuth("修改", "modify", "modifyItem", "modifyItemBtn", "icon-edit", "修改按钮"));
		buttonAuths.add(new ButtonAuth("删除", "del", "delItem", "delItemBtn", "icon-remove", "删除按钮"));
		return buttonAuths;
	}

	private String lvlDictName = "FAST框架-用户级别";
	@Override
	protected void initModel(FastModel model) {
		model.getModelStructure().setActionName(this.getClass().getName());
		ArrayList<FastPo> dictData = new ArrayList<FastPo>();
		String dictTbl = SystemContext.dbObjectPrefix+"T_DICT";
		dictData.add(FastPo.getPo(dictTbl).element("VC_CODE", "0").element("VC_TEXT", "管理员").element("VC_GROUP", lvlDictName).element("N_SEQ", "0"));
		dictData.add(FastPo.getPo(dictTbl).element("VC_CODE", "1").element("VC_TEXT", "开发人员").element("VC_GROUP", lvlDictName).element("N_SEQ", "1"));
		dictData.add(FastPo.getPo(dictTbl).element("VC_CODE", "2").element("VC_TEXT", "用户维护人员").element("VC_GROUP", lvlDictName).element("N_SEQ", "2"));
		dictData.add(FastPo.getPo(dictTbl).element("VC_CODE", "3").element("VC_TEXT", "作废").element("VC_GROUP", lvlDictName).element("N_SEQ", "3"));
		model.getModelStructure().setDictData(dictData);
	}


}

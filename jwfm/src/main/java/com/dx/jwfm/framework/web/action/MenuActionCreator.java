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

@FastModelInfo(author="宋帅杰", devDate = "2016-11-09", updateInfo = "")
public class MenuActionCreator extends ActionCreator {

	static FastModel model;
	@Override
	protected String getGroup() {
		return "Fast开发平台";
	}

	@Override
	protected String getName() {
		return "菜单管理";
	}

	@Override
	protected String getVersion() {
		return "2016-11-18";
	}
	@Override
	protected String getUrl() {
		return "/jwfm/core/menu";
	}
	
	@Override
	protected String getAuthor() {
		return "宋帅杰";
	}

	protected String getEditTable() {
		return getSimpleEditTableHtml("VC_ID".split(","), "VC_GROUP,VC_NAME,VC_URL,VC_VERSION,VC_NOTE".split(","),2);
	}

	private String tblPre = SystemContext.dbObjectPrefix;

	@Override
	public FastTable getMainTable() {
		FastTable tbl = new FastTable();
		tbl.setName("Fast菜单库");
		tbl.setCode(SystemContext.dbObjectPrefix+"T_MENU_LIB");
		tbl.getColumns().add(new FastColumn("主键", "VC_ID", null, FastColumnType.String, 50, null, null, false, true));
		tbl.getColumns().add(new FastColumn("菜单名", "VC_NAME", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("菜单URL", "VC_URL", null, FastColumnType.String, 200, "", null, false, false));
		tbl.getColumns().add(new FastColumn("模块控制结构", "VC_STRUCTURE", null, FastColumnType.String, -1, "", null, true, false));
		tbl.getColumns().add(new FastColumn("所在分组", "VC_GROUP", null, FastColumnType.String, 200, "", null, true, false));
		tbl.getColumns().add(new FastColumn("版本", "VC_VERSION", null, FastColumnType.String, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("添加人", "VC_ADD", null, FastColumnType.String, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("添加时间", "DT_ADD", null, FastColumnType.Date, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("修改人", "VC_MODIFY", null, FastColumnType.String, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("修改时间", "DT_MODIFY", null, FastColumnType.Date, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("功能说明及更改历史", "VC_NOTE", null, FastColumnType.String, -1, "", null, true, false));
		tbl.getColumns().add(new FastColumn("删除标记", "N_DEL", null, FastColumnType.Integer, 0, "0", null, false, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}

	@Override
	protected SearchModel getSearchModel() {
		SearchModel search = new SearchModel();
		List<SearchColumn> cols = search.getSearchColumns();
		cols.add(new SearchColumn("所在分组", "VC_GROUP", "sqlDict:select distinct vc_group,vc_group vc_name from "+tblPre+"T_MENU_LIB order by vc_group", 
						120, null, "=", "and t.VC_GROUP=${VC_GROUP} "));
		cols.get(cols.size()-1).setVcEditorJs("$('#search_VC_GROUP').change(function(){doSearch();});");
		cols.add(new SearchColumn("菜单名", "VC_NAME", "text", 120, null, "like", "and t.vc_name like '%'||${VC_NAME}||'%' "));
		cols.add(new SearchColumn("菜单URL", "VC_URL", "text", 120, null, "like", "and t.VC_URL like '%'||${VC_URL}||'%' "));
		search.setSearchSelectSql("select t.* from "+tblPre+"T_MENU_LIB t ");
		search.setSearchOrderBySql("VC_GROUP,VC_NAME");
		List<SearchResultColumn> list = search.getSearchResultColumns();
		search.getSearchResultColumns().add(new SearchResultColumn("所在分组", "VC_GROUP", "text", 85, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("菜单名", "VC_NAME", "text", 145, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("版本", "VC_VERSION", "text", 95, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("菜单URL", "VC_URL", "text", 215, null, "asc"));
		list.get(list.size()-1).setAlign("left");
		search.getSearchResultColumns().add(new SearchResultColumn("功能说明及更改历史", "VC_NOTE", "text", 245, null, null));
		list.get(list.size()-1).setAlign("left");
		search.getSearchResultColumns().add(new SearchResultColumn("添加人", "VC_ADD", "text", 60, null, null));
		search.getSearchResultColumns().add(new SearchResultColumn("添加时间", "DT_ADD", "text", 120, "yyyy-MM-dd HH:mm", "desc"));
		search.getSearchResultColumns().add(new SearchResultColumn("修改人", "VC_MODIFY", "text", 60, null, null));
		search.getSearchResultColumns().add(new SearchResultColumn("修改时间", "DT_MODIFY", "text", 120, "yyyy-MM-dd HH:mm", "desc"));
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

	@Override
	protected void initModel(FastModel model) {
		model.getModelStructure().setActionName(MenuAction.class.getName());
		model.getModelStructure().setForward("openAddPage", "/jwfm/core/menuEdit.jsp");
		model.getModelStructure().setForward("openModifyPage", "/jwfm/core/menuEdit.jsp");
		ArrayList<FastPo> dictData = new ArrayList<FastPo>();
		String dictTbl = SystemContext.dbObjectPrefix+"T_DICT";
		dictData.add(FastPo.getPo(dictTbl).element("VC_CODE", "SYSMENU_BASE_PACKAGE").element("VC_TEXT", "com.dx.jwfm.framework.web").element("VC_GROUP", "SYS_REGEDIT").element("VC_NOTE", "新建菜单时基础包路径").element("N_SEQ", "0"));
		dictData.add(FastPo.getPo(dictTbl).element("VC_CODE", "SYSMENU_PRESET_TBLCOLS_ARY").element("VC_TEXT", "").element("VC_GROUP", "SYS_REGEDIT").element("VC_NOTE", "新建菜单时按钮组选择项").element("N_SEQ", "0"));
		model.getModelStructure().setDictData(dictData);
	}


}

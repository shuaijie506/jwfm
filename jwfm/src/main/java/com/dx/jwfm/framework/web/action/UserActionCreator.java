package com.dx.jwfm.framework.web.action;

import java.util.ArrayList;
import java.util.List;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.annotations.FastModelInfo;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastColumnType;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.model.ButtonAuth;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.core.model.search.SearchResultColumn;
import com.dx.jwfm.framework.core.model.view.DictNode;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.exception.ValidateException;

@FastModelInfo(group = "Fast开发平台", name = "用户管理", url = "/jwfm/core/user", author="宋帅杰", devDate = "2015-12-03", updateInfo = "")
public class UserActionCreator extends ActionCreator {

	@Override
	protected String addItem() throws ValidateException {
		if(po.getString("VC_PWD").length()<32){
			po.put("VC_PWD", FastUtil.toMd5String(FastUtil.toMd5String(po.getString("VC_PWD"))));
		}
		return super.addItem();
	}

	@Override
	protected String openModifyPage() {
		String res = super.openModifyPage();
		po.remove("VC_PWD");
		return res;
	}

	@Override
	protected String modifyItem() throws ValidateException {
		if(FastUtil.isNotBlank(po.getString("VC_PWD")) && po.getString("VC_PWD").length()<32){
			po.put("VC_PWD", FastUtil.toMd5String(FastUtil.toMd5String(po.getString("VC_PWD"))));
		}
		else{
			po.remove("VC_PWD");
		}
		return super.modifyItem();
	}

	@Override
	protected FastTable getMainTable() {
		FastTable tbl = new FastTable();
		tbl.setTitle("Fast开发用户表");
		tbl.setName(SystemContext.dbObjectPrefix+"T_USER");
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
		cols.add(new SearchColumn("级别", "N_LEVEL", "select:dict:"+lvlDictName, null, "=", "and t.N_LEVEL=${N_LEVEL} "));
		cols.get(cols.size()-1).setVcEditorJs("$('#search_N_LEVEL').change(function(){doSearch();});");
		cols.add(new SearchColumn("用户名", "VC_NAME", "text", null, "like", "t.vc_name"));
		cols.add(new SearchColumn("创建时间", "DT_ADD", "date:yyyy-MM-dd", null, "dateRange", "t.dt_add"));
		search.setSearchSelectSql("select t.* from "+SystemContext.dbObjectPrefix+"T_USER t ");
		search.setSearchOrderBySql("n_level,dt_add desc");
		search.getSearchResultColumns().add(new SearchResultColumn("姓名", "VC_NAME", 85, null, "true"));
		search.getSearchResultColumns().add(new SearchResultColumn("权限级别", "N_LEVEL", 75, "dict:"+lvlDictName, "true"));
		search.getSearchResultColumns().add(new SearchResultColumn("创建时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", "true"));
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

	private String lvlDictName = "框架-用户级别";
	@Override
	protected void initModel(FastModel model) {
		model.getModelStructure().setActionName(this.getClass().getName());
		
		ArrayList<DictNode> dictData = new ArrayList<DictNode>();
		dictData.add(new DictNode(lvlDictName,"0", "管理员", 0));
		dictData.add(new DictNode(lvlDictName,"1", "开发人员", 1));
		dictData.add(new DictNode(lvlDictName,"2", "用户维护人员", 2));
		dictData.add(new DictNode(lvlDictName,"9", "作废", 3));
		model.getModelStructure().setDictData(dictData);
		model.getModelStructure().setPageHTML("edit", "编辑页面", getSimpleEditTableHtml("VC_ID".split(","), "VC_NAME,VC_PWD,N_LEVEL,DT_ADD".split(","),2));
	}


}

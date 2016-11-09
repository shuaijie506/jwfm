package com.dx.jwfm.framework.web.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastColumnType;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.FastModelStructure;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.core.model.search.SearchModel;

public class MainActionCreator {

	static FastModel model;
	public static FastModel getModel(){
		if(model!=null){
			return model;
		}
		model = new FastModel();
		model.version = "2015-12-03";
		model.setVcId("fast-main");
		model.setVcGroup("Fast开发平台");
		model.setVcName("Fast开发平台");
		model.setVcUrl("/jwfm/main/main.action");
		model.setVcAuth("");
		model.setVcAdd("宋帅杰");
		model.setVcModify("宋帅杰");
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2014-12-18 11:44");
			model.setDtAdd(d);
			model.setDtModify(d);
		} catch (ParseException e) {
		}
		FastModelStructure struct = new FastModelStructure();
		model.setModelStructure(struct);
		struct.setName(model.getVcName());
		struct.setMainTableName(SystemContext.dbObjectPrefix+"T_MAIN");
		struct.setVcUri(model.getVcUrl());
		struct.setVcGroup(model.getVcGroup());
		struct.setPackageName("fast.main");
		struct.setActionName("com.dx.jwfm.framework.web.action.FastMainAction");
		struct.setActionHandleName(null);
		struct.setDefaultSearchData(true);
		struct.setActionDefaultValueParser(null);
		struct.getForwards().put("success", "/jwfm/main/main.jsp");
		struct.getForwards().put("login", 	"/jwfm/main/login.jsp");
		struct.getForwards().put("loginsuccess","redirect:"+model.getVcUrl().substring(model.getVcUrl().lastIndexOf("/")+1));
		struct.setMainTable(getMainTable());
		struct.getOtherTables().add(getFastDictTable());
		struct.getOtherTables().add(getFastDbUpdateTable());
		struct.setSearch(getSearchModel());
		return model;
	}
	
	/**
	 * //初始化框架中的所有功能菜单
	 * @param flist
	 * @param menuMap
	 */
	public static void initFrameworkModel(ArrayList<FastModel> flist,HashMap<String,FastModel> menuMap){
		ArrayList<FastModel> list = new ArrayList<FastModel>();
		//先注册两个框架基础表
		list.add(getModel());
		list.add(new UserActionCreator().getFastModel());
		for(FastModel model:list){
			flist.add(model);
			model.init();
			menuMap.put(SystemContext.getPath()+model.getVcUrl(), model);
		}
	}

	private static FastTable getMainTable() {
		FastTable tbl = new FastTable();
		tbl.setName("Fast菜单库");
		tbl.setCode(SystemContext.dbObjectPrefix+"T_MENU_LIB");
		tbl.getColumns().add(new FastColumn("主键", "VC_ID", null, FastColumnType.String, 50, "uuid", null, false, true));
		tbl.getColumns().add(new FastColumn("菜单名", "VC_NAME", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("菜单URL", "VC_URL", null, FastColumnType.String, 200, "", null, false, false));
		tbl.getColumns().add(new FastColumn("按钮权限列表", "VC_AUTH", null, FastColumnType.String, -1, "", null, true, false));
		tbl.getColumns().add(new FastColumn("模块控制结构", "VC_STRUCTURE", null, FastColumnType.String, -1, "", null, false, false));
		tbl.getColumns().add(new FastColumn("所在分组", "VC_GROUP", null, FastColumnType.String, 200, "", null, true, false));
		tbl.getColumns().add(new FastColumn("版本", "VC_VERSION", null, FastColumnType.String, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("添加人", "VC_ADD", null, FastColumnType.String, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("添加时间", "DT_ADD", null, FastColumnType.Date, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("修改人", "VC_MODIFY", null, FastColumnType.String, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("修改时间", "DT_MODIFY", null, FastColumnType.Date, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("功能说明及更改历史", "VC_NOTE", null, FastColumnType.String, -1, "", null, true, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}

	private static FastTable getFastDictTable() {
		FastTable tbl = new FastTable();
		tbl.setName("Fast字典表");
		tbl.setCode(SystemContext.dbObjectPrefix+"T_DICT");
		tbl.getColumns().add(new FastColumn("主键", "VC_ID", null, FastColumnType.String, 50, "uuid", null, false, true));
		tbl.getColumns().add(new FastColumn("分组", "VC_GROUP", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("编码", "VC_CODE", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("显示文本", "VC_TEXT", "", FastColumnType.String, 500, "", null, false, false));
		tbl.getColumns().add(new FastColumn("备注", "VC_NOTE", "", FastColumnType.String, 500, "", null, true, false));
		tbl.getColumns().add(new FastColumn("排序", "N_SEQ", null, FastColumnType.Integer, 0, "", null, false, false));
		tbl.getColumns().add(new FastColumn("删除标记", "N_DEL", null, FastColumnType.Integer, 0, "0", null, false, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}
	private static FastTable getFastDbUpdateTable() {
		FastTable tbl = new FastTable();
		tbl.setName("Fast字典表");
		tbl.setCode(SystemContext.dbObjectPrefix+"T_DB_UPDATE");
		tbl.getColumns().add(new FastColumn("主键", "VC_ID", null, FastColumnType.String, 50, "uuid", null, false, true));
		tbl.getColumns().add(new FastColumn("文件路径", "VC_PATH", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("文件名", "VC_FILE", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("执行时间", "DT_EXEC", null, FastColumnType.Date, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("执行结果", "VC_RESULT", null, FastColumnType.String, -1, "", null, false, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}

	private static SearchModel getSearchModel() {
		SearchModel search = new SearchModel();
		search.getSearchColumns().add(new SearchColumn("分组", "VC_GROUP", "sqlSelect", 120, null, "=", "and t.vc_group =||${VC_GROUP} "));
		search.getSearchColumns().add(new SearchColumn("菜单名", "VC_GROUP", "sqlSelect", 120, null, "like", "and t.vc_name like '%'||${VC_NAME}||'%' "));
		return search;
	}


}

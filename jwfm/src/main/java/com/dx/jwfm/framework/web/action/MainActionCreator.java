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
import com.dx.jwfm.framework.core.model.view.DictNode;
import com.dx.jwfm.framework.util.FastUtil;

public class MainActionCreator {

	static FastModel model;
	public static FastModel getModel(){
		if(model!=null){
			return model;
		}
		model = new FastModel();
		model.vcVersion = "2017-01-09";
		model.setVcId("fast-main");
		model.setVcGroup("Fast开发平台");
		model.setVcName("Fast开发平台");
		model.setVcUrl("/jwfm/core/main");
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
		FastUtil.copyBeanPropts(struct, model);
		model.setModelStructure(struct);
		struct.setMainTableName(SystemContext.dbObjectPrefix+"T_MAIN");
		struct.setPackageName("fast.main");
		struct.setActionName("com.dx.jwfm.framework.web.action.FastMainAction");
//		struct.setActionHandleName(null);
		struct.setDefaultSearchData(true);
//		struct.setActionDefaultValueParser(null);
		struct.setForward("success", "/jwfm/main/main.jsp");
		struct.setForward("login", 	"/jwfm/main/login.jsp");
		struct.setForward("loginsuccess","redirect:"+model.getVcUrl().substring(model.getVcUrl().lastIndexOf("/")+1));
		struct.setMainTable(getMainTable());
		struct.getOtherTables().add(getFastDictTable());
		struct.getOtherTables().add(getSessionInfoTable());
		struct.setSearch(getSearchModel());
		struct.getDictData().add(new DictNode("SYS_REGEDIT", "SYS_SSOID_REMOTE_URL", "", "如果需要使用远程URL进行SSO_ID到USER_ID的转换时，请配置此地址。如：http://10.0.0.1/mis/check.jsp?ssoid=", 0));
		return model;
	}
	
	/**
	 * //初始化框架中的所有功能菜单
	 * @param flist
	 * @param menuMap
	 * @param actionExt 
	 */
	public static void initFrameworkModel(ArrayList<FastModel> flist,HashMap<String,FastModel> menuMap, String actionExt){
		ArrayList<FastModel> list = new ArrayList<FastModel>();
		//先注册两个框架基础表
		list.add(getModel());
		list.add(new UserActionCreator().getFastModel());
		list.add(new MenuActionCreator().getFastModel());
		list.add(new ToolsAction().getFastModel());
		list.add(new FileUploadAction().getFastModel());
		list.add(new UserFileUploadAction().getFastModel());
		for(FastModel model:list){
			String key = SystemContext.getPath()+model.getVcUrl()+actionExt;
			FastModel old = menuMap.get(key);
			if(old!=null && old.getVcVersion().equals(model.getVcVersion())){
				continue;
			}
			menuMap.put(key , model);
			flist.add(model);
		}
	}

	public static FastTable getMainTable() {
		return new MenuActionCreator().getMainTable();
	}

	private static FastTable getFastDictTable() {
		FastTable tbl = new FastTable();
		tbl.setTitle("Fast字典表");
		tbl.setName(SystemContext.dbObjectPrefix+"T_DICT");
		tbl.getColumns().add(new FastColumn("主键", "VC_ID", null, FastColumnType.String, 50, null, null, false, true));
		tbl.getColumns().add(new FastColumn("用户ID", "VC_USER_ID", "用户ID，此字段值为sys时表示为系统参数和字典", FastColumnType.String, 50, "sys", null, false, false));
		tbl.getColumns().add(new FastColumn("分组", "VC_GROUP", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("编码", "VC_CODE", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("显示文本", "VC_TEXT", "", FastColumnType.String, 500, "", null, true, false));
		tbl.getColumns().add(new FastColumn("备注", "VC_NOTE", "", FastColumnType.String, 500, "", null, true, false));
		tbl.getColumns().add(new FastColumn("排序", "N_SEQ", null, FastColumnType.Integer, 0, "", null, false, false));
		tbl.getColumns().add(new FastColumn("删除标记", "N_DEL", null, FastColumnType.Integer, 0, "0", null, false, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}
	
	private static FastTable getSessionInfoTable() {
		FastTable tbl = new FastTable();
		tbl.setTitle("Fast字典表");
		tbl.setName(SystemContext.dbObjectPrefix+"T_SSO_INFO");
		tbl.getColumns().add(new FastColumn("主键", "SSO_ID", null, FastColumnType.String, 50, null, null, false, true));
		tbl.getColumns().add(new FastColumn("用户ID", "VC_USER_ID", "用户ID，此字段值为sys时表示为系统参数和字典", FastColumnType.String, 50, "sys", null, false, false));
		tbl.getColumns().add(new FastColumn("创建时间", "DT_ADD", null, FastColumnType.Date, 0, "${nowTime}", null, false, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}

	private static SearchModel getSearchModel() {
		SearchModel search = new SearchModel();
		search.getSearchColumns().add(new SearchColumn("分组", "VC_GROUP", "sqlSelect", null, "=", "and t.vc_group =${VC_GROUP} "));
		search.getSearchColumns().add(new SearchColumn("菜单名", "VC_GROUP", "sqlSelect", null, "like", "t.vc_name"));
		return search;
	}


}

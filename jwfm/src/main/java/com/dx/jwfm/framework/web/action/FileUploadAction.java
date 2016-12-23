package com.dx.jwfm.framework.web.action;

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

@FastModelInfo(group = "Fast开发平台", name = "文件上传下载", url = "/jwfm/fileUpload", author="宋帅杰", devDate = "2015-12-19", updateInfo = "")
public class FileUploadAction extends ActionCreator {

	private String tblPre = SystemContext.dbObjectPrefix;
	@Override
	protected FastTable getMainTable() {
		FastTable tbl = new FastTable();
		tbl.setTitle("文件上传下载记录表");
		tbl.setName(tblPre+"T_FILE_UPLOAD");
		tbl.getColumns().add(new FastColumn("主键", "VC_ID", null, FastColumnType.String, 50, null, null, false, true));
		tbl.getColumns().add(new FastColumn("文件名称", "VC_NAME", null, FastColumnType.String, 500, "", null, false, false));
		tbl.getColumns().add(new FastColumn("所属模块", "VC_MOUDLE", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("引用次数", "N_QUOTE", null, FastColumnType.Integer, 0, "", null, true, false));
		tbl.getColumns().add(new FastColumn("引用表名及字段", "VC_QUOTE_INFO", null, FastColumnType.String, 4000, "", null, true, false));
		tbl.getColumns().add(new FastColumn("文件大小", "N_BYTES", null, FastColumnType.Long, 0, "", null, true, false));
		tbl.getColumns().add(new FastColumn("上传时间", "DT_UPLOAD", null, FastColumnType.Date, 8, "", null, true, false));
		tbl.getColumns().add(new FastColumn("上传人ID", "VC_USER_ID", null, FastColumnType.String, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("上传人姓名", "VC_USER_NAME", null, FastColumnType.String, 200, "", null, true, false));
		tbl.getColumns().add(new FastColumn("下载次数", "N_DOWNLOAD", null, FastColumnType.Integer, 0, "", null, true, false));
		tbl.getColumns().add(new FastColumn("最近下载时间", "DT_DOWNLOAD", null, FastColumnType.Date, 8, "", null, true, false));
		tbl.getColumns().add(new FastColumn("服务器标志", "VC_SERVER", null, FastColumnType.String, 50, "", null, true, false));
		tbl.getColumns().add(new FastColumn("备注", "VC_NOTE", null, FastColumnType.String, 4000, "", null, true, false));
		tbl.getColumns().add(new FastColumn("删除标记", "N_DEL", null, FastColumnType.Integer, 0, "0", null, false, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}

	@Override
	protected SearchModel getSearchModel() {
		SearchModel search = new SearchModel();
		search.setSearchSelectSql("select t.* from "+tblPre+"T_FILE_UPLOAD t ");
		search.setSearchOrderBySql("VC_GROUP,VC_NAME");
		List<SearchColumn> cols = search.getSearchColumns();
		cols.add(new SearchColumn("所属模块", "VC_MOUDLE", "text",  null, "like", "t.VC_MOUDLE"));
		cols.add(new SearchColumn("文件名称", "VC_NAME", "text", null, "like", "and t.vc_name like '%'||${VC_NAME}||'%' "));
		cols.add(new SearchColumn("文件大小", "N_BYTES", "${}", null, "like", "and t.VC_URL like '%'||${VC_URL}||'%' "));
		List<SearchResultColumn> list = search.getSearchResultColumns();
		search.getSearchResultColumns().add(new SearchResultColumn("所在分组", "VC_GROUP", 85, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("菜单名", "VC_NAME", 145, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("版本", "VC_VERSION", 95, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("菜单URL", "VC_URL", 215, null, "asc"));
		list.get(list.size()-1).setAlign("left");
		search.getSearchResultColumns().add(new SearchResultColumn("功能说明及更改历史", "VC_NOTE", 245, null, null));
		list.get(list.size()-1).setAlign("left");
		search.getSearchResultColumns().add(new SearchResultColumn("添加人", "VC_ADD", 60, null, null));
		search.getSearchResultColumns().add(new SearchResultColumn("添加时间", "DT_ADD", 120, "yyyy-MM-dd HH:mm", "desc"));
		search.getSearchResultColumns().add(new SearchResultColumn("修改人", "VC_MODIFY", 60, null, null));
		search.getSearchResultColumns().add(new SearchResultColumn("修改时间", "DT_MODIFY", 120, "yyyy-MM-dd HH:mm", "desc"));
		return search;
	}

	@Override
	protected List<ButtonAuth> getButtonList() {
		// TODO Auto-generated method stub
		return super.getButtonList();
	}

	@Override
	protected void initModel(FastModel model) {
		// TODO Auto-generated method stub
		super.initModel(model);
	}

}

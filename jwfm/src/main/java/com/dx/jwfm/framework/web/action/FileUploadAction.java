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
import com.dx.jwfm.framework.util.NetFileUtil;
import com.dx.jwfm.framework.web.search.FileUploadSearch;

@FastModelInfo(group = "Fast开发平台", name = "文件上传下载", url = "/jwfm/fileUpload", author="宋帅杰", devDate = "2015-12-19", updateInfo = "")
public class FileUploadAction extends ActionCreator {
	
	private String tblPre = SystemContext.dbObjectPrefix;
	private String fileSizeDictName = "框架-文件大小选项";
	private String remoteDictName = "框架-远程文件下载地址";
	
	protected void initModel(FastModel model){
		model.getModelStructure().setActionName(FileUploadAction.class.getName());
		model.getModelStructure().setSearchClassName(FileUploadSearch.class.getName());
		model.getModelStructure().getSearch().setHeadHTML("<script>function downItems(){}</script>");
		ArrayList<DictNode> dictData = new ArrayList<DictNode>();
		dictData.add(new DictNode(fileSizeDictName,"0,"+(1024*1024), "小于1M", 0));
		dictData.add(new DictNode(fileSizeDictName,(1024*1024)+",", "大于1M", 1));
		dictData.add(new DictNode(fileSizeDictName,(10*1024*1024)+",", "大于10M", 2));
		dictData.add(new DictNode(fileSizeDictName,(100*1024*1024)+",", "大于100M", 2));
		dictData.add(new DictNode(fileSizeDictName,(1024*1024*1024)+",", "大于1G", 2));
		dictData.add(new DictNode(remoteDictName,"DEMO_ID", "http://192.168.0.1/jwfm/uploadfile/", "值填写服务器ID，文本中填写文件下载的URL地址前缀", 2));
		model.getModelStructure().setDictData(dictData);
	}
	
	protected FastTable getMainTable() {
		FastTable tbl = new FastTable();
		tbl.setTitle("文件上传下载记录表");
		tbl.setName(tblPre+"T_FILE_UPLOAD");
		tbl.getColumns().add(new FastColumn("主键", "VC_ID", null, FastColumnType.String, 50, null, null, false, true));
		tbl.getColumns().add(new FastColumn("文件名称", "VC_NAME", null, FastColumnType.String, 500, "", null, false, false));
		tbl.getColumns().add(new FastColumn("所属模块", "VC_MODULE", null, FastColumnType.String, 100, "", null, false, false));
		tbl.getColumns().add(new FastColumn("引用次数", "N_QUOTE", null, FastColumnType.Integer, 0, "0", null, true, false));
		tbl.getColumns().add(new FastColumn("引用表名及字段", "VC_QUOTE_INFO", null, FastColumnType.String, 4000, "", null, true, false));
		tbl.getColumns().add(new FastColumn("文件大小", "N_BYTES", null, FastColumnType.Long, 0, "0", null, true, false));
		tbl.getColumns().add(new FastColumn("文件存放位置", "VC_PATH", null, FastColumnType.String, 500, "", null, false, false));
		tbl.getColumns().add(new FastColumn("MD5特征码", "VC_MD5", null, FastColumnType.String, 50, "", null, false, false));
		tbl.getColumns().add(new FastColumn("上传时间", "DT_UPLOAD", null, FastColumnType.Date, 8, "${nowTime}", null, true, false));
		tbl.getColumns().add(new FastColumn("上传人ID", "VC_USER_ID", null, FastColumnType.String, 50, "${curUserId}", null, true, false));
		tbl.getColumns().add(new FastColumn("上传人姓名", "VC_USER_NAME", null, FastColumnType.String, 200, "${curUserName}", null, true, false));
		tbl.getColumns().add(new FastColumn("下载次数", "N_DOWNLOAD", null, FastColumnType.Integer, 0, "0", "0", true, false));
		tbl.getColumns().add(new FastColumn("最近下载时间", "DT_DOWNLOAD", null, FastColumnType.Date, 8, "", null, true, false));
		tbl.getColumns().add(new FastColumn("服务器标志", "VC_SERVER", null, FastColumnType.String, 50, "${serverId}", null, true, false));
		tbl.getColumns().add(new FastColumn("备注", "VC_NOTE", null, FastColumnType.String, 4000, "", null, true, false));
		tbl.getColumns().add(new FastColumn("删除标记", "N_DEL", null, FastColumnType.Integer, 0, "0", null, false, false));
		tbl.setColumns(tbl.getColumns());
		return tbl;
	}

	protected SearchModel getSearchModel() {
		SearchModel search = new SearchModel();
		search.setSearchSelectSql("select t.* from "+tblPre+"T_FILE_UPLOAD t ");
		search.setSearchOrderBySql("DT_UPLOAD desc,VC_NAME");
		List<SearchColumn> cols = search.getSearchColumns();
		cols.add(new SearchColumn("所属模块", "VC_MODULE", "text",  null, "like", "t.VC_MODULE"));
		cols.add(new SearchColumn("文件名称", "VC_NAME", "text", null, "like", "t.vc_name"));
		cols.add(new SearchColumn("上传人", "VC_USER_NAME", "text", null, "like", "t.vc_user_name"));
		cols.add(new SearchColumn("文件大小", "N_BYTES", "select:dict:"+fileSizeDictName, null, "<=", "and t.n_bytes>=${N_BYTES} "));
		cols.add(new SearchColumn("上传时间", "DT_UPLOAD", "date:yyyy-MM-dd", "", "dateRange", "t.DT_UPLOAD"));
		search.getSearchResultColumns().add(new SearchResultColumn("文件名", "VC_NAME", 145, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("文件大小", "N_BYTES:filesize", 65, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("所属模块", "VC_MODULE", 85, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("下载次数", "N_DOWNLOAD", 65, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("最近下载时间", "DT_DOWNLOAD", 100, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("上传人", "VC_USER_NAME", 50, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("上传时间", "DT_UPLOAD", 100, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("文件存放位置", "VC_PATH", 135, null, ""));
		search.getSearchResultColumns().add(new SearchResultColumn("MD5特征码", "VC_MD5", 85, null, ""));
		return search;
	}

	protected List<ButtonAuth> getButtonList() {
		List<ButtonAuth> buttonAuths = new ArrayList<ButtonAuth>();
		buttonAuths.add(new ButtonAuth("打包下载", "downItems", "downItems", "downItemsBtn", "icon-down", "打包下载"));
		buttonAuths.add(new ButtonAuth("删除文件", "del", "delItem", "delItemBtn", "icon-remove", "删除按钮"));
		return buttonAuths;
	}

	@Override
	public String delete() {
		String[] ids = getParameterValues("chkSelf");
		if(ids==null){
			ids = getParameterValues("chkSelf[]");
		}
		String res = super.delete();
		NetFileUtil.deleteFilePhysical(FastUtil.join(ids,","));
		return res;
	}
	
	

}

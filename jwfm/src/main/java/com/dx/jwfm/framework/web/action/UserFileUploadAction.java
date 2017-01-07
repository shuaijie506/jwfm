package com.dx.jwfm.framework.web.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.List;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.annotations.FastModelInfo;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.core.model.search.SearchResultColumn;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.util.NetFileUtil;
import com.dx.jwfm.framework.web.search.FileUploadSearch;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@FastModelInfo(group = "Fast开发平台", name = "文件上传历史记录", url = "/jwfm/userFileUpload", author="宋帅杰", devDate = "2015-12-19", updateInfo = "")
public class UserFileUploadAction extends ActionCreator {
	
	private String tblPre = SystemContext.dbObjectPrefix;
	private String fileSizeDictName = "框架-文件大小选项";
	
	protected void initModel(FastModel model){
		model.getModelStructure().setActionName(UserFileUploadAction.class.getName());
		model.getModelStructure().setSearchClassName(FileUploadSearch.class.getName());
	}
	
	protected FastTable getMainTable() {
		return new FileUploadAction().getMainTable();
	}

	protected SearchModel getSearchModel() {
		SearchModel search = new SearchModel();
		search.setSearchSelectSql("select t.* from "+tblPre+"T_FILE_UPLOAD t ");
		search.setSearchOrderBySql("DT_UPLOAD desc,VC_NAME");
		List<SearchColumn> cols = search.getSearchColumns();
		cols.add(new SearchColumn("所属模块", "VC_MODULE", "text",  null, "like", "t.VC_MODULE"));
		cols.add(new SearchColumn("文件名称", "VC_NAME", "text", null, "like", "t.vc_name"));
		cols.add(new SearchColumn("上传人", "VC_USER_ID", "text", "${curUserId}", "like", "t.vc_user_name"));
		cols.add(new SearchColumn("文件大小", "N_BYTES", "select:dict:"+fileSizeDictName, null, "<=", "and t.n_bytes>=${N_BYTES} "));
		cols.add(new SearchColumn("上传时间", "DT_UPLOAD", "date:yyyy-MM-dd", "", "dateRange", "t.DT_UPLOAD"));
		search.getSearchResultColumns().add(new SearchResultColumn("文件名", "VC_NAME", 145, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("文件大小", "N_BYTES:filesize", 65, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("所属模块", "VC_MODULE", 85, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("下载次数", "N_DOWNLOAD", 65, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("最近下载时间", "DT_DOWNLOAD", 95, null, "asc"));
		search.getSearchResultColumns().add(new SearchResultColumn("上传人", "VC_USER_NAME", 50, null, "asc"));
		return search;
	}

	private File file;
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2017年1月6日 下午5:24:20
	 * 功能描述: 上传文件到服务器
	 * 方法的参数和返回值: 
	 * @return
	 */
	public String uploadFile(){
		String vcModule = getParameter("fileType");//文件归属模块类别
		String fileName = getParameter("fileName");
		String fileSize = getParameter("fileSize");
		String lastModify = getParameter("lastModify");
		String start = getParameter("start");
		String md5 = FastUtil.toMd5String(getCookie("SSO_ID")+fileSize+fileName+lastModify);
		String basePath = System.getProperty("java.io.tmpdir");
		File f = new File(basePath,"tmpsplitfile-"+md5);
		JSONObject obj = new JSONObject();
		if(file!=null){//如果上传文件不为空，则执行文件写入操作
			try {
				long fileLen = Long.parseLong(fileSize);
				long startPos = Long.parseLong(start);
				RandomAccessFile out = new RandomAccessFile(f,"rw");//随机读写
				out.seek(startPos);//文件指针移动到指定位置
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
				byte[] buff = new byte[8192];
				int len = 0;
				while((len=in.read(buff))>=0){//合并文件
					out.write(buff,0,len);
				}
				in.close();
				out.close();
				obj.put("success", true);
				obj.put("length", f.length());
				String fileId = null;
				if(f.length()>=fileLen){//存入文件池
					po = FastPo.getPo(SystemContext.dbObjectPrefix+"T_FILE_UPLOAD");
					po.element("VC_NAME", fileName).element("VC_MODULE", vcModule);
					fileId = NetFileUtil.saveFile(f, po);
					f.delete();
				}
				obj.put("fileId", fileId);
			} catch (IOException e) {
				obj.put("success", false);
				obj.put("info", "文件写入失败:"+e.getMessage());
				logger.error(e.getMessage(),e);
			} catch (Exception e) {
				obj.put("success", false);
				obj.put("info", "上传过程中发生错误:"+e.getMessage());
				logger.error(e.getMessage(),e);
			}
		}
		else{//否则返回文件已上传字节数
			obj.put("success", true);
			obj.put("length", f.exists()?f.length():0);
		}
		return writeHTML(obj.toString());
	}
	
	public String loadFile(){
		String vcId = getParameter("vcId");
		NetFileUtil.downloadFile(vcId);
		return null;
	}
	
	public String loadFileInfo(){
		String ids = getParameter("ids");
		JSONArray ary = new JSONArray();
		try {
			DbHelper db = new DbHelper();
			List<FastPo> list = NetFileUtil.searchFilePo(ids);
			String sql = "update "+SystemContext.dbObjectPrefix+"T_FILE_UPLOAD set n_del=0 where vc_id in ('"+ids.replaceAll(",", "','")+"')";
			db.executeSqlUpdate(sql);//将文件删除标记更新为0
			ary.addAll(list);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		return writeHTML(ary.toString());
	}
	
	public String delFiles(){
		String ids = getParameter("delIds");
		try {
			DbHelper db = new DbHelper();
			List<FastPo> list = NetFileUtil.searchFilePo(ids);
			for(FastPo po:list){
				po.setPropt("N_DEL", 1);
			}
			db.updatePo(list);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			return writeHTML("error:"+FastUtil.getExceptionInfo(e));
		}
		return writeHTML("ok");
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}

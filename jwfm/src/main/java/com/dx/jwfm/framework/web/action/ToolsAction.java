package com.dx.jwfm.framework.web.action;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.annotations.FastModelInfo;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.model.ButtonAuth;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.core.parser.MacroValueNode;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.view.Node;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

@FastModelInfo(group = "Fast开发平台", name = "工具Action", url = "/jwfm/tools", author="宋帅杰", devDate = "2015-12-03", updateInfo = "")
public class ToolsAction extends ActionCreator {
	
	Logger logger = Logger.getLogger(ToolsAction.class);

	protected void initModel(FastModel model){
		model.getModelStructure().setActionName(ToolsAction.class.getName());
	}
	
	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年11月18日 上午8:39:40
	 * 功能描述: 加载下拉框数据，可使用参数 dict和sql，分别从系统字典中加载和执行SQL语句加载
	 * 方法的参数和返回值: 
	 * @return
	 */
	public String comboData(){
		JSONArray ary = new JSONArray();
		String dict = getParameter("dict");
		String sql = getParameter("sql");
		Map<String, String> map = null;
		if(FastUtil.isNotBlank(dict)){
			map = FastUtil.getDictsMap(dict);
		}
		else if(FastUtil.isNotBlank(sql)){
			DbHelper db = new DbHelper();
			try {
				map = db.getMapSqlQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				map = new HashMap<String,String>();
				map.put("", "SQL查询出错！"+FastUtil.getExceptionInfo(e));
			}
		}
		if(map!=null){
			for(String key:map.keySet()){
				ary.add(new Node(key,map.get(key)));
			}
		}
		return writeHTML(ary.toString());
	}
	
	public String loadMacroListJson(){
		Collection<MacroValueNode> list = SystemContext.getAllMacros();
		JSONArray ary = new JSONArray();
		JsonConfig conf = new JsonConfig();
		conf.setExcludes(new String[]{"valueHandel"});
		ary.addAll(list,conf);
		return writeHTML(ary.toString());
	}
/*
	private File file;
	public String uploadSplitFile(){
		String fileType = getParameter("fileType");//文件归属模块类别
		String fileName = getParameter("fileName");
		String fileSize = getParameter("fileSize");
		String lastModify = getParameter("lastModify");
		String start = getParameter("start");
		String md5 = FastUtil.toMd5String(fileSize+fileName+lastModify);
		String basePath = System.getProperty("java.io.tmpdir");
		File f = new File(basePath,md5);
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
					fileId = NetFileUtil.saveFile(f, fileName, fileType);
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
	
	public String loadFileInfo(){
		String ids = getParameter("ids");
		String sql = "select vc_id,vc_filename,n_filesize from pub_t_file_upload_log where vc_id in ('"+ids.replaceAll(",", "','")+"')";
		List<HashMap<String, Object>> list = DbUtil.executeSqlQuery(sql, new ISQLMapper<HashMap<String,Object>>() {
			public HashMap<String, Object> fromSQLQuery(ResultSet rs, int num) {
				HashMap<String, Object> view = new HashMap<String, Object>();
				try {
					view.put("fileId", rs.getString("vc_id"));
					view.put("fileName", rs.getString("vc_filename"));
					view.put("fileSize", rs.getLong("n_filesize"));
				} catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
				return view;
			}
		});
		sql = "update pub_t_file_upload_log set vc_del$flag=0 where vc_id in ('"+ids.replaceAll(",", "','")+"')";
		DbUtil.executeSqlUpdate(sql);//将文件删除标记更新为0
		JSONArray ary = new JSONArray();
		ary.addAll(list);
		return writeHTML(ary.toString());
	}
*/
	@Override
	protected FastTable getMainTable() {
		return null;
	}

}

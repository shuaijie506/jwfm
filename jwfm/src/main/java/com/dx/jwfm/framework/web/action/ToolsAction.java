package com.dx.jwfm.framework.web.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
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
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.parser.MacroValueNode;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.util.NetFileUtil;
import com.dx.jwfm.framework.web.view.Node;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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

	static{
		new Thread(){
			public void run() {
				this.setName("clear tmp file thread");
				String basePath = System.getProperty("java.io.tmpdir");
				File[] fs = new File(basePath).listFiles();
				for(File f:fs){
					try {
						if(f.isFile() && f.getName().startsWith("tmpsplitfile-") && 
								System.currentTimeMillis()-f.lastModified()>3*24*60*60000){//最后修改时间超过3天的自动清除
							f.delete();
						}
					} catch (Exception e) {
						Logger.getLogger(ToolsAction.class).error(e.getMessage(),e);
					}
				}
				super.run();
			}
		}.start();
	}
	
	@Override
	protected FastTable getMainTable() {
		return null;
	}

}

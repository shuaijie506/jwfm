package com.dx.jwfm.framework.core.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.dao.model.FastDbObject;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.parser.IDefaultValueParser;
import com.dx.jwfm.framework.core.process.IActionHandel;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.util.Uuid;
import com.dx.jwfm.framework.web.action.FastBaseAction;

public class FastModel {

	static Logger logger = Logger.getLogger(FastModel.class);
	
	public String version;

	/**菜单ID*/
	private String vcId;
	/**菜单所在分组*/
	private String vcGroup;
	/**菜单名称*/
	private String vcName;
	/**菜单URL*/
	private String vcUrl;
	/**开发人员*/
	private String vcAuth;
	/** 菜单结构字符串{search:{},dictData:[],edittable:{},viewtable:{}}*/
	private String vcStructure;
	/**开发人姓名*/
	private String vcAdd;
	/**开发时间*/
	private Date dtAdd;
	/**最后修改人姓名*/
	private String vcModify;
	/**最后修改时间*/
	private Date dtModify;
	/**备注*/
	private String vcNote;
	/**控制器*/
	private Class<?> actionClass;
	
	private Class<IActionHandel> actionHandel;
	/**菜单按钮权限列表*/
	private List<ButtonAuth> buttonAuths;
	/**用户自定义默认值解析器*/
	private ArrayList<IDefaultValueParser> actionDefaultValueParser;
	/**模型结构对象*/
	private FastModelStructure modelStructure;

	/**
	 * 将结构化数据转换为字符串，以便于持久化到数据库
	 */
	public void doPersistent(){
		
	}
	
	/**
	 * 将字符串中的内容按格式转换为结构化对象，以便于程序调用
	 */
	public synchronized void undoPersistent(){
		
	}
	private boolean inited = false;
	/**
	 * 当此对象的modelStructure属性为空时，执行undoPersistent方法，否则什么不执行
	 * 方便系统启动后可以使用多线程同时加载模块配置，缩短系统启动时间
	 */
	public synchronized void init(){
		if(modelStructure==null){
			undoPersistent();
		}
		if(!inited){
			inited = true;
			if(modelStructure!=null){//将表注册到FastPo中
				new FastPo(modelStructure.getMainTable());
				for(FastTable ft:modelStructure.getOtherTables()){
					new FastPo(ft);
				}
			}
			//以下对数据库结构进行更新
			DbHelper db = new DbHelper();
			String version = null;
			try {
				if(isMenuTblExist(db.getDatabaseDialect())){
					version = db.getFirstStringSqlQuery("select vc_version from "+SystemContext.dbObjectPrefix+"T_MENU_LIB where vc_id=?",new Object[]{vcId});
				}
			} catch (SQLException e1) {
				logger.error(e1.getMessage(),e1);
			}
			if(modelStructure==null)return;//因反序列代码尚未开发，所以结构为空的不执行
			if(version==null || version.compareTo(this.version)<0){
				FastTable tbl = modelStructure.getMainTable();
				ArrayList<String> sqls = new ArrayList<String>();
				if(tbl!=null){
					new FastPo(tbl);//将表注册到FastPo中
					sqls.addAll(db.getDatabaseDialect().getTableCreateOrUpdateSql(tbl));
				}
				for(FastTable ft:modelStructure.getOtherTables()){
					new FastPo(ft);//将表注册到FastPo中
					sqls.addAll(db.getDatabaseDialect().getTableCreateOrUpdateSql(ft));
				}
				for(FastDbObject ft:modelStructure.getOtherDbObjects()){
					sqls.addAll(ft.getObjectUpdateSql(db.getDatabaseDialect()));
				}
				if(sqls.size()>0){
					for(String sql:sqls){
						try {
							db.executeSqlUpdate(sql);
						} catch (SQLException e) {
							logger.error(e.getMessage(),e);
						}
					}
				}
				updateDictData(db);
				updateSqlFile(db,modelStructure.getInitDataSqlList());
				try {
					if(!menuTblExist){
						menuTblExist = null;
					}
					if(isMenuTblExist(db.getDatabaseDialect())){
						FastPo po = FastPo.getPo(SystemContext.dbObjectPrefix+"T_MENU_LIB");
						if(version!=null){
							po = db.loadFastPo(po, vcId);
						}
						if(po==null){
							po = null;
						}
						po.put("VC_ID", vcId);
						po.put("VC_NAME", vcName);
						po.put("VC_URL", vcUrl);
						po.put("VC_AUTH", vcAuth);
						po.put("VC_STRUCTURE", getVcStructure());
						po.put("VC_GROUP", vcGroup);
						po.put("VC_VERSION", this.version);
						po.put("VC_ADD", vcAdd);
						po.put("DT_ADD", dtAdd);
						po.put("VC_MODIFY", vcModify);
						po.put("DT_MODIFY", dtModify);
						po.put("VC_NOTE", vcNote);
						if(version==null){
							db.addPo(po);
						}
						else{
							db.updatePo(po);
						}
					}
				} catch (SQLException e1) {
					logger.error(e1.getMessage(),e1);
				}
			}
		}
	}
	private void updateSqlFile(DbHelper db, List<String> list) {
		String sql = "select count(*) from "+SystemContext.dbObjectPrefix+"T_DB_UPDATE where vc_file=?";
		String insertsql = "insert "+SystemContext.dbObjectPrefix+"T_DB_UPDATE(vc_id,vc_path,vc_file,DT_EXEC,VC_RESULT) values(?,?,?,?,?)";
		for(String filepath:list){
			File f = new File(SystemContext.getAppPath(),filepath);
			if(!f.exists())continue;//如果文件不存在，则不处理
			String fname = f.getName();
			try {//使用文件名进行查询，同名的文件不会重复执行
				if(db.getFirstIntSqlQuery(sql,new Object[]{fname})==0){
					StringBuffer msg = new StringBuffer();
					List<String> sqls = readSqlFile(f);
					for(String tsql:sqls){
						msg.append(tsql).append("\n");
						try {
							db.executeSqlUpdate(tsql);
							msg.append("执行成功！");
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							msg.append("执行失败！错误信息：").append(e.getMessage());
						}
						msg.append("\n");
					}
					db.executeSqlUpdate(insertsql,new Object[]{Uuid.getUuid(),filepath,fname,new Date(),msg.toString()});
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	private List<String> readSqlFile(File f) {
		ArrayList<String> sqls = new ArrayList<String>();
		boolean multi = f.getName().toLowerCase().endsWith(".txt");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			String line = null;
			StringBuffer buff = new StringBuffer();
			while((line=in.readLine())!=null){
				String tline = line.trim();
				if(multi){//多语句组成的SQL对象执行
					if(tline.equals("/")){
						String sql = buff.toString();
						if(sql.trim().length()>0){
							sqls.add(sql);
						}
						buff.setLength(0);
						continue;
					}
				}
				else{//单SQL语句
					if(tline.endsWith(";")){
						buff.append(line.replaceAll(";\\s*", ""));
						String sql = buff.toString();
						if(sql.trim().length()>0){
							sqls.add(sql);
						}
						buff.setLength(0);
						continue;
					}
				}
				buff.append(line).append("\n");
			}
			String sql = buff.toString();
			if(sql.trim().length()>0){
				sqls.add(sql);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return sqls;
	}
	private static Boolean menuTblExist;
	private boolean isMenuTblExist(DatabaseDialect dialect){
		if(menuTblExist==null){
			try {
				menuTblExist = dialect.isTableExist(SystemContext.dbObjectPrefix+"T_MENU_LIB");
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
		return menuTblExist;
	}

	private void updateDictData(DbHelper db) {
		HashSet<String> dictGroup = new HashSet<String>();
		ArrayList<String> sqls = new ArrayList<String>();
		ArrayList<Object[]> params = new ArrayList<Object[]>();
		if(modelStructure.getDictData()!=null){
			for(FastPo dict:modelStructure.getDictData()){
				if("SYSTEM".equals(dict.getString("VC_GROUP"))){//系统注册项
					try {
						if(db.getFirstIntSqlQuery("select count(*) from "+SystemContext.dbObjectPrefix+"T_DICT where vc_group=? and vc_code=?",
								new Object[]{dict.getString("VC_GROUP"),dict.getString("VC_CODE")})>0){
							sqls.add("update "+SystemContext.dbObjectPrefix+"T_DICT set vc_text=? where vc_group=? and vc_code=?");
							params.add(new Object[]{dict.getString("VC_TEXT"),dict.getString("VC_GROUP"),dict.getString("VC_CODE")});
						}
						else{
							sqls.add(dict.getTblModel().getInsertSql());
							params.add(dict.getInsertParams());
						}
					} catch (SQLException e) {
						logger.error(e.getMessage(),e);
					}
				}
				if(!dictGroup.contains(dict.getString("VC_GROUP"))){
					dictGroup.add(dict.getString("VC_GROUP"));
					sqls.add("delete from "+SystemContext.dbObjectPrefix+"T_DICT where vc_group=?");
					params.add(new Object[]{dict.getString("VC_GROUP")});
				}
				dict.put("VC_ID", Uuid.getUuid());
				sqls.add(dict.getTblModel().getInsertSql());
				params.add(dict.getInsertParams());
			}
			try {
				db.executeSqlUpdate(sqls, params);
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public Class<?> getActionClass() {
		if(actionClass==null){
			try {
				actionClass = this.getClass().getClassLoader().loadClass(modelStructure.getActionName());
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(),e);
				actionClass = FastBaseAction.class;
			}
		}
		return actionClass;
	}

	public ArrayList<IDefaultValueParser> getActionDefaultValueParser() {
		if(actionDefaultValueParser==null){
			actionDefaultValueParser = new ArrayList<IDefaultValueParser>();
			String[] ary = FastUtil.nvl(modelStructure.getActionDefaultValueParser(),"").split(",");
			for(String str:ary){//初始化模块默认值解析部分
				if(str.trim().length()>0){
					try {
						actionDefaultValueParser.add((IDefaultValueParser) FastUtil.newInstance(str.trim()));
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
			List<IDefaultValueParser> list = RequestContext.getSystemDefaultValueParser();
			if(list!=null){//附加系统默认值解析部分
				for(int i=0;i<list.size();i++){
					actionDefaultValueParser.add(list.get(i));
				}
			}
		}
		return actionDefaultValueParser;
	}

	public void setActionClass(Class<?> actionClass) {
		this.actionClass = actionClass;
	}

	public Class<IActionHandel> getActionHandel() {
		return actionHandel;
	}

	public void setActionHandel(Class<IActionHandel> actionHandel) {
		this.actionHandel = actionHandel;
	}
	
	public String getVcId() {
		return vcId;
	}

	public void setVcId(String vcId) {
		this.vcId = vcId;
	}

	public String getVcGroup() {
		return vcGroup;
	}

	public void setVcGroup(String vcGroup) {
		this.vcGroup = vcGroup;
	}

	public String getVcName() {
		return vcName;
	}

	public void setVcName(String vcName) {
		this.vcName = vcName;
	}

	public String getVcUrl() {
		return vcUrl;
	}

	public void setVcUrl(String vcUrl) {
		this.vcUrl = vcUrl;
	}

	public String getVcAuth() {
		return vcAuth;
	}

	public void setVcAuth(String vcAuth) {
		this.vcAuth = vcAuth;
	}

	public String getVcStructure() {
		return vcStructure==null?" ":vcStructure;
	}

	public void setVcStructure(String vcStructure) {
		this.vcStructure = vcStructure;
	}

	public String getVcAdd() {
		return vcAdd;
	}

	public void setVcAdd(String vcAdd) {
		this.vcAdd = vcAdd;
	}

	public Date getDtAdd() {
		return dtAdd;
	}

	public void setDtAdd(Date dtAdd) {
		this.dtAdd = dtAdd;
	}

	public String getVcModify() {
		return vcModify;
	}

	public void setVcModify(String vcModify) {
		this.vcModify = vcModify;
	}

	public Date getDtModify() {
		return dtModify;
	}

	public void setDtModify(Date dtModify) {
		this.dtModify = dtModify;
	}

	public String getVcNote() {
		return vcNote;
	}

	public void setVcNote(String vcNote) {
		this.vcNote = vcNote;
	}

	public FastModelStructure getModelStructure() {
		return modelStructure;
	}

	public void setModelStructure(FastModelStructure modelStructure) {
		this.modelStructure = modelStructure;
	}

	public List<ButtonAuth> getButtonAuths() {
		return buttonAuths;
	}

	public void setButtonAuths(List<ButtonAuth> buttonAuths) {
		this.buttonAuths = buttonAuths;
	}

}

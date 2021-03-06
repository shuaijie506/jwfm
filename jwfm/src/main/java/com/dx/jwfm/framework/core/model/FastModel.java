package com.dx.jwfm.framework.core.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastDbObject;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.core.model.search.SearchResultColumn;
import com.dx.jwfm.framework.core.model.view.DictNode;
import com.dx.jwfm.framework.util.EncryptUtil;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.action.FastBaseAction;
import com.dx.jwfm.framework.web.view.Node;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

public class FastModel {

	static Logger logger = Logger.getLogger(FastModel.class);
	

	/**菜单ID*/
	private String vcId;
	/**菜单所在分组*/
	private String vcGroup;
	/**菜单名称*/
	private String vcName;
	/**菜单URL*/
	private String vcUrl;
	/**菜单版本*/
	public String vcVersion;
	/**开发人员*/
	private String vcAuth;
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
	
	
	/** 菜单结构字符串{search:{},dictData:[],edittable:{},viewtable:{}}*/
	private String vcStructure;
	/**控制器*/
	private Class<?> actionClass;
	/**模型结构对象*/
	private FastModelStructure modelStructure;
	/**是否从数据库表中加载的数据，如果是，则不再执行数据库对象升级*/
	private boolean fromDB;


	private String encryptKey = SystemContext.getSysParam("GLOBA_ENCRYPT_KEY", "hhkjcsmis");

	public FastModel() {
	}
	
	public FastModel(FastPo po) {
		this.setVcId(po.getString("VC_ID"));
		this.setVcName(po.getString("VC_NAME"));
		this.setVcGroup(po.getString("VC_GROUP"));
		this.setVcUrl(po.getString("VC_URL"));
		this.setVcAuth(po.getString("VC_AUTH"));
		this.vcVersion = po.getString("VC_VERSION");
		this.setVcStructure(po.getString("VC_STRUCTURE"));
	}
	
	public FastModel(String jsonStruct) {
		this.setVcStructure(jsonStruct);
		this.undoPersistent();
		this.setVcId(modelStructure.getVcId());
		this.setVcName(modelStructure.getVcName());
		this.setVcGroup(modelStructure.getVcGroup());
		this.setVcUrl(modelStructure.getVcUrl());
		this.setVcAuth(modelStructure.getVcAuth());
		this.vcVersion = modelStructure.getVcVersion();
	}

	public FastModel(String vcName, String vcUrl, String vcVersion, String vcStructure) {
		super();
		this.vcName = vcName;
		this.vcUrl = vcUrl;
		this.vcVersion = vcVersion;
		this.vcStructure = vcStructure;
	}

	/**
	 * 将结构化数据转换为字符串，以便于持久化到数据库
	 */
	public void doPersistent(){
		JsonConfig conf = new JsonConfig();
		conf.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object obj, String proptName, Object val) {
				if(obj instanceof Date){
					return "timezoneOffset".equals(proptName) || "day".equals(proptName);
				}
				return false;
			}
		});
		JSONObject obj = JSONObject.fromObject(modelStructure,conf);
		vcStructure = EncryptUtil.encryptMix(obj.toString(),encryptKey );
	}
	
	/**
	 * 将字符串中的内容按格式转换为结构化对象，以便于程序调用
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void undoPersistent(){
		if(FastUtil.isBlank(vcStructure)){
			modelStructure = new FastModelStructure();
		}
		else{
			String vcStructure = EncryptUtil.decryptMix(this.vcStructure, encryptKey);
//			if(vcStructure.charAt(0)!='{'){
//				vcStructure = EncryptUtil.decryptMix(vcStructure, encryptKey);
//			}
			JSONObject obj = JSONObject.fromObject(vcStructure);
			Map<String, Class> cmap = new HashMap<String, Class>();
			cmap.put("buttonAuths", ButtonAuth.class);
			cmap.put("mainTable", FastTable.class);
			cmap.put("otherTables", FastTable.class);
			cmap.put("columns", FastColumn.class);
			cmap.put("otherDbObjects", FastDbObject.class);
			cmap.put("search", SearchModel.class);
			cmap.put("dictData", DictNode.class);
			cmap.put("searchColumns", SearchColumn.class);
			cmap.put("searchColumnMap", SearchColumn.class);
			cmap.put("searchResultColumns", SearchResultColumn.class);
			cmap.put("forwards", Node.class);
			cmap.put("pageHTMLAry", Node.class);
			try {
				modelStructure = (FastModelStructure) JSONObject.toBean(obj, FastModelStructure.class,cmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		FastUtil.copyBeanPropts(this, modelStructure);
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
			if(fromDB){//如果是从数据库表中加载的数据，则不再执行数据库对象升级
				return;
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
			if(version==null || version.compareTo(this.vcVersion)<0){
				//比对主业务表并进行表结构更新
				FastTable tbl = modelStructure.getMainTable();
				ArrayList<String> sqls = new ArrayList<String>();
				if(tbl!=null && FastUtil.isNotBlank(tbl.getName())){
					new FastPo(tbl);//将表注册到FastPo中
					sqls.addAll(db.getDatabaseDialect().getTableCreateOrUpdateSql(tbl));
				}
				//比对业务从表并进行表结构更新
				for(FastTable ft:modelStructure.getOtherTables()){
					new FastPo(ft);//将表注册到FastPo中
					sqls.addAll(db.getDatabaseDialect().getTableCreateOrUpdateSql(ft));
				}
				//比对其他数据库对象并进行表结构更新
				for(FastDbObject ft:modelStructure.getOtherDbObjects()){
					sqls.addAll(ft.getObjectUpdateSql(db.getDatabaseDialect()));
				}
				//将上述表结构更新语句批量执行
				if(sqls.size()>0){
					for(String sql:sqls){
						try {
							db.executeSqlUpdate(sql);
						} catch (SQLException e) {
							logger.error(e.getMessage(),e);
						}
					}
				}
				//更新公共字典的值
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
						po.put("VC_VERSION", this.vcVersion);
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
					db.executeSqlUpdate(insertsql,new Object[]{FastUtil.getUuid(),filepath,fname,new Date(),msg.toString()});
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
//		HashSet<String> dictGroup = new HashSet<String>();
		ArrayList<String> sqls = new ArrayList<String>();
		ArrayList<Object[]> params = new ArrayList<Object[]>();
		if(modelStructure.getDictData()!=null){
			for(DictNode n:modelStructure.getDictData()){
				FastPo dict = n.toPo();
				dict.initDefaults();
				try {
					if(db.getFirstIntSqlQuery("select count(*) from "+SystemContext.dbObjectPrefix+"T_DICT where vc_group=? and vc_code=?",
							new Object[]{dict.getString("VC_GROUP"),dict.getString("VC_CODE")})==0){
						sqls.add(dict.getTblModel().insertSql());
						dict.put("VC_ID", FastUtil.getUuid());
						params.add(dict.getInsertParams());
					}
				} catch (SQLException e) {
					logger.error(e.getMessage(),e);
				}
				//如果在现场系统中已更改了相应值之后，升级时不应覆盖用户自行设定的值，故注释此代码
//				if("SYSTEM".equals(dict.getString("VC_GROUP"))){//系统注册项
//					try {
//						if(db.getFirstIntSqlQuery("select count(*) from "+SystemContext.dbObjectPrefix+"T_DICT where vc_group=? and vc_code=?",
//								new Object[]{dict.getString("VC_GROUP"),dict.getString("VC_CODE")})>0){
//							sqls.add("update "+SystemContext.dbObjectPrefix+"T_DICT set vc_text=? where vc_group=? and vc_code=?");
//							params.add(new Object[]{dict.getString("VC_TEXT"),dict.getString("VC_GROUP"),dict.getString("VC_CODE")});
//						}
//						else{
//							sqls.add(dict.getTblModel().insertSql());
//							params.add(dict.getInsertParams());
//						}
//					} catch (SQLException e) {
//						logger.error(e.getMessage(),e);
//					}
//				}
//				if(!dictGroup.contains(dict.getString("VC_GROUP"))){
//					dictGroup.add(dict.getString("VC_GROUP"));
//					sqls.add("delete from "+SystemContext.dbObjectPrefix+"T_DICT where vc_group=?");
//					params.add(new Object[]{dict.getString("VC_GROUP")});
//				}
//				dict.put("VC_ID", Uuid.getUuid());
//				sqls.add(dict.getTblModel().insertSql());
//				params.add(dict.getInsertParams());
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

	public void setActionClass(Class<?> actionClass) {
		this.actionClass = actionClass;
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
		if(FastUtil.isBlank(vcStructure)){
			doPersistent();
		}
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

	public String getVcVersion() {
		return vcVersion;
	}

	public void setVcVersion(String vcVersion) {
		this.vcVersion = vcVersion;
	}

	public boolean isFromDB() {
		return fromDB;
	}

	public void setFromDB(boolean fromDB) {
		this.fromDB = fromDB;
	}

}

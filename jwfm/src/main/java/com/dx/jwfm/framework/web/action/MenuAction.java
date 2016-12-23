package com.dx.jwfm.framework.web.action;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.dx.jwfm.framework.core.FastFilter;
import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.dao.model.FastColumn;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.FastModelStructure;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.exception.ValidateException;

import net.sf.json.JSONObject;

public class MenuAction extends FastBaseAction {

	@Override
	protected String openAddPage() {
		DbHelper db = new DbHelper();
		DatabaseDialect dia = db.getDatabaseDialect();
		List<FastPo> list = dia.listTables();
		setAttribute("tables", list);
		return super.openAddPage();
	}
	
	public String loadTable(){
		String tblCode = getParameter("tblCode");
		DbHelper db = new DbHelper();
		DatabaseDialect dia = db.getDatabaseDialect();
		FastTable tbl = dia.loadTableInfo(tblCode);
		return writeJson(tbl);
	}

	@Override
	protected String addItem() throws ValidateException {
		initNewModel();
		initModel();
		return super.addItem();
	}
	
	protected String addItemAjax() {
		try {
			addItem();
			return writeHTML(ajaxResult(true, po.getString("VC_ID")));
		} catch (Exception e) {
			logger.error(e);
			return writeHTML(ajaxResult(false, "保存时出现错误。详细信息："+e.getClass().getName()+":"+e.getMessage()));
		}
	}

	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年12月16日 上午8:54:19
	 * 功能描述: 根据表名对FastModel进行默认值写入
	 * 方法的参数和返回值: 
	 */
	private void initNewModel() {
		FastTable tbl = model.getMainTable();
		FastPo user = (FastPo) RequestContext.getRequest().getSession().getAttribute("FAST_USER");
		String userName = user==null?"未登录人员":user.getString("VC_NAME");
		po.put("VC_ID", FastUtil.getUuid());
		po.put("VC_NAME", tbl.getTitle());
		po.put("VC_ADD", userName);
		po.put("DT_ADD", new Date());
		po.put("VC_GROUP", "");
		po.put("VC_MODIFY", userName);
		po.put("VC_URL", genUrlPrev(tbl.getName())+"/"+toHumpString(tbl.getName()));
		po.put("VC_VERSION", FastUtil.format(new Date(), "yyyy-MM-dd"));
		model.setVcAuth(userName);
		model.setPackageName(FastUtil.getRegVal("SYSMENU_BASE_PACKAGE")+genUrlPrev(tbl.getName()).replaceAll("/", "."));
		model.setActionName(FastBaseAction.class.getName());
		model.setDefaultSearchData(false);
		model.setUseAjaxOperator(true);
		model.setNewMenu(true);
		SearchModel srh = model.getSearch();
		srh.setSearchSelectSql("select t.* from "+tbl.getName().toLowerCase()+" t");
		String dtField = null;
		for(FastColumn col:tbl.getColumns()){
			if("Date".equals(col.getType())){
				dtField = col.getName();
				break;
			}
		}
		srh.setSearchOrderBySql((dtField!=null?"t."+dtField+" desc,":"")+"t.vc_id");
		srh.setHeadHTML("<script></script>\n<style></style>");
	}

	private String genUrlPrev(String name) {
		if(name==null){
			return null;
		}
		name = name.toLowerCase();
		String url = "/module";
		int pos = name.indexOf("_");
		if(pos>0){
			url = "/"+name.substring(0, pos);
			int pos2 = name.indexOf("_",pos);
			if(pos2-pos>2){
				url += "/"+name.substring(pos+1, pos2);
			}
		}
		return url;
	}

	/**
	 * 开发人：宋帅杰
	 * 开发日期: 2016年12月16日 上午9:29:52
	 * 功能描述: 转换表名为驼峰命名法
	 * 方法的参数和返回值: 
	 * @param str
	 * @return
	 */
	private String toHumpString(String str) {
		if(str==null){
			return null;
		}
		str = str.toLowerCase();
		StringBuilder buff = new StringBuilder();
		boolean toUpper = false;
		for(int i=0;i<str.length();i++){
			char ch = str.charAt(i);
			if(Character.getType(ch)==Character.LOWERCASE_LETTER){
				if(toUpper){
					buff.append(Character.toUpperCase(ch));
					toUpper = false;
				}
				else{
					buff.append(ch);
				}
			}
			else{
				toUpper = true;
			}
		}
		return buff.toString();
	}

	@Override
	protected String openModifyPage() {
		String res = super.openModifyPage();
		setModel();
		return res;
	}

	@Override
	public String look() {
		String res = super.look();
		setModel();
		return res;
	}
//	
//	public String loadHistory(){
//		String url = getParameter("url");
//		if(FastUtil.isNotBlank(url)){
//			DbHelper db = new DbHelper();
//			List<FastPo> list = db.executeSqlQuery("select vc_id,vc_name,vc_url,vc_version,vc_modify,dt_modify from "+SystemContext.dbObjectPrefix+"T_MENU_LIB where n_del=2 and vc_url=?",new Object[]{url});
//		}
//	}

	private void setModel() {
		FastModel fm = new FastModel(po);
		fm.undoPersistent();
		model = fm.getModelStructure();
		setAttribute("model", fm.getModelStructure());
		JSONObject obj = JSONObject.fromObject(fm.getModelStructure());
		setAttribute("modeljson", obj.toString());
	}
	
	private FastModelStructure model;

	@Override
	protected String modifyItem() throws ValidateException {
		model.setNewMenu(false);
		initModel();
		FastModel fm = new FastModel(po);
		DbHelper db = new DbHelper();
		String struct = null;
		try {
			struct = db.getFirstStringSqlQuery("select VC_STRUCTURE from "+SystemContext.dbObjectPrefix+"T_MENU_LIB where vc_id=?",new Object[]{po.getVcId()});
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		String res = null;
		if(struct!=null && !struct.equals(fm.getVcStructure())){
			String oldId = po.getVcId();
			po.setPropt("VC_ID", FastUtil.getUuid());
			res = super.addItem();
			try {
				db.executeSqlUpdate("update "+SystemContext.dbObjectPrefix+"T_MENU_LIB set n_del=2 where vc_id=?",new Object[]{oldId});
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
		else{
			res = super.modifyItem();
		}
		FastFilter.updateFastModel(fm );
		return res;
	}

	private void initModel() {
		model.setVcName(po.getString("VC_NAME"));
		model.setVcAdd(po.getString("VC_ADD"));
		model.setDtAdd(FastUtil.parseDate(po.getString("DT_ADD")));
		model.setVcAuth(po.getString("VC_ADD"));
		model.setVcGroup(po.getString("VC_GROUP"));
		model.setVcId(po.getString("VC_ID"));
		model.setVcModify(po.getString("VC_MODIFY"));
		model.setDtModify(new Date());
		po.put("DT_MODIFY", new Date());
		model.setVcNote(po.getString("VC_NOTE"));
		model.setVcUrl(po.getString("VC_URL"));
		model.setVcVersion(po.getString("VC_VERSION"));
		FastModel fm = new FastModel();
		fm.setModelStructure(model);
		fm.doPersistent();
		po.put("VC_STRUCTURE", fm.getVcStructure());
	}

	public FastModelStructure getModel() {
		return model;
	}

	public void setModel(FastModelStructure model) {
		this.model = model;
	}

}

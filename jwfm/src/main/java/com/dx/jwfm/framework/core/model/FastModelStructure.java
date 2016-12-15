package com.dx.jwfm.framework.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.dx.jwfm.framework.core.dao.model.FastDbObject;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.model.flow.FlowModel;
import com.dx.jwfm.framework.core.model.search.SearchModel;
import com.dx.jwfm.framework.core.model.view.DictNode;
import com.dx.jwfm.framework.web.view.Node;

public class FastModelStructure {

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
	
	
	/**模块主要表名称*/
	private String mainTableName;
	/**模块所在包名称*/
	private String packageName;
	
	
	/**业务Action类名*/
	private String actionName;
	/**业务Action辅助类类名*/
	private String actionHandleName;
	/**默认打开页面时是否执行查询数据操作*/
	private boolean defaultSearchData;
	/**是否使用Ajax执行增删改操作*/
	private boolean useAjaxOperator;
	/**用户自定义默认值解析器类名*/
	private String actionDefaultValueParser;
	/**菜单按钮权限列表*/
	private List<ButtonAuth> buttonAuths = new ArrayList<ButtonAuth>();
	/**action中的转向页面*/
	private List<Node> forwards = new ArrayList<Node>();
	private LinkedHashMap<String,String> forwardsMap = new LinkedHashMap<String, String>();
	
	/**业务主表表结构*/
	private FastTable mainTable = new FastTable();
	/**业务附加表表结构*/
	private List<FastTable> otherTables = new ArrayList<FastTable>();
	/**业务相关的触发器、函数、存储过程等数据库对象*/
	private List<FastDbObject> otherDbObjects = new ArrayList<FastDbObject>();
	/**业务相关的触发器、函数、存储过程等数据库对象*/
	private List<String> initDataSqlList = new ArrayList<String>();
	
	
	/**查询条件及结果展示格式*/
	private SearchModel search = new SearchModel();
	/**模型字典数据*/
	private List<DictNode> dictData = new ArrayList<DictNode>();
//	/**编辑页面模型(HTML代码)*/
//	private String editTable;
	/**独立页面的页面模型(id:code,name:名称,data:HTML代码)*/
	private List<Node> pageHTMLAry = new ArrayList<Node>();
	/**简单流程模板*/
	private FlowModel flowModel = new FlowModel();
	
	private List<FastModelUpdateLog> updateLogs = new ArrayList<FastModelUpdateLog>();
	
	public String getForward(String name){
		if(forwardsMap.size()!=forwards.size()){
			setForwards(forwards);
		}
		return forwardsMap.get(name);
	}
	
	public void setForward(String name,String uri){
		forwards.add(new Node(name,uri));
		forwardsMap.put(name,uri);
	}

	public void setPageHTML(String id, String text, String data){
		Node n = getPageHTMLNode(id);
		if(n==null){
			n = new Node(id, text, null, data);
			pageHTMLAry.add(n);
		}
		else{
			n.setText(text);
			n.setData(data);
		}
	}
	
	public Node getPageHTMLNode(String id){
		for(Node n:getPageHTMLAry()){
			if(id.equals(n.getId())){
				return n;
			}
		}
		return null;
	}

	public String getVcName() {
		return vcName;
	}

	public void setVcName(String vcName) {
		this.vcName = vcName;
	}

	public String getMainTableName() {
		if(mainTableName==null && mainTable!=null){
			mainTableName = mainTable.getCode();
		}
		return mainTableName;
	}

	public void setMainTableName(String mainTableName) {
		this.mainTableName = mainTableName;
	}

	public String getVcUrl() {
		return vcUrl;
	}

	public void setVcUrl(String vcUrl) {
		this.vcUrl = vcUrl;
	}

	public String getVcGroup() {
		return vcGroup;
	}

	public void setVcGroup(String vcGroup) {
		this.vcGroup = vcGroup;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getActionHandleName() {
		return actionHandleName;
	}

	public void setActionHandleName(String actionHandleName) {
		this.actionHandleName = actionHandleName;
	}

	public boolean isDefaultSearchData() {
		return defaultSearchData;
	}

	public void setDefaultSearchData(boolean defaultSearchData) {
		this.defaultSearchData = defaultSearchData;
	}

	public String getActionDefaultValueParser() {
		return actionDefaultValueParser;
	}

	public void setActionDefaultValueParser(String actionDefaultValueParser) {
		this.actionDefaultValueParser = actionDefaultValueParser;
	}

	public List<Node> getForwards() {
		return forwards;
	}

	public void setForwards(List<Node> forwards) {
		this.forwards = forwards;
		if(forwards!=null){
			forwardsMap.clear();
			for(Node n:forwards){
				forwardsMap.put(n.getId(), n.getText());
			}
		}
	}

	public String getVcNote() {
		return vcNote;
	}

	public void setVcNote(String vcNote) {
		this.vcNote = vcNote;
	}

	public FastTable getMainTable() {
		return mainTable;
	}

	public void setMainTable(FastTable mainTable) {
		this.mainTable = mainTable;
	}

	public List<FastTable> getOtherTables() {
		return otherTables;
	}

	public void setOtherTables(List<FastTable> otherTables) {
		this.otherTables = otherTables;
	}

	public SearchModel getSearch() {
		return search;
	}

	public void setSearch(SearchModel search) {
		this.search = search;
	}

	public FlowModel getFlowModel() {
		return flowModel;
	}

	public void setFlowModel(FlowModel flowModel) {
		this.flowModel = flowModel;
	}

	public List<FastModelUpdateLog> getUpdateLogs() {
		return updateLogs;
	}

	public void setUpdateLogs(List<FastModelUpdateLog> updateLogs) {
		this.updateLogs = updateLogs;
	}

	public List<FastDbObject> getOtherDbObjects() {
		return otherDbObjects;
	}

	public void setOtherDbObjects(List<FastDbObject> otherDbObjects) {
		this.otherDbObjects = otherDbObjects;
	}

	public boolean isUseAjaxOperator() {
		return useAjaxOperator;
	}

	public void setUseAjaxOperator(boolean useAjaxOperator) {
		this.useAjaxOperator = useAjaxOperator;
	}
	public List<DictNode> getDictData() {
		return dictData;
	}

	public void setDictData(List<DictNode> dictData) {
		this.dictData = dictData;
	}

//	public String getEditTable() {
//		return editTable;
//	}
//
//	public void setEditTable(String editTable) {
//		this.editTable = editTable;
//	}

	public List<String> getInitDataSqlList() {
		return initDataSqlList;
	}

	public void setInitDataSqlList(List<String> initDataSqlList) {
		this.initDataSqlList = initDataSqlList;
	}

	public String getVcId() {
		return vcId;
	}

	public void setVcId(String vcId) {
		this.vcId = vcId;
	}

	public String getVcAuth() {
		return vcAuth;
	}

	public void setVcAuth(String vcAuth) {
		this.vcAuth = vcAuth;
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

	public List<ButtonAuth> getButtonAuths() {
		return buttonAuths;
	}

	public void setButtonAuths(List<ButtonAuth> buttonAuths) {
		this.buttonAuths = buttonAuths;
	}

	public String getVcVersion() {
		return vcVersion;
	}

	public void setVcVersion(String vcVersion) {
		this.vcVersion = vcVersion;
	}

	public List<Node> getPageHTMLAry() {
		if(pageHTMLAry==null){
			pageHTMLAry = new ArrayList<Node>();
		}
		if(pageHTMLAry.isEmpty()){
			pageHTMLAry.add(new Node("edit","编辑页面"));
			pageHTMLAry.add(new Node("view","查看页面"));
		}
		return pageHTMLAry;
	}
	
	public void setPageHTMLAry(List<Node> pageHTMLAry) {
		this.pageHTMLAry = pageHTMLAry;
	}
	
}

package com.dx.jwfm.framework.core.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.dx.jwfm.framework.core.dao.model.FastDbObject;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.flow.FlowModel;
import com.dx.jwfm.framework.core.model.search.SearchModel;

public class FastModelStructure {

	/**模块名称*/
	private String name;
	/**模块主要表名称*/
	private String mainTableName;
	/**模块URI路径，默认由mainTableName自动生成*/
	private String vcUri;
	/**菜单所在分组*/
	private String vcGroup;
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
	/**action中的转向页面*/
	private LinkedHashMap<String,String> forwards = new LinkedHashMap<String, String>();
	/**功能说明*/
	private String vcNote;
	
	/**业务主表表结构*/
	private FastTable mainTable;
	/**业务附加表表结构*/
	private List<FastTable> otherTables = new ArrayList<FastTable>();
	/**业务相关的触发器、函数、存储过程等数据库对象*/
	private List<FastDbObject> otherDbObjects = new ArrayList<FastDbObject>();
	/**业务相关的触发器、函数、存储过程等数据库对象*/
	private List<String> initDataSqlList = new ArrayList<String>();
	/**查询条件及结果展示格式*/
	private SearchModel search;
	/**模型字典数据*/
	private List<FastPo> dictData;
	/**编辑页面模型(HTML代码)*/
	private String editTable;
	/**简单流程模板*/
	private FlowModel flowModel;
	
	private List<FastModelUpdateLog> updateLogs = new ArrayList<FastModelUpdateLog>();
	
	public String getForward(String name){
		return forwards.get(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMainTableName() {
		return mainTableName;
	}

	public void setMainTableName(String mainTableName) {
		this.mainTableName = mainTableName;
	}

	public String getVcUri() {
		return vcUri;
	}

	public void setVcUri(String vcUri) {
		this.vcUri = vcUri;
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

	public LinkedHashMap<String, String> getForwards() {
		return forwards;
	}

	public void setForwards(LinkedHashMap<String, String> forwards) {
		this.forwards = forwards;
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
	public List<FastPo> getDictData() {
		return dictData;
	}

	public void setDictData(List<FastPo> dictData) {
		this.dictData = dictData;
	}

	public String getEditTable() {
		return editTable;
	}

	public void setEditTable(String editTable) {
		this.editTable = editTable;
	}

	public List<String> getInitDataSqlList() {
		return initDataSqlList;
	}

	public void setInitDataSqlList(List<String> initDataSqlList) {
		this.initDataSqlList = initDataSqlList;
	}
	
}

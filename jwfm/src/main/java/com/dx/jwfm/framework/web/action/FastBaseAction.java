package com.dx.jwfm.framework.web.action;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.contants.RequestContants;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.model.FastTable;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.builder.IDatagridBuilder;
import com.dx.jwfm.framework.web.exception.DatagridBuilderNotFound;
import com.dx.jwfm.framework.web.exception.ValidateException;
import com.dx.jwfm.framework.web.logic.FastBaseLogic;
import com.dx.jwfm.framework.web.search.Pager;
import com.dx.jwfm.framework.web.search.Search;

import net.sf.json.JSONObject;

public class FastBaseAction extends BaseAction {
	
	protected Logger logger = Logger.getLogger(this.getClass());

	/**页面提交执行的操作 */
	protected String op;
	/**页面提交内容时主记录对应的持久化对象*/
	protected FastPo po;
	/**页面提交内容时从表记录对应的持久化对象*/
	protected List<FastPo> items;
	/**页面查询时提交的查询参数*/
	protected Search search = new Search();
	/**页面查询时提交的分页参数*/
	protected Pager pager = new Pager();
	/**逻辑处理类*/
	protected FastBaseLogic logic = new FastBaseLogic();
	
	public FastBaseAction() {
		init();
	}

	public void init() {
		FastModel model = (FastModel) getAttribute(RequestContants.REQUEST_FAST_MODEL);
		if(model!=null){
			po = FastPo.getPo(model.getModelStructure().getMainTableName());
		}
	}

	/**
	 * 默认执行方法
	 * @return
	 */
	public String execute(){
		if(FastUtil.isNotBlank(op)){
			try {
				Method m = this.getClass().getMethod(op, new Class[0]);
				if(m!=null){
					return (String) m.invoke(this, new Object[0]);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		FastModel fmodel = RequestContext.getFastModel();
		if(fmodel!=null && fmodel.getModelStructure().isDefaultSearchData()){
			searchData();
		}
		return "success";
	}

	/**
	 * AJAX加载数据的操作
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws DatagridBuilderNotFound 
	 */
	@SuppressWarnings("unchecked")
	public String searchDataAjax() throws ClassNotFoundException, DatagridBuilderNotFound{
		searchData();
		List<FastPo> list = (List<FastPo>) RequestContext.getRequest().getAttribute("searchResultData");
		String type = RequestContext.getParameter("datagrid_type");
		IDatagridBuilder builder = SystemContext.getDatagridBuilder(type);
		if(builder!=null){
			String json = builder.buildResultJson(list, pager,search);
			return writeHTML(json);
		}
		else{
			throw new DatagridBuilderNotFound("The type ["+type+"] is not found! please set [datagridBuilder."+type+
					"] in fast.properties,the value should be implements IDatagridBuilder");
		}
	}

	/**
	 * 查询数据的操作
	 * @return
	 */
	public String searchData(){
		FastModel fmodel = RequestContext.getFastModel();
		List<FastPo> list = logic.searchData(fmodel.getModelStructure().getSearch(),fmodel.getModelStructure().getMainTable(),search,pager);
		RequestContext.setRequestAttr("searchResultData", list);
		return "success";
	}
	
	/**
	 * 添加操作
	 * @return
	 * @throws ValidateException 
	 */
	public String add() throws ValidateException{
//		FastModel fmodel = RequestContext.getFastModel();
		if("save".equals(op)){
			return addItemAjax();
		}
		else{
			return openAddPage();
		}
	}

	/**
	 * 打开添加页面
	 * @return
	 */
	protected String openAddPage() {
		if(po!=null){
			po.initDefaults();
		}
		return "openAddPage";
	}

	/**
	 * 执行添加操作
	 * @return
	 * @throws ValidateException 
	 */
	protected String addItem() throws ValidateException {
		String msg = validateData();
		if(FastUtil.isBlank(msg)){
			po.initIdDelValue();
			try {
				logic.addPo(po,items);
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException("数据保存时出错。原因："+e.getMessage(),e);
			}
			return "addSuccess";
		}else{
			throw new ValidateException(msg);
		}
	}

	/**
	 * 执行添加操作后返回Json对象
	 * @return
	 */
	protected String addItemAjax() {
		try {
			addItem();
			return writeHTML(ajaxResult(true, "保存成功！"));
		} catch (Exception e) {
			logger.error(e);
			return writeHTML(ajaxResult(false, "保存时出现错误。详细信息："+e.getClass().getName()+":"+e.getMessage()));
		}
	}

	/**
	 * 添加操作
	 * @return
	 * @throws ValidateException 
	 */
	public String modify() throws ValidateException{
		FastModel fmodel = RequestContext.getFastModel();
		if(fmodel==null){
			throw new RuntimeException("can't find FastModel in this URL");
		}
		if("save".equals(op)){
			return modifyItemAjax();
		}
		else{
			return openModifyPage();
		}
	}

	/**
	 * 打开添加页面
	 * @return
	 */
	protected String openModifyPage() {
		if(po!=null){
			String id = getSelectId();
			try {
				DbHelper db = new DbHelper();
				po = db.loadFastPo(po, id);
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
			if(po==null){
				return writeHTML("can't find record by id:["+id+"]");
			}
		}
		return "openModifyPage";
	}

	/**
	 * 执行添加操作
	 * @return
	 * @throws ValidateException 
	 */
	protected String modifyItem() throws ValidateException {
		String msg = validateData();
		if(FastUtil.isBlank(msg)){
			String itemTblName = getItemTableName();
			if(items!=null && itemTblName!=null){
				for(FastPo p:items){
					p.setTableModelName(itemTblName);
				}
				throw new RuntimeException("can't find FastModel in this URL");
			}
			try {
				DbHelper db = new DbHelper();
				try {//从数据库中查出原记录，将提交过来的PO中的新值赋给原记录，再保存原记录，保证不丢字段的值
					String keyCode = po.getTblModel().getKeyColCode();
					if(keyCode!=null){
						FastPo p = db.loadFastPo(po, po.getString(keyCode ));
						if(p!=null && p.getString(keyCode)!=null){
							for(String key:po.keySet()){
								p.put(key, po.get(key));
							}
							po = p;
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				logic.updatePo(po,items);
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new RuntimeException(e.getMessage(),e);
			}
			return "addSuccess";
		}else{
			throw new ValidateException(msg);
		}
	}
	
	protected String getItemTableName(){
		return null;
	}

	/**
	 * 执行添加操作后返回Json对象
	 * @return
	 */
	protected String modifyItemAjax() {
		try {
			modifyItem();
			return writeHTML(ajaxResult(true, "保存成功！"));
		} catch (Exception e) {
			logger.error(e);
			return writeHTML(ajaxResult(false, "保存时出现错误。详细信息："+e.getClass().getName()+":"+e.getMessage()));
		}
	}

	/**
	 * 执行查看操作
	 * @return
	 */
	public String look() {
		if(po!=null){
			try {
				DbHelper db = new DbHelper();
				FastPo p = db.loadFastPo(po, getSelectId());
				po = p==null?po:p;
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
		}
		return "openViewPage";
	}
	
	public String delete(){
		String[] ids = getParameterValues("chkSelf");
		if(ids==null){
			ids = getParameterValues("chkSelf[]");
		}
		if(ids!=null && ids.length>0){
			FastModel model = RequestContext.getFastModel();
			if(model==null){
				return writeHTML(ajaxResult(false, "找不到对应的功能模型！"));
			}
			FastTable tbl = model.getModelStructure().getMainTable();
			if(tbl==null){
				return writeHTML(ajaxResult(false, "在功能模型中找不到主表模型！"));
			}
			String sql = "update "+tbl.getCode()+" set "+tbl.getDelCol()+"=1 where "+tbl.getPkColumns().get(0).getCode()+
					" in ('"+FastUtil.join(ids,"','")+"')";
			DbHelper db = new DbHelper();
			try {
				int cnt = db.executeSqlUpdate(sql);
				return writeHTML(ajaxResult(true, "共删除成功 "+cnt+"条记录 ！"));
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				return writeHTML(ajaxResult(false, "删除时发生错误："+e.getMessage()+"\n"+e.getClass().getName()));
			}
		}
		return writeHTML(ajaxResult(false, "您没有指定任何要删除的数据！"));
	}

	protected String getSelectId() {
		return getRequest().getParameter("chkSelf");
	}

	protected String ajaxResult(boolean successed,String info){
		return new JSONObject().element("successed", successed)
				.element("info", info).toString();
	}

	/**
	 * 用于提交数据时的验证函数。添加和编辑操作时会先调用该函数，子类中可以重写该方法实现自定义验证
	 * @return 验证出错时返回错误信息
	 */
	protected String validateData(){
		return null;
	}
    
	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public FastPo getPo() {
		return po;
	}

	public void setPo(FastPo po) {
		this.po = po;
	}

	public List<FastPo> getItems() {
		return items;
	}

	public void setItems(List<FastPo> items) {
		this.items = items;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

}

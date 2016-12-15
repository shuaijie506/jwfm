package com.dx.jwfm.framework.web.action;

import java.util.Date;

import com.dx.jwfm.framework.core.RequestContext;
import com.dx.jwfm.framework.core.dao.po.FastPo;
import com.dx.jwfm.framework.core.model.FastModel;
import com.dx.jwfm.framework.core.model.FastModelStructure;
import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.exception.ValidateException;

import net.sf.json.JSONObject;

public class MenuAction extends FastBaseAction {

	@Override
	protected String openAddPage() {
		po.put("VC_VERSION", FastUtil.format(new Date(), "yyyy-MM-dd"));
		po.put("DT_ADD", new Date());
		FastPo user = (FastPo) RequestContext.getRequest().getSession().getAttribute("FAST_USER");
		if(user!=null){
			po.put("VC_ADD", user.getString("VC_NAME"));
		}
		setModel();
		return super.openAddPage();
	}

	@Override
	protected String addItem() throws ValidateException {
		initModel();
		return super.addItem();
	}

	@Override
	protected String openModifyPage() {
		String res = super.openModifyPage();
		setModel();
		return res;
	}

	private void setModel() {
		FastModel fm = new FastModel(po);
		fm.undoPersistent();
		setAttribute("model", fm.getModelStructure());
		JSONObject obj = JSONObject.fromObject(fm.getModelStructure());
		setAttribute("modeljson", obj.toString());
	}
	
	private FastModelStructure model;

	@Override
	protected String modifyItem() throws ValidateException {
		initModel();
		return super.modifyItem();
	}

	private void initModel() {
		model.setVcName(po.getString("VC_NAME"));
		model.setVcAdd(po.getString("VC_ADD"));
		model.setDtAdd(FastUtil.parseDate(po.getString("DT_ADD")));
		model.setVcAuth(po.getString("VC_ADD"));
		model.setVcGroup(po.getString("VC_GROUP"));
		model.setVcId(po.getString("VC_ID"));
		model.setVcModify(po.getString("VC_MODIFY"));
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

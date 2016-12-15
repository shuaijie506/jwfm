package com.dx.jwfm.framework.core.model.view;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.po.FastPo;

public class DictNode {

	private String group,code,text,note;
	
	private int seq;

	public DictNode() {
	}
	
	public DictNode(String group, String code, String text, int seq) {
		super();
		this.group = group;
		this.code = code;
		this.text = text;
		this.seq = seq;
	}
	
	public DictNode(String group, String code, String text, String note, int seq) {
		super();
		this.group = group;
		this.code = code;
		this.text = text;
		this.note = note;
		this.seq = seq;
	}
	
	public FastPo toPo(){
		FastPo dict = FastPo.getPo(SystemContext.dbObjectPrefix+"T_DICT");
		dict.element("VC_GROUP", group).element("VC_CODE", code).element("VC_TEXT", text).element("VC_NOTE", note).element("N_SEQ", seq);
		return dict;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}
	
}

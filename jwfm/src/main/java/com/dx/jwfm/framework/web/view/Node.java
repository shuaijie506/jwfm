package com.dx.jwfm.framework.web.view;

public class Node {
	
	private String id,text,pid,data;

	public Node() {
	}

	public Node(String id, String text) {
		super();
		this.id = id;
		this.text = text;
	}

	public Node(String id, String text, String pid) {
		super();
		this.id = id;
		this.text = text;
		this.pid = pid;
	}

	public Node(String id, String text, String pid, String data) {
		super();
		this.id = id;
		this.text = text;
		this.pid = pid;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}

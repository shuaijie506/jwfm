package com.dx.jwfm.framework.web.search;

public class Pager {
	/**每页行数和页号*/
	protected int rows=20,page=1;
	/**查询结果的总行数*/
	protected int rowAmount;
	
	private int[] nums;
	
	public int[] getNums() {
		return nums;
	}

	public void setNums(int[] nums) {
		this.nums = nums;
	}

	public int getPageAmount(){
		return (int) Math.max(Math.round(((double)rowAmount)/rows+0.499999), 1);
	}

	public int getBegRowNo(){
		return (page - 1) * this.rows;
	}

	public int getEndRowNo(){
		return page * this.rows;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRowAmount() {
		return rowAmount;
	}

	public void setRowAmount(int rowAmount) {
		this.rowAmount = rowAmount;
	}

}

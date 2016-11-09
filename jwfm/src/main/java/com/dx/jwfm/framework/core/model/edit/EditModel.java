package com.dx.jwfm.framework.core.model.edit;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class EditModel {

	/** 是否为高级模式，普通模式下支持字段单列、双列、三列显示，高级模式下为Excel表格模式 */
	private boolean advModel; 
	
	/**编辑页面所示表格中的行数和列数，数字从1开始*/
	private int rows,cols;
	/**编辑页面所示表格中的单元格列表*/
	private List<EditTableCell> cells = new ArrayList<EditTableCell>();

	/**编辑页面的附加css样式，不含<style></style>标签部分*/
	private String customCss;
	/**编辑页面的附加js代码，不含<script></script>标签部分
	 * window.isAdd==true时表示为添加页面，否则为修改页面*/
	private String customJs;

	public void addCell(String code, String cellType, boolean notNull) {
		addCell(code, cellType, notNull, 1);
	}
	public void addCell(String code, String cellType, boolean notNull,int colSpan) {
		EditTableCell cell = new EditTableCell();
		cell.setCode(code);
		cell.setCellType(cellType);
		cell.setNotNull(notNull);
		EditTableCell preCell = cells.isEmpty()?new EditTableCell():cells.get(cells.size()-1);
		if(preCell.getColIdx()+preCell.getColSpan()>=cols){
			cell.setRowIdx(preCell.getRowIdx()+1);
			cell.setColIdx(1);
		}
		else{
			cell.setRowIdx(preCell.getRowIdx());
			cell.setColIdx(preCell.getColIdx()+preCell.getColSpan());
		}
	}
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public boolean isAdvModel() {
		return advModel;
	}

	public void setAdvModel(boolean advModel) {
		this.advModel = advModel;
	}

	public List<EditTableCell> getCells() {
		return cells;
	}

	public void setCells(List<EditTableCell> cells) {
		this.cells = cells;
	}

	public String getCustomCss() {
		return customCss;
	}

	public void setCustomCss(String customCss) {
		this.customCss = customCss;
	}

	public String getCustomJs() {
		return customJs;
	}

	public void setCustomJs(String customJs) {
		this.customJs = customJs;
	}

}

package com.dx.jwfm.framework.web.tag;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.dx.jwfm.framework.util.FastUtil;
import com.dx.jwfm.framework.web.search.Pager;

public class PageTag extends BaseViewTag {

	private static final long serialVersionUID = 1L;
	private int formIndex;

	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		JspWriter out = pageContext.getOut();
		Pager pager = (Pager) request.getAttribute("searchResultPage");
		try {
			if (pager != null) {
				String page = getPageStr(pager, request, formIndex);
				out.write(page);
			} else {
				out.write("未找到分页信息！");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	static DecimalFormat df = new DecimalFormat("0.##");
	public static String getPageStr(Pager pager, HttpServletRequest request,
			int form) {
		StringBuilder buff = new StringBuilder();
		buff.append("<nobr style='height:20px;overflow:hidden;'>");
		buff.append("<a class=btn onclick='goPage(")//刷新
		.append(pager.getPage()).append(");return false;'><span class='pagination-load'>&nbsp;</span></a>")
		.append("共").append(pager.getRowAmount()).append("条")
		.append("<span class=split>&nbsp;</span>");
		if(pager!=null && pager.getPage()>1){
			buff.append("<a class=btn onclick='goPage(1);return false;'><span class='pagination-first'>&nbsp;</span></a>")
			.append("<a class=btn onclick='goPage(").append(pager.getPage()-1).append(");return false;'><span class='pagination-prev'>&nbsp;</span></a>");
		}else{
			buff.append("<a class=btn><span class='pagination-first disable'>&nbsp;</span></a>")
			.append("<a class=btn><span class='pagination-prev disable'>&nbsp;</span></a>");
		}
		buff.append("第<input type=text val='").append(pager.getPage()).append("' style='width:25px;height:14px;' name='page' value='")
		.append(pager.getPage()).append("' onblur='if(this.value!=$(this).attr(\"val\"))goPage(this.value)' ")
		.append("onkeyup='if(event.keyCode==13&&this.value!=$(this).attr(\"val\"))goPage(this.value)'>页,共").append(pager.getPageAmount()).append("页");
		if(pager!=null && pager.getPage()<pager.getPageAmount()){
			buff.append("<a class=btn onclick='goPage(").append(pager.getPage()+1).append(");return false;'><span class='pagination-next'>&nbsp;</span></a>")
			.append("<a class=btn onclick='goPage(").append(pager.getPageAmount()).append(");return false;'><span class='pagination-last'>&nbsp;</span></a>");
		}else{
			buff.append("<a class=btn><span class='pagination-next disable'>&nbsp;</span></a>")
			.append("<a class=btn><span class='pagination-last disable'>&nbsp;</span></a>");
		}
		
		buff.append("<select name=\"rows\" style='width:88px;' onchange=\"setRows(this)\">");
		String pages = FastUtil.getRegVal("SYSTEM_PAGE_SIZE_LIST");
		if(FastUtil.isBlank(pages)){
			pages = "10,20,50,200";
		}
		for(String p:pages.split(",")){
			if(FastUtil.isInteger(p)){
				buff.append("<option value='").append(p).append("'").append(getIsSelect(pager.getRows(),p))
				.append(">每页").append(p).append("条</option>");
			}
		}
		buff.append("</select></nobr>");
		return buff.toString();
	}

	static String getIsSelect(int num, String gdnum) {
		if (gdnum != null && gdnum.equals(""+num))
			return " selected";
		return "";
	}

	public int getFormIndex() {
		return formIndex;
	}

	public void setFormIndex(int formIndex) {
		this.formIndex = formIndex;
	}
	
}

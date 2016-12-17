package com.dx.jwfm.framework.web.logic;

import java.util.Map;

import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.dialect.DatabaseDialect;
import com.dx.jwfm.framework.core.model.search.SearchColumn;
import com.dx.jwfm.framework.util.FastUtil;

public class DefaultSQLConditionParser implements SQLConditionParser {

	@Override
	public String createSqlFragment(SearchColumn srhcol, Map<String, Object> paramMap) {
		String sqlSearchType = srhcol.getSqlSearchType();
		String vcEditorType = srhcol.getVcEditorType();
		String vcCode = srhcol.getVcCode();
		String sqlFragment = srhcol.getSqlFragment();
				
		//日期型的需要特殊处理
		if(sqlSearchType.startsWith("date")){
			String format = null;
			if(vcEditorType!=null && vcEditorType.startsWith("date:")){
				format = vcEditorType.substring(5);
			}
			if(FastUtil.isBlank(format)){
				format = "yyyy-MM-dd";
			}
			StringBuffer buff = new StringBuffer();
			DbHelper db = new DbHelper();
			DatabaseDialect dialect = db.getDatabaseDialect();
			if("dateRange".equals(sqlSearchType)){//日期范围
				if(FastUtil.isNotBlank((String)paramMap.get(vcCode+"Begin"))){
					buff.append(" and ").append(dialect.getDate2StringFun(FastUtil.nvl(sqlFragment, vcCode), format))
					.append(">=${").append(vcCode).append("Begin}");
				}
				if(FastUtil.isNotBlank((String)paramMap.get(vcCode+"End"))){
					buff.append(" and ").append(dialect.getDate2StringFun(FastUtil.nvl(sqlFragment, vcCode), format))
					.append("<=${").append(vcCode).append("End}");
				}
			}
			else{//日期 > < >= <=运算
				String operate = sqlSearchType.substring(4);
				buff.append(" and ").append(dialect.getDate2StringFun(FastUtil.nvl(sqlFragment, vcCode), format))
				.append(operate).append("${").append(vcCode).append("End}");
			}
			return buff.toString();
		}
		else if("like".equals(sqlSearchType)){//模糊匹配
			DbHelper db = new DbHelper();
			DatabaseDialect dialect = db.getDatabaseDialect();
			if(dialect!=null){
				return " and "+FastUtil.nvl(sqlFragment, vcCode)+" like "+dialect.concatString(new String[]{"'%'","${"+vcCode+"}","'%'"});
			}
			else{
				return " and "+FastUtil.nvl(sqlFragment, vcCode)+" like '%"+paramMap.get(vcCode)+"%'";
			}
		}
		else if("in".equals(sqlSearchType)){//多选匹配
			Object obj = paramMap.get(vcCode);
			String[] ary = null;
			if(obj instanceof String[]){
				ary = (String[])obj;
			}
			else if(obj!=null){
				ary = obj.toString().split("\\s*,\\s*");
			}
			if(ary==null || ary.length==0 || (ary.length==1 && FastUtil.isBlank(ary[0]))){
				return "";
			}
			return " and "+FastUtil.nvl(sqlFragment, vcCode)+" in ('"+FastUtil.join(ary,"','")+"')";
		}
		return null;
	}

}

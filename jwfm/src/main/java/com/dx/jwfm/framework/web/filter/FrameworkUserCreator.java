package com.dx.jwfm.framework.web.filter;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.dx.jwfm.framework.core.SystemContext;
import com.dx.jwfm.framework.core.dao.DbHelper;
import com.dx.jwfm.framework.core.dao.po.FastPo;

public class FrameworkUserCreator implements LoginUserCreator {
	
	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Object createUser(String userId) {
		DbHelper db = new DbHelper();
		FastPo user = null;
		try {
			user = db.loadFastPo(FastPo.getPo(SystemContext.dbObjectPrefix+"T_USER"), userId);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		return user;
	}

}

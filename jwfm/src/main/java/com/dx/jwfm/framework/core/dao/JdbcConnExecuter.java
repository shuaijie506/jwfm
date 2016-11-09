package com.dx.jwfm.framework.core.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface JdbcConnExecuter {

	public int execute(Connection con) throws SQLException;
}

package com.dx.jwfm.framework.core.dao;

import java.sql.SQLException;
import java.sql.Statement;

public interface JdbcStatementExecuter {

	public int execute(Statement st) throws SQLException;
}

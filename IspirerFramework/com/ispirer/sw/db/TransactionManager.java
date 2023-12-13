/*
	© 2021, Ispirer Systems OÜ. All rights reserved.
	NOTICE OF LICENSE
	This file\library is Ispirer Reusable Code (“IRC”) and you are granted a non-exclusive, worldwide, perpetual, irrevocable and fully paid up license to use, modify, adapt, sublicense and otherwise exploit this IRC as provided below and under the terms of Ispirer Systems OÜ. Reusable Code License Agreement (“License”), which can be found in supplementary LICENSE.txt file. By using this IRC, you acknowledge that you have read the License and agree with its terms as well as with the fact that IRC is the property of and belongs to Ispirer Systems OÜ only.
	IF YOU ARE NOT AGREE WITH THE TERMS OF THE LICENSE, PLEASE, STOP USING THIS IRC IMMEDIATELY! 
	PLEASE, NOTE, THAT IRC IS DISTRIBUTED “AS IS” AND WITHOUT ANY WARRANTY. IN NO EVENT WILL ISPIRER BE LIABLE FOR ANY DAMAGES, CLAIMS OR COSTS WHATSOEVER OR ANY CONSEQUENTIAL, INDIRECT, INCIDENTAL DAMAGES, OR ANY LOST PROFITS OR LOST SAVINGS. 
	Redistributions of this IRC must retain the above copyright notice and a list of significant changes made to this IRC with indication of its author(s) and date of changes.
	If you need more information, or you think that the License has been violated, please let us know by e-mail: legal.department@ispirer.com  
*/
package com.ispirer.sw.db;

import com.ispirer.sw.db.utils.Oraca;
import com.ispirer.sw.types.PictureType;
import org.apache.commons.lang3.mutable.MutableInt;
import com.ispirer.sw.db.utils.SqlCA;
import java.sql.*;
import java.util.HashMap;
import java.util.function.Function;

public class TransactionManager extends SqlCA {
	private static TransactionManager instance;

	private boolean useSqlca = false;
	private boolean useOraca = false;

	private Function<SQLException, SQLException> sqlerror = e -> null;
	private Function<SQLWarning, SQLWarning> sqlwarning = e -> null;
	private Function<String, String> notFound = e -> null;

	private SQLException exception;
	private String statementText;

	PreparedStatement statement;

	private HashMap<String, Cursor> cursors = new HashMap<>();
	//private HashMap<String, Boolean> isScrollable = new HashMap<>();

	private TransactionManager() {
	}

	public static TransactionManager getInstance() {
		if (instance == null) {
			instance = new TransactionManager();
		}
		return instance;
	}

	// can execute SQL statements, such as DDL statements
	public void executeUpdate(String command) {
		try {
			checkAndClose();
			statement = SqlConnect.getInstance().getConnect().prepareStatement(command);
			statement.executeUpdate();
			statementText = command;
			handleWarning(statement.getWarnings());
			setSuccess();
			if (useSqlca) {
				sqlerrd[2] = statement.getUpdateCount();
				sqlerrd[4] = statement.getUpdateCount();
			}
			if (useOraca) {
				Oraca.oranpr++;
				Oraca.oranex++;
			}
			statement.close();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	// can execute queries that doesn't return ResultSet
	public void execute(String command) {
		try {
			checkAndClose();
			statement = SqlConnect.getInstance().getConnect().prepareStatement(command);
			statement.execute();
			statementText = command;
			if (statement.getUpdateCount() < 1) {
				handleNotFound();
			} else {
				handleWarning(statement.getWarnings());
				setSuccess();
				if (useSqlca) {
					sqlerrd[2] = statement.getUpdateCount();
					sqlerrd[4] = statement.getUpdateCount();
				}
				if (useOraca) {
					Oraca.oranpr++;
					Oraca.oranex++;
				}
			}
			statement.close();
		} catch (SQLException e) {
			handleError(e);
		}
	}

	// executes query that returns ResultSet
	public ResultSet executeInto(String command) {
		ResultSet rs = null;
		try {
			checkAndClose();
			statement = SqlConnect.getInstance().getConnect().prepareStatement(command);
			rs = statement.executeQuery();
			statementText = command;
			handleWarning(statement.getWarnings());
			handleWarning(rs.getWarnings());
			setSuccess();
			if (useOraca) {
				Oraca.oranpr++;
				Oraca.oranex++;
			}
			return rs;
		} catch (SQLException e) {
			handleError(e);
		}
		return rs;
	}

	private void checkAndClose() throws SQLException {
		if (statement != null && !statement.isClosed()) {
			statement.close();
		}
	}

	// commits changes
	public void commit() {
		try {
			if (!SqlConnect.getInstance().getConnect().getAutoCommit())
				SqlConnect.getInstance().getConnect().commit();
			setSuccess();
			if (useOraca) {
				Oraca.oranex++;
			}
		} catch (SQLException e) {
			handleError(e);
		}
	}

	// rollback changes
	public void rollback() {
		try {
			if (SqlConnect.getInstance().getConnect() != null && !SqlConnect.getInstance().getConnect().getAutoCommit())
				SqlConnect.getInstance().getConnect().rollback();
			setSuccess();
			if (useOraca) {
				Oraca.oranex++;
			}
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public void handleError(SQLException exception) {
		setError(exception);
		setException(exception);
		sqlerror.apply(exception);
	}

	public void handleWarning(SQLWarning warning) {
		if (warning != null) {
			while (warning != null) {
				sqlwarning.apply(warning);
				warning = warning.getNextWarning();
			}
		}
	}

	public void handleNotFound() {
		setDataNotFound();
		notFound.apply("");
	}

	public String sqlca() {
		return "";
	}

	public void declareCursor(String name, String query) {
		cursors.put(name, new Cursor(query));
	}

	public void declareCursor(String name, String query, boolean isScrollable) {
		cursors.put(name, new Cursor(query, isScrollable));
	}

	/**
	 * function for getting full text of error message
	 * 
	 * @param buffLen maxLength of message
	 * @param msglen  outpet var for the actual length of message
	 * @return full text of error message
	 */
	public String sqlglm(int buffLen, MutableInt msglen) {
		if (exception != null) {
			msglen.setValue(exception.getMessage().length());
			return exception.getMessage();
		}
		msglen.setValue(0);
		return "";
	}

	public String sqlglm(int buffLen, PictureType<Integer> msglen) {
		if (exception != null) {
			msglen.setValue(exception.getMessage().length());
			return exception.getMessage();
		}
		msglen.setValue(0);
		return "";
	}

	/**
	 * returns the following information:
	 * 
	 * @param stmlen The length of the statement
	 * @param sqlfc  A function code
	 * @return The text of the most recently parsed SQL statement
	 */
	public String sqlgls(MutableInt stmlen, MutableInt sqlfc) {
		stmlen.setValue(statementText.length());
		sqlfc.setValue(getFunctionCode());
		return statementText;
	}

	public String sqlgls(PictureType<Integer> stmlen, PictureType<Integer> sqlfc) {
		stmlen.setValue(statementText.length());
		sqlfc.setValue(getFunctionCode());
		return statementText;
	}

	public int getFunctionCode() {
		if (statementText.split(" ")[0].equalsIgnoreCase("SELECT")) {
			return 4;
		}
		if (statementText.split(" ")[0].equalsIgnoreCase("Update")) {
			return 5;
		}
		return -1;
	}

	public Connection getConnection() {
		return SqlConnect.getInstance().getConnect();
	}

	public Function<SQLException, SQLException> getSqlerror() {
		return sqlerror;
	}

	public void setSqlerror(Function<SQLException, SQLException> sqlerror) {
		this.sqlerror = sqlerror;
	}

	public Function<SQLWarning, SQLWarning> getSqlwarning() {
		return sqlwarning;
	}

	public void setSqlwarning(Function<SQLWarning, SQLWarning> sqlwarning) {
		this.sqlwarning = sqlwarning;
	}

	public Function<String, String> getNotFound() {
		return notFound;
	}

	public void setNotFound(Function<String, String> notFound) {
		this.notFound = notFound;
	}

	public HashMap<String, Cursor> getCursors() {
		return cursors;
	}

	public void setCursors(HashMap<String, Cursor> cursors) {
		this.cursors = cursors;
	}

	public SQLException getException() {
		return exception;
	}

	public void setException(SQLException exception) {
		this.exception = exception;
	}

	public boolean isUseSqlca() {
		return useSqlca;
	}

	public void setUseSqlca(boolean useSqlca) {
		this.useSqlca = useSqlca;
	}

	public boolean isUseOraca() {
		return useOraca;
	}

	public void setUseOraca(boolean useOraca) {
		this.useOraca = useOraca;
	}

	public int getSqlcode() {
		return sqlcode;
	}

	public void setSqlcode(int sqlcode) {
		SqlCA.sqlcode = sqlcode;
	}

	public PreparedStatement getStatement() {
		return statement;
	}

	public void setStatement(PreparedStatement statement) {
		this.statement = statement;
	}
}

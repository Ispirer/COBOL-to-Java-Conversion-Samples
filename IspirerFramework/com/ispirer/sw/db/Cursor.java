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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class implements Cursor behavior.
 */
public class Cursor {

	private PreparedStatement statement; // statement that execute Select query for cursor
	public ResultSet resultSet; // resultSet that store the result of query
	private String query; // the text of query

	/**
	 * The constructor of Cursor. It creates PreparedStatement for query
	 *
	 * @param query is a Select query.
	 */
	public Cursor(String query) {
		this.query = query;
		try {
			statement = SqlConnect.getInstance().getConnect().prepareStatement(query);
			TransactionManager.setSuccess();
			if (TransactionManager.getInstance().isUseOraca()) {
				Oraca.oranpr++;
				Oraca.oranex++;
			}
		} catch (SQLException e) {
			TransactionManager.getInstance().handleError(e);
		}
	}

	public Cursor(String query, boolean isScrollable) {
		this.query = query;
		try {
			statement = SqlConnect.getInstance().getConnect().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			TransactionManager.setSuccess();
			if (TransactionManager.getInstance().isUseOraca()) {
				Oraca.oranpr++;
				Oraca.oranex++;
			}
		} catch (SQLException e) {
			TransactionManager.getInstance().handleError(e);
		}
	}

	public PreparedStatement getStatement() {
		return statement;
	}

	public void setStatement(PreparedStatement statement) {
		this.statement = statement;
	}

	/**
	 * This method is using to get result of query execution to fetch all records
	 * one by one
	 *
	 * @return the result of query execution
	 */
	public ResultSet getResultSet() {
		TransactionManager.setSuccess();
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * This method execute Select query for cursor.
	 */
	public void open() {
		try {
			resultSet = statement.executeQuery();
			TransactionManager.getInstance().handleWarning(statement.getWarnings());
			TransactionManager.getInstance().handleWarning(resultSet.getWarnings());
			TransactionManager.setSuccess();
			if (TransactionManager.getInstance().isUseOraca()) {
				Oraca.orastxtc = query;
				Oraca.orastxtl = query.length();
				Oraca.oracoc++;
				if (Oraca.oramoc < Oraca.oracoc) {
					Oraca.oramoc = Oraca.oracoc;
					Oraca.oranor = Oraca.orahoc - Oraca.oramoc;
				}
				Oraca.oranex++;
			}
		} catch (SQLException e) {
			TransactionManager.getInstance().handleError(e);
		}
	}

	/**
	 * This method closes resultSet and Statement objects
	 */
	public void close() {
		try {
			statement.close();
			resultSet.close();
			TransactionManager.setSuccess();
			if (TransactionManager.getInstance().isUseOraca()) {
				Oraca.oracoc--;
				Oraca.oranex++;
			}
		} catch (SQLException e) {
			TransactionManager.getInstance().handleError(e);
		}
	}
}

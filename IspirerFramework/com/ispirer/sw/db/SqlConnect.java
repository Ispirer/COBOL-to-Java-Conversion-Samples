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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnect {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);
	private static SqlConnect instance;
	private Connection conn;
	private String url = "";
	private String username = "";
	private String password = "";

	private SqlConnect() {
	}

	public static SqlConnect getInstance() {
		if (instance == null) {
			instance = new SqlConnect();
		}
		return instance;
	}

	public void createConnect(String username, String password, String dbname) {
		this.username = username.trim();
		this.password = password.trim();
		try {
			conn = DriverManager.getConnection(this.url + dbname.trim(), this.username, this.password);
			TransactionManager.setSuccess();
		} catch (SQLException e) {
			LOGGER.info(String.valueOf(e));
			TransactionManager.getInstance().handleError(e);
		}
	}

	public Connection getConnect() {
		try {
			if (conn == null || conn.isClosed()) {
				createConnect(url, username, password);
			}
		} catch (SQLException e) {
			LOGGER.info(String.valueOf(e));
		}
		return conn;
	}

	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			LOGGER.info(String.valueOf(e));
		}
	}

	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			LOGGER.info(String.valueOf(e));
		}
	}
}

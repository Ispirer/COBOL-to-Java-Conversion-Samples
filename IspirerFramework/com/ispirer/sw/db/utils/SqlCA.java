/*
	© 2021, Ispirer Systems OÜ. All rights reserved.
	NOTICE OF LICENSE
	This file\library is Ispirer Reusable Code (“IRC”) and you are granted a non-exclusive, worldwide, perpetual, irrevocable and fully paid up license to use, modify, adapt, sublicense and otherwise exploit this IRC as provided below and under the terms of Ispirer Systems OÜ. Reusable Code License Agreement (“License”), which can be found in supplementary LICENSE.txt file. By using this IRC, you acknowledge that you have read the License and agree with its terms as well as with the fact that IRC is the property of and belongs to Ispirer Systems OÜ only.
	IF YOU ARE NOT AGREE WITH THE TERMS OF THE LICENSE, PLEASE, STOP USING THIS IRC IMMEDIATELY! 
	PLEASE, NOTE, THAT IRC IS DISTRIBUTED “AS IS” AND WITHOUT ANY WARRANTY. IN NO EVENT WILL ISPIRER BE LIABLE FOR ANY DAMAGES, CLAIMS OR COSTS WHATSOEVER OR ANY CONSEQUENTIAL, INDIRECT, INCIDENTAL DAMAGES, OR ANY LOST PROFITS OR LOST SAVINGS. 
	Redistributions of this IRC must retain the above copyright notice and a list of significant changes made to this IRC with indication of its author(s) and date of changes.
	If you need more information, or you think that the License has been violated, please let us know by e-mail: legal.department@ispirer.com  
*/
package com.ispirer.sw.db.utils;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

/**
 * This class implements SCLCA data structure that store information about
 * execution of SQL statements
 */

public class SqlCA {

	public String sqlcaID = "SQLCA   ";
	public long sqlcaBC = 136;
	public static int sqlcode = 0;
	public static String sqlerrm;
	public static int sqlerrml = 0;
	public static String sqlerrmc = "";
	public static String sqlerrp;
	public static int[] sqlerrd = new int[6];
	public static String sqlwarn;
	public static String sqlwarn0 = "";
	public static String sqlwarn1 = "";
	public static String sqlwarn2 = "";
	public static String sqlwarn3 = "";
	public static String sqlwarn4 = "";
	public static String sqlwarn5 = "";
	public static String sqlwarn6 = "";
	public static String sqlwarn7 = "";
	public static String sqlwarn8 = "";
	public static String sqlwarn9 = "";
	public static String sqlwarn10 = "";
	public static String sqlwarna = "";
	public static String sqlstate;

	/**
	 * This method fills fields of this clas when any exception is thrown
	 * 
	 * @param se is the exception that is thrown
	 */
	protected static synchronized void setError(SQLException se) {
		sqlcode = se.getErrorCode();
		sqlerrm = se.getMessage();
		sqlerrmc = StringUtils.substring(se.getMessage(), 0, 70);
		sqlerrml = sqlerrmc.length();
		sqlstate = se.getSQLState();
	}

	/**
	 * This method set this structure into Success mode. This means that query was
	 * executed successful
	 */
	public static synchronized void setSuccess() {
		sqlcode = 0;
		sqlerrm = "";
		sqlerrmc = "";
		sqlerrml = 0;
		sqlstate = "00000";
	}

	/**
	 * This method set this structure in the DataNotFound mode. This means that
	 * result of query execution was empty
	 */
	public static synchronized void setDataNotFound() {
		sqlcode = 1403; // This value has to be 1403 IN ORACLE DB
		sqlerrm = "ORA-01403: no data found";
		sqlerrmc = "ORA-01403: no data found";
		sqlerrml = sqlerrmc.length();
		sqlstate = "02000";
	}

}
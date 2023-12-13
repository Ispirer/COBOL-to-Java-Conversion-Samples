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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Repo {

	private static final Logger LOGGER = LoggerFactory.getLogger(Repo.class);

	protected int eof;
	public int error;
	public int equal;
	public boolean isInvalidKey;
	public boolean isEof;
	public boolean restartAfterRead = false;
	public List<EntityCl> list = new LinkedList<>();
	protected String selectQuery = "select FIRST ? * from ";
	protected String selectSkipQuery = "select SKIP ? LIMIT %s * from ";
	protected String orderClause;
	protected String tableName;
	protected String whereKeyClause;
	protected String concatKeys;
	protected int recordCount;
	protected int k = 0;
	protected int curMinRecNo;
	protected int curMaxRecNo;
	protected int fetchSize = 10000;
	protected boolean openLater = false;
	protected Class<?> entityClass;

	public void open(Repo.OpenMode openMode) {
		switch (openMode) {
		case OUTPUT:
			Connection conn = SqlConnect.getInstance().getConnect();
			try (PreparedStatement stmnt = conn.prepareStatement("truncate table " + tableName);) {
				stmnt.executeUpdate();
			} catch (Exception e) {
				LOGGER.info(String.valueOf(e));
				eof = 1;
			}
		case INPUT:
		case I_O:
			// open();
			break;
		case EXTEND:
			break;
		}
	}

	public void countRows() {
		fetchSize = (fetchSize == 0) ? fetchSize : fetchSize;
		Connection conn = SqlConnect.getInstance().getConnect();
		String sql = "SELECT COUNT(1) FROM " + tableName;
		try (PreparedStatement stmnt = conn.prepareStatement(sql);) {
			try (ResultSet rs = stmnt.executeQuery()) {
				rs.next();
				recordCount = rs.getInt(1);
			}
		} catch (Exception e) {
			LOGGER.info(String.valueOf(e));
			eof = 1;
		}
	}

	protected EntityCl openTable() {
		countRows();
		curMinRecNo = 1;
		curMaxRecNo = fetchSize;
		Connection conn = SqlConnect.getInstance().getConnect();
		String sql = String.format(selectSkipQuery, fetchSize) + tableName + orderClause;
		try (PreparedStatement stmnt = conn.prepareStatement(sql);) {
//                stmnt.setMaxRows(fetchSize);
			stmnt.setInt(1, 0);
			try (ResultSet rs = stmnt.executeQuery()) {
				fillList(rs);
				if (list.isEmpty()) {
					eof = 1;
					return null;
				}
				eof = 0;
				isEof = false;
			}
		} catch (Exception e) {
			LOGGER.info(String.valueOf(e));
			eof = 1;
			return null;
		}
		return list.get(0);
	}

//    protected EntityCl readFirstRec() {
//        Connection conn = SqlConnect.getInstance().getConnect();
//        String sql;
//        if (OLD_VERSION) {
//            sql = selectQuery + tableName + orderClause;
//        } else {
//            sql = String.format(selectSkipQuery, 1) + tableName + orderClause;
//        }
//        EntityCl rec;
//        try (PreparedStatement stmnt = conn.prepareStatement(sql);) {
//            if (OLD_VERSION) {
//                stmnt.setMaxRows(1);
//            } else {
////                stmnt.setMaxRows(1);
//                stmnt.setInt(1, 0);
//            }
//            try (ResultSet rs = stmnt.executeQuery()) {
//                rec = fillRec(rs);
//                if (rec == null) {
//                    eof = 1;
//                    return null;
//                }
//                eof = 0;
//                isEof = false;
//                list.add(rec);
//            }
//        } catch (Exception e) {
//            LOGGER.info(String.valueOf(e));
//            eof = 1;
//            return null;
//        }
//        return rec;
//    }
	protected EntityCl findRecBiggestKey() {
		Connection conn = SqlConnect.getInstance().getConnect();
		try (PreparedStatement stmnt = conn.prepareStatement(selectQuery + tableName + orderClause + " desc");) {
			stmnt.setMaxRows(1);
			try (ResultSet rs = stmnt.executeQuery()) {
				return fillRec(rs);
			}
		} catch (Exception e) {
			LOGGER.info(String.valueOf(e));
			return null;
		}
	}

	public EntityCl reopen(int currentLast) {
		k++;
		curMinRecNo = currentLast + 1;
		curMaxRecNo = currentLast + fetchSize;
		Connection conn = SqlConnect.getInstance().getConnect();
		String sql = String.format(selectSkipQuery, fetchSize) + tableName + orderClause;
		try (PreparedStatement stmnt = conn.prepareStatement(sql);) {
//            stmnt.setMaxRows(fetchSize);
			stmnt.setInt(1, currentLast);
			try (ResultSet rs = stmnt.executeQuery()) {
				fillList(rs);
				if (list.isEmpty()) {
					eof = 1;
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.info(String.valueOf(e));
			eof = 1;
			return null;
		}
		eof = 0;
		isEof = false;
		return (EntityCl) list.get(0);
	}

//    public EntityCl findStartRecord(String sign, String keyValue) {
//        Connection conn = SqlConnect.getInstance().getConnect();
//        String order;
//        switch (sign) {
//            case "<":
//            case "<=":
//                order = "desc";
//                break;
//            default:
//                order = "asc";
//                break;
//        }
//        EntityCl rec = null;
//        String sql;
//        if (OLD_VERSION) {
//            sql = "select *, " + concatKeys + " as key from " + tableName + " where " + concatKeys + sign + " ? " + orderClause
//                    + order;
//        } else {
//            sql = "select * from (select *, " + concatKeys + " as key from " + tableName + orderClause + order
//                    + ") where key " + sign + " ?";
//        }
//        try (PreparedStatement stmnt = conn.prepareStatement(sql);) {
//            stmnt.setString(1, keyValue);
//            try (ResultSet rs = stmnt.executeQuery()) {
//                rec = (EntityCl) fillRec(rs);
//                if (rec == null) {
//                    isInvalidKey = true;
//                    return rec;
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.info(String.valueOf(e));
//            eof = 1;
//            isInvalidKey = true;
//        }
//        return rec;
//    }
	public EntityCl reopenBack(int currentFirst) {
		k++;
		curMinRecNo = currentFirst - fetchSize < 1 ? 0 : currentFirst - fetchSize - 1;
		curMaxRecNo = currentFirst - 1;
		Connection conn = SqlConnect.getInstance().getConnect();
		String sql = String.format(selectSkipQuery, curMaxRecNo) + tableName + orderClause;
		try (PreparedStatement stmnt = conn.prepareStatement(sql);) {
//            stmnt.setMaxRows(curMaxRecNo);
			stmnt.setInt(1, curMinRecNo);
			try (ResultSet rs = stmnt.executeQuery()) {
				fillList(rs);
				if (list.isEmpty()) {
					eof = 1;
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.info(String.valueOf(e));
			eof = 1;
			return null;
		}
		eof = 0;
		isEof = false;
		return (EntityCl) list.get(list.size() - 1);
	}

	protected String determineOrder(String sign) {
		switch (sign) {
		case "<":
		case "<=":
			return "desc";
		default:
			return "asc";
		}
	}

	protected String getSecondSign(String sign) {
		switch (sign) {
		case "<":
			return "<=";
		case ">":
			return ">=";
		default:
			return sign;
		}
	}

	public void close() {
		curMinRecNo = 0;
		curMaxRecNo = 0;
		list.clear();
	}

	public enum OpenMode {
		INPUT, OUTPUT, I_O, EXTEND;
	}

	public abstract void open();

	protected abstract EntityCl fillRec(ResultSet rs) throws SQLException;

	protected abstract void fillList(ResultSet rs) throws SQLException;
}

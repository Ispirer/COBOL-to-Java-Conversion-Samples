package programs;
import java.io.*;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ispirer.sw.db.TransactionManager;
import com.ispirer.sw.exception.ExitProgram;
import com.ispirer.sw.file.FileDescription;
import com.ispirer.sw.strings.AlphanumericFormat;
import com.ispirer.sw.strings.DecimalFormat;
import com.ispirer.sw.types.PictureType;
import com.ispirer.sw.types.StrField;
import models.include.prhist.cpy.*;
import models.mnprocess.*;

public class Mnprocess {
   private static final Logger LOGGER = LoggerFactory.getLogger(Mnprocess.class);
   private static Mnprocess instance;
   private List<String> performThruSet = new LinkedList<>();
   public boolean isCalled = false;
              
                   
   private FileDescription IAvalgrFile = new FileDescription("AVALGR", 3, 0, true);
   private StrField IAvalgrRec = new StrField("X(3)");
   private FileDescription OAvrepFile = new FileDescription("AVREP", 66, 0, true);
   private OAvrepRec oAvrepRec = new OAvrepRec();
   private PictureType<Integer> WsCnt = new PictureType<>(PictureType.Type.Integer, new DecimalFormat("9(7)"));
   private ProdPriceHistory prodPriceHistory = new ProdPriceHistory();
   public PictureType<String> LoadHistStatus = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(2)"), PictureType.DefaultValue.Spaces);
   private HistoryTable historyTable = new HistoryTable();
   private int WsHistInx = 0;
   public List<ProdGrp> prodGrp = Stream.generate(ProdGrp::new).limit(3).collect(Collectors.toList());

                  
   public void mnprocessProcedureDivision() {
      prodPriceHistory.initialize();
      main1000(true);
   }
   private boolean main1000(boolean isPerform) {
      if(initializeCount(false)) {
         return true;
      }
      if(WsCnt.compareTo(0) > 0) {
         if(loadHistory(false)) {
            return true;
         }
      }
      if(isPerform) {
         return initializeCount(true);
      }
      return false;
   }
   private boolean initializeCount(boolean isPerform) {
      try(ResultSet rs = TransactionManager.getInstance().executeInto("SELECT count(*)                       FROM PROD_PRICE_HIST")) {
         if(rs != null && rs.next()) {
            WsCnt.setValue(rs.getInt(1));
         } else {
            TransactionManager.getInstance().handleNotFound();
         }
      } catch(SQLException se) {
         TransactionManager.getInstance().handleError(se);
      }
      System.out.println("PROD-HISTORY-REC NUMBER: " + WsCnt);
      System.out.println(" ");
      if(isPerform) {
         return loadHistory(true);
      }
      return false;
   }
   private boolean loadHistory(boolean isPerform) {
      System.out.println("LOADING OF PRODUCT HISTORY RECORDS...");
      performThruSet.add("exit2000");
      if(declareHistoryCur2000(true) && !performThruSet.contains("exit2000")) {
         return true;
      }
      performThruSet.remove("exit2000");
      WsHistInx = 0;
      while(LoadHistStatus.compareTo(PictureType.DefaultValue.HighValues) != 0) {
         performThruSet.add("setHistoryTab");
         if(fetchHistoryCur(true) && !performThruSet.contains("setHistoryTab")) {
            return true;
         }
         performThruSet.remove("setHistoryTab");
      }
      if(isPerform) {
         return declareHistoryCur2000(true);
      }
      return false;
   }
   private boolean declareHistoryCur2000(boolean isPerform) {
      TransactionManager.getInstance().declareCursor("C1", "SELECT PROD_SYMB, PREV_DAY_PRICE," +
                      " LATEST_PRICE, END_OF_MNTH_PRICE" +
                      " FROM PROD_PRICE_HIST");
      if(isPerform) {
         return openHistoryCur2000(true);
      }
      return false;
   }
   private boolean openHistoryCur2000(boolean isPerform) {
      TransactionManager.getInstance().getCursors().get("C1").open();
      if(TransactionManager.sqlcode != 0) {
         System.out.println("ERROR! SQLCODE - " + TransactionManager.sqlcode);
         if(closeHistoryCur(true)) {
            return true;
         }
         System.exit(0);
      }
      if(isPerform) {
         if(!performThruSet.isEmpty() && performThruSet.contains("exit2000")) {
            removePrevious("exit2000");
            return true;
         }
         return fetchHistoryCur(true);
      }
      return false;
   }
   private boolean fetchHistoryCur(boolean isPerform) {
      try {
         ResultSet rs = TransactionManager.getInstance().getCursors().get("C1").getResultSet();
         if(rs != null && rs.next()) {
            prodPriceHistory.ProdSymb.setValue(rs.getString(1), rs.getMetaData().getColumnType(1));
            prodPriceHistory.PrevDayP.setValue(rs.getBigDecimal(2));
            prodPriceHistory.LatestP.setValue(rs.getBigDecimal(3));
            prodPriceHistory.EndOfMnthP.setValue(rs.getBigDecimal(4));
         } else {
            TransactionManager.getInstance().handleNotFound();
         }
      } catch(SQLException se) {
         TransactionManager.getInstance().handleError(se);
      }
      if(TransactionManager.sqlcode == 1403) {
         LoadHistStatus.setValue(PictureType.DefaultValue.HighValues.toString());
         if(closeHistoryCur(true)) {
            return true;
         }
         System.exit(0);
      } else if(TransactionManager.sqlcode != 0) {
         System.out.println("ERROR! SQLCODE - " + TransactionManager.sqlcode);
         if(closeHistoryCur(true)) {
            return true;
         }
         System.exit(0);
      }
      if(isPerform) {
         return setHistoryTab(true);
      }
      return false;
   }
   private boolean setHistoryTab(boolean isPerform) {
      System.out.println(prodPriceHistory);
      WsHistInx += 1;
      historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.setData(prodPriceHistory.toString().toCharArray());
      if(isPerform) {
         if(!performThruSet.isEmpty() && performThruSet.contains("setHistoryTab")) {
            removePrevious("setHistoryTab");
            return true;
         }
         return closeHistoryCur(true);
      }
      return false;
   }
   private boolean closeHistoryCur(boolean isPerform) {
      System.out.println("LOADING FINISHED.");
      historyTable.WsHistCnt.setValue(WsCnt);
      TransactionManager.getInstance().getCursors().get("C1").close();
      if(isPerform) {
         return avalGrpsRead(true);
      }
      return false;
   }
   private boolean avalGrpsRead(boolean isPerform) {
      System.out.println(" ");
      try {
         IAvalgrFile.openInput(null);
      } catch(IOException e) {
         LOGGER.info(String.valueOf(e));
      }
      System.out.println("AVAILABLE GROUPS: ");
      WsCnt.setValue(0);
      do {
         WsCnt.setValue(WsCnt.add(1));
         try {
            prodGrp.get(WsCnt.getValue() - 1).IGrpnm.setValue(IAvalgrFile.read(IAvalgrRec));
            System.out.println(prodGrp.get(WsCnt.getValue() - 1).IGrpnm);
         } catch(IOException e) {
            try {
               IAvalgrFile.close();
            } catch(IOException e2) {
               LOGGER.info(String.valueOf(e2));
            }
         }
         performThruSet.add("findAvPricesExit");
         if(historyFindAvPrices(true) && !performThruSet.contains("findAvPricesExit")) {
            return true;
         }
         performThruSet.remove("findAvPricesExit");
      } while(WsCnt.compareTo(3) != 0);
      if(isCalled) {
         throw new ExitProgram();
      }
      if(isPerform) {
         return historyFindAvPrices(true);
      }
      return false;
   }
   private boolean historyFindAvPrices(boolean isPerform) {
      System.out.println(" ");
      System.out.println("CALCULATION OF THE AVERAGE COST BY GROUPS...");
      WsHistInx = 0;
      do {
         WsHistInx = WsHistInx + 1;
         if(historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.wsProdSymbDt.WsProdGr.compareTo(prodGrp.get(0).IGrpnm) == 0) {
            prodGrp.get(0).IGrpRecNm.setValue(prodGrp.get(0).IGrpRecNm.add(1));
            prodGrp.get(0).iAvPrices.IWsPrevDayPAv.setValue(prodGrp.get(0).iAvPrices.IWsPrevDayPAv.addTrunc(prodGrp.get(0).iAvPrices.IWsPrevDayPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsPrevDayP));
            prodGrp.get(0).iAvPrices.IWsLatestPAv.setValue(prodGrp.get(0).iAvPrices.IWsLatestPAv.addTrunc(prodGrp.get(0).iAvPrices.IWsLatestPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsLatestP));
            prodGrp.get(0).iAvPrices.IWsEndOfMnthPAv.setValue(prodGrp.get(0).iAvPrices.IWsEndOfMnthPAv.addTrunc(prodGrp.get(0).iAvPrices.IWsEndOfMnthPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsEndOfMnthP));
         } else if(historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.wsProdSymbDt.WsProdGr.compareTo(prodGrp.get(1).IGrpnm) == 0) {
            prodGrp.get(1).IGrpRecNm.setValue(prodGrp.get(1).IGrpRecNm.add(1));
            prodGrp.get(1).iAvPrices.IWsPrevDayPAv.setValue(prodGrp.get(1).iAvPrices.IWsPrevDayPAv.addTrunc(prodGrp.get(1).iAvPrices.IWsPrevDayPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsPrevDayP));
            prodGrp.get(1).iAvPrices.IWsLatestPAv.setValue(prodGrp.get(1).iAvPrices.IWsLatestPAv.addTrunc(prodGrp.get(1).iAvPrices.IWsLatestPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsLatestP));
            prodGrp.get(1).iAvPrices.IWsEndOfMnthPAv.setValue(prodGrp.get(1).iAvPrices.IWsEndOfMnthPAv.addTrunc(prodGrp.get(1).iAvPrices.IWsEndOfMnthPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsEndOfMnthP));
         } else if(historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.wsProdSymbDt.WsProdGr.compareTo(prodGrp.get(2).IGrpnm) == 0) {
            prodGrp.get(2).IGrpRecNm.setValue(prodGrp.get(2).IGrpRecNm.add(1));
            prodGrp.get(2).iAvPrices.IWsPrevDayPAv.setValue(prodGrp.get(2).iAvPrices.IWsPrevDayPAv.addTrunc(prodGrp.get(2).iAvPrices.IWsPrevDayPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsPrevDayP));
            prodGrp.get(2).iAvPrices.IWsLatestPAv.setValue(prodGrp.get(2).iAvPrices.IWsLatestPAv.addTrunc(prodGrp.get(2).iAvPrices.IWsLatestPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsLatestP));
            prodGrp.get(2).iAvPrices.IWsEndOfMnthPAv.setValue(prodGrp.get(2).iAvPrices.IWsEndOfMnthPAv.addTrunc(prodGrp.get(2).iAvPrices.IWsEndOfMnthPAv, historyTable.prodHistories.get(WsHistInx - 1).wsProdPriceHistory.WsEndOfMnthP));
         } else {
            System.out.println("WRONG GROUP WAS FOUND.");
         }
      } while(historyTable.WsHistCnt.compareTo(WsHistInx) != 0);
      System.out.println(" ");
      oAvrepRec.setData(("" + "| GROUP " + "| NUM " + "| PREV DAY PRICE " + "| LATEST PRICE " + "| END OF MNTH PRICE|").toCharArray());
      System.out.println(oAvrepRec);
      WsCnt.setValue(0);
      do {
         WsCnt.setValue(WsCnt.add(1));
         prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsPrevDayPAv.setValue(prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsPrevDayPAv.divideTrunc(prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsPrevDayPAv, 3));
         prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsLatestPAv.setValue(prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsLatestPAv.divideTrunc(prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsLatestPAv, 3));
         prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsEndOfMnthPAv.setValue(prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsEndOfMnthPAv.divideTrunc(prodGrp.get(WsCnt.getValue() - 1).iAvPrices.IWsEndOfMnthPAv, 3));
         System.out.println(prodGrp.get(WsCnt.getValue() - 1));
      } while(WsCnt.compareTo(3) != 0);
      System.out.println("CALCULATION IS FINISHED.");
      System.out.println(" ");
      if(isPerform) {
         return writeRepHeader(true);
      }
      return false;
   }
   private boolean writeRepHeader(boolean isPerform) {
                 
                 
      try {
         OAvrepFile.openOutput(null, false);
         OAvrepFile.write(oAvrepRec.toString());
         OAvrepFile.close();
      } catch(IOException e) {
         LOGGER.info(String.valueOf(e));
      }
      if(isPerform) {
         return writeAvpriceVal(true);
      }
      return false;
   }
   private boolean writeAvpriceVal(boolean isPerform) {
      try {
         OAvrepFile.openOutput(null, true);
      } catch(IOException e) {
         LOGGER.info(String.valueOf(e));
      }
      WsCnt.setValue(0);
      do {
         WsCnt.setValue(WsCnt.add(1));
         oAvrepRec.setData(prodGrp.get(WsCnt.getValue() - 1).toString().toCharArray());
         try {
            OAvrepFile.write(oAvrepRec.toString());
         } catch(IOException e) {
            LOGGER.info(String.valueOf(e));
         }
      } while(WsCnt.compareTo(3) != 0);
      try {
         OAvrepFile.close();
      } catch(IOException e) {
         LOGGER.info(String.valueOf(e));
      }
      return true;
   } 
   public static Mnprocess getInstance() {
      return instance == null? instance = new Mnprocess(): instance;
   }
 
   public static void cancel() {
      instance = new Mnprocess();
   }
 
   
   public void removePrevious(String parName) {
      while (!performThruSet.get(performThruSet.size() - 1).equals(parName)) {
         performThruSet.remove(performThruSet.size() - 1);
      }
   }

}

package programs;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ispirer.sw.db.TransactionManager;
import com.ispirer.sw.exception.ExitProgram;
import com.ispirer.sw.strings.AlphanumericFormat;
import com.ispirer.sw.types.PictureType;

public class Drptab {
   private static final Logger LOGGER = LoggerFactory.getLogger(Drptab.class);
   private static Drptab instance;
   public boolean isCalled = false;
   public PictureType<String> TableNm = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(100)"));
   public void drptabProcedureDivision() {
      drptabProcedureDivision(null);
   }
   public void drptabProcedureDivision(String TableNmParam) {
      if(TableNmParam != null) {
         TableNm.setValue(TableNmParam);
      }
      System.out.println(" ");
      switch(TableNm.getValue().trim()) {
         case "PROD_PRICE_HIST" :
            dropProdPriceHist(true);
            System.exit(0);
         default :
            System.out.println("Unknown action.");
            break;
      }
      if(isCalled) {
         throw new ExitProgram();
      }
      dropProdPriceHist(true);
   }
   private void dropProdPriceHist(boolean isPerform) {
      TransactionManager.getInstance().executeUpdate("DROP TABLE PROD_PRICE_HIST");
      if(TransactionManager.sqlcode == 0) {
         System.out.println("TABLE WAS DROPPED.");
      } else {
         System.out.println("Unable to drop table");
         System.out.println("SQLCODE = " + TransactionManager.sqlcode);
      }
   } 
   public static Drptab getInstance() {
      return instance == null? instance = new Drptab(): instance;
   }
 
   public static void cancel() {
      instance = new Drptab();
   }
 public PictureType<String> getTableNm() {
      return TableNm;
   }
}

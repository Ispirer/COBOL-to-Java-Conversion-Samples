package programs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ispirer.sw.db.SqlConnect;
import com.ispirer.sw.db.TransactionManager;
import com.ispirer.sw.exception.ExitProgram;
import com.ispirer.sw.strings.AlphanumericFormat;
import com.ispirer.sw.types.PictureType;
import com.ispirer.sw.exception.StopRun;
import models.programdemo.*;

public class Programdemo {
   private static final Logger LOGGER = LoggerFactory.getLogger(Programdemo.class);
   private static Programdemo instance;
   public boolean isCalled = false;
   private Username username = new Username();
   private Passwd passwd = new Passwd();
   private Dbname dbname = new Dbname();
   private Crtab crtab = Crtab.getInstance();
   private Mnprocess mnprocess = Mnprocess.getInstance();
   private Drptab drptab = Drptab.getInstance();
   public void programdemoProcedureDivision() {
      System.out.println("Migration ProCOBOL to Oracle PL/SQL");
      username.UsernameArr.setValue("ora");
      username.UsernameLen.setValue(4);
      passwd.PasswdArr.setValue("ora");
      passwd.PasswdLen.setValue(3);
      dbname.DbnameArr.setValue("UTEST");
      dbname.DbnameLen.setValue(7);
      System.out.println("CONNECTING...");
      SqlConnect.getInstance().connect(username.toString(), passwd.toString(), dbname.toString());
      if(TransactionManager.sqlcode == 0) {
         System.out.println(" ");
         System.out.println("CONNECTION SUCCESSFUL.");
         System.out.println("RUN PROCCESS.");
         crtab.isCalled = true;
         try {
            crtab.crtabProcedureDivision();
         } catch(ExitProgram e) {
            LOGGER.info("EXIT PROGRAM");
         }
         crtab.isCalled = false;
         mnprocess.isCalled = true;
         try {
            mnprocess.mnprocessProcedureDivision();
         } catch(ExitProgram e) {
            LOGGER.info("EXIT PROGRAM");
         }
         mnprocess.isCalled = false;
         drptab.isCalled = true;
         try {
            drptab.drptabProcedureDivision("PROD_PRICE_HIST");
         } catch(ExitProgram e) {
            LOGGER.info("EXIT PROGRAM");
         }
         drptab.isCalled = false;
         System.out.println("PROCESS IS FINISHED.");
      } else {
         System.out.println("UNABLE TO CONNECT!");
         System.out.println("SQLCODE = " + TransactionManager.sqlcode);
         System.out.println("PROGRAM WAS STOPPED!");
      }
      throw new StopRun();
   } 
   public static Programdemo getInstance() {
      return instance == null? instance = new Programdemo(): instance;
   }
 
   public static void cancel() {
      instance = new Programdemo();
   }

}

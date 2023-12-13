package programs;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ispirer.sw.db.TransactionManager;
import com.ispirer.sw.exception.ExitProgram;
import com.ispirer.sw.strings.AlphanumericFormat;
import com.ispirer.sw.strings.DecimalFormat;
import com.ispirer.sw.types.PictureType;
import com.ispirer.sw.exception.StopRun;

public class Crtab {
	private static final Logger LOGGER = LoggerFactory.getLogger(Crtab.class);
	private static Crtab instance;
	public boolean isCalled = false;

	private Drptab drptab = Drptab.getInstance();

	public void crtabProcedureDivision() {
		System.out.println(" ");
		System.out.println("CREATING PROD_PRICE_HIST TABLE AND INSERTING DATA...");
		TransactionManager.getInstance().executeUpdate("CREATE TABLE PROD_PRICE_HIST " + "               ( "
				+ "                   PROD_SYMB        VARCHAR2(13), "
				+ "                   PREV_DAY_PRICE   NUMBER(8,3), "
				+ "                   LATEST_PRICE   NUMBER(8,3),                   END_OF_MNTH_PRICE   NUMBER(8,3)               )");
		if (TransactionManager.sqlcode == 0) {
			System.out.println("Table was created.");
			insertData(false);
			if (isCalled) {
				throw new ExitProgram();
			}
		} else {
			System.out.println("UNABLE TO CREATE TABLE!");
			System.out.println("SQLCODE = " + TransactionManager.sqlcode);
			System.out.println("PROGRAM WAS STOPPED!");
			throw new StopRun();
		}
		insertData(true);
	}

	private void insertData(boolean isPerform) {
		TransactionManager.getInstance().execute("INSERT INTO PROD_PRICE_HIST "
				+ "              SELECT PROD.PROD_NM || PROD.PROD_GR, "
				+ "                     A.PREV_DAY_PRICE, B.LATEST_PRICE, "
				+ "                     C.END_OF_MNTH_PRICE "
				+ "              FROM PROD, PROD_PREV_DAY_PR A, PROD_LATEST_PR B, "
				+ "                   PROD_END_OF_MNTH_PR C "
				+ "              WHERE PROD.PROD_ID = A.PROD_ID AND                    B.PROD_ID = C.PROD_ID AND                    PROD.PROD_ID = B.PROD_ID");
		if (TransactionManager.sqlcode == 0) {
			TransactionManager.getInstance().commit();
			System.out.println("DATA WAS INSERTED.");
		} else {
			System.out.println("UNABLE TO INSERT DATA!");
			System.out.println("SQLCODE = " + TransactionManager.sqlcode);
			drptab.isCalled = true;
			try {
				drptab.drptabProcedureDivision("PROD_PRICE_HIST");
			} catch (ExitProgram e) {
				LOGGER.info("EXIT PROGRAM");
			}
			drptab.isCalled = false;
			System.out.println("PROGRAM WAS STOPPED");
			throw new StopRun();
		}
	}

	public static Crtab getInstance() {
		return instance == null ? instance = new Crtab() : instance;
	}

	public static void cancel() {
		instance = new Crtab();
	}

}

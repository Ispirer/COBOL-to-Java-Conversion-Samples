package models.mnprocess;
import java.math.BigDecimal;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class WsProdPriceHistory extends StructureModel {
   private int sizes[];
   public StrField WsProdSymb = new StrField("X(13)");
   public WsProdSymbDt wsProdSymbDt = new WsProdSymbDt(WsProdSymb);
   public PictureType<BigDecimal> WsPrevDayP = new PictureType<>(PictureType.Type.BigDecimal, new DecimalFormat("9(5)v9(3)"));
   public PictureType<BigDecimal> WsLatestP = new PictureType<>(PictureType.Type.BigDecimal, new DecimalFormat("9(5)v9(3)"));
   public PictureType<BigDecimal> WsEndOfMnthP = new PictureType<>(PictureType.Type.BigDecimal, new DecimalFormat("9(5)v9(3)"));
   public WsProdPriceHistory() {
      sizes = new int[] { WsProdSymb.getSize(),WsPrevDayP.getSize(),WsLatestP.getSize(),WsEndOfMnthP.getSize() };
   }
   @Override
   public String toString() {
      return "" + WsProdSymb + WsPrevDayP + WsLatestP + WsEndOfMnthP;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      WsProdSymb.setData(data[0]);
      wsProdSymbDt.setData(data[0].toCharArray());
      WsPrevDayP.setDataFromFile(data[1].getBytes());
      WsLatestP.setDataFromFile(data[2].getBytes());
      WsEndOfMnthP.setDataFromFile(data[3].getBytes());
   }
   @Override
   public int getSize() {
      int sum = 0;
      for(int i = 0; i < sizes.length; i ++) {
         sum += sizes[i];
      }
      return sum;
   }
   @Override
   public void initialize() {
      WsProdSymb.initialize();
      wsProdSymbDt.initialize();
      WsPrevDayP.initialize();
      WsLatestP.initialize();
      WsEndOfMnthP.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, WsProdSymb.toFile());
      structure = ArrayUtils.addAll(structure, WsPrevDayP.toFile());
      structure = ArrayUtils.addAll(structure, WsLatestP.toFile());
      structure = ArrayUtils.addAll(structure, WsEndOfMnthP.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      WsProdSymb.setDataFromFile(Arrays.copyOf(bytes, WsProdSymb.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsProdSymb.getSize(), bytes.length);
      wsProdSymbDt.setData(WsProdSymb.toString().toCharArray());
      WsPrevDayP.setDataFromFile(Arrays.copyOf(bytes, WsPrevDayP.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsPrevDayP.getSize(), bytes.length);
      WsLatestP.setDataFromFile(Arrays.copyOf(bytes, WsLatestP.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsLatestP.getSize(), bytes.length);
      WsEndOfMnthP.setDataFromFile(Arrays.copyOf(bytes, WsEndOfMnthP.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsEndOfMnthP.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      WsProdSymb.setDefaultValue(value);
      wsProdSymbDt.setData(WsProdSymb.toString().toCharArray());
      WsPrevDayP.setDefaultValueFromStructure(value);
      WsLatestP.setDefaultValueFromStructure(value);
      WsEndOfMnthP.setDefaultValueFromStructure(value);
   }
}
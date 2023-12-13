package models.include.prhist.cpy;
import java.math.BigDecimal;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class ProdPriceHistory extends StructureModel {
   private int sizes[];
   public PictureType<String> ProdSymb = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(13)"));
   public PictureType<BigDecimal> PrevDayP = new PictureType<>(PictureType.Type.BigDecimal, new DecimalFormat("9(5)v9(3)"));
   public PictureType<BigDecimal> LatestP = new PictureType<>(PictureType.Type.BigDecimal, new DecimalFormat("9(5)v9(3)"));
   public PictureType<BigDecimal> EndOfMnthP = new PictureType<>(PictureType.Type.BigDecimal, new DecimalFormat("9(5)v9(3)"));
   public ProdPriceHistory() {
      sizes = new int[] { ProdSymb.getSize(),PrevDayP.getSize(),LatestP.getSize(),EndOfMnthP.getSize() };
   }
   @Override
   public String toString() {
      return "" + ProdSymb + PrevDayP + LatestP + EndOfMnthP;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      ProdSymb.setValue(data[0]);
      PrevDayP.setDataFromFile(data[1].getBytes());
      LatestP.setDataFromFile(data[2].getBytes());
      EndOfMnthP.setDataFromFile(data[3].getBytes());
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
      ProdSymb.initialize();
      PrevDayP.initialize();
      LatestP.initialize();
      EndOfMnthP.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, ProdSymb.toFile());
      structure = ArrayUtils.addAll(structure, PrevDayP.toFile());
      structure = ArrayUtils.addAll(structure, LatestP.toFile());
      structure = ArrayUtils.addAll(structure, EndOfMnthP.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      ProdSymb.setDataFromFile(Arrays.copyOf(bytes, ProdSymb.getSize()));
      bytes = Arrays.copyOfRange(bytes, ProdSymb.getSize(), bytes.length);
      PrevDayP.setDataFromFile(Arrays.copyOf(bytes, PrevDayP.getSize()));
      bytes = Arrays.copyOfRange(bytes, PrevDayP.getSize(), bytes.length);
      LatestP.setDataFromFile(Arrays.copyOf(bytes, LatestP.getSize()));
      bytes = Arrays.copyOfRange(bytes, LatestP.getSize(), bytes.length);
      EndOfMnthP.setDataFromFile(Arrays.copyOf(bytes, EndOfMnthP.getSize()));
      bytes = Arrays.copyOfRange(bytes, EndOfMnthP.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      ProdSymb.setDefaultValueFromStructure(value);
      PrevDayP.setDefaultValueFromStructure(value);
      LatestP.setDefaultValueFromStructure(value);
      EndOfMnthP.setDefaultValueFromStructure(value);
   }
}
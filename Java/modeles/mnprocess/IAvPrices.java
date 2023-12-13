package models.mnprocess;
import java.math.BigDecimal;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class IAvPrices extends StructureModel {
   private int sizes[];
   public PictureType<BigDecimal> IWsPrevDayPAv = new PictureType<>(new DecimalFormat("9(10)v9(6)"), new BigDecimal("0"));
   public PictureType<String> Filler4 = new PictureType<>(new AlphanumericFormat("X(1)"), "|");
   public PictureType<BigDecimal> IWsLatestPAv = new PictureType<>(new DecimalFormat("9(10)v9(4)"), new BigDecimal("0"));
   public PictureType<String> Filler5 = new PictureType<>(new AlphanumericFormat("X(1)"), "|");
   public PictureType<BigDecimal> IWsEndOfMnthPAv = new PictureType<>(new DecimalFormat("9(10)v9(8)"), new BigDecimal("0"));
   public PictureType<String> Filler6 = new PictureType<>(new AlphanumericFormat("X(1)"), "|");
   public IAvPrices() {
      sizes = new int[] { IWsPrevDayPAv.getSize(),Filler4.getSize(),IWsLatestPAv.getSize(),Filler5.getSize(),
      IWsEndOfMnthPAv.getSize(),Filler6.getSize() };
   }
   @Override
   public String toString() {
      return "" + IWsPrevDayPAv + Filler4 + IWsLatestPAv + Filler5 + IWsEndOfMnthPAv + Filler6;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      IWsPrevDayPAv.setDataFromFile(data[0].getBytes());
      Filler4.setValue(data[1]);
      IWsLatestPAv.setDataFromFile(data[2].getBytes());
      Filler5.setValue(data[3]);
      IWsEndOfMnthPAv.setDataFromFile(data[4].getBytes());
      Filler6.setValue(data[5]);
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
      IWsPrevDayPAv.initialize();
      Filler4.initialize();
      IWsLatestPAv.initialize();
      Filler5.initialize();
      IWsEndOfMnthPAv.initialize();
      Filler6.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, IWsPrevDayPAv.toFile());
      structure = ArrayUtils.addAll(structure, Filler4.toFile());
      structure = ArrayUtils.addAll(structure, IWsLatestPAv.toFile());
      structure = ArrayUtils.addAll(structure, Filler5.toFile());
      structure = ArrayUtils.addAll(structure, IWsEndOfMnthPAv.toFile());
      structure = ArrayUtils.addAll(structure, Filler6.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      IWsPrevDayPAv.setDataFromFile(Arrays.copyOf(bytes, IWsPrevDayPAv.getSize()));
      bytes = Arrays.copyOfRange(bytes, IWsPrevDayPAv.getSize(), bytes.length);
      Filler4.setDataFromFile(Arrays.copyOf(bytes, Filler4.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler4.getSize(), bytes.length);
      IWsLatestPAv.setDataFromFile(Arrays.copyOf(bytes, IWsLatestPAv.getSize()));
      bytes = Arrays.copyOfRange(bytes, IWsLatestPAv.getSize(), bytes.length);
      Filler5.setDataFromFile(Arrays.copyOf(bytes, Filler5.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler5.getSize(), bytes.length);
      IWsEndOfMnthPAv.setDataFromFile(Arrays.copyOf(bytes, IWsEndOfMnthPAv.getSize()));
      bytes = Arrays.copyOfRange(bytes, IWsEndOfMnthPAv.getSize(), bytes.length);
      Filler6.setDataFromFile(Arrays.copyOf(bytes, Filler6.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler6.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      IWsPrevDayPAv.setDefaultValueFromStructure(value);
      Filler4.setDefaultValueFromStructure(value);
      IWsLatestPAv.setDefaultValueFromStructure(value);
      Filler5.setDefaultValueFromStructure(value);
      IWsEndOfMnthPAv.setDefaultValueFromStructure(value);
      Filler6.setDefaultValueFromStructure(value);
   }
}
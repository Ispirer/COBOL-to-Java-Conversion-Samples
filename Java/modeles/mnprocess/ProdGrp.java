package models.mnprocess;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class ProdGrp extends StructureModel {
   private int sizes[];
   public PictureType<String> Filler1 = new PictureType<>(new AlphanumericFormat("X(1)"), "|");
   public PictureType<String> IGrpnm = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(7)"));
   public PictureType<String> Filler2 = new PictureType<>(new AlphanumericFormat("X(1)"), "|");
   public PictureType<Integer> IGrpRecNm = new PictureType<>(new DecimalFormat("9(5)"), 0);
   public PictureType<String> Filler3 = new PictureType<>(new AlphanumericFormat("X(1)"), "|");
   public IAvPrices iAvPrices = new IAvPrices();
   public ProdGrp() {
      sizes = new int[] { Filler1.getSize(),IGrpnm.getSize(),Filler2.getSize(),IGrpRecNm.getSize(),
      Filler3.getSize(),iAvPrices.getSize() };
   }
   @Override
   public String toString() {
      return "" + Filler1 + IGrpnm + Filler2 + IGrpRecNm + Filler3 + iAvPrices;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      Filler1.setValue(data[0]);
      IGrpnm.setValue(data[1]);
      Filler2.setValue(data[2]);
      IGrpRecNm.setDataFromFile(data[3].getBytes());
      Filler3.setValue(data[4]);
      iAvPrices.setData(data[5].toCharArray());
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
      Filler1.initialize();
      IGrpnm.initialize();
      Filler2.initialize();
      IGrpRecNm.initialize();
      Filler3.initialize();
      iAvPrices.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, Filler1.toFile());
      structure = ArrayUtils.addAll(structure, IGrpnm.toFile());
      structure = ArrayUtils.addAll(structure, Filler2.toFile());
      structure = ArrayUtils.addAll(structure, IGrpRecNm.toFile());
      structure = ArrayUtils.addAll(structure, Filler3.toFile());
      structure = ArrayUtils.addAll(structure, iAvPrices.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      Filler1.setDataFromFile(Arrays.copyOf(bytes, Filler1.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler1.getSize(), bytes.length);
      IGrpnm.setDataFromFile(Arrays.copyOf(bytes, IGrpnm.getSize()));
      bytes = Arrays.copyOfRange(bytes, IGrpnm.getSize(), bytes.length);
      Filler2.setDataFromFile(Arrays.copyOf(bytes, Filler2.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler2.getSize(), bytes.length);
      IGrpRecNm.setDataFromFile(Arrays.copyOf(bytes, IGrpRecNm.getSize()));
      bytes = Arrays.copyOfRange(bytes, IGrpRecNm.getSize(), bytes.length);
      Filler3.setDataFromFile(Arrays.copyOf(bytes, Filler3.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler3.getSize(), bytes.length);
      iAvPrices.setDataFromFile(Arrays.copyOf(bytes, iAvPrices.getSize()));
      bytes = Arrays.copyOfRange(bytes, iAvPrices.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      Filler1.setDefaultValueFromStructure(value);
      IGrpnm.setDefaultValueFromStructure(value);
      Filler2.setDefaultValueFromStructure(value);
      IGrpRecNm.setDefaultValueFromStructure(value);
      Filler3.setDefaultValueFromStructure(value);
      iAvPrices.setDefaultValue(value);
   }
}
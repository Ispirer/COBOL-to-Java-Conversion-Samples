package models.programdemo;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class Dbname extends StructureModel {
   private int sizes[];
   public PictureType<Integer> DbnameLen = new PictureType<>(PictureType.Type.Integer, new DecimalFormat("S9(4)", DecimalFormat.CompType.Comp));
   public PictureType<String> DbnameArr = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(10)"));
   public Dbname() {
      sizes = new int[] { DbnameLen.getSize(),DbnameArr.getSize() };
   }
   @Override
   public String toString() {
      return "" + new String(DbnameLen.toFile()) + DbnameArr;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      DbnameLen.setDataFromFile(data[0].getBytes());
      DbnameArr.setValue(data[1]);
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
      DbnameLen.initialize();
      DbnameArr.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, DbnameLen.toFile());
      structure = ArrayUtils.addAll(structure, DbnameArr.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      DbnameLen.setDataFromFile(Arrays.copyOf(bytes, DbnameLen.getSize()));
      bytes = Arrays.copyOfRange(bytes, DbnameLen.getSize(), bytes.length);
      DbnameArr.setDataFromFile(Arrays.copyOf(bytes, DbnameArr.getSize()));
      bytes = Arrays.copyOfRange(bytes, DbnameArr.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      DbnameLen.setDefaultValueFromStructure(value);
      DbnameArr.setDefaultValueFromStructure(value);
   }
}
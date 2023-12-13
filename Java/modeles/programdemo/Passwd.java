package models.programdemo;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class Passwd extends StructureModel {
   private int sizes[];
   public PictureType<Integer> PasswdLen = new PictureType<>(PictureType.Type.Integer, new DecimalFormat("S9(4)", DecimalFormat.CompType.Comp));
   public PictureType<String> PasswdArr = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(10)"));
   public Passwd() {
      sizes = new int[] { PasswdLen.getSize(),PasswdArr.getSize() };
   }
   @Override
   public String toString() {
      return "" + new String(PasswdLen.toFile()) + PasswdArr;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      PasswdLen.setDataFromFile(data[0].getBytes());
      PasswdArr.setValue(data[1]);
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
      PasswdLen.initialize();
      PasswdArr.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, PasswdLen.toFile());
      structure = ArrayUtils.addAll(structure, PasswdArr.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      PasswdLen.setDataFromFile(Arrays.copyOf(bytes, PasswdLen.getSize()));
      bytes = Arrays.copyOfRange(bytes, PasswdLen.getSize(), bytes.length);
      PasswdArr.setDataFromFile(Arrays.copyOf(bytes, PasswdArr.getSize()));
      bytes = Arrays.copyOfRange(bytes, PasswdArr.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      PasswdLen.setDefaultValueFromStructure(value);
      PasswdArr.setDefaultValueFromStructure(value);
   }
}
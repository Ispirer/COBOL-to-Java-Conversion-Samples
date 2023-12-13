package models.programdemo;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class Username extends StructureModel {
   private int sizes[];
   public PictureType<Integer> UsernameLen = new PictureType<>(PictureType.Type.Integer, new DecimalFormat("S9(4)", DecimalFormat.CompType.Comp));
   public PictureType<String> UsernameArr = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(10)"));
   public Username() {
      sizes = new int[] { UsernameLen.getSize(),UsernameArr.getSize() };
   }
   @Override
   public String toString() {
      return "" + new String(UsernameLen.toFile()) + UsernameArr;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      UsernameLen.setDataFromFile(data[0].getBytes());
      UsernameArr.setValue(data[1]);
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
      UsernameLen.initialize();
      UsernameArr.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, UsernameLen.toFile());
      structure = ArrayUtils.addAll(structure, UsernameArr.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      UsernameLen.setDataFromFile(Arrays.copyOf(bytes, UsernameLen.getSize()));
      bytes = Arrays.copyOfRange(bytes, UsernameLen.getSize(), bytes.length);
      UsernameArr.setDataFromFile(Arrays.copyOf(bytes, UsernameArr.getSize()));
      bytes = Arrays.copyOfRange(bytes, UsernameArr.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      UsernameLen.setDefaultValueFromStructure(value);
      UsernameArr.setDefaultValueFromStructure(value);
   }
}
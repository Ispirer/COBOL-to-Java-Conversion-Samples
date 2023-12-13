package models.mnprocess;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class WsProdSymbDt extends StructureModel {
   private int sizes[];
   public PictureType<String> WsProdDt = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(10)"));
   public PictureType<String> WsProdGr = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(3)"));
   public WsProdSymbDt(StructureModel ... objs) {
      sizes = new int[] { WsProdDt.getSize(),WsProdGr.getSize() };
      initRedefineObjs(objs);
   }
   @Override
   public String toString() {
      return "" + WsProdDt + WsProdGr;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      WsProdDt.setValue(data[0]);
      WsProdGr.setValue(data[1]);
      redefine();
   }
   public void setWsProdDt(String WsProdDt) {
      this.WsProdDt.setValue(WsProdDt);
      redefine();
   }
   public void setWsProdGr(String WsProdGr) {
      this.WsProdGr.setValue(WsProdGr);
      redefine();
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
      WsProdDt.initialize();
      WsProdGr.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, WsProdDt.toFile());
      structure = ArrayUtils.addAll(structure, WsProdGr.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      WsProdDt.setDataFromFile(Arrays.copyOf(bytes, WsProdDt.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsProdDt.getSize(), bytes.length);
      WsProdGr.setDataFromFile(Arrays.copyOf(bytes, WsProdGr.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsProdGr.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      WsProdDt.setDefaultValueFromStructure(value);
      WsProdGr.setDefaultValueFromStructure(value);
      redefine();
   }
}
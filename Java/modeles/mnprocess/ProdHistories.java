package models.mnprocess;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.types.*;

public class ProdHistories extends StructureModel {
   private int sizes[];
   public WsProdPriceHistory wsProdPriceHistory = new WsProdPriceHistory();
   public ProdHistories() {
      sizes = new int[] { wsProdPriceHistory.getSize() };
   }
   @Override
   public String toString() {
      return "" + wsProdPriceHistory;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      wsProdPriceHistory.setData(data[0].toCharArray());
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
      wsProdPriceHistory.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, wsProdPriceHistory.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      wsProdPriceHistory.setDataFromFile(Arrays.copyOf(bytes, wsProdPriceHistory.getSize()));
      bytes = Arrays.copyOfRange(bytes, wsProdPriceHistory.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      wsProdPriceHistory.setDefaultValue(value);
   }
}
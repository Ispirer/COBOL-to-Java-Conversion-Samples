package models.mnprocess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class HistoryTable extends StructureModel {
   private int sizes[];
   public PictureType<Integer> WsHistCnt = new PictureType<>(PictureType.Type.Integer, new DecimalFormat("9(5)"));
   public List<ProdHistories> prodHistories = Stream.generate(ProdHistories::new).limit(1000).collect(Collectors.toList());
   private int WsHistInx = 0;
   public HistoryTable() {
      sizes = new int[] { WsHistCnt.getSize(),prodHistories.stream().mapToInt(StructureModel::getSize).sum() };
   }
   @Override
   public String toString() {
      return "" + WsHistCnt + getArrayString(prodHistories, WsHistCnt.getValue());
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      WsHistCnt.setDataFromFile(data[0].getBytes());
      StringUtil.initializeArray(prodHistories, data[1]);
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
      WsHistCnt.initialize();
      for(int i = 0; i < 1000; i ++) {
         prodHistories.get(i).initialize();
      }
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, WsHistCnt.toFile());
      for(StructureModel el : prodHistories) {
         structure = ArrayUtils.addAll(structure, el.toFile());
      }
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      WsHistCnt.setDataFromFile(Arrays.copyOf(bytes, WsHistCnt.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsHistCnt.getSize(), bytes.length);
      for(StructureModel el : prodHistories) {
         el.setDataFromFile(Arrays.copyOf(bytes, el.getSize()));
         bytes = Arrays.copyOfRange(bytes, el.getSize(), bytes.length);
      }
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      WsHistCnt.setDefaultValueFromStructure(value);
      for(StructureModel el : prodHistories) {
         el.setDefaultValue(value);
      }
   }
}
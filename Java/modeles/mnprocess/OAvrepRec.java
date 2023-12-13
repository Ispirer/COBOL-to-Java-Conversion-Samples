package models.mnprocess;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import com.ispirer.sw.strings.*;
import com.ispirer.sw.types.*;

public class OAvrepRec extends StructureModel {
   private int sizes[];
   public PictureType<String> Filler7 = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(1)"));
   public PictureType<String> Grpnm = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(7)"));
   public PictureType<String> Filler8 = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(1)"));
   public PictureType<String> GrpRecNm = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(5)"));
   public PictureType<String> Filler9 = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(1)"));
   public PictureType<String> WsPrevDayPAv = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(16)"));
   public PictureType<String> Filler10 = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(1)"));
   public PictureType<String> WsLatestPAv = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(14)"));
   public PictureType<String> Filler11 = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(1)"));
   public PictureType<String> WsEndOfMnthPAv = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(18)"));
   public PictureType<String> Filler12 = new PictureType<>(PictureType.Type.String, new AlphanumericFormat("X(1)"));
   public OAvrepRec() {
      sizes = new int[] { Filler7.getSize(),Grpnm.getSize(),Filler8.getSize(),GrpRecNm.getSize(),
      Filler9.getSize(),WsPrevDayPAv.getSize(),Filler10.getSize(),WsLatestPAv.getSize(),
      Filler11.getSize(),WsEndOfMnthPAv.getSize(),Filler12.getSize() };
   }
   @Override
   public String toString() {
      return "" + Filler7 + Grpnm + Filler8 + GrpRecNm + Filler9 + WsPrevDayPAv + Filler10 + WsLatestPAv + Filler11 + WsEndOfMnthPAv + Filler12;
   }
   @Override
   public void setData(char[] data) {
      setData(getStringValues(data, sizes));
   }
   @Override
   public void setData(String[] data) {
      Filler7.setValue(data[0]);
      Grpnm.setValue(data[1]);
      Filler8.setValue(data[2]);
      GrpRecNm.setValue(data[3]);
      Filler9.setValue(data[4]);
      WsPrevDayPAv.setValue(data[5]);
      Filler10.setValue(data[6]);
      WsLatestPAv.setValue(data[7]);
      Filler11.setValue(data[8]);
      WsEndOfMnthPAv.setValue(data[9]);
      Filler12.setValue(data[10]);
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
      Filler7.initialize();
      Grpnm.initialize();
      Filler8.initialize();
      GrpRecNm.initialize();
      Filler9.initialize();
      WsPrevDayPAv.initialize();
      Filler10.initialize();
      WsLatestPAv.initialize();
      Filler11.initialize();
      WsEndOfMnthPAv.initialize();
      Filler12.initialize();
   }
   @Override
   public byte[] toFile() {
      byte[] structure = new byte[] { };
      structure = ArrayUtils.addAll(structure, Filler7.toFile());
      structure = ArrayUtils.addAll(structure, Grpnm.toFile());
      structure = ArrayUtils.addAll(structure, Filler8.toFile());
      structure = ArrayUtils.addAll(structure, GrpRecNm.toFile());
      structure = ArrayUtils.addAll(structure, Filler9.toFile());
      structure = ArrayUtils.addAll(structure, WsPrevDayPAv.toFile());
      structure = ArrayUtils.addAll(structure, Filler10.toFile());
      structure = ArrayUtils.addAll(structure, WsLatestPAv.toFile());
      structure = ArrayUtils.addAll(structure, Filler11.toFile());
      structure = ArrayUtils.addAll(structure, WsEndOfMnthPAv.toFile());
      structure = ArrayUtils.addAll(structure, Filler12.toFile());
      return structure;
   }
   @Override
   public void setDataFromFile(byte[] bytes) {
      bytes = getFullArray(bytes);
      Filler7.setDataFromFile(Arrays.copyOf(bytes, Filler7.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler7.getSize(), bytes.length);
      Grpnm.setDataFromFile(Arrays.copyOf(bytes, Grpnm.getSize()));
      bytes = Arrays.copyOfRange(bytes, Grpnm.getSize(), bytes.length);
      Filler8.setDataFromFile(Arrays.copyOf(bytes, Filler8.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler8.getSize(), bytes.length);
      GrpRecNm.setDataFromFile(Arrays.copyOf(bytes, GrpRecNm.getSize()));
      bytes = Arrays.copyOfRange(bytes, GrpRecNm.getSize(), bytes.length);
      Filler9.setDataFromFile(Arrays.copyOf(bytes, Filler9.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler9.getSize(), bytes.length);
      WsPrevDayPAv.setDataFromFile(Arrays.copyOf(bytes, WsPrevDayPAv.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsPrevDayPAv.getSize(), bytes.length);
      Filler10.setDataFromFile(Arrays.copyOf(bytes, Filler10.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler10.getSize(), bytes.length);
      WsLatestPAv.setDataFromFile(Arrays.copyOf(bytes, WsLatestPAv.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsLatestPAv.getSize(), bytes.length);
      Filler11.setDataFromFile(Arrays.copyOf(bytes, Filler11.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler11.getSize(), bytes.length);
      WsEndOfMnthPAv.setDataFromFile(Arrays.copyOf(bytes, WsEndOfMnthPAv.getSize()));
      bytes = Arrays.copyOfRange(bytes, WsEndOfMnthPAv.getSize(), bytes.length);
      Filler12.setDataFromFile(Arrays.copyOf(bytes, Filler12.getSize()));
      bytes = Arrays.copyOfRange(bytes, Filler12.getSize(), bytes.length);
   }
   @Override
   public void setDefaultValue(PictureType.DefaultValue value) {
      Filler7.setDefaultValueFromStructure(value);
      Grpnm.setDefaultValueFromStructure(value);
      Filler8.setDefaultValueFromStructure(value);
      GrpRecNm.setDefaultValueFromStructure(value);
      Filler9.setDefaultValueFromStructure(value);
      WsPrevDayPAv.setDefaultValueFromStructure(value);
      Filler10.setDefaultValueFromStructure(value);
      WsLatestPAv.setDefaultValueFromStructure(value);
      Filler11.setDefaultValueFromStructure(value);
      WsEndOfMnthPAv.setDefaultValueFromStructure(value);
      Filler12.setDefaultValueFromStructure(value);
   }
}
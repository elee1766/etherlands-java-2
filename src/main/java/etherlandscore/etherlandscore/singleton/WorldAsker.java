package etherlandscore.etherlandscore.singleton;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.tr7zw.nbtinjector.javassist.bytecode.AccessFlag;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.services.ImpatientAsker;
import etherlandscore.etherlandscore.state.District;
import etherlandscore.etherlandscore.state.Gamer;
import kotlin.Triple;

import java.util.*;

public class WorldAsker {

  private static Cache<String, String> forever_cache = Caffeine.newBuilder()
      .maximumSize(1000000)
      .build();

  public static ArrayList<Triple<Integer, Integer, Integer>> ClustersOfDistrict(Integer key) {
    if (key == null) {
      return null;
    }
    return ClustersOfDistrict(key.toString());
  }

  public static ArrayList<Triple<Integer, Integer, Integer>> ClustersOfDistrict(String key) {
    ArrayList<Triple<Integer, Integer, Integer>> output =
        new ArrayList<Triple<Integer, Integer, Integer>>();
    String name = ImpatientAsker.AskWorld(15, "district", key, "clusters");
    if (name.equals("")) {
      return null;
    }
    String[] indiv = name.split("@");
    for (String s : indiv) {
      String[] data = s.split(":");
      if (data.length != 3) {
        return null;
      }
      Triple<Integer, Integer, Integer> triple =
          new Triple<>(
              Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
      output.add(triple);
    }
    return output;
  }

  public static UUID GetAddressUUID(String ownerAddress) {
    return ImpatientAsker.AskWorldUUID(15, "links", ownerAddress);
  }

  public static Set<String> GetDistrictNames() {
    String unsplit = ImpatientAsker.AskWorld(15, "query", "district_names");
    if (unsplit == null) {
      return new HashSet<>();
    }
    return new HashSet<>(Arrays.asList(unsplit.split(";")));
  }

  public static Integer GetDistrictOfName(String name) {
    try{
      return Integer.parseInt(name);
    }catch (Exception e){
      if(name.startsWith("#")){
        try{
        return Integer.parseInt(name.replace("#",""));
        }catch(Exception e2){
          return ImpatientAsker.AskWorldInteger(15, "query", "district_by_name", name);
        }
      }
      return ImpatientAsker.AskWorldInteger(15,"query", "district_by_name", name);
    }
  }

  public static Integer GetDistrictOfPlot(Integer key) {
    if (key == null) {
      return null;
    }
    return GetDistrictOfPlot(key.toString());
  }

  public static Integer GetDistrictOfPlot(String key) {
    return ImpatientAsker.AskWorldInteger(-1,"plot", key, "district");
  }

  public static Gamer GetGamer(UUID uniqueId) {
    return new Gamer(uniqueId);
  }

  public static Triple<Integer, Integer, Integer> GetGamerXYZ(UUID uuid) {
    String raw =
        ImpatientAsker.AskWorld("gamer", uuid.toString().toLowerCase(Locale.ROOT), "address");
    if (raw == null) {
      return new Triple<>(0, 0, 0);
    }
    String[] split = raw.split(";");
    if (split.length != 3) {
      return new Triple<>(0, 0, 0);
    }
    try {
      return new Triple<>(
          Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    } catch (Exception ignored) {
      return new Triple<>(0, 0, 0);
    }
  }

  public static String GetNameOfDistrict(Integer key) {
    if (key == null) {
      return null;
    }
    return GetNameOfDistrict(key.toString());
  }

  public static String GetNameOfDistrict(String key) {
    return ImpatientAsker.AskWorld("district", key, "name");
  }

  public static String GetOwnerOfDistrict(String key) {
    return ImpatientAsker.AskWorld(5,"district", key, "owner_addr");
  }

  public static Integer GetPlotID(Integer x, Integer z) {
    if(Math.abs(x) < 3 || Math.abs(z) < 3){
      return null;
    }
    return ImpatientAsker.AskWorldInteger(-1,"query", "plot_coord", x.toString() + ";" + z.toString());
  }

  public static District GetDistrict(Integer x, Integer z) {
    if(Math.abs(x) < 3 || Math.abs(z) < 3){
      return null;
    }
    return ImpatientAsker.AskWorldDistrict(15,"query", "district_coord", x.toString() + ";" + z.toString());
  }

  public static Integer GetPlotX(Integer key) {
    return GetPlotX(key.toString());
  }

  public static Integer GetPlotX(String key) {
    return ImpatientAsker.AskWorldInteger(-1,"plot", key, "x");
  }

  public static Integer GetPlotZ(Integer key) {
    return GetPlotZ(key.toString());
  }

  public static Integer GetPlotZ(String key) {
    return ImpatientAsker.AskWorldInteger(-1,"plot", key, "z");
  }

  public static Set<Integer> GetPlotsInDistrict(String key) {
    Set<Integer> output = new HashSet<>();
    String name = ImpatientAsker.AskWorld("district", key, "plots");
    String[] elements = name.split(";");
    for (String element : elements) {
      try {
        output.add(Integer.parseInt(element));
      } catch (Exception ignored) {
      }
    }
    return output;
  }

  public static FlagValue GetDefaultTeamFlagValue(String town, String team, AccessFlag flag){
    return GetDistrictTeamFlagValue(town,team,0,flag);
  }
  public static FlagValue GetDistrictTeamFlagValue(String town,String team,Integer district, AccessFlag flag){
    String cleanflag = flag.toString().toLowerCase(Locale.ROOT);
    String result = ImpatientAsker.AskWorld("town",town,"team",team,"district",district.toString(),"flag",cleanflag);
    FlagValue val = FlagValue.NONE;
    try {
      val = FlagValue.valueOf(result.toUpperCase(Locale.ROOT));
    }catch(Exception ignored){}
    return val;
  }

  public static String[] GetTownNames() {
    String result = ImpatientAsker.AskWorld(15,"query","towns");
    if(result == null){
      return new String[]{};
    }
    return result.split(";");
  }
}

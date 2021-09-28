package etherlandscore.etherlandscore.singleton;

import de.tr7zw.nbtinjector.javassist.bytecode.AccessFlag;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.services.ImpatientAsker;
import etherlandscore.etherlandscore.state.write.District;
import etherlandscore.etherlandscore.state.write.Gamer;
import kotlin.Triple;

import java.util.*;

public class Asker {

  public static ArrayList<Triple<Integer, Integer, Integer>> ClustersOfDistrict(Integer key) {
    if (key == null) {
      return null;
    }
    return ClustersOfDistrict(key.toString());
  }

  public static ArrayList<Triple<Integer, Integer, Integer>> ClustersOfDistrict(String key) {
    ArrayList<Triple<Integer, Integer, Integer>> output =
        new ArrayList<Triple<Integer, Integer, Integer>>();
    String name = ImpatientAsker.AskWorld("district", key, "clusters");
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
    return ImpatientAsker.AskWorldUUID("links", ownerAddress);
  }

  public static Set<String> GetDistrictNames() {
    String unsplit = ImpatientAsker.AskWorld("query", "district_names");
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
          return ImpatientAsker.AskWorldInteger("query", "district_by_name", name);
        }
      }
      return ImpatientAsker.AskWorldInteger("query", "district_by_name", name);
    }
  }

  public static Integer GetDistrictOfPlot(Integer key) {
    if (key == null) {
      return null;
    }
    return GetDistrictOfPlot(key.toString());
  }

  public static Integer GetDistrictOfPlot(String key) {
    return ImpatientAsker.AskWorldInteger("plot", key, "district");
  }

  public static Set<Integer> GetDistricts() {
    HashSet<Integer> output = new HashSet<>();
    String unsplit = ImpatientAsker.AskWorld("query", "district_ids");
    if (unsplit == null) {
      return output;
    }
    for (String s : unsplit.split(";")) {
      try {
        output.add(Integer.parseInt(s));
      } catch (Exception ignored) {
      }
    }
    return output;
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
    return ImpatientAsker.AskWorld("district", key, "owner_addr");
  }

  public static Integer GetPlotID(Integer x, Integer z) {
    return ImpatientAsker.AskWorldInteger("query", "plot_coord", x.toString() + ";" + z.toString());
  }

  public static District GetDistrict(Integer x, Integer z) {
    return ImpatientAsker.AskWorldDistrict("query", "district_coord", x.toString() + ";" + z.toString());
  }

  public static Integer GetPlotX(Integer key) {
    return GetPlotX(key.toString());
  }

  public static Integer GetPlotX(String key) {
    return ImpatientAsker.AskWorldInteger("plot", key, "x");
  }

  public static Integer GetPlotZ(Integer key) {
    return GetPlotZ(key.toString());
  }

  public static Integer GetPlotZ(String key) {
    return ImpatientAsker.AskWorldInteger("plot", key, "z");
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
}

package etherlandscore.etherlandscore.singleton;

import etherlandscore.etherlandscore.services.ImpatientAsker;
import kotlin.Triple;

import java.util.*;

public class Asker {
  public static Triple<Integer, Integer, Integer> GetGamerXYZ(UUID uuid){
    String raw = ImpatientAsker.AskWorld(
        "gamer",
        uuid.toString().toLowerCase(Locale.ROOT),
        "address"
    );
    if(raw == null){
      return new Triple<>(0,0,0);
    }
    String[] split = raw.split("_");
    if(split.length != 3){
      return new Triple<>(0,0,0);
    }
    try{
      return new Triple<>(
          Integer.parseInt(split[0]),
          Integer.parseInt(split[1]),
           Integer.parseInt(split[2])
      );
    }catch(Exception ignored){
      return new Triple<>(0,0,0);
    }
  }

  public static String GetGamerAddress(UUID uuid){
    return ImpatientAsker.AskWorld(
        "gamer",
        uuid.toString().toLowerCase(Locale.ROOT),
        "address"
    );
  }

  public static UUID GetAddressUUID(String ownerAddress) {
    return ImpatientAsker.AskWorldUUID(
        "links",
        ownerAddress
    );
  }

  public static Integer GetPlotX(Integer key){
    return GetPlotX(key.toString());
  }
  public static Integer GetPlotX(String key){
    return ImpatientAsker.AskWorldInteger(
        "plot",
        key,
        "x"
    );
  }
  public static Integer GetDistrictOfPlot(Integer key) {
    if(key == null){
      return null;
    }
    return GetDistrictOfPlot(key.toString());
  }

  public static Integer GetDistrictOfPlot(String key) {
    return ImpatientAsker.AskWorldInteger(
        "plot",
        key,
        "district"
    );
  }
  public static Integer GetPlotZ(Integer key){
    return GetPlotZ(key.toString());
  }
  public static Integer GetPlotZ(String key){
    return ImpatientAsker.AskWorldInteger(
        "plot",
        key,
        "z"
    );
  }

  public static Integer GetPlotID(Integer x, Integer z){
    return ImpatientAsker.AskWorldInteger(
        "query",
        "plot_coord",
        x.toString()+"_"+z.toString()
    );
  }

  public static String GetOwnerOfDistrict(String key){
    return ImpatientAsker.AskWorld(
        "district",
        key,
        "owner_addr"
    );
  }

  public static String GetNameOfDistrict(Integer key){
    if(key == null){
      return null;
    }
    return GetNameOfDistrict(key.toString());
  }

  public static String GetNameOfDistrict(String key){
    return ImpatientAsker.AskWorld(
        "district",
        key,
        "name"
    );
  }

  public static Set<String> GetDistrictNames(){
    String unsplit = ImpatientAsker.AskWorld(
        "query",
        "district_names"
    );
    if(unsplit == null){
      return new HashSet<>();
    }
    return new HashSet<>(Arrays.asList(unsplit.split("_")));
  }
  public static Set<Integer> GetDistricts(){
    HashSet<Integer> output = new HashSet<>();
    String unsplit = ImpatientAsker.AskWorld(
        "query",
        "district_ids"
    );
    if(unsplit == null){
      return output;
    }
    for (String s : unsplit.split("_")) {
      try{
        output.add(Integer.parseInt(s));
      }catch(Exception ignored){}
    }
    return output;
  }

  public static Integer GetDistrictOfName(String name){
    return ImpatientAsker.AskWorldInteger(
        "query",
        "district_by_name",
        name
    );
  }

  public static ArrayList<Triple<Integer, Integer, Integer>> ClustersOfDistrict(Integer key) {
    if(key == null){
      return null;
    }
    return ClustersOfDistrict(key.toString());
  }
  public static ArrayList<Triple<Integer, Integer, Integer>> ClustersOfDistrict(String key) {
    ArrayList<Triple<Integer, Integer, Integer>> output = new ArrayList<Triple<Integer,Integer,Integer>>();
    String name = ImpatientAsker.AskWorld(
        "district",
        key,
        "clusters"
    );
    if(name.equals("")){
      return null;
    }
    String[] indiv = name.split("@");
    for (String s : indiv) {
      String[] data = s.split(":");
      if(data.length != 3){
        return null;
      }
      Triple<Integer, Integer, Integer> triple = new Triple<>(Integer.parseInt(data[0]),Integer.parseInt(data[1]),Integer.parseInt(data[2]));
      output.add(triple);
    }
    return output;
  }

  public static Set<Integer> GetPlotsInDistrict(String key){
    Set<Integer> output = new HashSet<>();
    String name = ImpatientAsker.AskWorld(
        "district",
        key,
        "plots"
    );
    String[] elements = name.split("_");
    for (String element : elements) {
      try{
        output.add(Integer.parseInt(element));
      }catch(Exception ignored){}
    }
    return output;
  }


}

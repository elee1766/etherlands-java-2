package etherlandscore.etherlandscore.singleton;

import kotlin.Triple;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
public class RedisGetter {


  public static Integer GetPlotX(Integer key){
    Integer output = 0;
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":x");
      output = Integer.parseInt(jedis.get(input));
    }catch (Exception e){
      return null;
    }
    return output;
  }

  public static String GetPlotX(String key){
    String output = "";
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":x");
      output = jedis.get(input);
    }catch (Exception ignored){
      return null;
    }
    return output;
  }

  public static Integer GetPlotZ(Integer key){
    int output = 0;
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":z");
      output = Integer.parseInt(jedis.get(input));
    }catch (Exception ignored){
      return null;
    }
    return output;
  }

  public static String GetPlotZ(String key){
    String output = "";
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":z");
      output = jedis.get(input);
    }catch (Exception ignored){
      return null;
    }
    return output;
  }

  public static Integer GetPlotID(String x, String z){
    int plotID = 0;
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      plotID = Integer.parseInt(jedis.get("plot_coord:"+x+"_"+z));
    } catch (Exception e){
      return null;
    }
    return plotID;
  }

  public static String GetOwnerOfDistrict(String key){
    String owner = "0x0000000000000000000000000000000000000000";
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      owner = jedis.get("district:"+key+":address");
    }catch (Exception ignored){
      return owner;
    }
    return owner;
  }

  public static String GetNameOfDistrict(Integer key){
    String name = "#" + key.toString();
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      name = jedis.hget("district_name",key.toString());
    }catch (Exception ignored){
      return name;
    }
    return name;
  }

  public static Set<String> GetDistrictNames(){
    Set<String> output = new HashSet<>();
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      output.addAll(jedis.hkeys("name_district"));
    }catch (Exception ignored){
      return output;
    }
    return output;
  }
  public static Set<Integer> GetDistricts(){
    Set<Integer> output = new HashSet<>();
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      for (String district_name : jedis.hkeys("district_name")) {
        output.add(Integer.parseInt(district_name));
      }
    }catch (Exception ignored){
      return output;
    }
    return output;
  }



  public static Integer GetDistrictOfName(String name){
    int districtId = 0;
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      districtId = Integer.parseInt(jedis.hget("name_district",name));
    } catch (Exception e){
      return null;
    }
    return districtId;
  }


  public static Integer GetDistrictOfPlot(Integer key) {
    Double district;
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      district = jedis.zscore("districtZplot", key.toString());
    } catch (Exception ignored){
      return null;
    }
    return Math.toIntExact(Math.round(district));
  }

  public static Integer GetDistrictOfPlot(String key) {
    Double district;
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      district = jedis.zscore("districtZplot", key);
      if(district == null){
        return null;
      }
    }catch (Exception ignored){
      return null;
    }
    return Math.toIntExact(Math.round(district));
  }


  public static ArrayList<Triple<Integer, Integer, Integer>> ClustersOfDistrict(Integer key) {
    ArrayList<Triple<Integer, Integer, Integer>> output = new ArrayList<Triple<Integer,Integer,Integer>>();

    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      String name = jedis.get("district:"+key.toString()+":clusters");
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
    }catch (Exception ignored){
      return null;
    }
    return output;
  }

  public static Set<Integer> GetPlotsInDistrict(String key){
    double minmax = Double.parseDouble(key);
    Set<String> districts;
    Set<Integer> output = new HashSet<>();
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      districts = jedis.zrangeByScore("districtZplot", minmax, minmax);
      for (String district : districts) {
        output.add(Integer.parseInt(district));
      }
    } catch (Exception ignored){
      return null;
    }
    return output;
  }


}

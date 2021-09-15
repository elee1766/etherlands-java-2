package etherlandscore.etherlandscore.singleton;

import redis.clients.jedis.Jedis;

import java.util.Set;
public class RedisGetter {


  public static Integer GetPlotX(Integer key){
    Integer output = 0;
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":x");
      output = Integer.parseInt(jedis.get(input));
    }catch (Exception e){
    }
    return output;
  }

  public static String GetPlotX(String key){
    String output = "";
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":x");
      output = jedis.get(input);
    }catch (Exception e){
    }
    return output;
  }

  public static Integer GetPlotZ(Integer key){
    int output = 0;
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":z");
      output = Integer.parseInt(jedis.get(input));
    }catch (Exception e){
    }
    return output;
  }

  public static String GetPlotZ(String key){
    String output = "";
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":z");
      output = jedis.get(input);
    }catch (Exception e){
    }
    return output;
  }

  public static Integer GetPlotID(String x, String z){
    int plotID = 0;
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      plotID = Integer.parseInt(jedis.get("plot_coord:"+x+"_"+z));
    } catch (Exception e){
    }
    return plotID;
  }

  public static String GetOwnerOfDistrict(String key){
    String owner = "0x0000000000000000000000000000000000000000";
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      owner = jedis.get("district:"+key+":address");
    }catch (Exception e){
    }
    return owner;
  }


  public static Integer GetDistrictOfPlot(Integer key) {
    Double district;
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      district = jedis.zscore("districtZplot", key.toString());
      if(district == null){
        return null;
      }
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
    }
    return Math.toIntExact(Math.round(district));
  }

  public static Set<String> GetPlotsInDistrict(String key){
    double minmax = Double.parseDouble(key);
    Set<String> districts;
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      districts = jedis.zrangeByScore("districtZplot", minmax, minmax);
    }
    return districts;
  }

}

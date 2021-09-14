package etherlandscore.etherlandscore.singleton;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
public class RedisGetter {

  public static String getPlotX(String key){
    String output = "";
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":x");
      output = jedis.get(input);
    }
    return output;
  }

  public static String getPlotZ(String key){
    String output = "";
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      String input = ("plot:"+key+":z");
      output = jedis.get(input);
    }
    return output;
  }

  public static String getPlotID(String x, String z){
    String plotID;
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      plotID = jedis.get("plot_coord:"+x+"_"+z);
    }
    return plotID;
  }

  public static String getOwnerOfDistrict(String key){
    String owner;
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      owner = jedis.get("district:"+key+":address");
    }
    return owner;
  }

  public static Double getDistrictOfPlot(String key) {
    Double district;
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      district = jedis.zscore("districtZplot", key);
    }
    return district;
  }

  public static Set<String> getPlotsinDistrict(String key){
    Double minmax = Double.parseDouble(key);
    Set<String> districts;
    try (redis.clients.jedis.Jedis jedis = JedisFactory.getPool().getResource()) {
      districts = jedis.zrangeByScore("districtZplot", minmax, minmax);
    }
    return districts;
  }

}

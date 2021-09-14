package etherlandscore.etherlandscore.singleton;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;


public class RedisGetter {

  public static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost:6379");

  public static JedisPool GetRedisPool(){
    if(pool == null){
      pool = new JedisPool(new JedisPoolConfig(), "localhost:6379");
    }
    return pool;
  }

  public static String getPlotX(String key){
    String output = "";
    try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
      String input = ("plot:"+key+":x");
      output = jedis.get(input);
    }
    pool.close();
    return output;
  }

  public static String getPlotZ(String key){
    String output = "";
    try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
      String input = ("plot:"+key+":z");
      output = jedis.get(input);
    }
    pool.close();
    return output;
  }

  public static Set<String> getDistrictOfPlot(String key){
    Double minmax = Double.parseDouble(key);
    Set<String> districts;
    try (redis.clients.jedis.Jedis jedis = pool.getResource()) {
      districts = jedis.zrangeByScore("districtZplot", minmax, minmax);
    }
    pool.close();
    return districts;
  }

}

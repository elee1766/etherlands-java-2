package etherlandscore.etherlandscore.singleton;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;


public class RedisGetter {
  public JedisPoolConfig poolConfig;
  public JedisPool jedisPool;
  public RedisGetter(){
    this.poolConfig = buildPoolConfig();
    this.jedisPool = new JedisPool(this.poolConfig, "localhost");
  }

  public JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
  }

  public String getPlotX(String key){
    String output = "";
    try (Jedis jedis = this.jedisPool.getResource()) {
      String input = ("plot:"+key+":x");
      output = jedis.get(input);
    }
    return output;
  }

  public String getPlotZ(String key){
    String output = "";
    try (redis.clients.jedis.Jedis jedis = this.jedisPool.getResource()) {
      String input = ("plot:"+key+":z");
      output = jedis.get(input);
    }
    return output;
  }

  public Set<String> getDistrictOfPlot(String key){
    Double minmax = Double.parseDouble(key);
    Set<String> districts;
    try (redis.clients.jedis.Jedis jedis = this.jedisPool.getResource()) {
      districts = jedis.zmscore("districtZplot", minmax, minmax);
    }
    return districts;
  }

  public Set<String> getPlotsinDistrict(String key){
    Double minmax = Double.parseDouble(key);
    Set<String> districts;
    try (redis.clients.jedis.Jedis jedis = this.jedisPool.getResource()) {
      districts = jedis.zrangeByScore("districtZplot", minmax, minmax);
    }
    return districts;
  }

}

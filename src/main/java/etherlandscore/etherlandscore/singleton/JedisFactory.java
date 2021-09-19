package etherlandscore.etherlandscore.singleton;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class JedisFactory {

  public static JedisPool pool;

  public static JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(17);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
  }

  public static JedisPool getPool(){
    if(pool==null){
      JedisPoolConfig poolConfig = buildPoolConfig();
      pool = new JedisPool(poolConfig, "localhost");
    }
    return pool;
  }



}

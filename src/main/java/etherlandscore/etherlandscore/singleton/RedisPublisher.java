package etherlandscore.etherlandscore.singleton;

import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

public class RedisPublisher {

  public static void RequestImageDownload(String collection, String id){
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      jedis.publish("smp:image_download",collection + ":" + id);
    } catch (Exception e){
      Bukkit.getLogger().info("Failed to request for image" + collection + ":" + id);
      e.printStackTrace();
    }
  }

}

package etherlandscore.etherlandscore.singleton;

import etherlandscore.etherlandscore.state.read.Gamer;
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

  public static void CreateLinkRequest(Gamer gamer, String a, String b, String c){
    try (Jedis jedis = JedisFactory.getPool().getResource()) {
      jedis.publish("smp:link_request",gamer.getUuid().toString() + ":" + a + ":" + b + ":" + c);
    } catch (Exception e){
      e.printStackTrace();
    }
  }

}

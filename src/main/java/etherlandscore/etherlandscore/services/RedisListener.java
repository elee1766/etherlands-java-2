package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.eth.LinkInformation;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.singleton.JedisFactory;
import etherlandscore.etherlandscore.state.sender.StateSender;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisListener extends ListenerClient {

  private final Channels channels;


  public RedisListener(Channels channels, Fiber fiber){
    super(channels,fiber);
    this.channels = channels;
    JedisPubSub pubSub  = new JedisPubSub() {
      @Override
      public void onMessage(String channel, String message) {
        Bukkit.getLogger().info("redis sub: " + channel + " " + message);
        switch(channel){
          case "link"-> link(message);
        }
      }
      @Override
      public void onSubscribe(String channel, int subscribedChannels) {
        Bukkit.getLogger().info("redislistener: subscribed to "+ channel);
      }

      @Override
      public void onUnsubscribe(String channel, int subscribedChannels) {
        Bukkit.getLogger().info("redislistener: unsubscribed from "+ channel);
      }
    };
    try(Jedis jedis = JedisFactory.getPool().getResource()){
      jedis.subscribe(pubSub,"link");
      }catch(Exception e ){
      Bukkit.getLogger().warning("Failed to connect to redis pub/sub!!!");
      }
  }


  // format [message]:[signature]:[publickey]
  // message [UUID]![expiration]
  private void link(String message){
    String[] args = message.split(":");
    if(args.length == 3){
      LinkInformation link =  new LinkInformation(args[0],args[1],args[2]);
      try{
        if(link.pubkey()){
          if(link.getPubkey().equals(args[2])){
            String[] payload = message.split("!");
            if(payload.length == 2){
              UUID uuid = UUID.fromString(payload[0]);
              if(!uuid.equals(new UUID(0,0))){
                StateSender.setAddress(channels,uuid,payload[1]);
                return;
              }
            }
          }
        }
        }catch (Exception ignored){}
    }
    Bukkit.getLogger().info("invalid link information");
  }



}

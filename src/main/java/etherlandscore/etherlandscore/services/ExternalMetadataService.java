package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.singleton.Hitter;
import kotlin.Pair;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.bukkit.Bukkit;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ExternalMetadataService extends ListenerClient {

  private enum IM {
    save_bytes
  }

  public static final MemoryChannel<Message<IM>> internal_request= new MemoryChannel<>();

  private final Channels channels;
  private static final Map<Pair<String,String>, BufferedImage> imageCache = new HashMap<>();

  private static final AsyncHttpClient client = Dsl.asyncHttpClient();

  public ExternalMetadataService(Channels channels, Fiber fiber){
    super(channels,fiber);
    this.channels = channels;

    internal_request.subscribe(fiber, this::process_message);
  }

  private void process_message(Message<IM> message){
    try{
      Bukkit.getLogger().info("external metadata:" + message.getCommand());
      Object[] _args = message.getArgs();
      switch(message.getCommand()){
        case save_bytes -> this.cacheNewBytes((String) _args[0], (String) _args[1], (byte[]) _args[2]);
      }
    }catch(Exception e){
      Bukkit.getLogger().warning("internal metadata command failed: " + message.getCommand());
      e.printStackTrace();
    }
  }

  private void cacheNewBytes(String contract_address, String token_id, byte[] bytes) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
    try {
      cacheNewBuffer(contract_address,token_id,ImageIO.read(inputStream));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void cacheNewBuffer(String contract_address, String token_id, BufferedImage image){
    imageCache.put(new Pair<>(contract_address,token_id), image);
  }

  public static BufferedImage getCachedBuffer(String contract_address, String token_id){
    BufferedImage candidiate = imageCache.getOrDefault(new Pair<>(contract_address, token_id), null);
    if(candidiate == null){
      try {
        askForImage(contract_address, token_id);
      } catch (Exception ignored) {
      }
    }
    return candidiate;
  }

  public static void askForImage(String contract_address, String token_id) {
    URI target = getLocalUrl(contract_address, token_id);
    client.prepareGet(target.toString()).execute().toCompletableFuture()
        .thenApplyAsync(response->{
          if(response.getStatusCode() == 200){
            internal_request.publish(new Message<>(IM.save_bytes,contract_address, token_id, response.getResponseBodyAsBytes()));
          }else{
            Hitter.RequestImageDownload(contract_address,token_id);
          }
          return null;
        });
  }

  private static URI getLocalUrl(String contract_address, String token_id){
    return URI.create("http://localhost:10100/nft_image/"+contract_address+"/"+token_id);
    }
}

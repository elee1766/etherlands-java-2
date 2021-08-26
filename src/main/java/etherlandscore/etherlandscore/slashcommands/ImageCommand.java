package etherlandscore.etherlandscore.slashcommands;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.write.WriteNFT;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetlang.fibers.Fiber;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.scanner.ScannerImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Map;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class ImageCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public ImageCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public URL getImage(String contractaddr, String token_id) throws IOException {
    WriteNFT nft = state().getNFTs().get(contractaddr, token_id);
    if(nft!=null){
      return new URL(nft.getUrl());
    }else{
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder()
          .url("https://api.opensea.io/api/v1/asset/"+contractaddr+"/"+token_id+"/")
          .get()
          .build();
      Response response = client.newCall(request).execute();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      WriteNFT entity = null;
      try {
        entity = objectMapper.readValue(response.body().string(), WriteNFT.class);
      }catch(Exception ex){
        ex.printStackTrace();
      }
      channels.master_command.publish(new Message<>(MasterCommand.nft_create_nft, entity, contractaddr));
      return new URL(entity.getUrl());
    }
  }

  public void imageMap(CommandSender sender, Object[] args) {
    Player player = (Player) sender;
    int width = (int) args[2];
    int mapCount = width*width;
    Image image = null;
    try {
      URL url = getImage((String) args[0],(String) args[1]);
      if(url==null){
        return;
      }
      image = ImageIO.read(url);
    }catch(Exception ex) {
      ex.printStackTrace();
      return;
    }
    Image tmp = image.getScaledInstance(width*128,width*128,Image.SCALE_SMOOTH);
    BufferedImage photo = new BufferedImage(width*128,width*128, BufferedImage.TYPE_INT_ARGB);
    ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
    ArrayList<MapView> maps = new ArrayList<MapView>();
    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    for(int i = 0; i < width; i++) {
      for (int j = 0; j < width; j++) {
        stacks.add(new ItemStack(Material.FILLED_MAP, 1));
        maps.add(Bukkit.getServer().createMap(Bukkit.getServer().getWorlds().get(0)));
        Graphics2D g2d = photo.createGraphics();
        g2d.drawImage(tmp, 0, 0 , null);
        images.add(photo.getSubimage(j * 128, i * 128, 128, 128));
      }
    }
    for(int i = 0; i < mapCount; i++){
      for(MapRenderer render : maps.get(i).getRenderers()) {
        maps.get(i).removeRenderer(render);
        }
      int finalI = i;

      MapRenderer mr = new MapRenderer() {

          @Override
          public void render(MapView map, MapCanvas canvas, Player player) {
            if(map.getRenderers().isEmpty()) {
              canvas.drawImage(0, 0, images.get(finalI));
            }
          }
        };

      maps.get(i).addRenderer(mr);
      channels.master_command.publish(new Message<>(MasterCommand.map_create_map, maps.get(i).getId(), images.get(i)));

      MapMeta meta = ((MapMeta) stacks.get(i).getItemMeta());
      meta.setMapView(maps.get(i));
      stacks.get(i).setItemMeta(meta);
      player.getInventory().addItem(stacks.get(i));
    }
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage("IMAGE TESTING SHIT AHHHHH");
  }

  public void register(){
    CommandAPICommand ImageCommand =
        new CommandAPICommand("image")
            .withPermission("etherlands.public")
            .executesPlayer(this::help);
    ImageCommand.withSubcommand(
        new CommandAPICommand("display")
            .withArguments(new TextArgument("contract address"))
            .withArguments(new TextArgument("tokenID"))
            .withArguments(new IntegerArgument("width"))
            .executesPlayer(this::imageMap));
    ImageCommand.register();
  }
}

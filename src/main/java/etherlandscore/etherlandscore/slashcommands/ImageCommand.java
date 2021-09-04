package etherlandscore.etherlandscore.slashcommands;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.write.ResponseHelper;
import etherlandscore.etherlandscore.state.write.WriteMap;
import etherlandscore.etherlandscore.state.write.WriteNFT;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetlang.fibers.Fiber;
import org.json.simple.JSONObject;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.yaml.snakeyaml.scanner.ScannerImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class ImageCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();

  public ImageCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void renderMap(Player sender, Object[] args) {
    boolean contract = false;
    String slug = String.valueOf(args[0]);
    String item_id = String.valueOf(args[1]);
    if(slug.contains(".")) {
      contract = true;
      Web3j web3 = Web3j.build(new HttpService(settings.get("NodeUrl")));
      EnsResolver ens = new EnsResolver(web3, 300);
      slug = ens.resolve(slug);
    }
    if(slug.startsWith("0x")) { contract = true; }
    int width = (int) args[2];
    Block placed = sender.getTargetBlock(null, 10);
    BlockFace facing = sender.getTargetBlockFace(10);
    if(facing==BlockFace.WEST){
      placed = placed.getWorld().getBlockAt(placed.getX()-1, placed.getY(), placed.getZ());
    }else if(facing==BlockFace.EAST){
      placed = placed.getWorld().getBlockAt(placed.getX()+1, placed.getY(), placed.getZ());
    }else if(facing==BlockFace.NORTH){
      placed = placed.getWorld().getBlockAt(placed.getX(), placed.getY(), placed.getZ()-1);
    }else if(facing==BlockFace.SOUTH){
      placed = placed.getWorld().getBlockAt(placed.getX(), placed.getY(), placed.getZ()+1);
    }
    sender.sendMessage(slug + " " + item_id + " " + width);
    if(canBuildHere(width, placed, sender, facing)) {
      imageMap(sender, width, slug, item_id, placed, facing, contract);
    }else{
      sender.sendMessage("You cannot build that here");
    }
  }
  //x+ is east
  //z+ is south
  private boolean canBuildHere(int width, Block placed, Player player, BlockFace blockFace) {
    int x = placed.getX();
    int z = placed.getZ();
    switch (blockFace) {
      case WEST:
        //check north
        for(int i = 0; i<width; i++){
          Plot p = context.getPlot(x,z-i);
          if(p==null){
            if(player.isOp()){
              break;
            }
          }
          if(!p.canGamerPerform(AccessFlags.BUILD, context.getGamer(player.getUniqueId()))){
            return false;
          }
        }
        break;
      case EAST:
        //check south
        for(int i = 0; i<width; i++){
          Plot p = context.getPlot(x,z+i);
          if(p==null){
            if(player.isOp()){
              break;
            }
          }
          if(!p.canGamerPerform(AccessFlags.BUILD, context.getGamer(player.getUniqueId()))){
            return false;
          }
        }
        break;
      case NORTH:
        //check east
        for(int i = 0; i<width; i++){
          Plot p = context.getPlot(x+i,z);
          if(p==null){
            if(player.isOp()){
              break;
            }
          }
          if(!p.canGamerPerform(AccessFlags.BUILD, context.getGamer(player.getUniqueId()))){
            return false;
          }
        }
        break;
      case SOUTH:
        //check west
        for(int i = 0; i<width; i++){
          Plot p = context.getPlot(x+i,z);
          if(p==null){
            if(player.isOp()){
              break;
            }
          }
          if(p.canGamerPerform(AccessFlags.BUILD, context.getGamer(player.getUniqueId()))){
            return false;
          }
        }
        break;
      default:
        break;
    }
    return true;
  }
  public URL getImage(String slug, String token_id, boolean contract) throws IOException {
    WriteNFT nft = state().getNFTs().get(slug, token_id);
    if(nft!=null){
      return new URL(nft.getUrl());
    }else{
      OkHttpClient client = new OkHttpClient();
      Request request;

      if(contract){
        request = new Request.Builder()
            .url("https://api.opensea.io/api/v1/assets?token_ids="+token_id+"&asset_contract_address="+slug+"&order_direction=desc&offset=0&limit=20")
            .get()
            .build();
      }else {
        request = new Request.Builder()
            .url("https://api.opensea.io/api/v1/assets?token_ids=" + token_id + "&order_direction=asc&offset=0&limit=1&collection=" + slug)
            .get()
            .build();
      }
      Response response = client.newCall(request).execute();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      ResponseHelper entity = null;
      try {
        entity = objectMapper.readValue(response.body().string(), ResponseHelper.class);
      }catch(Exception ex){
        ex.printStackTrace();
      }
      Bukkit.getLogger().info(entity.getImageurl());
      WriteNFT writeNft = new WriteNFT(entity.getImageurl(), slug, token_id);
      channels.master_command.publish(new Message<>(MasterCommand.nft_create_nft, writeNft));
      return new URL(entity.getImageurl());
    }
  }

  public void imageMap(Player player, int width, String slug, String item_id, Block block, BlockFace blockFace, boolean contract) {
    int mapCount = width * width;
    Image image = null;
    URL url = null;
    try {
      url = getImage(slug, item_id, contract);
      if (url == null) {
        return;
      }
      image = ImageIO.read(url);
    } catch (Exception ex) {
      ex.printStackTrace();
      return;
    }
    Image tmp = image.getScaledInstance(width * 128, width * 128, Image.SCALE_SMOOTH);
    BufferedImage photo = new BufferedImage(width * 128, width * 128, BufferedImage.TYPE_INT_ARGB);
    ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
    ArrayList<MapView> maps = new ArrayList<MapView>();
    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < width; j++) {
        stacks.add(new ItemStack(Material.FILLED_MAP, 1));
        maps.add(Bukkit.getServer().createMap(Bukkit.getServer().getWorlds().get(0)));
        Graphics2D g2d = photo.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        images.add(photo.getSubimage(j * 128, i * 128, 128, 128));
      }
    }
    Set<Integer> mapIDs = new HashSet<>();
    for (int i = 0; i < mapCount; i++) {
      mapIDs.add(maps.get(i).getId());
      Bukkit.getLogger().info(String.valueOf(maps.get(i).getId()));
      for (MapRenderer render : maps.get(i).getRenderers()) {
        maps.get(i).removeRenderer(render);
      }
      int finalI = i;
      MapRenderer mr = new MapRenderer() {
        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
          canvas.drawImage(0, 0, images.get(finalI));
        }
      };
      maps.get(i).addRenderer(mr);
      MapMeta meta = ((MapMeta) stacks.get(i).getItemMeta());
      meta.setMapView(maps.get(i));
      stacks.get(i).setItemMeta(meta);
    }
    WriteMap wm = new WriteMap(String.valueOf(maps.get(0).getId()), mapIDs, url);
    channels.master_command.publish(new Message<>(MasterCommand.map_create_map, wm));
    int i = 0;
    switch (blockFace) {
      case NORTH:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < width; y++) {
            Location loc = new Location(block.getWorld(), block.getX() + y, block.getY() + x, block.getZ());
            ItemFrame frame = block.getWorld().spawn(loc, ItemFrame.class);
            frame.setItem(stacks.get(stacks.size() - i - 1));
            i++;
          }
        }
      case SOUTH:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < width; y++) {
            Location loc = new Location(block.getWorld(), block.getX() + y, block.getY() + width-x-1, block.getZ());
            ItemFrame frame = block.getWorld().spawn(loc, ItemFrame.class);
            frame.setItem(stacks.get(i));
            i++;
          }
        }
      case EAST:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < width; y++) {
            Location loc = new Location(block.getWorld(), block.getX(), block.getY() + x, block.getZ()+y);
            ItemFrame frame = block.getWorld().spawn(loc, ItemFrame.class);
            frame.setItem(stacks.get(stacks.size() - i - 1));
            i++;
          }
        }
      case WEST:
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < width; y++) {
            Location loc = new Location(block.getWorld(), block.getX(), block.getY() + x, block.getZ()-y);
            ItemFrame frame = block.getWorld().spawn(loc, ItemFrame.class);
            frame.setItem(stacks.get(stacks.size() - i - 1));
            i++;
          }
        }
    }
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage("/image display [contract address|collection name] [tokenID] [size]");
  }

  public void register(){
    CommandAPICommand ImageCommand =
        new CommandAPICommand("image")
            .withPermission("etherlands.public")
            .executesPlayer(this::help);
    ImageCommand.withSubcommand(
        new CommandAPICommand("display")
            .withArguments(new TextArgument("contract address or collection name"))
            .withArguments(new TextArgument("tokenID"))
            .withArguments(new IntegerArgument("width"))
            .executesPlayer(this::renderMap));
    ImageCommand.register();
  }
}

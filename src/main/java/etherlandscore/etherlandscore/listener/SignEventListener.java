package etherlandscore.etherlandscore.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import etherlandscore.etherlandscore.actions.BlockAction.BlockBreakAction;
import etherlandscore.etherlandscore.actions.BlockAction.BlockPlaceAction;
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
import etherlandscore.etherlandscore.state.write.WritePlot;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.util.Vector;
import org.jetlang.fibers.Fiber;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class SignEventListener extends ListenerClient implements Listener {

  public final Fiber fiber;
  public final Channels channels;
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();

  public SignEventListener(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
  }

  @EventHandler
  public void onSignEdit(SignChangeEvent signChangeEvent) {
    boolean contract = false;
    String[] lines = signChangeEvent.getLines();
    if(lines[0].contains("nft") || lines[0].contains("NFT")){
      String slug = "";
      String item_id = "";
      String width = "";
      String[] strArr = lines[0].split("\\s+");
      if(lines[1].startsWith("0x")){
        contract = true;
        slug += lines[1]+lines[2]+lines[3];
        item_id += strArr[2];
      }else{
        if (lines[3]!="") {
          slug += lines[1] + lines[2];
          item_id += lines[3];
        } else {
          slug += lines[1];
          item_id += lines[2];
        }
      }
      width += strArr[1];
      Block placed = signChangeEvent.getBlock();
      Player player = signChangeEvent.getPlayer();
      player.sendMessage(slug + " " + item_id + " " + width);
      BlockFace facing = facing(placed);
      if(canBuildHere(width, placed, player, facing)) {
        signChangeEvent.getBlock().setType(Material.AIR);
        imageMap(player, Integer.parseInt(width), slug, item_id, placed, facing, contract);
      }
    }
  }

  private BlockFace facing(Block placed){
    return ((Directional) placed.getBlockData()).getFacing();
  }

  //x+ is east
  //z+ is south
  private boolean canBuildHere(String width, Block placed, Player player, BlockFace blockFace) {
    int size = Integer.valueOf(width);
    int x = placed.getX();
    int z = placed.getZ();
    switch (blockFace) {
      case WEST:
        //check north
        for(int i = 0; i<size; i++){
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
        for(int i = 0; i<size; i++){
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
        for(int i = 0; i<size; i++){
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
        for(int i = 0; i<size; i++){
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
}

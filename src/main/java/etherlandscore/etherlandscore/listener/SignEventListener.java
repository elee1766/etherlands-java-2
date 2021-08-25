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
import etherlandscore.etherlandscore.state.read.Plot;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class SignEventListener extends ListenerClient implements Listener {

  public final Fiber fiber;
  public final Channels channels;

  public SignEventListener(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
  }

  @EventHandler
  public void onSignEdit(SignChangeEvent signChangeEvent) {
    signChangeEvent.getPlayer().sendMessage("Good job fucker");
    String[] lines = signChangeEvent.getLines();
    String command = lines[0];
    signChangeEvent.getPlayer().sendMessage(command);
    if(command.equals("nft")){
      signChangeEvent.getPlayer().sendMessage("IT SAYS NFT");
      String contractAddr = "0xb47e3cd837ddf8e4c57f05d70ab865de6e193bbb";
      String item_id = lines[1];
      String width = lines[2];
      Block placed = signChangeEvent.getBlock();
      Player player = signChangeEvent.getPlayer();
      player.sendMessage(contractAddr + " " + item_id + " " + width);
      BlockFace facing = facing(placed);
      if(canBuildHere(width, placed, player, facing)) {
        signChangeEvent.getBlock().setType(Material.AIR);
        player.sendMessage("YOU CAN BUILD HERE");
        imageMap(player, Integer.parseInt(width), contractAddr, item_id, placed, facing);
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
  public URL getImage(String contractaddr, String token_id) throws IOException {
    WriteNFT nft = state().getNFTs().get(contractaddr, token_id);
    if(nft!=null){
      return new URL(nft.getURL());
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
      return new URL(entity.getURL());
    }
  }

  public void imageMap(Player player, int width, String contractAddr, String item_id, Block block, BlockFace blockFace) {
    int mapCount = width * width;
    Image image = null;
    try {
      URL url = getImage(contractAddr, item_id);
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
    for (int i = 0; i < mapCount; i++) {
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

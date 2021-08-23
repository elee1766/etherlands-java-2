package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public ImageCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void imageMap(CommandSender sender, Object[] args) {
    Player player = (Player) sender;
    URL url = getURL(String.valueOf(args[0]));
    ItemStack item = new ItemStack(Material.FILLED_MAP,1);
    MapView map = Bukkit.getServer().createMap(Bukkit.getServer().getWorlds().get(0));

    for(MapRenderer render : map.getRenderers()) {
      map.removeRenderer(render);
    }
    map.addRenderer(new MapRenderer() {
      @Override
      public void render(MapView map, MapCanvas canvas, Player player) {
        try {

          Image image = ImageIO.read(url);
          Image tmp = image.getScaledInstance(132,128,Image.SCALE_SMOOTH);
          BufferedImage photo = new BufferedImage(128,128, BufferedImage.TYPE_INT_ARGB);
          Graphics2D g2d = photo.createGraphics();
          g2d.drawImage(tmp, 0, 0 , null);

          canvas.drawImage(0, 0, tmp);

        }catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    MapMeta meta = ((MapMeta) item.getItemMeta());
    meta.setMapView(map);
    item.setItemMeta(meta);

    player.getInventory().addItem(item);
  }

  URL getURL(String imgID){
    URL url = null;
    try {
      url = new URL(imgID);
      return url;
    }catch(MalformedURLException ex){
      ex.printStackTrace();
    }
    return url;
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
            .withArguments(new TextArgument("url"))
            .executesPlayer(this::imageMap));
    ImageCommand.register();
  }
}

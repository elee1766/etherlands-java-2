package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.readonly.ReadContext;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class MapMenu extends ListenerClient {
  private final Channels channels;
  private final Fiber fiber;
  private final Gamer gamer;

  public MapMenu(Gamer gamer, Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    this.fiber = fiber;
    this.gamer = gamer;
  }

  public void mapMenu() {
    Player player = this.gamer.getPlayer();
    TextComponent map = new TextComponent("");
    TextComponent unclaimedKey = new TextComponent("-");
    unclaimedKey.setColor(ChatColor.GRAY);
    TextComponent claimedKey = new TextComponent("+");
    claimedKey.setColor(ChatColor.YELLOW);
    TextComponent ownedKey = new TextComponent("+");
    ownedKey.setColor(ChatColor.GREEN);
    TextComponent playerKey = new TextComponent("+");
    playerKey.setColor(ChatColor.RED);
    TextComponent friendKey = new TextComponent("+");
    friendKey.setColor(ChatColor.DARK_GREEN);
    Location playerLoc = player.getLocation();
    Chunk chunk = playerLoc.getChunk();
    float yaw = playerLoc.getYaw();
    String facing;
    if (yaw < 0) {
      yaw += 360;
    }
    if (yaw >= 315 || yaw < 45) {
      facing = "S";
    } else if (yaw < 135) {
      facing = "W";
    } else if (yaw < 225) {
      facing = "N";
    } else if (yaw < 315) {
      facing = "E";
    } else {
      facing = "N";
    }

    int x = chunk.getX();
    int z = chunk.getZ();
    TextComponent title = new TextComponent("===========,[Etherlands Map (" + x + ", " + z + ")" + facing + " ],===========\n");
    map.addExtra(title);
    x = x - (4);
    z = z - (23);
    for (int i = 0; i < 7; i++) {
      x++;
      for (int j = 0; j < 46; j++) {
        z++;
        boolean claimedflag = false;
        boolean ownedflag = false;
        boolean playerflag = false;
        boolean friendflag = false;
        System.out.print("Checking " + x + ", " + z + ": ");
        Plot plot = context.getPlot(x,z);
        System.out.println(plot);
        if (plot != null) {
          System.out.println(plot.getX() + ", " + plot.getZ());
          claimedflag = true;
          if (plot.getOwner().equals(this.gamer)) {
            ownedflag = true;
          }
          Entity[] entities = chunk.getEntities();
          for (Entity ent : entities) {
            if (ent instanceof Player) {
              if (this.gamer.getFriends().contains(ent)) {
                friendflag = true;
              } else {
                playerflag = true;
              }
              break;
            }
          }
        }
        if (friendflag) {
          map.addExtra(friendKey);
        } else if (playerflag) {
          map.addExtra(playerKey);
        } else if (ownedflag) {
          map.addExtra(ownedKey);
        } else if (claimedflag) {
          map.addExtra(claimedKey);
        } else {
          map.addExtra(unclaimedKey);
        }
      }

      if (i != 6) {
        map.addExtra("\n");
      }
      z = z - 46;
    }
    BaseComponent[] key = new ComponentBuilder("-").color(ChatColor.GRAY).append(" = Unclaimed ").color(ChatColor.WHITE).
            append("+ = Claimed ").append("+").color(ChatColor.GREEN).append(" = your plot \n").color(ChatColor.WHITE).append("+").color(ChatColor.YELLOW).
            append(" = your plot ").color(ChatColor.WHITE).append("+").color(ChatColor.DARK_GREEN).append(" = Friend ").color(ChatColor.WHITE).append("+").color(ChatColor.RED).append(" = Players").color(ChatColor.WHITE).create();
    player.sendMessage(map);
    player.sendMessage(key);
  }
}

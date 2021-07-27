package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.ArrayList;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class MapMenu extends ListenerClient {
  private final Channels channels;
  private final Fiber fiber;
  private final Gamer gamer;
  private final int WIDTH;
  private final int HEIGHT;
  private final TextComponent unclaimedKey;
  private final TextComponent claimedKey;
  private final TextComponent ownedKey;
  private final TextComponent playerKey;
  private final TextComponent selfKey;
  private final TextComponent friendKey;

  public MapMenu(Gamer gamer, Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    this.fiber = fiber;
    this.gamer = gamer;

    WIDTH = 45;
    HEIGHT = 7;

    unclaimedKey = new TextComponent("-");
    unclaimedKey.setColor(ChatColor.GRAY);
    claimedKey = new TextComponent("+");
    claimedKey.setColor(ChatColor.WHITE);
    ownedKey = new TextComponent("+");
    ownedKey.setColor(ChatColor.GREEN);
    playerKey = new TextComponent("+");
    playerKey.setColor(ChatColor.RED);
    selfKey = new TextComponent("^");
    selfKey.setColor(ChatColor.YELLOW);
    friendKey = new TextComponent("+");
    friendKey.setColor(ChatColor.DARK_GREEN);
  }

  public String playerDirection(float yaw){
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

    return facing;
  }

  public void mapMenu() {
    Player player = this.gamer.getPlayer();
    TextComponent map = new TextComponent("");
    Location playerLoc = player.getLocation();
    Chunk chunk = player.getChunk();
    String facing = playerDirection(playerLoc.getYaw());

    int x = chunk.getX();
    int z = chunk.getZ();
    TextComponent title = new TextComponent("===========,[Etherlands Map (" + x + ", " + z + ") " + facing + " ],===========\n");
    map.addExtra(title);
    if(facing == "E"||facing == "W"){
      x = x - HEIGHT/2-1;
      z = z - WIDTH/2-1;
    }else{
      z = z - HEIGHT/2-1;
      x = x - WIDTH/2-1;
    }
    TextComponent[][] mapArray = new TextComponent[WIDTH][HEIGHT];
    for (int i = 0; i < HEIGHT; i++) {
      if(facing == "E"||facing == "W"){
        x++;
      }else{
        z++;
      }
      for (int j = 0; j < WIDTH; j++) {
        if(facing == "E"||facing == "W"){
          z++;
        }else{
          x++;
        }
        boolean claimedflag = false;
        boolean ownedflag = false;
        boolean playerflag = false;
        boolean friendflag = false;
        boolean selfFlag = false;
        Plot plot;
        if(facing == "E"||facing == "W"){
           plot = state().getPlot(x,z);
        }else{
          plot = state().getPlot(z, x);
        }
        if (plot != null) {
          claimedflag = true;
          if (plot.isOwner(gamer)) {
            ownedflag = true;
          }
        }

        for(String n : getPlayerStrings()){
          Player p = Bukkit.getPlayer(n);
          Location l = p.getLocation();
          if(l.getZ()==z&&l.getX()==x){
            if(p.equals(player)){
              selfFlag = true;
            }else if (this.gamer.getFriends().contains(p.getUniqueId())) {
              friendflag = true;
            } else {
              playerflag = true;
            }
            break;
          }
        }

        if(selfFlag){
          mapArray[j][i] = selfKey;
        } else if (friendflag) {
          mapArray[j][i] = friendKey;
        } else if (playerflag) {
          mapArray[j][i] = playerKey;
        } else if (ownedflag) {
          mapArray[j][i] = ownedKey;
        } else if (claimedflag) {
          mapArray[j][i] = claimedKey;
        } else {
          mapArray[j][i] = unclaimedKey;
        }
      }
      if(facing == "E"||facing == "W"){
        z = z - WIDTH;
      }else{
        x = x - WIDTH;
      }
    }
    for(int i = 0; i<HEIGHT;i++){
      map.addExtra("|");
      for(int j = 0; j<WIDTH;j++){
        if(facing=="S"||facing=="E"){
          map.addExtra(mapArray[WIDTH-j-1][HEIGHT-i-1]);
        }else{
          map.addExtra(mapArray[j][i]);
        }

      }
      map.addExtra("|");
      if(i!=HEIGHT-1){
        map.addExtra("\n");
      }
    }
    BaseComponent[] key = new ComponentBuilder("-").color(ChatColor.GRAY).append(" = Unclaimed ").color(ChatColor.WHITE).
            append("+ = Claimed ").append("+").color(ChatColor.GREEN).append(" = Your Plot \n").color(ChatColor.WHITE).append("^").color(ChatColor.YELLOW).
            append(" = You ").color(ChatColor.WHITE).append("+").color(ChatColor.DARK_GREEN).append(" = Friend ").color(ChatColor.WHITE).append("+").color(ChatColor.RED).append(" = Players").color(ChatColor.WHITE).create();
    player.sendMessage(map);
    player.sendMessage(key);
  }

}

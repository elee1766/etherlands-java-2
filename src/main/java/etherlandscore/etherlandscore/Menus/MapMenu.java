package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Plot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;
import org.w3c.dom.Text;

import java.util.Locale;

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

    WIDTH = 37;
    HEIGHT = 7;

    unclaimedKey = new TextComponent("-");
    unclaimedKey.setColor(ChatColor.GRAY);
    claimedKey = new TextComponent("+");
    claimedKey.setColor(ChatColor.LIGHT_PURPLE);
    ownedKey = new TextComponent("+");
    ownedKey.setColor(ChatColor.GREEN);
    playerKey = new TextComponent("+");
    playerKey.setColor(ChatColor.RED);
    selfKey = new TextComponent("^");
    selfKey.setColor(ChatColor.YELLOW);
    friendKey = new TextComponent("+");
    friendKey.setColor(ChatColor.DARK_GREEN);
  }

  public void mapMenuCoord(String facingIn, int xin, int zin) {
    Player player = this.gamer.getPlayer();
    TextComponent map = new TextComponent("");
    String facing = facingIn;

    int x = xin;
    int z = zin;
    TextComponent[] compass = compass(facing, x, z);
    TextComponent title =
        new TextComponent(
            "===========,[Etherlands Map (" + x + ", " + z + ") " + facing + " " + facingCoord(facing) + " ],===========\n");
    map.addExtra(title);
    if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
      x = x - HEIGHT / 2 - 1;
      z = z - WIDTH / 2 - 1;
    } else {
      z = z - HEIGHT / 2 - 1;
      x = x - WIDTH / 2 - 1;
    }

    TextComponent[][] mapArray = new TextComponent[WIDTH][HEIGHT];
    for (int i = 0; i < HEIGHT; i++) {
      if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
        x++;
      } else {
        z++;
      }
      for (int j = 0; j < WIDTH; j++) {
        if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
          z++;
        } else {
          x++;
        }
        boolean claimedflag = false;
        boolean ownedflag = false;
        boolean playerflag = false;
        boolean friendflag = false;
        boolean selfFlag = false;
        Plot plot;
        if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
          plot = state().getPlot(x, z);
        } else {
          plot = state().getPlot(z, x);
        }
        if (plot != null) {
          claimedflag = true;
          if (plot.isOwner(gamer)) {
            ownedflag = true;
          }
        }

        for (Player p : getOnlinePlayers()) {
          Chunk pc = p.getChunk();
          if (pc.getZ() == z && pc.getX() == x) {
            if (p.equals(player)) {
              selfFlag = true;
            } else if (this.gamer.hasFriend(p)) {
              friendflag = true;
            } else {
              playerflag = true;
            }
          }
        }

        if (selfFlag) {
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
      if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
        z = z - WIDTH;
      } else {
        x = x - WIDTH;
      }
    }
    for (int i = 0; i < HEIGHT; i++) {
      map.addExtra(compass[i]);
      map.addExtra("|");
      for (int j = 0; j < WIDTH; j++) {
        if (facing.toLowerCase().contains("s") || facing.toLowerCase().contains("e")) {
          map.addExtra(mapArray[WIDTH - j - 1][HEIGHT - i - 1]);
        } else {
          map.addExtra(mapArray[j][i]);
        }
      }
      map.addExtra("|");
      if (i != HEIGHT - 1) {
        map.addExtra("\n");
      }
    }
    BaseComponent[] key =
        new ComponentBuilder("-")
            .color(ChatColor.GRAY)
            .append(" = Unclaimed ")
            .color(ChatColor.WHITE)
            .append("+ = Claimed ")
            .append("+")
            .color(ChatColor.GREEN)
            .append(" = Your Plot \n")
            .color(ChatColor.WHITE)
            .append("^")
            .color(ChatColor.YELLOW)
            .append(" = You ")
            .color(ChatColor.WHITE)
            .append("+")
            .color(ChatColor.DARK_GREEN)
            .append(" = Friend ")
            .color(ChatColor.WHITE)
            .append("+")
            .color(ChatColor.RED)
            .append(" = Players")
            .color(ChatColor.WHITE)
            .create();
    player.sendMessage(map);
    player.sendMessage(key);
  }

  public static String facingCoord(String facing){
    if(facing.toLowerCase().contains("n")){
      return "-z";
    }else if(facing.toLowerCase().contains("s")){
      return "+z";
    }else if(facing.toLowerCase().contains("w")){
      return "-x";
    }else {
      return "+x";
    }
  }

  public void mapMenu() {
    Player player = this.gamer.getPlayer();
    TextComponent map = new TextComponent("");
    Location playerLoc = player.getLocation();
    Chunk chunk = player.getChunk();
    String facing = playerDirection(playerLoc.getYaw());

    int x = chunk.getX();
    int z = chunk.getZ();
    TextComponent[] compass = compass(facing, x, z);
    TextComponent title =
        new TextComponent(
            "=====,[Etherlands Map (" + x + ", " + z + ") " + facing + " " + facingCoord(facing) + " ],=======\n");
    map.addExtra(title);
    if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
      x = x - HEIGHT / 2 - 1;
      z = z - WIDTH / 2 - 1;
    } else {
      z = z - HEIGHT / 2 - 1;
      x = x - WIDTH / 2 - 1;
    }
    
    TextComponent[][] mapArray = new TextComponent[WIDTH][HEIGHT];
    for (int i = 0; i < HEIGHT; i++) {
      if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
        x++;
      } else {
        z++;
      }
      for (int j = 0; j < WIDTH; j++) {
        if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
          z++;
        } else {
          x++;
        }
        boolean claimedflag = false;
        boolean ownedflag = false;
        boolean playerflag = false;
        boolean friendflag = false;
        boolean selfFlag = false;
        Plot plot;
        if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
          plot = state().getPlot(x, z);
        } else {
          plot = state().getPlot(z, x);
        }
        if (plot != null) {
          claimedflag = true;
          if (plot.isOwner(gamer)) {
            ownedflag = true;
          }
        }

        for (Player p : getOnlinePlayers()) {
          Chunk pc = p.getChunk();
          if (pc.getZ() == z && pc.getX() == x) {
            if (p.equals(player)) {
              selfFlag = true;
            } else if (this.gamer.hasFriend(p)) {
              friendflag = true;
            } else {
              playerflag = true;
            }
          }
        }

        if (selfFlag) {
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
      if (facing.toLowerCase().contains("e") || facing.toLowerCase().contains("w")) {
        z = z - WIDTH;
      } else {
        x = x - WIDTH;
      }
    }
    for (int i = 0; i < HEIGHT; i++) {
      map.addExtra(compass[i]);
      map.addExtra("|");
      for (int j = 0; j < WIDTH; j++) {
        if (facing.toLowerCase().contains("s") || facing.toLowerCase().contains("w")) {
          map.addExtra(mapArray[WIDTH - j - 1][HEIGHT - i - 1]);
        } else {
          map.addExtra(mapArray[j][i]);
        }
      }
      map.addExtra("|");
      if (i != HEIGHT - 1) {
        map.addExtra("\n");
      }
    }
    BaseComponent[] key =
        new ComponentBuilder("-")
            .color(ChatColor.GRAY)
            .append(" = Unclaimed ")
            .color(ChatColor.WHITE)
            .append("+ = Claimed ")
            .append("+")
            .color(ChatColor.GREEN)
            .append(" = Your Plot \n")
            .color(ChatColor.WHITE)
            .append("^")
            .color(ChatColor.YELLOW)
            .append(" = You ")
            .color(ChatColor.WHITE)
            .append("+")
            .color(ChatColor.DARK_GREEN)
            .append(" = Friend ")
            .color(ChatColor.WHITE)
            .append("+")
            .color(ChatColor.RED)
            .append(" = Players")
            .color(ChatColor.WHITE)
            .create();
    player.sendMessage(map);
    player.sendMessage(key);
  }

  private TextComponent[] compass(String facing, int xin, int zin) {
    TextComponent[] compassComps = new TextComponent[HEIGHT];
    ClickEvent gosouth = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "S" + " " + xin + " " + (zin+2));
    ClickEvent gonorth = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "N" + " " + xin + " " + (zin-2));
    ClickEvent goeast = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "E" + " " + (xin-2) + " " + zin);
    ClickEvent gowest = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "W" + " " + (xin+2) + " " + zin);
    ClickEvent goleft;
    ClickEvent goright;
    ClickEvent goup;
    ClickEvent godown;
    String left = "";
    String right = "";
    String down = "";
    if (facing.toLowerCase().contains("n")) {
      goup = gonorth;
      left = "W";
      goleft = gowest;
      right = "E";
      goright = goeast;
      down = "S";
      godown = gosouth;
    } else if (facing.toLowerCase().contains("s")) {
      goup = gosouth;
      left = "E";
      goleft = goeast;
      right = "W";
      goright = gowest;
      down = "N";
      godown = gonorth;
    } else if (facing.toLowerCase().contains("w")) {
      goup = gowest;
      left = "S";
      goleft = gosouth;
      right = "N";
      goright = gonorth;
      down = "E";
      godown = goeast;
    } else {
      goup = goeast;
      left = "N";
      goleft = gonorth;
      right = "S";
      goright = gosouth;
      down = "W";
      godown = gowest;
    }
    int c = 0;
    for(int i = 0; i<HEIGHT;i++) {
      TextComponent line = new TextComponent("");
      for (int j = 0; j < 5; j++) {
        TextComponent spot = new TextComponent("");
        if (i == 5) {
          if (j == 1) {
            spot.addExtra(left);
            spot.setClickEvent(goleft);
          }else if (j == 3) {
            spot.addExtra(right);
            spot.setClickEvent(goright);
          }else{
            spot.addExtra("-");
            spot.setColor(ChatColor.BLACK);
          }
        } else if (i == 4) {
            if(j==2){
              spot.addExtra(facing);
              spot.setClickEvent(goup);
            }else{
              spot.addExtra("-");
              spot.setColor(ChatColor.BLACK);
            }
        } else if (i == 6) {
          if(j==2) {
            spot.addExtra(down);
            spot.setClickEvent(godown);
          }else{
            spot.addExtra("-");
            spot.setColor(ChatColor.BLACK);
          }
        } else {
          spot = new TextComponent("-");
          spot.setColor(ChatColor.BLACK);
        }
        line.addExtra(spot);
      }
      compassComps[c] = line;
      c++;
    }
    return compassComps;
  }

  public String playerDirection(float yaw) {
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
}

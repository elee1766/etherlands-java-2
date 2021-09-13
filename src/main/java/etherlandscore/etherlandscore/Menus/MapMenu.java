package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Plot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

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

    WIDTH = 11;
    HEIGHT = 11;

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
            "=======,[Etherlands Map (" + x + ", " + z + ") " + facing + " " + facingCoord(facing) + " ],=======\n");
    map.addExtra(title);
    x = x - HEIGHT / 2 - 1;
    z = z - WIDTH / 2 - 1;

    TextComponent[][] mapArray = new TextComponent[WIDTH][HEIGHT];
    for (int i = 0; i < HEIGHT; i++) {
      x++;
      for (int j = 0; j < WIDTH; j++) {
        z++;
        boolean claimedflag = false;
        boolean ownedflag = false;
        boolean playerflag = false;
        boolean friendflag = false;
        boolean selfFlag = false;
        HoverEvent friendHover = null;
        HoverEvent playerHover = null;
        HoverEvent claimedHover = null;
        Plot plot;
        plot = state().getPlot(x, z);
        if (plot != null) {
          claimedflag = true;
          claimedHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+plot.getX() + ", " + plot.getZ()+")"));
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
              friendHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Friend: " + p.getName()));
            } else {
              playerflag = true;
              playerHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Player: " + p.getName()));
            }
          }
        }

        if (selfFlag) {
          mapArray[j][i] = new TextComponent(selfKey);
          mapArray[j][i].setHoverEvent((new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("You"))));
        } else if (friendflag) {
          mapArray[j][i] = new TextComponent(friendKey);
          mapArray[j][i].setHoverEvent(friendHover);
        } else if (playerflag) {
          mapArray[j][i] = new TextComponent(playerKey);
          mapArray[j][i].setHoverEvent(playerHover);
        } else if (ownedflag) {
          mapArray[j][i] = new TextComponent(ownedKey);
          mapArray[j][i].setHoverEvent(claimedHover);
        } else if (claimedflag) {
          mapArray[j][i] = new TextComponent(claimedKey);
          mapArray[j][i].setHoverEvent(claimedHover);
        } else {
          mapArray[j][i] = new TextComponent(unclaimedKey);
          mapArray[j][i].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Unclaimed: ("+x+", "+z+")")));
        }
      }
      z = z - WIDTH;
    }


    if(facing.toLowerCase().contains("n")){
      flipHorizontalInPlace(mapArray);
      mapArray = rotateMap(rotateMap(rotateMap(mapArray)));
    } else if(facing.toLowerCase().contains("w")){
      Bukkit.getLogger().info("facing west");
      mapArray = rotateMap(mapArray);
      flipInPlace(mapArray);
      mapArray = rotateMap(mapArray);
      flipInPlace(mapArray);
    } else if(facing.toLowerCase().contains("s")){
      mapArray = rotateMap(mapArray);
      flipInPlace(mapArray);
    }

    TextComponent[] key = key();

    for (int i = 0; i < HEIGHT; i++) {
      map.addExtra(compass[i]);
      map.addExtra("|");
      for (int j = 0; j < WIDTH; j++) {
        map.addExtra(mapArray[j][HEIGHT - i - 1]);
      }
      map.addExtra("|");
      map.addExtra(key[i]);
      if (i != HEIGHT - 1) {
        map.addExtra("\n");
      }
    }
    channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, map));
  }

  private TextComponent[][] rotateMap(TextComponent[][] mapArray) {
    TextComponent[][] newMapArray = new TextComponent[WIDTH][HEIGHT];
    for (int i = 0; i < mapArray[0].length; i++) {
      for (int j = mapArray.length - 1; j >= 0; j--) {
        newMapArray[i][j] = mapArray[j][i];
      }
    }
    return newMapArray;
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

  public static void flipInPlace(Object[][] theArray) {
    for(int i = 0; i < (theArray.length / 2); i++) {
      Object[] temp = theArray[i];
      theArray[i] = theArray[theArray.length - i - 1];
      theArray[theArray.length - i - 1] = temp;
    }
  }

  public static void flipHorizontalInPlace(Object[][] theArray) {
    Object temp;
    for (int i = 0; i < theArray.length / 2; i++) {
      for (int j = 0; j < theArray[i].length; j++) {
        temp = theArray[i][j];
        theArray[i][j] = theArray[theArray.length - 1 - i][j];
        theArray[theArray.length - 1 -i][j] = temp;
      }
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
            "[Etherlands Map (" + x + ", " + z + ") " + facing + " " + facingCoord(facing) + " ]\n");
    map.addExtra(title);
    x = x - HEIGHT / 2 - 1;
    z = z - WIDTH / 2 - 1;
    
    TextComponent[][] mapArray = new TextComponent[WIDTH][HEIGHT];
    for (int i = 0; i < HEIGHT; i++) {
      x++;
      for (int j = 0; j < WIDTH; j++) {
        z++;
        boolean claimedflag = false;
        boolean ownedflag = false;
        boolean playerflag = false;
        boolean friendflag = false;
        boolean selfFlag = false;
        HoverEvent friendHover = null;
        HoverEvent playerHover = null;
        HoverEvent claimedHover = null;
        ClickEvent friendClick = null;
        ClickEvent playerClick = null;
        ClickEvent claimedClick = null;
        Plot plot;
        plot = state().getPlot(x, z);
        if (plot != null) {
          claimedflag = true;
          claimedHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+plot.getX() + ", " + plot.getZ()+")"));
          claimedClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/district info " + plot.getDistrict()));
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
              friendHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Friend: " + p.getName()));
              friendClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/gamer info " + p.getName()));
            } else {
              playerflag = true;
              playerHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Player: " + p.getName()));
              playerClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/gamer info " + p.getName()));
            }
          }
        }

        if (selfFlag) {
          mapArray[j][i] = new TextComponent(selfKey);
          mapArray[j][i].setHoverEvent((new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("You"))));
          mapArray[j][i].setClickEvent((new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/gamer info")));
        } else if (friendflag) {
          mapArray[j][i] = new TextComponent(friendKey);
          mapArray[j][i].setHoverEvent(friendHover);
          mapArray[j][i].setClickEvent(friendClick);
        } else if (playerflag) {
          mapArray[j][i] = new TextComponent(playerKey);
          mapArray[j][i].setHoverEvent(playerHover);
          mapArray[j][i].setClickEvent(playerClick);
        } else if (ownedflag) {
          mapArray[j][i] = new TextComponent(ownedKey);
          mapArray[j][i].setHoverEvent(claimedHover);
          mapArray[j][i].setClickEvent(claimedClick);
        } else if (claimedflag) {
          mapArray[j][i] = new TextComponent(claimedKey);
          mapArray[j][i].setHoverEvent(claimedHover);
          mapArray[j][i].setClickEvent(claimedClick);
        } else {
          mapArray[j][i] = new TextComponent(unclaimedKey);
          mapArray[j][i].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Unclaimed: ("+x+", "+z+")")));
        }
      }
      z = z - WIDTH;
    }

    if(facing.toLowerCase().contains("n")){
      flipHorizontalInPlace(mapArray);
      mapArray = rotateMap(rotateMap(rotateMap(mapArray)));
    } else if(facing.toLowerCase().contains("w")){
      Bukkit.getLogger().info("facing west");
      mapArray = rotateMap(mapArray);
      flipInPlace(mapArray);
      mapArray = rotateMap(mapArray);
      flipInPlace(mapArray);
    } else if(facing.toLowerCase().contains("s")){
      mapArray = rotateMap(mapArray);
      flipInPlace(mapArray);
    }

    TextComponent[] key = key();

    for (int i = 0; i < HEIGHT-2; i++) {
      map.addExtra(compass[i]);
      map.addExtra("|");
      for (int j = 0; j < WIDTH; j++) {
        map.addExtra(mapArray[j][HEIGHT - i - 1]);
      }
      map.addExtra("|");
      map.addExtra(key[i]);
      if (i != HEIGHT - 3) {
        map.addExtra("\n");
      }
    }
    channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, map));
  }

  private TextComponent[] key() {
    TextComponent[] compassComps = new TextComponent[HEIGHT];

    TextComponent blank = new TextComponent("");

    TextComponent unclaimed = new TextComponent(" -");
    unclaimed.setColor(ChatColor.GRAY);
    unclaimed.addExtra(" = unclaimed");

    TextComponent claimed = new TextComponent(" +");
    claimed.setColor(ChatColor.LIGHT_PURPLE);
    claimed.addExtra(" = claimed");

    TextComponent yourPlot = new TextComponent(" +");
    yourPlot.setColor(ChatColor.GREEN);
    yourPlot.addExtra(" = your plot");

    TextComponent you = new TextComponent(" ^");
    you.setColor(ChatColor.YELLOW);
    you.addExtra(" = you");

    TextComponent friend = new TextComponent(" +");
    friend.setColor(ChatColor.DARK_GREEN);
    friend.addExtra(" = friend");

    TextComponent player = new TextComponent(" +");
    player.setColor(ChatColor.RED);
    player.addExtra(" = player");

    Player p = this.gamer.getPlayer();
    Location pLoc = p.getLocation();
    TextComponent pos = new TextComponent(" Pos: (" + (int)pLoc.getX() + ", " + (int)pLoc.getY() + ", " + (int)pLoc.getZ() + ")");
    TextComponent npos = new TextComponent(" Nether: (" + ((int)pLoc.getX())/8 + ", " + ((int)pLoc.getY()) + ", " + ((int)pLoc.getZ())/8 + ")");
    pos.setColor(ChatColor.GOLD);
    npos.setColor(ChatColor.RED);

    compassComps[0] = unclaimed;
    compassComps[1] = claimed;
    compassComps[2] = yourPlot;
    compassComps[3] = you;
    compassComps[4] = friend;
    compassComps[5] = player;
    compassComps[6] = blank;
    compassComps[7] = pos;
    compassComps[8] = blank;

    return compassComps;
  }

  private TextComponent[] compass(String facing, int xin, int zin) {
    TextComponent[] compassComps = new TextComponent[HEIGHT];
    ClickEvent gosouth = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "S" + " " + xin + " " + (zin+2));
    ClickEvent gonorth = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "N" + " " + xin + " " + (zin-2));
    ClickEvent gowest = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "W" + " " + (xin-2) + " " + zin);
    ClickEvent goeast = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "E" + " " + (xin+2) + " " + zin);
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

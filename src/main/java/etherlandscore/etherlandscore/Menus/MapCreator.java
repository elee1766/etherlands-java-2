package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.singleton.WorldAsker;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.state.District;
import etherlandscore.etherlandscore.state.Gamer;
import kotlin.Triple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class MapCreator {
  private final Gamer gamer;
  private final int SIZE_OF_SQUARE;
  private final int HEIGHT = 9;
  private final int WIDTH;
  private final BlockFace facing;
  private final int x;
  private final int z;
  private final TextComponent unclaimedKey;
  private final TextComponent claimedKey;
  private final TextComponent ownedKey;
  private final TextComponent playerKey;
  private final TextComponent selfKey;
  private final TextComponent friendKey;
  private TextComponent[][] mapArray;
  private boolean showGamer;
  private Map<UUID, Triple<Integer, Integer, Integer>> gamerLocations = new HashMap<>();

  public MapCreator(Gamer gamer, int xin, int zin, int size, boolean showGamer) {
    this.gamer = gamer;
    this.facing = getCardinalDirection(gamer.getPlayer());
    this.x = xin;
    this.z = zin;
    this.WIDTH = size;
    this.SIZE_OF_SQUARE = Math.max(this.WIDTH, this.HEIGHT);
    this.mapArray = new TextComponent[SIZE_OF_SQUARE][SIZE_OF_SQUARE];
    this.showGamer = showGamer;

    unclaimedKey = ComponentCreator.ColoredText("-",ChatColor.GRAY);
    claimedKey = ComponentCreator.ColoredText("+",ChatColor.LIGHT_PURPLE);
    ownedKey = ComponentCreator.ColoredText("+",ChatColor.GREEN);
    playerKey = ComponentCreator.ColoredText("+",ChatColor.DARK_RED);
    selfKey = ComponentCreator.ColoredText("^",ChatColor.YELLOW);
    friendKey = ComponentCreator.ColoredText("+",ChatColor.DARK_GREEN);
  }

  public MapCreator(Gamer gamer, int xin, int zin, boolean showGamer) {
    this.gamer = gamer;
    this.facing = getCardinalDirection(gamer.getPlayer());
    this.x = xin;
    this.z = zin;
    this.WIDTH = 25;
    this.SIZE_OF_SQUARE = Math.max(this.WIDTH, this.HEIGHT);
    this.mapArray = new TextComponent[SIZE_OF_SQUARE][SIZE_OF_SQUARE];
    this.showGamer = showGamer;

    unclaimedKey = ComponentCreator.ColoredText("-",ChatColor.GRAY);
    claimedKey = ComponentCreator.ColoredText("+",ChatColor.LIGHT_PURPLE);
    ownedKey = ComponentCreator.ColoredText("+",ChatColor.GREEN);
    playerKey = ComponentCreator.ColoredText("+",ChatColor.DARK_RED);
    selfKey = ComponentCreator.ColoredText("^",ChatColor.YELLOW);
    friendKey = ComponentCreator.ColoredText("+",ChatColor.DARK_GREEN);
  }

  private void getGamerLocations() {
    for(Player p : Bukkit.getOnlinePlayers()){
      UUID uuid = p.getUniqueId();
      gamerLocations.put(p.getUniqueId(), WorldAsker.GetGamerXYZ(uuid));
    }
  }

  public static BlockFace getCardinalDirection(Player player) {
    double rotation = (player.getLocation().getYaw() - 180) % 360;
    if (rotation < 0) {
      rotation += 360.0;
    }
    if (0 <= rotation && rotation < 22.5) {
      return BlockFace.NORTH;
    } else if (22.5 <= rotation && rotation < 67.5) {
      return BlockFace.NORTH_EAST;
    } else if (67.5 <= rotation && rotation < 112.5) {
      return BlockFace.EAST;
    } else if (112.5 <= rotation && rotation < 157.5) {
      return BlockFace.SOUTH_EAST;
    } else if (157.5 <= rotation && rotation < 202.5) {
      return BlockFace.SOUTH;
    } else if (202.5 <= rotation && rotation < 247.5) {
      return BlockFace.SOUTH_WEST;
    } else if (247.5 <= rotation && rotation < 292.5) {
      return BlockFace.WEST;
    } else if (292.5 <= rotation && rotation < 337.5) {
      return BlockFace.NORTH_WEST;
    } else if (337.5 <= rotation && rotation < 360.0) {
      return BlockFace.NORTH;
    } else {
      return BlockFace.NORTH;
    }
  }

  public BaseComponent combined(){
    BaseComponent title = title();
    BaseComponent[] map = mapMenu();
    BaseComponent[] key = key();
    BaseComponent[] compass = compass();
    BaseComponent output = new TextComponent("");

    output.addExtra(title);
    for (int i = 0; i < this.HEIGHT; i++) {
      output.addExtra(compass[i]);
      output.addExtra("|");
      output.addExtra(map[i]);
      output.addExtra("|");
      output.addExtra(key[i]);
      if (i != HEIGHT - 1) {
        output.addExtra("\n");
      }
    }
    return output;
  }

  private BaseComponent[] compass() {
    TextComponent[] compassComps = new TextComponent[HEIGHT];
    ClickEvent gosouth = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + x + " " + (z+2));
    ClickEvent gonorth = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + x + " " + (z-2));
    ClickEvent gowest = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + (x-2) + " " + z);
    ClickEvent goeast = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + (x+2) + " " + z);

    TextComponent center = ComponentCreator.ColoredText("+",ChatColor.RED);
    TextComponent tick = ComponentCreator.ColoredText("-",ChatColor.BLACK);
    TextComponent north = ComponentCreator.ColoredText("N",ChatColor.WHITE);
    north.setClickEvent(gonorth);
    TextComponent northeast = ComponentCreator.ColoredText("/",ChatColor.WHITE);
    TextComponent east = ComponentCreator.ColoredText("E",ChatColor.WHITE);
    east.setClickEvent(goeast);
    TextComponent southeast = ComponentCreator.ColoredText("\\",ChatColor.WHITE);
    TextComponent south = ComponentCreator.ColoredText("S",ChatColor.WHITE);
    south.setClickEvent(gosouth);
    TextComponent southwest = ComponentCreator.ColoredText("/",ChatColor.WHITE);
    TextComponent west = ComponentCreator.ColoredText("W",ChatColor.WHITE);
    west.setClickEvent(gowest);
    TextComponent northwest = ComponentCreator.ColoredText("\\",ChatColor.WHITE);

    switch (facing) {
      case NORTH:
        north.setColor(ChatColor.RED);
        break;
      case EAST:
        east.setColor(ChatColor.RED);
        break;
      case SOUTH:
        south.setColor(ChatColor.RED);
        break;
      case WEST:
        west.setColor(ChatColor.RED);
        break;
      case NORTH_EAST:
        northeast.setColor(ChatColor.RED);
        break;
      case NORTH_WEST:
        northwest.setColor(ChatColor.RED);
        break;
      case SOUTH_EAST:
        southeast.setColor(ChatColor.RED);
        break;
      case SOUTH_WEST:
        southwest.setColor(ChatColor.RED);
        break;
      default:
        break;
    }
    int c = 0;
    for(int i = 0; i< HEIGHT; i++) {
      TextComponent line = new TextComponent("");
      for (int j = 0; j < 5; j++) {
        TextComponent spot = new TextComponent("");
        switch (i) {
          case 5:
            switch (j) {
              case 1 -> spot.addExtra(west);
              case 2 -> spot.addExtra(center);
              case 3 -> spot.addExtra(east);
              default -> spot.addExtra(tick);
            }
            break;
          case 4:
            switch (j) {
              case 1 -> spot.addExtra(northwest);
              case 2 -> spot.addExtra(north);
              case 3 -> spot.addExtra(northeast);
              default -> spot.addExtra(tick);
            }
            break;
          case 6:
            switch (j) {
              case 1 -> spot.addExtra(southwest);
              case 2 -> spot.addExtra(south);
              case 3 -> spot.addExtra(southeast);
              default -> spot.addExtra(tick);
            }
            break;
          default:
            spot.addExtra(tick);
            break;
        }
        line.addExtra(spot);
      }
      compassComps[c] = line;
      c++;
    }
    return compassComps;
  }

  private BaseComponent[] key() {
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
    player.setColor(ChatColor.DARK_RED);
    player.addExtra(" = player");

    compassComps[0] = unclaimed;
    compassComps[1] = claimed;
    compassComps[2] = yourPlot;
    compassComps[3] = you;
    compassComps[4] = friend;
    compassComps[5] = player;
    compassComps[6] = blank;
    compassComps[7] = blank;
    compassComps[8] = blank;

    return compassComps;
  }

  public BaseComponent[] mapMenu() {
    getGamerLocations();
    int x = this.x - SIZE_OF_SQUARE / 2 - 1;
    int z = this.z - SIZE_OF_SQUARE / 2 - 1;
    for (int i = 0; i < SIZE_OF_SQUARE; i++) {
      x++;
      for (int j = 0; j < SIZE_OF_SQUARE; j++) {
        z++;
        boolean selfFlag = false;
        boolean friendflag = false;
        boolean playerflag = false;
        boolean claimedflag = false;
        boolean ownedflag = false;
        HoverEvent friendHover = null;
        HoverEvent playerHover = null;
        HoverEvent claimedHover = null;
        ClickEvent friendClick = null;
        ClickEvent playerClick = null;
        ClickEvent claimedClick = null;
        if(x == this.x && z == this.z){
          if(this.showGamer){
            selfFlag = true;
          }
        }
        Plot plot = state().getPlot(x,z);
        if(plot.getIdInt() != null){
          District district = state().getDistrict(x, z);
          if(district != null){
            claimedHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+x + ", " + z+")"));
            claimedClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/district " + district.getIdInt()));
            claimedflag = true;
            if (district.isOwner(gamer)) {
              ownedflag = true;
            }
          }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
          Triple<Integer, Integer, Integer> loc = gamerLocations.get(p.getUniqueId());
          if (loc.getThird() == z && loc.getFirst() == x) {
            if (p.getUniqueId()==this.gamer.getUuid()) {
              //selfFlag = true;
            } else if (this.gamer.hasFriend(p.getUniqueId())) {
              friendflag = true;
              friendHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Friend: " + p.getName()));
              friendClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/gamer " + p.getName()));
            } else {
              playerflag = true;
              playerHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Player: " + p.getName()));
              playerClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/gamer " + p.getName()));
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
      z = z - SIZE_OF_SQUARE;
    }

    this.mapArray = rotateMap(rotateMap(this.mapArray));
    TextComponent[] returnValue = new TextComponent[HEIGHT];

    int c = 0;
    int rangeCount = 0;
    if(WIDTH<HEIGHT){
      if(WIDTH%2!=0) {
        for (int i = 0; i < HEIGHT; i++) {
          TextComponent line = new TextComponent("");
          for (int j = (HEIGHT - WIDTH) / 2; j < (HEIGHT - (HEIGHT - WIDTH) / 2); j++) {
            line.addExtra(this.mapArray[i][j]);
          }
          returnValue[c] = line;
          c++;
        }
        return returnValue;
      }else {
        for (int i = 0; i < HEIGHT; i++) {
          TextComponent line = new TextComponent("");
          for (int j = (HEIGHT - WIDTH) / 2; j < (HEIGHT - (HEIGHT - WIDTH) / 2)-1; j++) {
            line.addExtra(this.mapArray[i][j]);
          }
          returnValue[c] = line;
          c++;
        }
        return returnValue;
      }
    }
    if(SIZE_OF_SQUARE%2==0){
      double num = SIZE_OF_SQUARE - HEIGHT;
      rangeCount = (int) Math.ceil(num/2.0);
      for(int i = rangeCount; i< SIZE_OF_SQUARE-(rangeCount)+1; i++){
        TextComponent line = new TextComponent("");
        TextComponent[] comp = this.mapArray[i];
        for(TextComponent block : comp){
          line.addExtra(block);
        }
        returnValue[c]=line;
        c++;
      }
      return returnValue;

    }else{
      rangeCount = (SIZE_OF_SQUARE-HEIGHT)/2;
      for(int i = rangeCount; i< SIZE_OF_SQUARE-(rangeCount); i++){
        TextComponent line = new TextComponent("");
        TextComponent[] comp = this.mapArray[i];
        for(TextComponent block : comp){
          line.addExtra(block);
        }
        returnValue[c]=line;
        c++;
      }
      return returnValue;
    }
  }

  private TextComponent[][] rotateMap(TextComponent[][] flip) {
    TextComponent[][] newMapArray = new TextComponent[SIZE_OF_SQUARE][SIZE_OF_SQUARE];
    for (int i = 0; i < flip[0].length; i++) {
      for (int j = flip.length - 1; j >= 0; j--) {
        newMapArray[i][j] = flip[j][i];
      }
    }
    return newMapArray;
  }

  public BaseComponent title(){
    int initial_length = 50;
    String title = "Map (" + x + ", " + z + ")";
    int title_length = title.length();
    int new_length = initial_length - title_length;
    int half_length = new_length / 2;
    String left = StringUtils.repeat("_",half_length) + ",[ ";
    String right = " ],"+StringUtils.repeat("_",half_length);
    TextComponent componentLeft = ComponentCreator.ColoredText(left, ChatColor.GOLD);
    TextComponent componentRight = ComponentCreator.ColoredText(right,ChatColor.GOLD);
    TextComponent header = new TextComponent("");
    header.addExtra(componentLeft);
    header.addExtra(title);
    header.addExtra(componentRight);
    header.addExtra("\n");
    return header;
  }

}

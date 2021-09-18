package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.ReadPlot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

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

  public MapCreator(Gamer gamer, int xin, int zin, int size) {
    this.gamer = gamer;
    this.facing = gamer.getPlayer().getFacing();
    this.x = xin;
    this.z = zin;
    this.WIDTH = size;
    this.SIZE_OF_SQUARE = Math.max(this.WIDTH, this.HEIGHT);
    this.mapArray = new TextComponent[SIZE_OF_SQUARE][SIZE_OF_SQUARE];

    unclaimedKey = new TextComponent("-");
    unclaimedKey.setColor(ChatColor.GRAY);
    claimedKey = new TextComponent("+");
    claimedKey.setColor(ChatColor.LIGHT_PURPLE);
    ownedKey = new TextComponent("+");
    ownedKey.setColor(ChatColor.GREEN);
    playerKey = new TextComponent("+");
    playerKey.setColor(ChatColor.DARK_RED);
    selfKey = new TextComponent("^");
    selfKey.setColor(ChatColor.YELLOW);
    friendKey = new TextComponent("+");
    friendKey.setColor(ChatColor.DARK_GREEN);
    ComponentBuilder builder = new ComponentBuilder();
  }

  public MapCreator(Gamer gamer, int xin, int zin) {
    this.gamer = gamer;
    this.facing = gamer.getPlayer().getFacing();
    this.x = xin;
    this.z = zin;
    this.WIDTH = 25;
    this.SIZE_OF_SQUARE = Math.max(this.WIDTH, this.HEIGHT);
    this.mapArray = new TextComponent[SIZE_OF_SQUARE][SIZE_OF_SQUARE];

    unclaimedKey = new TextComponent("-");
    unclaimedKey.setColor(ChatColor.GRAY);
    claimedKey = new TextComponent("+");
    claimedKey.setColor(ChatColor.LIGHT_PURPLE);
    ownedKey = new TextComponent("+");
    ownedKey.setColor(ChatColor.GREEN);
    playerKey = new TextComponent("+");
    playerKey.setColor(ChatColor.DARK_RED);
    selfKey = new TextComponent("^");
    selfKey.setColor(ChatColor.YELLOW);
    friendKey = new TextComponent("+");
    friendKey.setColor(ChatColor.DARK_GREEN);
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

  public BaseComponent[] mapMenu() {
    Player player = this.gamer.getPlayer();
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

        ReadPlot plot = state().getPlot(x,z);
        if(plot.getIdInt() != null){
          District district = state().getDistrict(x, z);
          if(district != null){
            claimedHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+x + ", " + z+")"));
            claimedClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/district info " + district.getIdInt()));
            claimedflag = true;
            if (district.isOwner(gamer)) {
              ownedflag = true;
            }
          }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
          Chunk pc = p.getChunk();
          if (pc.getZ() == z && pc.getX() == x) {
            if (p.equals(player)) {
              selfFlag = true;
            } else if (this.gamer.hasFriend(p.getUniqueId())) {
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
      z = z - SIZE_OF_SQUARE;
    }

    this.mapArray = rotateMap(rotateMap(this.mapArray));
    //flipHorizontalInPlace(this.mapArray);
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
    compassComps[7] = blank;
    compassComps[8] = blank;

    return compassComps;
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

  private TextComponent[][] rotateMap(TextComponent[][] flip) {
    TextComponent[][] newMapArray = new TextComponent[SIZE_OF_SQUARE][SIZE_OF_SQUARE];
    for (int i = 0; i < flip[0].length; i++) {
      for (int j = flip.length - 1; j >= 0; j--) {
        newMapArray[i][j] = flip[j][i];
      }
    }
    return newMapArray;
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

}

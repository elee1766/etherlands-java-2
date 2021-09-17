package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.ReadPlot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class MapCreator {
  private final Gamer gamer;
  private final int SIZE;
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

  public MapCreator(Gamer gamer, BlockFace facing, int xin, int zin) {
    this.gamer = gamer;
    this.facing = facing;
    this.x = xin;
    this.z = zin;
    this.SIZE = 11;
    this.mapArray = new TextComponent[SIZE][SIZE];

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

  public TextComponent combined(){
    TextComponent[] map = mapMenu();
    TextComponent[] key = key();
    TextComponent[] compass = compass();
    TextComponent output = new TextComponent("");

    for (int i = 0; i < this.SIZE-2; i++) {
      output.addExtra(compass[i]);
      output.addExtra("|");
      output.addExtra(map[i]);
      output.addExtra("|");
      output.addExtra(key[i]);
      if (i != SIZE - 1) {
        output.addExtra("\n");
      }
    }
    return output;
  }

  public TextComponent[] mapMenu() {
    Player player = this.gamer.getPlayer();
    int x = this.x - SIZE / 2 - 1;
    int z = this.z - SIZE / 2 - 1;
    for (int i = 0; i < SIZE; i++) {
      x++;
      for (int j = 0; j < SIZE; j++) {
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

        District district = state().getDistrict(x, z);
        if(district != null){
          claimedHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+x + ", " + z+")"));
          claimedClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/district info " + district.getIdInt()));
          claimedflag = true;
          if (district.isOwner(gamer)) {
            ownedflag = true;
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
      z = z - SIZE;
    }

    switch (this.facing) {
      case NORTH:
        this.mapArray = rotateMap(this.mapArray);
        flipHorizontalInPlace(this.mapArray);
        this.mapArray = rotateMap(rotateMap(rotateMap(this.mapArray)));
      case WEST:
        this.mapArray = rotateMap(this.mapArray);
        flipHorizontalInPlace(this.mapArray);
        this.mapArray = rotateMap(this.mapArray);
      case SOUTH:
        this.mapArray = rotateMap(this.mapArray);
        flipHorizontalInPlace(this.mapArray);
        this.mapArray = rotateMap(rotateMap(this.mapArray));
      case EAST:
        this.mapArray = rotateMap(this.mapArray);
        flipHorizontalInPlace(this.mapArray);
    }
    Bukkit.getLogger().info(this.mapArray.toString());
    TextComponent[] returnValue = new TextComponent[SIZE-2];
    for(int i = 1; i< SIZE-1; i++){
      TextComponent line = new TextComponent("");
      TextComponent[] comp = this.mapArray[i];
      for(TextComponent block : comp){
        line.addExtra(block);
      }
      returnValue[i-1]=line;
    }
    return returnValue;
  }

  private TextComponent[] key() {
    TextComponent[] compassComps = new TextComponent[SIZE];

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

  private TextComponent[] compass() {
    TextComponent[] compassComps = new TextComponent[SIZE];
    ClickEvent gosouth = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "S" + " " + x + " " + (z+2));
    ClickEvent gonorth = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "N" + " " + x + " " + (z-2));
    ClickEvent gowest = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "W" + " " + (x-2) + " " + z);
    ClickEvent goeast = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/map coord " + "E" + " " + (x+2) + " " + z);
    ClickEvent goleft;
    ClickEvent goright;
    ClickEvent goup;
    ClickEvent godown;
    String left = "";
    String right = "";
    String down = "";
    if (facing.equals(BlockFace.NORTH)) {
      goup = gonorth;
      left = "W";
      goleft = gowest;
      right = "E";
      goright = goeast;
      down = "S";
      godown = gosouth;
    } else if (facing.equals(BlockFace.SOUTH)) {
      goup = gosouth;
      left = "E";
      goleft = goeast;
      right = "W";
      goright = gowest;
      down = "N";
      godown = gonorth;
    } else if (facing.equals(BlockFace.WEST)) {
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
    for(int i = 0; i< SIZE; i++) {
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
              spot.addExtra(facing.toString().substring(0,1));
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

  private TextComponent[][] rotateMap(TextComponent[][] flip) {
    TextComponent[][] newMapArray = new TextComponent[SIZE][SIZE];
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

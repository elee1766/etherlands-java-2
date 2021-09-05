package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.ArrayList;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class FlagMenu extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public FlagMenu(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
  }

  public static void clickMenu(
      Gamer gamer, String flagType, String command, District writeDistrict, Player player) {
    clickMenu(gamer, flagType, command, writeDistrict, player.getName());
  }

  public static void clickMenu(
      Gamer gamer, String flagType, String command, District writeDistrict, Group writeGroup) {
    clickMenu(gamer, flagType, command, writeDistrict, writeGroup.getName());
  }

  public static void clickMenu(
      Gamer gamer, String flagType, String command, District writeDistrict, String item) {

    Player player = gamer.getPlayer();
    ArrayList<TextComponent> tc = new ArrayList<TextComponent>();

    TextComponent component = new TextComponent("");
    TextComponent space = new TextComponent(" ");
    TextComponent topBorder = new TextComponent("============== FLAGS ==============\n");
    TextComponent next = new TextComponent("next");
    topBorder.setColor(ChatColor.YELLOW);

    tc.add(topBorder);

    for (AccessFlags f : AccessFlags.values()) {
      String sep = "";
      if (f.toString() == "NONE") {
        continue;
      }
      for (int i = 0; i < 30 - String.valueOf(f).length(); i++) {
        sep = sep + "-";
      }
      String currentFlag = String.valueOf(f);
      TextComponent ff = new TextComponent(currentFlag + " " + sep + " ");
      ff.setColor(ChatColor.YELLOW);
      tc.add(ff);
      for (FlagValue fv : FlagValue.values()) {
        TextComponent value = new TextComponent(String.valueOf(fv));
        if (fv.toString() == "NONE") { // if flagvalue is set for the given accessflag
          continue;
        } else if (flagType == "player") {
          Gamer g1 = state().getGamer(Bukkit.getPlayer(item).getUniqueId());
          if (writeDistrict.checkFlags(f, g1) == fv) {
            value.setColor(ChatColor.YELLOW);
          } else {
            value.setColor(ChatColor.DARK_GRAY);
          }
        } else if (flagType == "group") {
          Group g1 = gamer.getTeamObject().getGroup(item);
          if (writeDistrict.checkFlags(f, g1) == fv) {
            value.setColor(ChatColor.YELLOW);
          } else {
            value.setColor(ChatColor.DARK_GRAY);
          }
        }
        value.setUnderlined(true);
        value.setClickEvent(
            new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/"
                    + command
                    + " "
                    + writeDistrict.getIdInt().toString()
                    + " "
                    + item
                    + " "
                    + currentFlag
                    + " "
                    + fv));
        tc.add(value);
        tc.add(space);
      }
      TextComponent newLine = new TextComponent("\n");
      tc.add(newLine);
    }

    for (TextComponent comps : tc) {
      component.addExtra(comps);
    }
    player.sendMessage(component);
  }

  public static void helper(String command) {}
}

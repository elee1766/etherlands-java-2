package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Objects;
import java.util.Set;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class MessageFormatter extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final TextComponent message;

  public MessageFormatter(TextComponent message, Fiber fiber, Channels channels) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.message = message;
  }

  private String abbreviate(String value, int totlen) {
    return value.substring(0, totlen / 2 - 2) + ".." + value.substring(value.length() - totlen / 4);
  }

  public void addBar(String bar, String title) {
    int rep = (40 - (title.length() + 2)) / 2;
    TextComponent titleBar =
        new TextComponent(
            StringUtils.repeat(bar, rep) + title + StringUtils.repeat(bar, rep) + "\n");
    titleBar.setColor(ChatColor.GRAY);
    message.addExtra(titleBar);
  }

  public void addDistricts(String name, Set<Integer> districts) {
    TextComponent valuecomp = new TextComponent("");
    for (Integer districtID : districts) {
      TextComponent dComp = new TextComponent(districtID.toString());
      dComp.setColor(ChatColor.DARK_AQUA);
      dComp.setClickEvent(
          new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/district " + districtID));
      valuecomp.addExtra(dComp);
      valuecomp.addExtra(" ");
    }
    TextComponent namecomp = new TextComponent(name);
    valuecomp.setHoverEvent(
        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to view more info")));
    namecomp.setColor(ChatColor.AQUA);
    namecomp.addExtra(": ");
    valuecomp.addExtra("\n");

    message.addExtra(namecomp);
    message.addExtra(valuecomp);
  }

  public void addField(String name, String value) {
    TextComponent valuecomp;
    if (Objects.equals(value, "null") || value == null) {
      valuecomp = new TextComponent("none");
      valuecomp.setColor(ChatColor.GRAY);
    } else if (name.toLowerCase().contains("address") || (name.toLowerCase().contains("uuid"))) {
      valuecomp = new TextComponent(abbreviate(value, 10));
      valuecomp.setColor(ChatColor.GRAY);
    } else if (name.toLowerCase().contains("team") || (name.toLowerCase().contains("members"))) {
      valuecomp = new TextComponent(value);
      valuecomp.setColor(ChatColor.GRAY);
    } else {
      valuecomp = new TextComponent(value);
      valuecomp.setColor(ChatColor.DARK_AQUA);
    }
    TextComponent namecomp = new TextComponent(name);

    if (name.toLowerCase().contains("address")) {
      String url = ("https://polygonscan.com/address/" + value);
      System.out.println("doing address");
      valuecomp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    } else if (name.toLowerCase().contains("town")) {
      valuecomp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/town " + value));
    } else if (name.equalsIgnoreCase("owner")) {
      valuecomp.setClickEvent(
          new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gamer " + value));
    } else {
      valuecomp.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value));
    }
    valuecomp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(value)));
    namecomp.setColor(ChatColor.AQUA);
    namecomp.addExtra(": ");
    valuecomp.addExtra("\n");

    message.addExtra(namecomp);
    message.addExtra(valuecomp);
  }

  public void addFriend(String value, String addr) {
    TextComponent addrcomp;
    TextComponent namecomp = new TextComponent("~ " + value);
    if (addr == null || addr == "") {
      addrcomp = new TextComponent("null");
    } else {
      addrcomp = new TextComponent(abbreviate(addr, 10));
    }

    TextComponent invite = new TextComponent("invite");
    TextComponent remove = new TextComponent("remove");

    invite.setHoverEvent(
        new HoverEvent(
            HoverEvent.Action.SHOW_TEXT, new Text("Click to invite them to your town!")));
    invite.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/town invite " + value));
    remove.setHoverEvent(
        new HoverEvent(
            HoverEvent.Action.SHOW_TEXT, new Text("Click to remove them from your friends list.")));
    remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend remove " + value));

    invite.setColor(ChatColor.GRAY);
    remove.setColor(ChatColor.GRAY);

    String url = ("https://etherscan.io/address/" + addr);
    addrcomp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

    addrcomp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(addr)));
    namecomp.setColor(ChatColor.AQUA);
    addrcomp.setColor(ChatColor.DARK_AQUA);

    message.addExtra(namecomp);
    message.addExtra(" [");
    message.addExtra(addrcomp);
    message.addExtra("] ");
    message.addExtra(invite);
    message.addExtra(" ");
    message.addExtra(remove);
    message.addExtra("\n");
  }

  public void addLine() {
    message.addExtra("\n");
  }

  public void plotIds(String title, TextComponent ids) {
    TextComponent titlecomp = new TextComponent(title);
    titlecomp.addExtra(": ");
    titlecomp.setColor(ChatColor.AQUA);
    ids.setColor(ChatColor.DARK_AQUA);
    message.addExtra(titlecomp);
    message.addExtra(ids);
    addLine();
  }

  public void printOut(Player player) {
    Gamer gamer = (Gamer) state().getGamer(player.getUniqueId());
    channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, message));
  }
}

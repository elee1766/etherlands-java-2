package etherlandscore.etherlandscore.Menus;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Locale;

public class MessageFormatter {
  private final TextComponent message;

  public MessageFormatter(TextComponent message) {
    this.message = message;
  }

  private String abbreviate(String value) {
    return value.substring(0, 6) + "..." + value.substring(value.length() - 3);
  }

  public void addBar(String bar, String title) {
    int rep = (40 - (title.length() + 2)) / 2;
    TextComponent titleBar =
        new TextComponent(
            StringUtils.repeat(bar, rep) + title + StringUtils.repeat(bar, rep) + "\n");
    titleBar.setColor(ChatColor.AQUA);
    message.addExtra(titleBar);
  }

  public void addLine() {
    message.addExtra("\n");
  }
  public void addFriendName(String name, String value) {
    TextComponent valuecomp;
    if (value.length() > 24) {
      valuecomp = new TextComponent(abbreviate(value));
    }else{
      valuecomp = new TextComponent(value);
    }
    TextComponent namecomp = new TextComponent(name);
    TextComponent invite = new TextComponent("invite");
    TextComponent remove = new TextComponent("remove");

    invite.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Click to invite them to your team!")));
    invite.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/invite "+value));
    remove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("Click to remove them from your friends list.")));
    remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend remove "+value));

    invite.setColor(ChatColor.GRAY);
    remove.setColor(ChatColor.GRAY);

    valuecomp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text(value)));
    namecomp.setColor(ChatColor.AQUA);
    namecomp.addExtra(": ");
    valuecomp.setColor(ChatColor.DARK_AQUA);

    message.addExtra(namecomp);
    message.addExtra(valuecomp);
    message.addExtra(" ");
    message.addExtra(invite);
    message.addExtra(" ");
    message.addExtra(remove);
    message.addExtra("\n");
  }
  public void addField(String name, String value) {
    TextComponent valuecomp;
    if (value.length() > 24) {
      valuecomp = new TextComponent(abbreviate(value));
    }else{
      valuecomp = new TextComponent(value);
    }
    TextComponent namecomp = new TextComponent(name);

    if(name.toLowerCase().contains("address")){
      String url = ("https://etherscan.io/address/"+value);
      System.out.println("doing address");
      valuecomp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,url));
    }else{
      valuecomp.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,value));
    }
    valuecomp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text(value)));
    namecomp.setColor(ChatColor.AQUA);
    namecomp.addExtra(": ");
    valuecomp.setColor(ChatColor.DARK_AQUA);
    valuecomp.addExtra("\n");

    message.addExtra(namecomp);
    message.addExtra(valuecomp);
  }

  public void printOut(Player player) {
    player.sendMessage(message);
  }
}

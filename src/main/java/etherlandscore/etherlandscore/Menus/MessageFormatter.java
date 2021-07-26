package etherlandscore.etherlandscore.Menus;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

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

  public void addField(String name, String value) {
    if (value.length() > 24) {
      value = abbreviate(value);
    }
    TextComponent namecomp = new TextComponent(name);
    TextComponent valuecomp = new TextComponent(value);
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

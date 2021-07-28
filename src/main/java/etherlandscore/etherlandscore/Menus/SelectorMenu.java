package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.read.Gamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class SelectorMenu extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final LocaleStrings locales = new LocaleStrings();

  public SelectorMenu(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
  }

  public static void menu(Gamer gamer, String[] args, String command) {
    Player player = gamer.getPlayer();

    TextComponent component = new TextComponent("");
    for (String arg : args) {
      TextComponent argument = new TextComponent(arg);
      argument.setUnderlined(true);
      argument.setColor(ChatColor.BLUE);
      System.out.println("/" + command + argument);
      argument.setClickEvent(
          new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command + " " + argument.getText()));
      component.addExtra(argument);
      component.addExtra(" ");
    }
    player.sendMessage(component);
  }

  public static void selectionMenu(String sels, Gamer gamer) {}
}

package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class Prettifier extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public Prettifier(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
  }

  public static void prettyPrint(Player sender, String title, TextComponent textcomp){
    TextComponent titlebar = new TextComponent("");
    TextComponent allcomp = new TextComponent("");
    titlebar.addExtra("============== " + title + " ==============");
    allcomp.addExtra(titlebar);
    allcomp.addExtra(textcomp);
    sender.sendMessage(allcomp);
  }

}

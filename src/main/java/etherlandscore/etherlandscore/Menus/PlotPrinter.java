package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.services.MasterService;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;
import org.jetlang.fibers.Fiber;

import java.lang.reflect.Field;

public class PlotPrinter {
  private final Plot plot;

  public PlotPrinter(Plot plot) {
    super();
    this.plot = plot;
  }

  public void printPlot(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=", "PlotInfo");

    Field[] fields = plot.getDeclaredFields();
    for (Field field : fields) {
      try {
        prettyPrint.addField(field.getName(), String.valueOf(field.get(this.plot)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }

}

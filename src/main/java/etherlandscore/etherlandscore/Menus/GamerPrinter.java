package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.Gamer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class GamerPrinter {
  private final Gamer gamer;

  public GamerPrinter(Gamer gamer) {
    super();
    this.gamer = gamer;
  }

  public void printGamer(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=", "GamerInfo");

    Field[] fields = gamer.getDeclaredFields();
    for (Field field : fields) {
      try {
        prettyPrint.addField(field.getName(), String.valueOf(field.get(this.gamer)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }
}

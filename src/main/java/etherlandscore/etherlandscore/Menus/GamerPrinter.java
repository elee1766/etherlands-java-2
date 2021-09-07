package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.Gamer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

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
        if(field.getName()=="friends") {
          String ds = "";
          Set<UUID> friends = (Set<UUID>) field.get(this.gamer);
          for (UUID friend : friends) {
            String memName = Bukkit.getOfflinePlayer(friend).getName();
            ds = memName + " ";
          }
          prettyPrint.addField(field.getName(), ds);
        }else if (field.getName() != "_id") {
          prettyPrint.addField(field.getName(), String.valueOf(field.get(this.gamer)));
        }
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }
}

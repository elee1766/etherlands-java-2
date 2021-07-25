package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GamerPrinter {
  private final Gamer gamer;

  public GamerPrinter(Gamer gamer) {
    super();
    this.gamer = gamer;
  }

  public void printGamer(Player sender){
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=","GamerInfo");

    Field[] fields = gamer.getDeclaredFields();
    for(Field field : fields) {
      try {
        prettyPrint.addField(field.getName(), String.valueOf(field.get(this.gamer)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }

}

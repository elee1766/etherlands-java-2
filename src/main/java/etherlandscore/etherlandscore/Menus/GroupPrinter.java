package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.Group;
import etherlandscore.etherlandscore.state.Team;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class GroupPrinter {
  private final Group group;

  public GroupPrinter(Group group) {
    super();
    this.group = group;
  }

  public void printGroup(Player sender){
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=","GroupInfo");

    Field[] fields = group.getDeclaredFields();
    for(Field field : fields) {
      try {
        prettyPrint.addField(field.getName(), String.valueOf(field.get(this.group)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }

}

package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.Group;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class GroupPrinter {
  private final Group writeGroup;

  public GroupPrinter(Group writeGroup) {
    super();
    this.writeGroup = writeGroup;
  }

  public void printGroup(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=", "GroupInfo");

    Field[] fields = writeGroup.getDeclaredFields();
    for (Field field : fields) {
      try {
        prettyPrint.addField(field.getName(), String.valueOf(field.get(this.writeGroup)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }
}

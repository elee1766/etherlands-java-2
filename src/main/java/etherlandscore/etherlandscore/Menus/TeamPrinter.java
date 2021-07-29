package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.Team;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TeamPrinter {
  private final Team writeTeam;

  public TeamPrinter(Team writeTeam) {
    super();
    this.writeTeam = writeTeam;
  }

  public void printTeam(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=", "TeamInfo");
    if(this.writeTeam == null){
      return;
    }
    Field[] fields = writeTeam.getDeclaredFields();
    for (Field field : fields) {
      try {
        prettyPrint.addField(field.getName(), String.valueOf(field.get(this.writeTeam)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }
}

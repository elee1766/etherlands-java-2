package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Team;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TeamPrinter {
  private final Team team;

  public TeamPrinter(Team team) {
    super();
    this.team = team;
  }

  public void printTeam(Player sender){
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=","TeamInfo");

    Field[] fields = team.getDeclaredFields();
    for(Field field : fields) {
      try {
        prettyPrint.addField(field.getName(), String.valueOf(field.get(this.team)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }

}

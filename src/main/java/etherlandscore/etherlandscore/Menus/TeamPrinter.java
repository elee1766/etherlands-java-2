package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.write.WriteDistrict;
import etherlandscore.etherlandscore.state.write.WriteGroup;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

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
        if (field.getName() == "districts") {
          String ds = "";
          Map<String, WriteDistrict> districts = (Map<String, WriteDistrict>) field.get(this.writeTeam);
          for (Map.Entry d : districts.entrySet()) {
            ds = ds + d.getKey() + ", ";
          }
          prettyPrint.addField(field.getName(), ds);
        }else if(field.getName()=="groups") {
          String ds = "";
          Map<String, WriteGroup> groups = (Map<String, WriteGroup>) field.get(this.writeTeam);
          for (Map.Entry g : groups.entrySet()) {
            ds = ds + g.getKey() + ", ";
          }
          prettyPrint.addField(field.getName(), ds);
        }else {
          prettyPrint.addField(field.getName(), String.valueOf(field.get(this.writeTeam)));
        }
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }
}

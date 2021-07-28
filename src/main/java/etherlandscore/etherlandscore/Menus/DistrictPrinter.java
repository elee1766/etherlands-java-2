package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.District;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;

public class DistrictPrinter {
  private final Map<String, District> districts;

  public DistrictPrinter(Map<String, District> districts) {
    super();
    this.districts = districts;
  }

  public void printDistrict(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=", "DistrictInfo");
    for (Map.Entry<String, District> district : districts.entrySet()) {
      Field[] fields = district.getValue().getDeclaredFields();
      for (Field field : fields) {
        try {
          prettyPrint.addField(field.getName(), String.valueOf(field.get(this.districts)));
        } catch (IllegalAccessException ex) {
          System.out.println(ex);
        }
      }
    }
    prettyPrint.printOut(sender);
  }
}

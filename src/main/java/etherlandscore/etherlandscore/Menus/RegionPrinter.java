package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.Group;
import etherlandscore.etherlandscore.state.Region;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;

public class RegionPrinter {
  private final Map<String, Region> regions;

  public RegionPrinter(Map<String, Region> regions) {
    super();
    this.regions = regions;
  }

  public void printRegion(Player sender){
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=","RegionInfo");
    for(Map.Entry<String, Region> region : regions.entrySet()){
      Field[] fields = region.getValue().getDeclaredFields();
      for(Field field : fields) {
        try {
          prettyPrint.addField(field.getName(), String.valueOf(field.get(this.regions)));
        } catch (IllegalAccessException ex) {
          System.out.println(ex);
        }
      }
    }
    prettyPrint.printOut(sender);
  }

}

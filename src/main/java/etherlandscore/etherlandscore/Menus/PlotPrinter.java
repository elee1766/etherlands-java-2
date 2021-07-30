package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.write.WriteDistrict;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PlotPrinter {
  private final Plot writePlot;

  public PlotPrinter(Plot writePlot) {
    super();
    this.writePlot = writePlot;
  }

  public void printPlot(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=", "PlotInfo");

    Field[] fields = writePlot.getDeclaredFields();
    for (Field field : fields) {
      try {
        if (field.getName() == "districts") {
          String ds = "";
          Set<String> districts = (Set<String>) field.get(this.writePlot);
          if(districts.size()==0){
            ds = "none";
          }else {
            for (String d : districts) {
              ds = ds + d + " ";
            }
          }
          prettyPrint.addField(field.getName(), ds);
        }else if (field.getName() != "chunk" && field.getName() != "_id") {
          prettyPrint.addField(field.getName(), String.valueOf(field.get(this.writePlot)));
        }

      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }
}

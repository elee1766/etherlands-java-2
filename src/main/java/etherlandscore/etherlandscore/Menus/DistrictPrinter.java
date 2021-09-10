package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.util.Map2;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DistrictPrinter {
  private final District writeDistrict;

  public DistrictPrinter(District writeDistrict) {
    super();
    this.writeDistrict = writeDistrict;
  }

  public void printDistrict(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=", "District: " + this.writeDistrict.getIdInt());

    Field[] fields = writeDistrict.getDeclaredFields();
    for (Field field : fields) {
      try {
        if (field.getName().equals("groupPermissionMap") || field.getName().equals("gamerPermissionMap")) {
          prettyPrint.addField(field.getName(), mapHelper(field.getName()));
        }else if (field.getName().equals("plotIds")) {
          prettyPrint.plotIds(field.getName(), plotHelper());
        }else if (!field.getName().equals("chunk") && !field.getName().equals("_id")) {
          prettyPrint.addField(field.getName(), String.valueOf(field.get(this.writeDistrict)));
        }

      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }

  private TextComponent plotHelper(){
    TextComponent combined = new TextComponent();
    Set<Plot> plots = this.writeDistrict.getPlotObjects();
    for(Plot plot : plots){
      TextComponent pcomp = new TextComponent(plot.getIdInt().toString());
      pcomp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+plot.getX() + ", " + plot.getZ()+")")));
      combined.addExtra(pcomp);
      combined.addExtra(" ");
    }
    return combined;
  }

  private String mapHelper(String fieldName){
    StringBuilder result = new StringBuilder();
    if(fieldName.equals("groupPermissionMap")){
      Map2<String, AccessFlags, FlagValue> gpMap = this.writeDistrict.getGroupPermissionMap();
      for (Map.Entry<String, Map<AccessFlags, FlagValue>> entry : gpMap.getMap().entrySet())
      {
        result.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
      }
    }else{
      Map2<UUID, AccessFlags, FlagValue> pMap = this.writeDistrict.getGamerPermissionMap();
      for (Map.Entry<UUID, Map<AccessFlags, FlagValue>> entry : pMap.getMap().entrySet())
      {
        result.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
      }
    }
    return result.toString();
  }
}

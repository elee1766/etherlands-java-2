package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.util.Map2;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DistrictPrinter extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final District writeDistrict;

  public DistrictPrinter(District writeDistrict, Fiber fiber, Channels channels) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.writeDistrict = writeDistrict;
  }

  public void printDistrict(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print, fiber, channels);
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
    Set<String> plotIDs = RedisGetter.getPlotsinDistrict(writeDistrict.getIdInt().toString());
    for(String plot : plotIDs){
      TextComponent pcomp = new TextComponent(plot);
      pcomp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+RedisGetter.getPlotX(plot) + ", " + RedisGetter.getPlotZ(plot)+")")));
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

package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.util.Map2;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DistrictPrinter extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final District district;

  public DistrictPrinter(District district, Fiber fiber, Channels channels) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.district = district;
  }

  public void printDistrict(Player sender) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print, fiber, channels);
    prettyPrint.addBar("=", "District: " + this.district.getIdInt());
    prettyPrint.addField("owner", this.district.getOwnerAddress());
    prettyPrint.printOut(sender);
  }

  private TextComponent plotHelper(){
    TextComponent combined = new TextComponent();
    Set<String> plotIDs = RedisGetter.GetPlotsInDistrict(district.getIdInt().toString());
    for(String plot : plotIDs){
      TextComponent pcomp = new TextComponent(plot);
      pcomp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+RedisGetter.GetPlotX(plot) + ", " + RedisGetter.GetPlotZ(plot)+")")));
      combined.addExtra(pcomp);
      combined.addExtra(" ");
    }
    return combined;
  }

  private String mapHelper(String fieldName){
    StringBuilder result = new StringBuilder();
    if(fieldName.equals("groupPermissionMap")){
      Map2<String, AccessFlags, FlagValue> gpMap = this.district.getGroupPermissionMap();
      for (Map.Entry<String, Map<AccessFlags, FlagValue>> entry : gpMap.getMap().entrySet())
      {
        result.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
      }
    }else{
      Map2<UUID, AccessFlags, FlagValue> pMap = this.district.getGamerPermissionMap();
      for (Map.Entry<UUID, Map<AccessFlags, FlagValue>> entry : pMap.getMap().entrySet())
      {
        result.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
      }
    }
    return result.toString();
  }
}

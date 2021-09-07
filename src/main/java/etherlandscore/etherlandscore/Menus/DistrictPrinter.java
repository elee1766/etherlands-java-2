package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.write.WriteDistrict;
import etherlandscore.etherlandscore.util.Map2;
import jnr.constants.platform.Access;
import net.kyori.adventure.bossbar.BossBar;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;
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
        if (field.getName() == "groupPermissionMap" || field.getName() == "gamerPermissionMap") {
          prettyPrint.addField(field.getName(), mapHelper(field.getName()));
        }else if (field.getName() != "chunk" && field.getName() != "_id") {
          prettyPrint.addField(field.getName(), String.valueOf(field.get(this.writeDistrict)));
        }

      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }

  private String mapHelper(String fieldName){
    String result = "";
    if(fieldName.equals("groupPermissionMap")){
      Map2<String, AccessFlags, FlagValue> gpMap = this.writeDistrict.getGroupPermissionMap();
      for (Map.Entry<String, Map<AccessFlags, FlagValue>> entry : gpMap.getMap().entrySet())
      {
        result+=entry.getKey() + ": " + entry.getValue() + " ";
      }
    }else{
      Map2<UUID, AccessFlags, FlagValue> pMap = this.writeDistrict.getGamerPermissionMap();
      for (Map.Entry<UUID, Map<AccessFlags, FlagValue>> entry : pMap.getMap().entrySet())
      {
        result+=entry.getKey() + ": " + entry.getValue() + " ";
      }
    }
    return result;
  }
}

package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.write.WriteDistrict;
import etherlandscore.etherlandscore.state.write.WriteGroup;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static etherlandscore.etherlandscore.services.MasterService.state;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
          Set<Integer> districtIds = new HashSet<>();
          Map<Integer, WriteDistrict> districts = state().getDistricts();
          Set<WriteDistrict> matches = new HashSet<>();
          for(WriteDistrict d : districts.values()){
            if(d.hasTeam()){
              if(d.getTeamObject().getName()==this.writeTeam.getName())
                matches.add(d);
            }
          }
          for (WriteDistrict wd : matches) {
            Bukkit.getLogger().info(wd.getId());
            districtIds.add(wd.getIdInt());
          }
          prettyPrint.addDistricts(field.getName(), districtIds);
        }else if(field.getName()=="groups") {
          String ds = "";
          Map<String, WriteGroup> groups = (Map<String, WriteGroup>) field.get(this.writeTeam);
          for (Map.Entry g : groups.entrySet()) {
            ds = ds + g.getKey() + " ";
          }
          prettyPrint.addField(field.getName(), ds);
        }else if(field.getName()=="members") {
          String ds = "";
          Set<UUID> members = (Set<UUID>) field.get(this.writeTeam);
          for (UUID member : members) {
            String memName = Bukkit.getOfflinePlayer(member).getName();
            ds = ds + memName + " ";
          }
          prettyPrint.addField(field.getName(), ds);
        }else if(field.getName()=="owner") {
          String ds = "";
          UUID ownerUUID = (UUID) field.get(this.writeTeam);
          String ownName = Bukkit.getOfflinePlayer(ownerUUID).getName();
          prettyPrint.addField(field.getName(), ownName);
        }

        else if (field.getName() != "_id" && field.getName() != "plots") {
          prettyPrint.addField(field.getName(), String.valueOf(field.get(this.writeTeam)));
        }
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    prettyPrint.printOut(sender);
  }
}

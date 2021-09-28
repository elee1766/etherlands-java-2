package etherlandscore.etherlandscore.state.write;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.services.ImpatientAsker;
import etherlandscore.etherlandscore.singleton.Asker;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;

public class District {
  private String _id;
  public District(int id) {
    this._id = String.valueOf(id);
  }


  public boolean canGamerPerform(AccessFlags flag, Gamer gamer) {
    try {
      if (gamer.getPlayer().isOp()) {
        return true;
      }
      if (hasTown()) {
        Town town = getTownObject();
        if (town.isManager(gamer)) {
          return true;
        }
        Bukkit.getLogger().info(gamer.getUuid() + " " + gamer.getTown());
        if (gamer.hasTown()) {
          if (gamer.getTownObject().equals(town)) {
            FlagValue res = FlagValue.NONE;
            Set<String> teamNames = gamer.getTeams();
            Integer bestPriority = -100;
            for (String teamName : teamNames) {
              Team team = town.getTeam(teamName);
              Bukkit.getLogger().info(team.getName() + " " + res);
              if (team.getPriority() > bestPriority) {
                res = checkFlags(flag, town.getTeam(teamName), res);
                Bukkit.getLogger().info(team.getName() + "  " + flag + " " + res);
                bestPriority = team.getPriority();
              }
            }
            res = checkFlags(flag, gamer, res);
            return res == FlagValue.ALLOW;
          }
        }
        FlagValue res = checkFlags(flag, town.getTeam("outsider"), FlagValue.NONE);
        return res == FlagValue.ALLOW;

      } else {
        Gamer owner = this.getOwnerObject();
        if (owner == null) {
          return false;
        }
        if (owner.equals(gamer)) {
          return true;
        }
        return owner.getFriends().contains(gamer.getUuid());
      }
    } catch (Exception e) {
      Bukkit.getLogger().info(e + "\n" + e.getMessage());
      return false;
    }
  }


  public FlagValue checkFlags(AccessFlags flag, Gamer gamer, FlagValue def) {
    return FlagValue.NONE;
  }


  public FlagValue checkFlags(AccessFlags flag, Team writeTeam, FlagValue def) {
    return FlagValue.NONE;
  }


  public String getId() {
    return this.getIdInt().toString();
  }

  public Integer getIdInt() {
    return Integer.parseInt(this._id);
  }


  public String getNickname() {
    return Asker.GetNameOfDistrict(this.getIdInt());
  }


  public String getOwnerAddress() {
    return Asker.GetOwnerOfDistrict(this._id);
  }
  public Gamer getOwnerObject() {
    return new Gamer(Asker.GetAddressUUID(this.getOwnerAddress()));
  }

  public UUID getOwnerUUID() {
    return Asker.GetAddressUUID(this.getOwnerAddress());
  }

  public Set<Integer> getPlots() {
    return Asker.GetPlotsInDistrict(this._id);
  }

  public String getTown() {
    return ImpatientAsker.AskWorld(
        "district",
        this.getId(),
        "town"
    );
  }

  public Town getTownObject() {
    return new Town(getTown());
  }


  public boolean hasTown() {
    return getTown() != null;
  }


  public boolean isOwner(Gamer gamer) {
    return this.getOwnerAddress().equals(gamer.getAddress());
  }


  public String toString() {
    return this.getId();
  }

}

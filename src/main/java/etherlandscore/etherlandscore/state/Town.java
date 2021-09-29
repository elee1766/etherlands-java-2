package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.services.ImpatientAsker;
import kotlin.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Town  {
  private final String name;

  public Town(String name) {
    this.name = name;
  }


  public District getDistrict(Integer x) {
    return new District(x);
  }


  public Set<District> getDistrictObjects() {
    Set<District> output = new HashSet<>();
    for (Integer id : getDistricts()) {
      District district = new District(id);
      output.add(district);
    }
    return output;
  }

  public Pair<Map<AccessFlags, FlagValue>,Map<AccessFlags, FlagValue>> getPermissions(String team, Integer district){
    return ImpatientAsker.AskWorldPermissionMaps(
        "flags",
        "team",
        this.getName(),
        district.toString(),
        team
    );
  }

  public Set<Integer> getDistricts() {
    return ImpatientAsker.AskWorldIntegerSet(3,"town",this.getName(),"districts");
  }

  public String getId() {
    return this.name;
  }

  public Set<Gamer> getMembers() {
    return ImpatientAsker.AskWorldGamerSet(3,"town",this.getName(),"members");
  }

  public String getName() {
    return name;
  }

  public UUID getOwner() {
    return ImpatientAsker.AskWorldUUID(3,"town", this.getName(), "owner");
  }

  public boolean exists(){
    return getOwner() != null;
  }

  public Team getTeam(String name) {
    return new Team(this.getName(), name);
  }

  public Map<String, Team> getTeams() {
    return ImpatientAsker.GetWorldTeams(3,this.getName());
  }

  public int hashCode() {
    return getName().hashCode();
  }

  public String toString() {
    return this.getName();
  }
}

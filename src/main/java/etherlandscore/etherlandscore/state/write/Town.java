package etherlandscore.etherlandscore.state.write;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.services.ImpatientAsker;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Town  {
  private final String name;
  private Set<Integer> districts;

  private String _id;

  public Town(String name) {
    this.name = name;
  }

  public Town(Gamer gamer, String name) {
    this._id = name;
    this.name = name;
    this.districts = new HashSet<>();
  }

  public void addDistrict(District district) {
    this.districts.add(district.getIdInt());
  }

  public void addMember(Gamer gamer) {
  }

  public boolean canAction(Gamer actor, Gamer receiver) {
    if (isManager(actor) && !isManager(receiver)) {
      return true;
    } else return isOwner(actor);
  }

  public boolean canJoin(Map<UUID, Long> invites, Gamer joiner) {
    Long invite = invites.get(joiner.getUuid());
    if (invite != null) {
      Bukkit.getLogger().info(invite.toString());
      return invite > Instant.now().getEpochSecond();
    }
    return false;
  }

  public void createTeam(String name) {}

  public void deleteDistrict(Integer id) {
    this.districts.remove(id);
  }

  public void deleteTeam(String name) {}


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Town town = (Town) o;
    return getName().equals(town.getName());
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

  public Map<AccessFlags, FlagValue> getPermissions(String team, Integer district){
    return ImpatientAsker.AskWorldPermissionMap(
        "flags",
        "team",
        this.getName(),
        district.toString(),
        team
    );
  }
  public Map<AccessFlags, FlagValue> getPermissions(UUID uuid, Integer district){
    return ImpatientAsker.AskWorldPermissionMap(
        "flags",
        "gamer",
        this.getName(),
        district.toString(),
        uuid.toString()
    );
  }

  public Set<Integer> getDistricts() {
    return ImpatientAsker.AskWorldIntegerSet("town",this.getName(),"districts");
  }

  public String getId() {
    return this.name;
  }

  public void setId(String string) {
    this._id = string;
  }

  public Set<Gamer> getMembers() {
    return ImpatientAsker.AskWorldGamerSet("town",this.getName(),"members");
  }

  public String getName() {
    return name;
  }

  public UUID getOwner() {
    return ImpatientAsker.AskWorldUUID("town", this.getName(), "owner");
  }

  public boolean exists(){
    return getOwner() != null;
  }

  public Team getTeam(String name) {
    return new Team(this.getName(), name);
  }


  public Map<String, Team> getTeams() {
    return ImpatientAsker.AskWorldTeams(this.getName());
  }

  public void setTeams(Map<String, Team> teams) {}


  public int hashCode() {
    return getName().hashCode();
  }


  public boolean isManager(Gamer manager) {
    if (manager.getUuid().equals(this.getOwner())) {
      return true;
    }
    return this.getTeam("manager").getMembers().contains(manager.getUuid());
  }


  public boolean isOwner(Gamer manager) {
    return manager.getUuid().equals(this.getOwner());
  }


  public String toString() {
    return this.getName();
  }
}

package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteTeam extends CouchDocument implements Team {
  private final String name;
  private Set<UUID> members;
  private Set<Integer> plots;
  private Set<Integer> districts;
  private Map<String, WriteGroup> groups;
  private UUID owner;

  @JsonProperty("_id")
  private String _id;

  @JsonCreator
  public WriteTeam(@JsonProperty("_id") String name){
    this.name = name;
  }

  public WriteTeam(Gamer gamer,  String name) {
    this._id = name;
    this.name = name;
    this.owner = gamer.getUuid();
    this.members = new HashSet<>();
    this.plots = new HashSet<>();
    this.districts = new HashSet<>();
    this.groups = new HashMap<>();
    this.groups.put("outsiders", new WriteGroup(this, "outsiders", -5, true));
    this.groups.put("member", new WriteGroup(this, "member", -1, true));
    this.groups.put("manager", new WriteGroup(this, "manager", 50, true));
  }

  public void addMember(Gamer gamer) {
    members.add(gamer.getUuid());
  }

  public void addDistrict(WriteDistrict district) {
    this.districts.add(district.getIdInt());
  }

  @Override
  public boolean canAction(Gamer actor, Gamer receiver) {
    if (isManager(actor) && !isManager(receiver)) {
      return true;
    } else return isOwner(actor);
  }

  @Override
  public boolean canInvite(Gamer inviter) {
    return inviter.getUuid().equals(this.owner);
  }

  public boolean canJoin(Map<UUID, Long> invites, Gamer joiner) {
    Long invite = invites.get(joiner.getUuid());
    if (invite != null) {
      Bukkit.getLogger().info(invite.toString());
      return invite > Instant.now().getEpochSecond();
    }
    return false;
  }

  public void createGroup(String name) {
    if (!this.groups.containsKey(name)) {
      this.groups.put(name, new WriteGroup(this, name, 1, false));
    }
  }

  public void deleteDistrict(Integer id) {
    this.districts.remove(id);
  }

  public void deleteGroup(String name) {
    if (groups.containsKey(name)) {
      if (!groups.get(name).isDefault()) {
        groups.remove(name);
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Team team = (Team) o;
    return getName().equals(team.getName());
  }

  @Override
  @JsonIgnore
  public Field[] getDeclaredFields() {
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field f : fields) {
      f.setAccessible(true);
    }
    return fields;
  }

  @Override
  @JsonIgnore
  public District getDistrict(Integer x) {
    return state().getDistrict(x);
  }


  @Override
  @JsonIgnore
  public Set<District> getDistrictObjects() {
    Set<District> output = new HashSet<>();
    for (Integer id : getDistricts()) {
      District district = state().getDistrict(id);
      if(district != null){
        output.add(district);
      }
    }
    return output;
  }

  @Override
  public Set<Integer> getDistricts() {
    return districts;
  }
  public void setDistricts(Set<Integer> districts) {
    this.districts = districts;
  }

  @Override
  public Group getGroup(String name) {
    return groups.get(name);
  }

  @Override
  public Map<String, Group> getGroups() {
    return (Map) groups;
  }

  public void setGroups(Map<String, WriteGroup> groups) {
    this.groups = groups;
  }

  @JsonProperty("_id")
  public String getId() {
    return this.name;
  }

  @JsonProperty("_id")
  public void setId(String string) {
    this._id = string;
  }

  @Override
  public Set<UUID> getMembers() {
    return members;
  }

  public void setMembers(Set<UUID> members) {
    this.members = members;
  }

  @Override
  public String getName() {
    return name;
  }

  public UUID getOwner() {
    return owner;
  }

  public void setOwner(UUID owner) {
    this.owner = owner;
  }

  @Override
  @JsonIgnore
  public String getOwnerServerName() {
    return Bukkit.getPlayer(this.owner).getName();
  }

  @Override
  @JsonIgnore
  public UUID getOwnerUUID() {
    return this.owner;
  }

  @Override
  public Set<Integer> getPlots() {
    return plots;
  }

  public void setPlots(Set<Integer> plots) {
    this.plots = plots;
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public void inviteGamer(Map<UUID, Long> invites, UUID arg) {
    invites.put(arg, (Instant.now().getEpochSecond()) + 5 * 60);
    Bukkit.getLogger().info(arg.toString() + " " + invites.get(arg).toString());
  }

  @Override
  public boolean isManager(Gamer manager) {
    if (manager.getUuid().equals(getOwnerUUID())) {
      return true;
    }
    return this.getGroup("manager").getMembers().contains(manager.getUuid());
  }

  @Override
  public boolean isMember(Gamer gamer) {
    if (isManager(gamer)) {
      return true;
    }
    return members.contains(gamer.getUuid());
  }

  @Override
  public boolean isOwner(Gamer manager) {
    return manager.getUuid().equals(getOwnerUUID());
  }

  public void removeMember(Gamer gamer) {
    members.remove(gamer.getUuid());
  }
}

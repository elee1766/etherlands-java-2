package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.read.Town;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteTeam implements Team {
  private final String name;
  private Set<UUID> members;

  public void setMembers(Set<UUID> members) {
    this.members = members;
  }

  @JsonProperty("default")
  private final boolean isDefault;

  private String town;
  private Integer priority;

  @JsonCreator
  public WriteTeam(@JsonProperty("town") String town, @JsonProperty("name") String name, @JsonProperty("priority") Integer priority, @JsonProperty("default") boolean isDefault) {
    this.name = name;
    this.town = town;
    this.members = new HashSet<>();
    this.priority = priority;
    this.isDefault = isDefault;
  }

  public WriteTeam(Town writeTown, String name, Integer priority, boolean isDefault) {
    this.name = name;
    this.town = writeTown.getName();
    this.priority = priority;
    this.isDefault = isDefault;
    this.members = new HashSet<>();
  }

  public void addMember(Gamer gamer) {
    this.members.add(gamer.getUuid());
  }

  @Override
  public int compareTo(@NotNull Team o) {
    return this.getPriority().compareTo(o.getPriority());
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
  public String toString(){
    return this.getName();
  }

  @Override
  @JsonProperty("default")
  public boolean getDefault() {
    return isDefault;
  }

  @Override
  public Set<UUID> getMembers() {
    if (isDefault()) {
      if (getName().equals("member")) {
        return this.getTownObject().getMembers();
      }
      if (getName().equals("outsiders")) {
        return new HashSet<>();
      }
    }
    return members;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer newPriority) {
    this.priority = newPriority;
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  @Override
  @JsonIgnore
  public Town getTownObject() {
    return state().getTown(this.town);
  }

  @Override
  public boolean hasMember(Player player) {
    return getMembers().contains(player.getUniqueId());
  }

  @Override
  public boolean isDefault() {
    return this.isDefault;
  }

  @Override
  public int memberCount() {
    return members.size();
  }

  public void removeMember(Gamer gamer) {
    if(isDefault()){
      if(getName().equals("member")){
        return;
      }
    }
    this.members.remove(gamer.getUuid());
  }

  @JsonIgnore
  public void setPrioritySafe(Integer newPriority) {
    if (isDefault) return;
    if (newPriority < 0) this.priority = 0;
    if (newPriority > 100) this.priority = 0;
  }
}

package etherlandscore.etherlandscore.state.write;

import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.StateHolder;
import etherlandscore.etherlandscore.state.read.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteGroup extends StateHolder implements Group {
  private final String name;
  private final String team;

  private final Set<UUID> members = new HashSet<>();
  private final boolean isDefault;
  private Integer priority;

  public WriteGroup(Team writeTeam, String name, Integer priority, boolean isDefault) {
    this.name = name;
    this.team = writeTeam.getName();
    this.priority = priority;
    this.isDefault = isDefault;
  }

  public void addMember(Gamer gamer) {
    this.members.add(gamer.getUuid());
  }

  @Override
  public int compareTo(@NotNull Group o) {
    return this.getPriority().compareTo(o.getPriority());
  }

  @Override
  public Field[] getDeclaredFields() {
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field f : fields) {
      f.setAccessible(true);
    }
    return fields;
  }

  @Override
  public boolean getDefault() {
    return isDefault;
  }

  @Override
  public Set<UUID> getMembers() {
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
    if (isDefault) return;
    if (newPriority < 0) this.priority = 0;
    if (newPriority > 100) this.priority = 0;
  }

  @Override
  public Team getTeamObject() {
    return state().getTeam(this.team);
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
    this.members.remove(gamer.getUuid());
  }
}

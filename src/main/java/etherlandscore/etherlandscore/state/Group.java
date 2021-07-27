package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Group extends StateHolder implements Comparable<Group> {
  private final String name;
  private final String team;

  private final Set<UUID> members = new HashSet<>();
  private final boolean isDefault;
  private Integer priority;

  public Group(Team team, String name, Integer priority, boolean isDefault) {
    this.name = name;
    this.team = team.getName();
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

  public Set<UUID> getMembers() {
    return members;
  }

  public String getName() {
    return name;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer newPriority) {
    if (isDefault) return;
    if (newPriority < 0) this.priority = 0;
    if (newPriority > 100) this.priority = 0;
  }

  public Team getTeamObject() {
    return state().getTeam(this.team);
  }

  public boolean isDefault() {
    return this.isDefault;
  }



  public void removeMember(Gamer gamer) {
    this.members.remove(gamer.getUuid());
  }
  public Field[] getDeclaredFields(){
    Field[] fields = this.getClass().getDeclaredFields();
    for(Field f : fields){
      f.setAccessible(true);
    }
    return fields;
  }

  public void setPriority(Channels channels, Integer priority) {
    if(isDefault) return;
    channels.master_command.publish(new Message<>(MasterCommand.group_set_priority,this, priority));
  }
}

package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Group extends StateHolder implements Comparable<Group>{
  private final String name;
  private final String team;

  private final Set<UUID> members = new HashSet<>();

  private Integer priority;

  private final boolean isDefault;

  public Group(Team team, String name, Integer priority, boolean isDefault) {

    this.name = name;
    this.team = team.getName();
    this.priority = priority;
    this.isDefault = isDefault;
  }

  public void addMember(Channels channels, Gamer gamer){
    channels.master_command.publish(new Message<>(MasterCommand.group_add_gamer,gamer));
  }

  public void removeMember(Channels channels, Gamer gamer){
    channels.master_command.publish(new Message<>(MasterCommand.group_remove_gamer,gamer));
  }

  public void addMember(Gamer gamer){
    this.members.add(gamer.getUuid());
  }

  public void removeMember(Gamer gamer){
    this.members.remove(gamer.getUuid());
  }

  public void setPriority(Channels channels, Integer priority){
    channels.master_command.publish(new Message<>(MasterCommand.group_set_priority,priority));
  }

  public Integer getPriority(){
    return priority;
  }

  public void setPriority(Integer newPriority){
    if(isDefault) return;
    if(newPriority < 0) this.priority = 0;
    if(newPriority > 100) this.priority = 0;
  }

  public boolean isDefault() {
    return this.isDefault;
  }

  public String getName() {
    return name;
  }

  public Team getTeamObject() {
    return state().getTeam(this.team);
  }

  @Override
  public int compareTo(@NotNull Group o) {
    return this.getPriority().compareTo(o.getPriority());
  }
}

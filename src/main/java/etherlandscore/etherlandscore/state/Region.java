package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.util.Map2;

import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Region extends StateHolder implements Comparable<Region> {
  private final Set<Integer> plotIds;
  private final String team;
  private final String name;
  private final boolean isDefault;
  private final Map2<String, AccessFlags, FlagValue> groupPermissionMap = new Map2<>();
  private final Map2<UUID, AccessFlags, FlagValue> gamerPermissionMap = new Map2<>();
  private Integer priority;

  Region(Team team, String name, Set<Integer> plotIds, Integer priority, boolean isDefault) {
    this.team = team.getName();
    this.plotIds = plotIds;
    this.name = name;
    this.isDefault = isDefault;
    this.priority = priority;
  }

  public void addPlot(Channels channels, Plot plot) {
    channels.master_command.publish(new Message<>(MasterCommand.region_add_plot, this, plot));
  }

  public void addPlot(Plot plot) {
    this.plotIds.add(plot.getId());
  }

  public FlagValue checkFlags(AccessFlags flag, Gamer gamer) {
    return gamerPermissionMap.getOrDefault(gamer.getUuid(), flag, FlagValue.NONE);
  }

  public FlagValue checkFlags(AccessFlags flag, Group group) {
    return groupPermissionMap.getOrDefault(group.getName(), flag, FlagValue.NONE);
  }

  public void clearGroupPermission(String name) {
    groupPermissionMap.clearGroup(name);
  }

  @Override
  public int compareTo(Region r) {
    return getPriority().compareTo(r.getPriority());
  }

  public String getName() {
    return name;
  }

  public Integer getPriority() {
    return this.priority;
  }

  public void setPriority(Integer newPriority) {
    if (isDefault) return;
    if (newPriority < 0) this.priority = 0;
    if (newPriority > 100) this.priority = 0;
  }

  public Team getTeam() {
    return state().getTeam(getName());
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void removePlot(Channels channels, Plot plot) {
    channels.master_command.publish(new Message<>(MasterCommand.region_remove_plot, this, plot));
  }

  public void removePlot(Plot plot) {
    this.plotIds.remove(plot.getId());
  }

  public void setGamerPermission(
      Channels channels, Gamer gamer, AccessFlags flag, FlagValue value) {
    channels.master_command.publish(
        new Message<>(MasterCommand.region_set_gamer_permission, this, gamer, flag, value));
  }

  public void setGroupPermission(
      Channels channels, Group group, AccessFlags flag, FlagValue value) {
    channels.master_command.publish(
        new Message<>(MasterCommand.region_set_group_permission, this, group, flag, value));
  }

  public void setPriority(Channels channels, Integer priority) {
    channels.master_command.publish(
        new Message<>(MasterCommand.region_set_priority, this, priority));
  }
}

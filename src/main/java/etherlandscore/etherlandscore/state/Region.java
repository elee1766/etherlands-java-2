package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.util.Map2;

import java.lang.reflect.Field;
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

  public Region(Team team, String name, Set<Integer> plotIds, Integer priority, boolean isDefault) {
    this.team = team.getName();
    this.plotIds = plotIds;
    this.name = name;
    this.isDefault = isDefault;
    this.priority = priority;
  }

  public void addPlot(Plot plot) {
    if (!isDefault) {
      this.plotIds.add(plot.getId());
    }
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

  public void setGroupPermission(Group group, AccessFlags flag, FlagValue value) {
    this.groupPermissionMap.put(group.getName(),flag,value);
  }

  public void setGamerPermission(Gamer gamer, AccessFlags flag, FlagValue value) {
    this.gamerPermissionMap.put(gamer.getUuid(),flag,value);
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

  public void removePlot(Plot plot) {
    this.plotIds.remove(plot.getId());
  }

  public Field[] getDeclaredFields(){
    Field[] fields = this.getClass().getDeclaredFields();
    for(Field f : fields){
      f.setAccessible(true);
    }
    return fields;
  }

  public FlagValue readGamerPermission(Gamer gamer,AccessFlags flag){
    return gamerPermissionMap.get(gamer.getUuid(),flag);
  };

  public FlagValue readGroupPermission(Group group, AccessFlags flag){
    return groupPermissionMap.get(group.getName(),flag);
  }
}

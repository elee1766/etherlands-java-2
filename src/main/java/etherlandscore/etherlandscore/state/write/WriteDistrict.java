package etherlandscore.etherlandscore.state.write;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.state.read.*;
import etherlandscore.etherlandscore.util.Map2;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteDistrict extends StateHolder implements District {
  private final Set<Integer> plotIds;
  private final String team;
  private final String name;
  private final boolean isDefault;
  private final Map2<String, AccessFlags, FlagValue> groupPermissionMap = new Map2<>();
  private final Map2<UUID, AccessFlags, FlagValue> gamerPermissionMap = new Map2<>();
  private Integer priority;

  public WriteDistrict(
      Team writeTeam, String name, Set<Integer> plotIds, Integer priority, boolean isDefault) {
    this.team = writeTeam.getName();
    this.plotIds = plotIds;
    this.name = name;
    this.isDefault = isDefault;
    this.priority = priority;
  }

  public void addPlot(Plot writePlot) {
    if (!isDefault) {
      this.plotIds.add(writePlot.getId());
    }
  }

  @Override
  public FlagValue checkFlags(AccessFlags flag, Gamer gamer) {
    return gamerPermissionMap.getOrDefault(gamer.getUuid(), flag, FlagValue.NONE);
  }

  @Override
  public FlagValue checkFlags(AccessFlags flag, Group writeGroup) {
    return groupPermissionMap.getOrDefault(writeGroup.getName(), flag, FlagValue.NONE);
  }

  public void clearGroupPermission(String name) {
    groupPermissionMap.clearGroup(name);
  }

  @Override
  public int compareTo(District r) {
    return getPriority().compareTo(r.getPriority());
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
  public String getName() {
    return name;
  }

  @Override
  public Set<Plot> getPlots() {
    Set<Plot> writePlots = new java.util.HashSet<>(Collections.emptySet());
    for (int pId : plotIds) {
      writePlots.add(state().getPlot(pId));
    }
    return writePlots;
  }

  @Override
  public Integer getPriority() {
    return this.priority;
  }

  public void setPriority(Integer newPriority) {
    if (isDefault) return;
    if (newPriority < 0) this.priority = 0;
    if (newPriority > 100) this.priority = 0;
  }

  @Override
  public Team getTeam() {
    return state().getTeam(getName());
  }

  @Override
  public boolean isDefault() {
    return isDefault;
  }

  @Override
  public FlagValue readGamerPermission(Gamer gamer, AccessFlags flag) {
    return gamerPermissionMap.get(gamer.getUuid(), flag);
  }

  @Override
  public FlagValue readGroupPermission(Group writeGroup, AccessFlags flag) {
    return groupPermissionMap.get(writeGroup.getName(), flag);
  }

  public void removePlot(Plot writePlot) {
    this.plotIds.remove(writePlot.getId());
  }

  public void setGamerPermission(Gamer gamer, AccessFlags flag, FlagValue value) {
    this.gamerPermissionMap.put(gamer.getUuid(), flag, value);
  }

  public void setGroupPermission(Group writeGroup, AccessFlags flag, FlagValue value) {
    this.groupPermissionMap.put(writeGroup.getName(), flag, value);
  }
}

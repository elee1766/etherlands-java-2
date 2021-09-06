package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.*;
import etherlandscore.etherlandscore.util.Map2;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteDistrict extends CouchDocument implements District {
  @JsonProperty("_id")
  private String _id;
  private String ownerAddress;
  private Set<Integer> plotIds;
  private Map2<String, AccessFlags, FlagValue> groupPermissionMap;
  private Map2<UUID, AccessFlags, FlagValue> gamerPermissionMap;
  private Integer priority;
  private String team;
  private String ownerUUID;
  private String ownerServerName;

  public void setPlotIds(Set<Integer> plotIds) {
    this.plotIds = plotIds;
  }

  public void setGroupPermissionMap(Map2<String, AccessFlags, FlagValue> groupPermissionMap) {
    this.groupPermissionMap = groupPermissionMap;
  }

  public void setGamerPermissionMap(Map2<UUID, AccessFlags, FlagValue> gamerPermissionMap) {
    this.gamerPermissionMap = gamerPermissionMap;
  }

  @JsonCreator
  public WriteDistrict(
      @JsonProperty("priority") Integer priority,
      @JsonProperty("ownerAddress") String ownerAddress,
      @JsonProperty("_id") Integer id,
      @JsonProperty("default") boolean isDefault) {
    this.priority = priority;
    this._id = id.toString();
    this.ownerAddress = ownerAddress;
  }

  public WriteDistrict(
      int id,
      Set<Integer> chunkIds,
      String ownerAddress) {
    this.plotIds = chunkIds;
    this.groupPermissionMap = new Map2<>();
    this.gamerPermissionMap = new Map2<>();
    this._id = String.valueOf(id);
    this.ownerAddress = ownerAddress;
  }

  @Override
  public boolean canGamerPerform(AccessFlags flag, Gamer gamer) {
    try {
      if (gamer.getPlayer().isOp()) {
        return true;
      }
      if (hasTeam()) {
        Team team = getTeamObject();
        if(team.isManager(gamer)){
          return true;
        }
        Bukkit.getLogger().info(gamer.getUuid() + " " + gamer.getTeam());
        if (gamer.getTeamObject().equals(team)) {
          FlagValue res = FlagValue.NONE;
          Set<String> groupNames = gamer.getGroups();
          Integer bestPriority = -100;
          for (String groupName : groupNames) {
            Group group = team.getGroup(groupName);
            Bukkit.getLogger().info(group.getName() + " " + res);
            if (group.getPriority() > bestPriority) {
              res = checkFlags(flag, team.getGroup(groupName), res);
              Bukkit.getLogger().info(group.getName() + "  " +flag+ " " + res);
              bestPriority = group.getPriority();
            }
          }
          res = checkFlags(flag, gamer, res);
          return res == FlagValue.ALLOW;
        } else {
          FlagValue res =
              checkFlags(flag,team.getGroup("outsiders"),FlagValue.NONE);
          return res == FlagValue.ALLOW;
        }
      } else {
        Gamer owner = this.getOwnerObject();
        if (owner == null) {
          return false;
        }
        if (owner.equals(gamer)) {
          return true;
        }
        return owner.getFriends().contains(gamer.getUuid());
      }
    } catch (Exception e) {
      Bukkit.getLogger().info(e + "\n" + e.getMessage());
      return false;
    }
  }

  @Override
  @JsonIgnore
  public Gamer getOwnerObject() {
    return state().getGamer(UUID.fromString(ownerUUID));
  }

  @Override
  public boolean hasTeam() {
    return team != null;
  }

  public void removeTeam() {
    this.team = null;
  }

  @JsonProperty("_id")
  public String getId() {
    return this._id;
  }

  @JsonProperty("_id")
  public void setId(String string) {
    this._id = string;
  }

  @Override
  public String getOwnerAddress() {
    return ownerAddress;
  }

  @Override
  public FlagValue checkFlags(AccessFlags flag, Gamer gamer, FlagValue def) {
    FlagValue out = gamerPermissionMap.getOrDefault(gamer.getUuid(), flag, FlagValue.NONE);
    if(out.equals(FlagValue.NONE)){
      return def;
    }
    return out;
  }

  @Override
  public FlagValue checkFlags(AccessFlags flag, Group writeGroup, FlagValue def) {
    FlagValue out = groupPermissionMap.getOrDefault(writeGroup.getName(), flag, FlagValue.NONE);
    if(out.equals(FlagValue.NONE)){
      return def;
    }
    return out;
  }

  public void clearGroupPermission(String name) {
    groupPermissionMap.clearGroup(name);
  }

  @Override
  public int compareTo(District r) {
    return getPriority().compareTo(r.getPriority());
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

  public Map2<UUID, AccessFlags, FlagValue> getGamerPermissionMap() {
    return gamerPermissionMap;
  }

  public Map2<String, AccessFlags, FlagValue> getGroupPermissionMap() {
    return groupPermissionMap;
  }

  public Set<Integer> getPlotIds() {
    return plotIds;
  }

  @Override
  @JsonIgnore
  public Set<Plot> getPlotObjects() {
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

  public String getTeam() {
    return team;
  }

  public void setTeam(String team) {
    this.team = team;
  }

  @Override
  @JsonIgnore
  public Team getTeamObject() {
    return state().getTeam(getTeam());
  }

  @Override
  public FlagValue readGamerPermission(Gamer gamer, AccessFlags flag) {
    return gamerPermissionMap.get(gamer.getUuid(), flag);
  }

  @Override
  public FlagValue readGroupPermission(Group writeGroup, AccessFlags flag) {
    return groupPermissionMap.get(writeGroup.getName(), flag);
  }

  @JsonIgnore
  @Override
  public Integer getIdInt() {
    return Integer.parseInt(this._id);
  }

  @JsonIgnore
  public void setGamerPermission(Gamer gamer, AccessFlags flag, FlagValue value) {
    this.gamerPermissionMap.put(gamer.getUuid(), flag, value);
  }

  @JsonIgnore
  public void setGroupPermission(Group writeGroup, AccessFlags flag, FlagValue value) {
    Bukkit.getLogger().info(flag + " "  + value + " " + writeGroup.getName());
    this.groupPermissionMap.put(writeGroup.getName(), flag, value);
  }

  @Override
  public UUID getOwnerUUID() {
    if(ownerUUID == ""||ownerUUID==null){
      return null;
    }
    return UUID.fromString(ownerUUID);
  }

  public void setOwnerUUID(UUID ownerUUID) {
    this.ownerUUID = ownerUUID.toString();
  }

  public void setOwnerAddress(String ownerAddress) {
    this.ownerAddress = ownerAddress;
  }

  public String getOwnerServerName() {
    return ownerServerName;
  }

  public void setOwnerServerName(String ownerServerName) {
    this.ownerServerName = ownerServerName;
  }

  @JsonIgnore
  public void setOwner(String ownerAddress, UUID ownerUUID) {
    this.ownerAddress = ownerAddress;
    if (!(ownerUUID ==null)) {
      this.ownerUUID = ownerUUID.toString();
    }
    if (this.ownerUUID != null) {
      OfflinePlayer player = Bukkit.getOfflinePlayer(this.ownerUUID);
      if (player.hasPlayedBefore()) {
        this.ownerServerName = player.getName();
      } else {
        this.ownerServerName = "player-uuid: [" + ownerUUID + "]";
      }
    }
  }

}

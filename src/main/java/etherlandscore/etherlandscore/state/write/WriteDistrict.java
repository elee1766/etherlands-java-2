package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.read.*;
import etherlandscore.etherlandscore.util.Map2;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteDistrict extends CouchDocument implements District {
  @JsonProperty("_id")
  private String _id;
  private Map2<String, AccessFlags, FlagValue> teamPermissionMap;
  private Map2<UUID, AccessFlags, FlagValue> gamerPermissionMap;
  private Integer priority;
  private String town;

  public void setTeamPermissionMap(Map2<String, AccessFlags, FlagValue> teamPermissionMap) {
    this.teamPermissionMap = teamPermissionMap;
  }

  public void setGamerPermissionMap(Map2<UUID, AccessFlags, FlagValue> gamerPermissionMap) {
    this.gamerPermissionMap = gamerPermissionMap;
  }

  @Override
  public String toString(){
    return this.getId();
  }

  @JsonCreator
  public WriteDistrict(
      @JsonProperty("priority") Integer priority,
      @JsonProperty("_id") Integer id,
      @JsonProperty("default") boolean isDefault) {
    this.priority = priority;
    this._id = id.toString();
  }

  public WriteDistrict(
      int id) {
    this.teamPermissionMap = new Map2<>();
    this.gamerPermissionMap = new Map2<>();
    this._id = String.valueOf(id);
  }

  @Override
  @JsonIgnore
  public String getNickname(){
    return RedisGetter.GetNameOfDistrict(this.getIdInt());
  }

  @Override
  public boolean canGamerPerform(AccessFlags flag, Gamer gamer) {
    try {
      if (gamer.getPlayer().isOp()) {
        return true;
      }
      if (hasTown()) {
        Town town = getTownObject();
        if(town.isManager(gamer)){
          return true;
        }
        Bukkit.getLogger().info(gamer.getUuid() + " " + gamer.getTown());
        if (gamer.hasTown()) {
          if (gamer.getTownObject().equals(town)) {
            FlagValue res = FlagValue.NONE;
            Set<String> teamNames = gamer.getTeams();
            Integer bestPriority = -100;
            for (String teamName : teamNames) {
              Team team = town.getTeam(teamName);
              Bukkit.getLogger().info(team.getName() + " " + res);
              if (team.getPriority() > bestPriority) {
                res = checkFlags(flag, town.getTeam(teamName), res);
                Bukkit.getLogger().info(team.getName() + "  " + flag + " " + res);
                bestPriority = team.getPriority();
              }
            }
            res = checkFlags(flag, gamer, res);
            return res == FlagValue.ALLOW;
          }
        }
        FlagValue res =
              checkFlags(flag,town.getTeam("outsiders"),FlagValue.NONE);
        return res == FlagValue.ALLOW;

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
  @JsonIgnore
  public void setDefaults(){
    for(AccessFlags af : AccessFlags.values()){
      setTeamPermission(this.getTownObject().getTeam("member"), af, FlagValue.ALLOW);
    }
  }

  @Override
  @JsonIgnore
  public Gamer getOwnerObject() {
    return state().getGamer(state().getLinks().getFirstOrDefault(RedisGetter.GetOwnerOfDistrict(this._id), null));
  }

  @Override
  public boolean hasTown() {
    return town != null;
  }

  public void removeTown() {
    this.town = null;
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
  @JsonIgnore
  public String getOwnerAddress() {
    return RedisGetter.GetOwnerOfDistrict(this._id);
  }

  @JsonIgnore
  @Override
  public Set<Integer> getPlots() {
    return RedisGetter.GetPlotsInDistrict(this._id);
  }

  @Override
  public boolean isOwner(Gamer gamer){
    return this.getOwnerAddress().equals(gamer.getAddress());
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
  public FlagValue checkFlags(AccessFlags flag, Team writeTeam, FlagValue def) {
    FlagValue out = teamPermissionMap.getOrDefault(writeTeam.getName(), flag, FlagValue.NONE);
    if(out.equals(FlagValue.NONE)){
      return def;
    }
    return out;
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
  @Override
  public Map2<UUID, AccessFlags, FlagValue> getGamerPermissionMap() {
    return gamerPermissionMap;
  }
  @Override
  public Map2<String, AccessFlags, FlagValue> getTeamPermissionMap() {
    return teamPermissionMap;
  }

  @Override
  public Integer getPriority() {
    return this.priority;
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
    return state().getTown(getTown());
  }

  @Override
  public FlagValue readGamerPermission(Gamer gamer, AccessFlags flag) {
    return gamerPermissionMap.get(gamer.getUuid(), flag);
  }

  @Override
  public FlagValue readTeamPermission(Team writeTeam, AccessFlags flag) {
    return teamPermissionMap.get(writeTeam.getName(), flag);
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
  public void setTeamPermission(Team writeTeam, AccessFlags flag, FlagValue value) {
    Bukkit.getLogger().info(flag + " "  + value + " " + writeTeam.getName());
    this.teamPermissionMap.put(writeTeam.getName(), flag, value);
  }

  @Override
  @JsonIgnore
  public UUID getOwnerUUID() {
    return state().getLinks().getFirstOrDefault(RedisGetter.GetOwnerOfDistrict(this._id), new UUID(0,0));
  }


}

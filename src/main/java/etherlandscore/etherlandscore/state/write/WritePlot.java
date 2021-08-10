package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.read.Team;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WritePlot extends CouchDocument implements Plot {
  private final Set<String> districts = new HashSet<>();
  private final Integer id;
  private final Integer x;
  private final Integer z;
  private transient Chunk chunk = null;
  private String ownerAddress;
  private String ownerUUID;
  private String ownerServerName;
  private String team;

  @JsonProperty("_id")
  private String _id;

  @JsonCreator
  public WritePlot(
      @JsonProperty("_id") String id,
      @JsonProperty("x") Integer x,
      @JsonProperty("z") Integer z,
      @JsonProperty("ownerAddress") String ownerAddress) {
    this.chunk = Bukkit.getWorld("world").getChunkAt(x, z);
    this._id = id;
    this.id = Integer.parseInt(id);
    this.x = x;
    this.z = z;
    this.ownerAddress = ownerAddress;
  }
  public WritePlot(
       Integer id,
      Integer x,
      Integer z,
      String ownerAddress) {
    this.chunk = Bukkit.getWorld("world").getChunkAt(x, z);
    this.id = id;
    this._id = id.toString();
    this.x = x;
    this.z = z;
    this.ownerAddress = ownerAddress;
  }

  public void addDistrict(District writeDistrict) {
    this.districts.add(writeDistrict.getName());
  }

  @Override
  public boolean canGamerPerform(AccessFlags flag, Gamer gamer) {
    try {
      if (gamer.getPlayer().isOp()) {
        return true;
      }
      if (hasTeam()) {
        Team team = getTeamObject();
        if (gamer.getTeamObject().equals(team)) {
          Integer bestPriority = -100;
          FlagValue res = FlagValue.NONE;
          for (String districtName : districts) {
            District writeDistrict = team.getDistrict(districtName);
            if (writeDistrict.getPriority() > bestPriority) {
              Set<String> groupNames = gamer.getGroups();
              for (String groupName : groupNames) {
                if (!writeDistrict
                    .readGroupPermission(team.getGroup(groupName), flag)
                    .equals(FlagValue.NONE)) {
                  res = writeDistrict.readGroupPermission(team.getGroup(groupName), flag);
                  bestPriority = writeDistrict.getPriority();
                }
              }
              if (!writeDistrict.readGamerPermission(gamer, flag).equals(FlagValue.NONE)) {
                res = writeDistrict.readGamerPermission(gamer, flag);
                bestPriority = writeDistrict.getPriority();
              }
            }
          }
          return res == FlagValue.ALLOW;
        } else {
          FlagValue res =
              team.getDistrict("global").readGroupPermission(team.getGroup("outsiders"), flag);
          return res == FlagValue.ALLOW;
        }
      } else {
        Gamer owner = getOwnerObject();
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Plot plot = (Plot) o;
    return getIdInt().equals(plot.getIdInt());
  }

  @Override
  @JsonIgnore
  public Chunk getChunk() {
    if (chunk == null) {
      this.chunk = Bukkit.getWorld("world").getChunkAt(x, z);
    }
    return chunk;
  }

  @Override
  @JsonIgnore
  public Field[] getDeclaredFields() {
    Field[] fields = super.getClass().getDeclaredFields();
    for (Field f : fields) {
      f.setAccessible(true);
    }
    return fields;
  }

  @Override
  @JsonIgnore
  public String getDeedHolder() {
    return this.ownerAddress;
  }

  @Override
  public Set<String> getDistricts() {
    return districts;
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
  public Integer getIdInt() {
    return this.id;
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

  @Override
  public String getOwnerAddress() {
    return ownerAddress;
  }

  public void setOwnerAddress(String ownerAddress) {
    this.ownerAddress = ownerAddress;
  }

  @Override
  @JsonIgnore
  public Gamer getOwnerObject() {
    return state().getGamer(UUID.fromString(ownerUUID));
  }

  public String getOwnerServerName() {
    return ownerServerName;
  }

  public void setOwnerServerName(String ownerServerName) {
    this.ownerServerName = ownerServerName;
  }

  @Override
  public String getTeam() {
    return team;
  }

  public void setTeam(String name) {
    this.team = name;
  }

  @JsonIgnore
  public void setTeam(Team writeTeam) {
    this.team = writeTeam.getName();
  }

  @Override
  @JsonIgnore
  public Team getTeamObject() {
    return state().getTeam(team);
  }

  @Override
  public Integer getX() {
    return x;
  }

  @Override
  public Integer getZ() {
    return z;
  }

  @Override
  public boolean hasTeam() {
    return team!=null;
  }

  @Override
  public int hashCode() {
    return getIdInt().hashCode();
  }

  @Override
  public boolean isOwner(Gamer gamer) {
    return gamer.getUuid().equals(getOwnerUUID());
  }

  public void removeDistrict(District writeDistrict) {
    this.districts.remove(writeDistrict.getName());
  }

  public void removeTeam() {
    this.team = null;
  }

  @JsonIgnore
  public void setOwner(String ownerAddress, UUID ownerUUID) {
    this.ownerAddress = ownerAddress;
    this.ownerUUID = ownerUUID.toString();
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

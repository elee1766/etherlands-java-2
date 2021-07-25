package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Plot extends StateHolder {
  private transient Chunk chunk = null;

  private final Set<String> regions = new HashSet<>();
  private final Integer id;
  private final Integer x;
  private final Integer z;
  private String ownerAddress = "";
  private UUID ownerUUID;
  private String ownerServerName = "";
  private String team = "";

  public Plot(Integer id, Integer x, Integer z, String ownerAddress) {
    this.chunk = Bukkit.getWorld("world").getChunkAt(x, z);
    this.id = id;
    this.x = x;
    this.z = z;
    this.ownerAddress = ownerAddress;
  }

  public void addRegion(Region region) {
    this.regions.add(region.getName());
  }

  public boolean canGamerPerform(AccessFlags flag, Gamer gamer) {
    if(gamer.getPlayer().isOp()){
      return true;
    }
    if(hasTeam()){
      Team team = getTeamObject();
      if(gamer.getTeamObject().equals(team)){
        Integer bestPriority = -100;
        FlagValue res = FlagValue.NONE;
        for (String regionName : regions) {
          Region region = team.getRegion(regionName);
          if(region.getPriority() > bestPriority){
            Set<String> groupNames = gamer.getGroups();
            for (String groupName : groupNames) {
              if (!region
                  .readGroupPermission(team.getGroup(groupName), flag)
                  .equals(FlagValue.NONE)) {
                res = region.readGroupPermission(team.getGroup(groupName),flag);
                bestPriority = region.getPriority();
              }
            }
            if(!region.readGamerPermission(gamer,flag).equals(FlagValue.NONE)){
              res = region.readGamerPermission(gamer,flag);
              bestPriority = region.getPriority();
            }
          }
       }
        return res == FlagValue.ALLOW;
      }else{
        FlagValue res = team.getRegion("global").readGroupPermission(team.getGroup("outsiders"),flag);
        return res == FlagValue.ALLOW;
      }
    }else{
      Gamer owner = getOwnerObject();
      if(owner.equals(gamer)){
        return true;
      }
      return owner.getFriends().contains(gamer.getUuid());
    }
  }

  public Chunk getChunk() {
    if(chunk == null){
      this.chunk = Bukkit.getWorld("world").getChunkAt(x,z);
    }
    return chunk;
  }

  public Field[] getDeclaredFields() {
    Field[] fields = super.getClass().getDeclaredFields();
    for (Field f : fields) {
      f.setAccessible(true);
    }
    return fields;
  }

  public String getDeedHolder() {
    return this.ownerAddress;
  }

  public Integer getId() {
    return this.id;
  }

  public UUID getOwner() {
    return ownerUUID;
  }

  public Gamer getOwnerObject() {
    return state().getGamer(ownerUUID);
  }

  public Set<String> getRegions() {
    return regions;
  }

  public String getTeam() {
    return team;
  }

  public boolean isOwner(Gamer gamer) {
    return gamer.getUuid().equals(getOwner());
  }

  public void removeRegion(Region region) {
    this.regions.remove(region.getName());
  }

  public void setTeam(String name) {
    this.team = name;
  }

  public void setTeam(Team team) {
    this.team = team.getName();
  }

  public Team getTeamObject() {
    return state().getTeam(team);
  }

  public Integer getX() {
    return x;
  }

  public Integer getZ() {
    return z;
  }

  public boolean hasTeam() {
    return !team.equals("");
  }

  public void reclaimPlot(Channels channels) {
    channels.master_command.publish(new Message<>(MasterCommand.plot_reclaim_plot, this));
  }

  public void removeTeam() {
    this.team = "";
  }

  public void setOwner(Channels channels, String ownerAddress) {
    channels.master_command.publish(new Message(MasterCommand.plot_set_owner, ownerAddress));
  }

  public void setOwner(String ownerAddress, UUID ownerUUID) {
    this.ownerAddress = ownerAddress;
    this.ownerUUID = ownerUUID;
    if (this.ownerUUID != null) {
      OfflinePlayer player = Bukkit.getOfflinePlayer(this.ownerUUID);
      if (player.hasPlayedBefore()) {
        this.ownerServerName = player.getName();
      } else {
        this.ownerServerName = "player-uuid: [" + ownerUUID + "]";
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Plot plot = (Plot) o;
    return getId().equals(plot.getId());
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }
}

package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.Menus.Prettifier;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Plot extends StateHolder {
  private final transient Chunk chunk;

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

  public Chunk getChunk() {
    return chunk;
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

  public String getDeedHolder() {
    return this.ownerAddress;
  }

  public Integer getId() {
    return this.id;
  }

  public boolean hasTeam() {
    return !team.equals("");
  }

  public String getTeam() {
    return team;
  }

  public Team getTeamObject(){
    return state().getTeam(team);
  }

  public void setTeam(String name) {
    this.team = name;
  }

  public void setTeam(Team team){
    this.team = team.getName();
  }

  public UUID getOwner() {
    return ownerUUID;
  }

  public Gamer getOwnerObject() {return state().getGamer(ownerUUID);}

  public Set<String> getRegions() {
    return regions;
  }

  public Integer getZ() {
    return z;
  }

  public Integer getX() {
    return x;
  }

  public void reclaimPlot(Channels channels) {
    channels.master_command.publish(new Message<>(MasterCommand.plot_reclaim_plot,this));
  }
  public void removeTeam(){
    this.team = "";
  }

  public void info(Gamer gamer){
    Player p = gamer.getPlayer();
    TextComponent info = new TextComponent("");
    Field[] fields = this.getClass().getDeclaredFields();
    for(Field field : fields) {
      TextComponent f = new TextComponent("");
      f.addExtra(" ");
      try {
        f.addExtra(field.getName());
        f.addExtra(": ");
        f.addExtra(String.valueOf(field.get(this)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
      f.addExtra("\n");
      info.addExtra(f);
    }
    Prettifier.prettyPrint(p, "Plot Info", info);
  }
}

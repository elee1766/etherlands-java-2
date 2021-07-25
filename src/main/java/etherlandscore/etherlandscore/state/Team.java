package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

public class Team extends StateHolder {
  private final String name;
  private final UUID owner;
  private final Set<UUID> members = new HashSet<>();
  private final Set<Integer> plots = new HashSet<>();
  private final Map<String, Region> regions = new HashMap<>();
  private final Map<String,Group> groups = new HashMap<>();


  public Team(Gamer gamer, String name) {
    this.name = name;
    this.owner = gamer.getUuid();
    this.regions.put("global", new Region(this,"global",new HashSet<>(),-1,true));
    this.groups.put("default", new Group(this,"default",-1,true));
    this.groups.put("manager", new Group(this,"manager",100,true));
  }

  public void createGroup(Channels channels, String name){
    channels.master_command.publish(new Message<>(MasterCommand.team_create_group,name));
  }
  public void deleteGroup(Channels channels, String name){
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_group,name));
  }
  public void createGroup(String name){
    if(!this.groups.containsKey(name)){
      this.groups.put(name, new Group(this,name,1,false));
    }
  }
  public void deleteGroup(String name){
    if(this.groups.containsKey(name)){
      if(!this.groups.get(name).isDefault()){
        this.groups.remove(name);
      }
    }
  }
  public void createRegion(Channels channels, String name){
    channels.master_command.publish(new Message<>(MasterCommand.team_add_region,name));
  }
  public void removeRegion(Channels channels, String name){
    channels.master_command.publish(new Message<>(MasterCommand.team_remove_region,name));
  }
  public void createRegion(String name){
    if(!this.regions.containsKey(name)){
      this.regions.put(name,new Region(this,name,new HashSet<>(),1,false));
    }
  }
  public void removeRegion(String name) {
    if (this.regions.containsKey(name)) {
      if (!this.regions.get(name).isDefault()) {
        this.regions.remove(name);
      }
    }
  }
  public void addMember(Channels channels, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.team_add_gamer, this, gamer));
  }

  public void addMember(Gamer gamer) {
    members.add(gamer.getUuid());
  }

  public void removeMember(Channels channels, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.team_remove_gamer, this, gamer));
  }

  public void removeMember(Gamer gamer) {
    members.remove(gamer.getUuid());
  }

  public Set<UUID> getMembers() {
    return members;
  }

  public String getName() {
    return name;
  }

  public String getOwner() {
    return Bukkit.getPlayer(this.owner).getName();
  }

  public UUID getOwnerUUID() {
    return this.owner;
  }

  public Region getRegion(String x) {
    return this.regions.getOrDefault(x, null);
  }

  public boolean canInvite(Gamer inviter) {
    return inviter.getUuid().equals(this.owner);
  }

  public void inviteGamer(Map<UUID, Long> invites, UUID arg) {
    invites.put(arg, (Instant.now().getEpochSecond()) + 5 * 60);
    Bukkit.getLogger().info(arg.toString() + " " + invites.get(arg).toString());
  }

  public boolean canJoin(Map<UUID, Long> invites, Gamer joiner) {
    Long invite = invites.get(joiner.getUuid());
    if (invite != null) {
      Bukkit.getLogger().info(invite.toString());
      return invite > Instant.now().getEpochSecond();
    }
    return false;
  }

  public void delegatePlot(Channels channels, Plot plot) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delegate_plot,this,plot));
  }

  public void addPlot(Plot plot){
    this.plots.add(plot.getId());
  }

  public void removePlot(Plot plot){
    this.plots.remove(plot.getId());
  }

  public Group getGroup(String name) {
    return getGroup(name);
  }

  public Field[] getDeclaredFields(){
    Field[] fields = this.getClass().getDeclaredFields();
    for(Field f : fields){
      f.setAccessible(true);
    }
    return fields;
  }
}

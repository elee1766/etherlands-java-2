package etherlandscore.etherlandscore.state;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Team extends StateHolder {
  private final String name;
  private final UUID owner;
  private final Set<UUID> members = new HashSet<>();
  private final Set<Integer> plots = new HashSet<>();
  private final Map<String, Region> regions = new HashMap<>();
  private final Map<String, Group> groups = new HashMap<>();

  public Team(Gamer gamer, String name) {
    this.name = name;
    this.owner = gamer.getUuid();
    this.regions.put("global", new Region(this, "global", new HashSet<>(), -1, true));
    this.groups.put("outsiders", new Group(this, "outsiders", -5, true));
    this.groups.put("member", new Group(this, "member", -1, true));
    this.groups.put("manager", new Group(this, "manager", 50, true));
  }

  public void addMember(Gamer gamer) {
    members.add(gamer.getUuid());
  }

  public void addPlot(Plot plot) {
    this.plots.add(plot.getId());
  }

  public boolean canJoin(Map<UUID, Long> invites, Gamer joiner) {
    Long invite = invites.get(joiner.getUuid());
    if (invite != null) {
      Bukkit.getLogger().info(invite.toString());
      return invite > Instant.now().getEpochSecond();
    }
    return false;
  }

  public void createGroup(String name) {
    if (!this.groups.containsKey(name)) {
      this.groups.put(name, new Group(this, name, 1, false));
    }
  }

  public void createRegion(String name) {
    if (!this.regions.containsKey(name)) {
      this.regions.put(name, new Region(this, name, new HashSet<>(), 10, false));
    }
  }

  public Group getGroup(String name) {
    return groups.get(name);
  }

  public Map<String, Group> getGroups() {
    return groups;
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

  public Set<Integer> getPlots() {
    return plots;
  }

  public Region getRegion(String x) {
    return this.regions.getOrDefault(x, null);
  }

  public boolean canInvite(Gamer inviter) {
    return inviter.getUuid().equals(this.owner);
  }

  public Map<String, Region> getRegions() {
    return regions;
  }

  public void inviteGamer(Map<UUID, Long> invites, UUID arg) {
    invites.put(arg, (Instant.now().getEpochSecond()) + 5 * 60);
    Bukkit.getLogger().info(arg.toString() + " " + invites.get(arg).toString());
  }


  public boolean canAction(Gamer actor, Gamer receiver){
    if(isManager(actor) && !isManager(receiver)){
      return true;
    }else return isOwner(actor);
  }

  public boolean isManager(Gamer manager) {
    if (manager.getUuid().equals(getOwnerUUID())) {
      return true;
    }
    return this.getGroup("manager").getMembers().contains(manager.getUuid());
  }

  public boolean isMember(Gamer gamer) {
    if(isManager(gamer)){
      return true;
    }
    return members.contains(gamer.getUuid());
  }

  public boolean isOwner(Gamer manager) {
    return manager.getUuid().equals(getOwnerUUID());
  }

  public void deleteGroup(String name) {
    if (groups.containsKey(name)) {
      if (!groups.get(name).isDefault()) {
        groups.remove(name);
      }
    }
  }

  public void removeMember(Gamer gamer) {
    members.remove(gamer.getUuid());
  }

  public void deleteRegion(String name) {
    if (regions.containsKey(name)) {
      if (!regions.get(name).isDefault()) {
        for (Integer plot : plots) {
          state().getPlot(plot).removeTeam();
        }
        this.regions.remove(name);
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Team team = (Team) o;
    return getName().equals(team.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  public Field[] getDeclaredFields() {
    Field[] fields = this.getClass().getDeclaredFields();
    for(Field f : fields){
      f.setAccessible(true);
    }
    return fields;
  }
}

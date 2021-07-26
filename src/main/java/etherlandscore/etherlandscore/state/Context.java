package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.util.Map2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Context {

  private final Map<UUID, Gamer> gamers = new HashMap<>();
  private final Map<String, Team> teams = new HashMap<>();
  private final Map<String, UUID> linked = new HashMap<>();
  private final Map<Integer, Plot> plots = new HashMap<>();
  // public final Map<Integer, Map<Integer, Integer>> plotLocations = new HashMap<>();
  private final Map2<Integer, Integer, Integer> plotLocations = new Map2<>();

  public void context_create_gamer(UUID uuid) {
    if (!this.getGamers().containsKey(uuid)) {
      Gamer gamer = new Gamer(uuid);
      this.getGamers().put(uuid, gamer);
    }
  }


  public void gamer_add_friend(Gamer a, Gamer b) {
    a.addFriend(b);
  }

  public void gamer_link_address(Gamer gamer, String address) {
    gamer.setAddress(address);
    getLinks().put(address, gamer.getUuid());
  }

  public void gamer_remove_friend(Gamer a, Gamer b) {
    a.removeFriend(b);
  }

  public Gamer getGamer(UUID uuid) {
    return gamers.get(uuid);
  }

  public Map<UUID, Gamer> getGamers() {
    return gamers;
  }

  public Map<String, UUID> getLinks() {
    return linked;
  }

  public Plot getPlot(Integer x, Integer z) {
    return getPlot(plotLocations.get(x, z));
  }

  public Plot getPlot(Integer id) {
    return plots.get(id);
  }

  public Map2<Integer, Integer, Integer> getPlotLocations() {
    return plotLocations;
  }

  public Map<Integer, Plot> getPlots() {
    return plots;
  }

  public Team getTeam(String team) {
    return teams.get(team);
  }

  public Map<String, Team> getTeams() {
    return teams;
  }

  public void group_add_gamer(Group group, Gamer gamer) {
    gamer.addGroup(group);
    group.addMember(gamer);
  }

  public void group_remove_gamer(Group group, Gamer gamer) {
    group.removeMember(gamer);
    gamer.removeGroup(group.getName());
  }

  public void group_set_priority(Group group, Integer b) {
    group.setPriority(b);
  }

  public void plot_reclaim_plot(Plot plot) {
    plot.removeTeam();
  }

  public void plot_set_owner(Plot plot, String address) {
    UUID ownerUUID = this.getLinks().getOrDefault(address, null);
    plot.setOwner(address, ownerUUID);
  }

  public void plot_update_plot(Integer id, Integer x, Integer z, String owner) {
    if (!this.getPlots().containsKey(id)) {
      this.getPlots().put(id, new Plot(id, x, z, owner));
    }
    Plot plot = this.getPlot(id);
    this.getPlotLocations().put(plot.getX(), plot.getZ(), plot.getId());
    plot_set_owner(plot, owner);
  }

  public void region_add_plot(Region region, Plot plot) {
    plot.addRegion(region);
    region.addPlot(plot);
  }

  public void region_remove_plot(Region region, Plot plot) {
    plot.removeRegion(region);
    region.removePlot(plot);
  }

  public void region_set_group_permission(Region region, Group group, AccessFlags flag, FlagValue value) {
    region.setGroupPermission(group,flag,value);
  }
  public void region_set_gamer_permission(Region region, Gamer gamer, AccessFlags flag, FlagValue value) {
    region.setGamerPermission(gamer,flag,value);
  }

  public void region_set_priority(Region region, Integer priority) {
    region.setPriority(priority);
  }

  public void team_add_gamer(Team team, Gamer gamer) {
    team.addMember(gamer);
    gamer.setTeam(team.getName());
  }

  public void team_create_group(Team team, String name) {
    team.createGroup(name);
  }

  public void team_create_region(Team team, String name) {
    team.createRegion(name);
  }

  public void team_create_team(Gamer gamer, String name) {
    Team team = new Team(gamer, name);
    if (!this.getTeams().containsKey(name)) {
      this.getTeams().put(name, team);
      gamer.setTeam(team.getName());
    }
  }

  public void team_delegate_plot(Team team, Plot plot) {
    plot_reclaim_plot(plot);
    team.addPlot(plot);
    plot.setTeam(team.getName());
  }

  public void team_delete_group(Team team, Group group) {
    for (UUID member : team.getMembers()) {
      getGamer(member).setTeam("");
    }
    getGamer(team.getOwnerUUID()).setTeam("");
    team.deleteGroup(group.getName());
  }

  public void team_delete_region(Team team, Region region) {
    for (Integer plotId: team.getPlots()) {
      getPlot(plotId).removeRegion(region);
    }
    team.deleteRegion(region.getName());
  }

  public void team_delete_team(Team team) {
    for (UUID member : team.getMembers()) {
      Gamer gamer = getGamer(member);
      gamer.setTeam("");
      gamer.clearGroups();
    }
    getGamer(team.getOwnerUUID()).setTeam("");
    for (Integer id : team.getPlots()) {
      getPlot(id).removeTeam();
    }

  teams.remove(team.getName());
  }

  public void team_remove_gamer(Team team, Gamer gamer) {
    team.removeMember(gamer);
    gamer.setTeam("");
    gamer.clearGroups();
  }
}

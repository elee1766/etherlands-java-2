package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.persistance.Couch.CouchPersister;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.write.*;
import etherlandscore.etherlandscore.util.Map2;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Context {

  private CouchPersister couchPersister;

  public final Map<UUID, WriteGamer> gamers = new HashMap<>();
  public final Map<String, WriteTeam> teams = new HashMap<>();
  public final Map<String, UUID> linked = new HashMap<>();
  public final Map<Integer, WritePlot> plots = new HashMap<>();
  public final Map2<Integer, Integer, Integer> plotLocations = new Map2<>();

  public Context(){
    try {
      this.couchPersister = new CouchPersister();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    couchPersister.populateContext(this);
  }

  public void saveAll(){
    couchPersister.saveContext(this);
  }

  public void context_create_gamer(UUID uuid) {
    if (!this.getGamers().containsKey(uuid)) {
      WriteGamer gamer = new WriteGamer(uuid);
      this.getGamers().put(uuid, gamer);
    }
  }

  public void district_add_plot(WriteDistrict district, WritePlot plot) {
    plot.addDistrict(district);
    district.addPlot(plot);
  }

  public void district_remove_plot(WriteDistrict district, WritePlot plot) {
    plot.removeDistrict(district);
    district.removePlot(plot);
  }

  public void district_set_gamer_permission(
      WriteDistrict district, WriteGamer gamer, AccessFlags flag, FlagValue value) {
    district.setGamerPermission(gamer, flag, value);
  }

  public void district_set_group_permission(
      WriteDistrict district, WriteGroup writeGroup, AccessFlags flag, FlagValue value) {
    district.setGroupPermission(writeGroup, flag, value);
  }

  public void district_set_priority(WriteDistrict district, Integer priority) {
    district.setPriorityBound(priority);
  }

  public void gamer_add_friend(WriteGamer a, Gamer b) {
    a.addFriend(b);
  }

  public void gamer_link_address(WriteGamer gamer, String address) {
    gamer.setAddress(address);
    getLinks().put(address, gamer.getUuid());
  }

  public void gamer_remove_friend(WriteGamer a, Gamer b) {
    a.removeFriend(b);
  }

  public WriteGamer getGamer(UUID uuid) {
    return gamers.get(uuid);
  }

  public Map<UUID, WriteGamer> getGamers() {
    return gamers;
  }

  public Map<String, UUID> getLinks() {
    return linked;
  }

  public WritePlot getPlot(Integer x, Integer z) {
    return getPlot(plotLocations.get(x, z));
  }

  public WritePlot getPlot(Integer id) {
    return plots.get(id);
  }

  public Map2<Integer, Integer, Integer> getPlotLocations() {
    return plotLocations;
  }

  public Map<Integer, WritePlot> getPlots() {
    return plots;
  }

  public Team getTeam(String team) {
    return teams.get(team);
  }

  public Map<String, WriteTeam> getTeams() {
    return teams;
  }

  public void group_add_gamer(WriteGroup group, WriteGamer gamer) {
    gamer.addGroup(group);
    group.addMember(gamer);
  }

  public void group_remove_gamer(WriteGroup group, WriteGamer gamer) {
    group.removeMember(gamer);
    gamer.removeGroup(group.getName());
  }

  public void group_set_priority(WriteGroup group, Integer b) {
    group.setPrioritySafe(b);
  }

  public void plot_reclaim_plot(WritePlot plot) {
    plot.removeTeam();
  }

  public void plot_set_owner(WritePlot plot, String address) {
    UUID ownerUUID = this.getLinks().getOrDefault(address, null);
    plot.setOwner(address, ownerUUID);
  }

  public void plot_update_plot(Integer id, Integer x, Integer z, String owner) {
    if (!this.getPlots().containsKey(id)) {
      this.getPlots().put(id, new WritePlot(id, x, z, owner));
    }
    WritePlot plot = this.getPlot(id);
    this.getPlotLocations().put(plot.getX(), plot.getZ(), plot.getIdInt());
    plot_set_owner(plot, owner);
  }

  public void team_add_gamer(WriteTeam team, WriteGamer gamer) {
    team.addMember(gamer);
    gamer.setTeam(team.getName());
  }

  public void team_create_district(WriteTeam team, String name) {
    team.createDistrict(name);
  }

  public void team_create_group(WriteTeam team, String name) {
    team.createGroup(name);
  }

  public void team_create_team(WriteGamer gamer, String name) {
    WriteTeam team = new WriteTeam(gamer, name);
    if (!this.getTeams().containsKey(name)) {
      this.getTeams().put(name, team);
      gamer.setTeam(team.getName());
    }
  }

  public void team_delegate_plot(WriteTeam team, WritePlot plot) {
    plot_reclaim_plot(plot);
    team.addPlot(plot);
    plot.setTeam(team.getName());
  }

  public void team_delete_district(WriteTeam team, WriteDistrict writeDistrict) {
    for (Integer plotId : team.getPlots()) {
      getPlot(plotId).removeDistrict(writeDistrict);
    }
    team.deleteDistrict(writeDistrict.getName());
  }

  public void team_delete_group(WriteTeam team, WriteGroup writeGroup) {
    for (UUID member : team.getMembers()) {
      getGamer(member).setTeam("");
    }
    getGamer(team.getOwnerUUID()).setTeam("");
    team.deleteGroup(writeGroup.getName());
  }

  public void team_delete_team(WriteTeam writeTeam) {
    for (UUID member : writeTeam.getMembers()) {
      WriteGamer gamer = getGamer(member);
      gamer.setTeam("");
      gamer.clearGroups();
    }
    getGamer(writeTeam.getOwnerUUID()).setTeam("");
    for (Integer id : writeTeam.getPlots()) {
      getPlot(id).removeTeam();
    }

    teams.remove(writeTeam.getName());
  }

  public void team_remove_gamer(WriteTeam team, WriteGamer gamer) {
    team.removeMember(gamer);
    gamer.setTeam("");
    gamer.clearGroups();
  }
}

package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Context {

  private final Map<UUID, Gamer> gamers = new HashMap<>();
  private final Map<String, Team> teams = new HashMap<>();
  private final Map<String, UUID> linked = new HashMap<>();
  private final Map<Integer, Plot> plots = new HashMap<>();
  private final Map<Integer, Map<Integer, Integer>> plotLocations = new HashMap<>();



  public Map<UUID, Gamer> getGamers() {
    return gamers;
  }

  public Map<String, UUID> getLinks() {
    return linked;
  }

  public Map<String, Team> getTeams() {
    return teams;
  }

  public void createTeam(Channels channels, Gamer gamer, String name) {
    channels.master_command.publish(new Message<>(MasterCommand.team_create_team, gamer, name));
  }

  public Plot findPlot(Integer x, Integer z) {
    Integer id = plotLocations.getOrDefault(x, new HashMap<>()).getOrDefault(z, null);
    Bukkit.getLogger().info(x + " " + z + " " + id);
    if (id != null) {
      return getPlot(id);
    }
    return null;
  }

  public Plot getPlot(Integer id) {
    return plots.get(id);
  }

  public Gamer getGamer(UUID uuid) {
    return gamers.get(uuid);
  }

  public Team getTeam(String team) {
    return teams.get(team);
  }

  public Map<Integer, Plot> getPlots() {
    return plots;
  }

  public Map<Integer, Map<Integer, Integer>> getPlotLocations() {
    return plotLocations;
  }
}

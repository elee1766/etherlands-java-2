package etherlandscore.etherlandscore.readonly;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.state.Team;

import java.util.Map;
import java.util.UUID;

public class ReadContext {
  private final Context context;

  public ReadContext(Context context) {
    this.context = context;
  }

  public Gamer getGamer(UUID uuid) {
    return context.getGamer(uuid);
  }

  public Map<UUID, Gamer> getGamers() {
    return context.getGamers();
  }

  public Plot getPlot(Integer id) {
    return context.getPlot(id);
  }

  public Plot getPlot(Integer x, Integer z) {
    return context.getPlot(x, z);
  }

  public Map<Integer, Plot> getPlots() {
    return context.getPlots();
  }

  public Team getTeam(String team) {
    return context.getTeam(team);
  }

  public Map<String, Team> getTeams() {
    return context.getTeams();
  }
  public void createTeam(Channels channels, Gamer gamer, String name) {
    channels.master_command.publish(new Message<>(MasterCommand.team_create_team, gamer, name));
  }

  public boolean hasGamer(UUID uniqueId) {
    return getGamers().containsKey(uniqueId);
  }

  public boolean hasTeam(String name) {
    return getTeams().containsKey(name);
  }
}

package etherlandscore.etherlandscore.readonly;

import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.state.Team;

import java.util.UUID;

public class ReadContext extends Context {

  private final Context context;

  public ReadContext(Context context){
    this.context = context;
  }

  @Override
  public Gamer getGamer(UUID uuid) {
    return super.getGamer(uuid);
  }

  @Override
  public Plot getPlot(Integer id) {
    return super.getPlot(id);
  }

  @Override
  public Plot getPlot(Integer x, Integer z){
    return super.getPlot(x,z);
  }
  @Override
  public Team getTeam(String team) {
    return super.getTeam(team);
  }
}

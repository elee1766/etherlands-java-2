package etherlandscore.etherlandscore.persistance.Couch;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Couch.state.GamerRepo;
import etherlandscore.etherlandscore.persistance.Couch.state.PlotRepo;
import etherlandscore.etherlandscore.persistance.Couch.state.TeamRepo;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import etherlandscore.etherlandscore.state.write.WriteNFT;
import etherlandscore.etherlandscore.state.write.WritePlot;
import etherlandscore.etherlandscore.state.write.WriteTeam;
import org.bukkit.Bukkit;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.jetlang.fibers.Fiber;

import java.net.MalformedURLException;
import java.util.Map;

public class CouchPersister extends ServerModule {

  private final CouchDbInstance instance;
  private final GamerRepo gamerRepo;
  private final TeamRepo teamRepo;
  private final PlotRepo plotRepo;
  private final CouchDbConnector linkConnector;
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();
  Channels channels;

  public CouchPersister(Channels channels, Fiber fiber) throws MalformedURLException {
    super(fiber);
    this.channels = channels;
    HttpClient httpClient =
        new StdHttpClient.Builder()
            .url(settings.get("CouchUrl"))
            .username(settings.get("CouchUsername"))
            .password(settings.get("CouchPassword"))
            .build();
    this.instance = new StdCouchDbInstance(httpClient);
    this.gamerRepo =
        new GamerRepo(this.instance.createConnector("gamers", true), WriteGamer.class);
    this.plotRepo = new PlotRepo(this.instance.createConnector("plots", true), WritePlot.class);
    this.teamRepo = new TeamRepo(this.instance.createConnector("teams", true), WriteTeam.class);
    this.linkConnector = this.instance.createConnector("linked",true);

    channels.db_gamer.subscribe(fiber, this::write);
    channels.db_team.subscribe(fiber, this::write);
    channels.db_plot.subscribe(fiber, this::write);

    channels.db_gamer_delete.subscribe(fiber, this::remove);
    channels.db_team_delete.subscribe(fiber, this::remove);
    channels.db_plot_delete.subscribe(fiber, this::remove);
  }

  public void saveContext(Context context) {
      gamerRepo.save(context.getGamers().values());
      plotRepo.save(context.getPlots().values());
      teamRepo.save(context.getTeams().values());
  }

  public void populateContext(Context empty){
    Bukkit.getLogger().info("doing gamers");
    for (WriteGamer writeGamer : this.gamerRepo.getAll()) {
      empty.gamers.put(writeGamer.getUuid(),writeGamer);
      if(!writeGamer.getAddress().equals("")){
        if (writeGamer.getAddress() != null) {
          empty.linked.put(writeGamer.getAddress(), writeGamer.getUuid());
        }
      }
    }
    Bukkit.getLogger().info("doing teams");
    for (WriteTeam writeTeam : this.teamRepo.getAll()) {
      empty.teams.put(writeTeam.getName(),writeTeam);
    }
    Bukkit.getLogger().info("doing plots");
    for (WritePlot writePlot : this.plotRepo.getAll()) {
      empty.plots.put(writePlot.getIdInt(),writePlot);
      empty.plotLocations.put(writePlot.getX(), writePlot.getZ(), writePlot.getIdInt());
    }
    Bukkit.getLogger().info("done reading from db");
  }

  public void write(WriteGamer gamer){
    this.gamerRepo.save(gamer);
  }
  public void write(WritePlot plot){
    this.plotRepo.save(plot);
  }
  public void write(WriteTeam team){
    this.teamRepo.save(team);
  }

  public void update(WriteGamer gamer){
    this.channels.db_gamer.publish(gamer);
  }
  public void update(WritePlot plot){
    this.channels.db_plot.publish(plot);
  }
  public void update(WriteTeam team){
    this.channels.db_team.publish(team);
  }
  public void update(WriteNFT nft) {this.channels.db_nft.publish(nft); }
  public void remove(WriteGamer gamer){
    this.gamerRepo.delete(gamer);
  }
  public void remove(WritePlot plot){
    this.plotRepo.delete(plot);
  }
  public void remove(WriteTeam team){
    this.teamRepo.delete(team);
  }


  public void delete(WriteGamer gamer){
    this.channels.db_gamer_delete.publish(gamer);
  }
  public void delete(WritePlot plot){
    this.channels.db_plot_delete.publish(plot);
  }
  public void delete(WriteTeam team){
    this.channels.db_team_delete.publish(team);
  }
}

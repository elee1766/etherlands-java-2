package etherlandscore.etherlandscore.persistance.Couch;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Couch.state.*;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.write.*;
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
  private final NFTRepo nftRepo;
  private final MapRepo mapRepo;
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
    this.mapRepo = new MapRepo(this.instance.createConnector("maps", true), WriteMap.class);
    this.nftRepo = new NFTRepo(this.instance.createConnector("nfts", true), WriteNFT.class);
    this.linkConnector = this.instance.createConnector("linked",true);

    channels.db_gamer.subscribe(fiber, this::write);
    channels.db_team.subscribe(fiber, this::write);
    channels.db_plot.subscribe(fiber, this::write);
    channels.db_nft.subscribe(fiber, this::write);
    channels.db_map.subscribe(fiber, this::write);


    channels.db_gamer_delete.subscribe(fiber, this::remove);
    channels.db_team_delete.subscribe(fiber, this::remove);
    channels.db_plot_delete.subscribe(fiber, this::remove);
    channels.db_nft_delete.subscribe(fiber, this::remove);
    channels.db_map_delete.subscribe(fiber, this::remove);
  }

  public void saveContext(Context context) {
      gamerRepo.save(context.getGamers().values());
      plotRepo.save(context.getPlots().values());
      teamRepo.save(context.getTeams().values());
      nftRepo.save(context.getNftUrls().values());
      mapRepo.save(context.getMaps());
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
    Bukkit.getLogger().info("doing maps");
    for (WriteMap writeMap: this.mapRepo.getAll()) {
      empty.maps.add(writeMap);
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
  public void write(WriteMap map){
    this.mapRepo.save(map);
  }
  public void write(WriteNFT nft){
    this.nftRepo.save(nft);
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
  public void update(WriteMap map) {this.channels.db_map.publish(map); }

  public void remove(WriteGamer gamer){
    this.gamerRepo.delete(gamer);
  }
  public void remove(WritePlot plot){
    this.plotRepo.delete(plot);
  }
  public void remove(WriteTeam team){
    this.teamRepo.delete(team);
  }
  public void remove(WriteMap map){
    this.mapRepo.delete(map);
  }
  public void remove(WriteNFT nft){
    this.nftRepo.delete(nft);
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

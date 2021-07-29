package etherlandscore.etherlandscore.persistance.Couch;

import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import etherlandscore.etherlandscore.state.write.WritePlot;
import etherlandscore.etherlandscore.state.write.WriteTeam;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import java.net.MalformedURLException;

public class CouchPersister {

  private final CouchDbInstance instance;
  private final CouchRepo<WriteGamer> gamerRepo;
  private final CouchRepo<WriteTeam> teamRepo;
  private final CouchRepo<WritePlot> plotRepo;
  private final CouchDbConnector linkConnector;

  public CouchPersister() throws MalformedURLException {
    HttpClient httpClient =
        new StdHttpClient.Builder()
            .url("http://owl.elee.bike:5984")
            .username("admin")
            .password("crypto")
            .build();

    this.instance = new StdCouchDbInstance(httpClient);
    this.gamerRepo =
        new CouchRepo<>(this.instance.createConnector("gamers", true), WriteGamer.class);
    this.plotRepo = new CouchRepo<>(this.instance.createConnector("plots", true), WritePlot.class);
    this.teamRepo = new CouchRepo<>(this.instance.createConnector("teams", true), WriteTeam.class);
    this.linkConnector = this.instance.createConnector("linked",true);
  }

  public void saveContext(Context context) {
      gamerRepo.save(context.getGamers().values());
      plotRepo.save(context.getPlots().values());
      teamRepo.save(context.getTeams().values());
  }

  public void populateContext(Context empty){
    for (WriteGamer writeGamer : this.gamerRepo.getAll()) {
      empty.gamers.put(writeGamer.getUuid(),writeGamer);
      if(!writeGamer.getAddress().equals("")){
        if (writeGamer.getAddress() != null) {
          empty.linked.put(writeGamer.getAddress(), writeGamer.getUuid());
        }
      }
    }
    for (WriteTeam writeTeam : this.teamRepo.getAll()) {
      empty.teams.put(writeTeam.getName(),writeTeam);
    }
    for (WritePlot writePlot : this.plotRepo.getAll()) {
      empty.plots.put(writePlot.getIdInt(),writePlot);
      empty.plotLocations.put(writePlot.getX(), writePlot.getZ(), writePlot.getIdInt());
    }
  }
}

package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WriteGamer;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public class GamerRepo extends CouchDbRepositorySupport<WriteGamer> {
  public GamerRepo(CouchDbConnector db) {
    super(WriteGamer.class, db, true);
  }

  public void save(WriteGamer gamer) {
    if (this.contains(gamer.getId())) {
      if (gamer.getRevision() == null) {
        gamer.setRevision(this.get(gamer.getId()).getRevision());
      }
      this.update(gamer);
      return;
    }
    this.add(gamer);
  }
}

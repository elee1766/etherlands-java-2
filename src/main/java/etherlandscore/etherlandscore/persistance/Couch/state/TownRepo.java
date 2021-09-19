package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WriteTown;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class TownRepo extends CouchRepo<WriteTown> {
  public TownRepo(CouchDbConnector db, Class<WriteTown> type) {
    super(db, type);
  }
}

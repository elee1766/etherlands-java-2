package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WriteMap;
import etherlandscore.etherlandscore.state.write.WriteNFT;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class MapRepo extends CouchRepo<WriteMap> {
  public MapRepo(CouchDbConnector db, Class<WriteMap> type) {
    super(db, type);
  }
}

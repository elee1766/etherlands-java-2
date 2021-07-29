package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WriteGamer;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class GamerRepo extends CouchRepo<WriteGamer> {
  public GamerRepo(CouchDbConnector db, Class<WriteGamer> type) {
    super(db, type);
  }
}

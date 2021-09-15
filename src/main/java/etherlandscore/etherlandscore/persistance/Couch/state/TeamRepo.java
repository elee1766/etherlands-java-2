package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WriteTeam;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class TeamRepo extends CouchRepo<WriteTeam> {
  public TeamRepo(CouchDbConnector db, Class<WriteTeam> type) {
    super(db, type);
  }
}

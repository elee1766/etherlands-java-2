package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WriteNFT;
import etherlandscore.etherlandscore.state.write.WriteTown;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class NFTRepo extends CouchRepo<WriteNFT> {
  public NFTRepo(CouchDbConnector db, Class<WriteNFT> type) {
    super(db, type);
  }
}

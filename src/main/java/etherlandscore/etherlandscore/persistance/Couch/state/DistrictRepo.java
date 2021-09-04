package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WriteDistrict;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class DistrictRepo extends CouchRepo<WriteDistrict> {
  public DistrictRepo(CouchDbConnector db, Class<WriteDistrict> type) {
    super(db, type);
  }
}

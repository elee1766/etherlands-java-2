package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WriteBankRecord;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class BankRecordRepo extends CouchRepo<WriteBankRecord> {
  public BankRecordRepo(CouchDbConnector db, Class<WriteBankRecord> type) {
    super(db, type);
  }
}

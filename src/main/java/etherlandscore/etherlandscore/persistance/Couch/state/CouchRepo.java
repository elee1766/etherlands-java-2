package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;

import java.util.Collection;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class CouchRepo<T extends CouchDocument> extends CouchDbRepositorySupport<T> {
  public CouchRepo(CouchDbConnector db, Class<T> type) {
    super(type, db, true);
    initStandardDesignDocument();
  }

  public void save(T entry) {
    if (entry != null) {
      if (this.contains(entry.getId())) {
        entry.setRevision(this.get(entry.getId()).getRevision());
        this.update(entry);
        return;
      }
      this.add(entry);
    }
  }

  public void save(Collection<T> entries) {
    for (T entry : entries) {
      if (entry != null) {
        if (this.contains(entry.getId())) {
          entry.setRevision(this.get(entry.getId()).getRevision());
        }
      }
    }
    entries.remove(null);
    super.db.executeBulk(entries);
  }

  public void delete(T entry){
    super.db.delete(entry);
  }

}

package etherlandscore.etherlandscore.persistance.Couch;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

import java.util.*;

public class CouchRepo<T extends CouchDocument> extends CouchDbRepositorySupport<T> {
  public CouchRepo(CouchDbConnector db, Class<T> type) {
    super(type, db, true);
  }

  public void save(T entry) {
    if (this.contains(entry.getId())) {
      if (entry.getRevision() == null) {
        entry.setRevision(this.get(entry.getId()).getRevision());
        }
        this.update(entry);
      return;
    }
    this.add(entry);
  }

  public void save(Collection<T> entries) {
    List<Object> bulkSave = new ArrayList<>();
    for (T entry : entries) {
      if (this.contains(entry.getId())) {
        if (entry.getRevision() == null) {
          entry.setRevision(this.get(entry.getId()).getRevision());
        }
      }
    }
    super.db.executeBulk(entries);
  }

}

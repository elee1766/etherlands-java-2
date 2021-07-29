package etherlandscore.etherlandscore.persistance.Couch.state;

import etherlandscore.etherlandscore.state.write.WritePlot;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

@View(name="all", map="function(doc){emit(null,doc._id)}")
public class PlotRepo extends CouchRepo<WritePlot> {
  public PlotRepo(CouchDbConnector db, Class<WritePlot> type) {
    super(db, type);
  }
}

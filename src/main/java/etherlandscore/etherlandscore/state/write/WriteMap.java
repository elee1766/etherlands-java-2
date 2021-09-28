package etherlandscore.etherlandscore.state.write;

import java.net.URL;
import java.util.Set;

public class WriteMap 
    implements etherlandscore.etherlandscore.state.read.MapRead {
  private final Set<Integer> maps;
  private final URL url;
  private String _id;

  public WriteMap(
      String id,
      Set<Integer> maps,
      URL url) {
    this.maps = maps;
    this.url = url;
    this._id = id;
  }


  public String getId() {
    return this._id;
  }


  public void setId(String string) {
    this._id = string;
  }

  @Override
  public Set<Integer> getMaps() {
    return maps;
  }

  @Override
  public URL getUrl() {
    return this.url;
  }
}

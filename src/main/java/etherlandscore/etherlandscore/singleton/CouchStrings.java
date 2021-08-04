package etherlandscore.etherlandscore.singleton;

import java.util.HashMap;
import java.util.Map;

public class CouchStrings {

  private final Map<String, String> settings = new HashMap<>();

  public Map<String, String> getSettings() {
    return settings;
  }

}

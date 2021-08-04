package etherlandscore.etherlandscore.singleton;

import com.google.gson.Gson;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class CouchSingleton {
  private static CouchStrings couch = null;
  private static CouchSingleton couch_instance = null;

  private final Gson gson;
  private final JsonPersister<CouchStrings> couchSettingsPersister;

  private CouchSingleton() {
    this.gson = new Gson();
    String root =
        Bukkit.getServer()
            .getPluginManager()
            .getPlugin("EtherlandsCore")
            .getDataFolder()
            .getAbsolutePath();
    new File(root).mkdirs();
    File json = new File(root + "/couch.json");
    try {
      if (json.createNewFile()) {
        CouchSingleton.loadDefaults(root);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.couchSettingsPersister = new JsonPersister<>(root + "/couch.json");
    couch = couchSettingsPersister.readJson(this.gson, CouchStrings.class);
  }

  public static CouchSingleton getInstance() {
    if (couch_instance == null) {
      couch_instance = new CouchSingleton();
    }
    return couch_instance;
  }

  public static CouchStrings getCouchSettings() {
    if (couch_instance == null) {
      couch_instance = new CouchSingleton();
    }
    return couch;
  }

  private static void loadDefaults(String root) throws IOException {
    // figure out how to load defaults into file
  }
}

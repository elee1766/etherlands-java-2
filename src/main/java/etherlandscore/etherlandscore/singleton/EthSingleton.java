package etherlandscore.etherlandscore.singleton;

import com.google.gson.Gson;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class EthSingleton {
  private static EthStrings eth = null;
  private static EthSingleton eth_instance = null;

  private final Gson gson;
  private final JsonPersister<EthStrings> ethSettingsPersister;

  private EthSingleton() {
    this.gson = new Gson();
    String root =
        Bukkit.getServer()
            .getPluginManager()
            .getPlugin("EtherlandsCore")
            .getDataFolder()
            .getAbsolutePath();
    new File(root).mkdirs();
    File json = new File(root + "/eth.json");
    try {
      if (json.createNewFile()) {
        EthSingleton.loadDefaults(root);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.ethSettingsPersister = new JsonPersister<>(root + "/eth.json");
    eth = ethSettingsPersister.readJson(this.gson, EthStrings.class);
  }

  public static EthSingleton getInstance() {
    if (eth_instance == null) {
      eth_instance = new EthSingleton();
    }
    return eth_instance;
  }

  public static EthStrings getEthSettings() {
    if (eth_instance == null) {
      eth_instance = new EthSingleton();
    }
    return eth;
  }

  private static void loadDefaults(String root) throws IOException {
    // figure out how to load defaults into file
  }
}

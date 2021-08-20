package etherlandscore.etherlandscore.singleton;

import com.google.gson.Gson;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class SettingsSingleton {
  private static SettingsStrings settings = null;
  private static SettingsSingleton settings_instance = null;

  private final Gson gson;
  private final JsonPersister<SettingsStrings> settingsPersister;

  private SettingsSingleton() {
    this.gson = new Gson();
    String root =
        Bukkit.getServer()
            .getPluginManager()
            .getPlugin("EtherlandsCore")
            .getDataFolder()
            .getAbsolutePath();
    new File(root).mkdirs();
    File json = new File(root + "/settings.json");
    try {
      if (json.createNewFile()) {
        SettingsSingleton.loadDefaults(root);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.settingsPersister = new JsonPersister<>(root + "/settings.json");
    settings = settingsPersister.readJson(this.gson, SettingsStrings.class);
  }

  public static SettingsSingleton getInstance() {
    if (settings_instance == null) {
      settings_instance = new SettingsSingleton();
    }
    return settings_instance;
  }

  public static SettingsStrings getSettings() {
    if (settings_instance == null) {
      settings_instance = new SettingsSingleton();
    }
    return settings;
  }

  private static void loadDefaults(String root) throws IOException {
    // figure out how to load defaults into file
  }
}

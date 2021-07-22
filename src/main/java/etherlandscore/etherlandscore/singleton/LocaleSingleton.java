package etherlandscore.etherlandscore.singleton;

import com.google.gson.Gson;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class LocaleSingleton {
  private static LocaleStrings locale = null;
  private static LocaleSingleton locale_instance = null;

  private final Gson gson;
  private final JsonPersister<LocaleStrings> localeStringsPersister;

  private LocaleSingleton(){
    this.gson = new Gson();
    String root = Bukkit.getServer().getPluginManager().getPlugin("EtherlandsCore").getDataFolder().getAbsolutePath();
    new File(root).mkdirs();
    File json = new File(root+"/locale.json");
    try {
      json.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.localeStringsPersister = new JsonPersister<>(root + "/locale.json");
    locale = localeStringsPersister.readJson(this.gson, LocaleStrings.class);
  }

  public static LocaleSingleton getInstance(){
    if(locale_instance==null){
      locale_instance = new LocaleSingleton();
    }
    return locale_instance;
  }
  public static LocaleStrings getLocale(){
    if(locale_instance==null){
      locale_instance = new LocaleSingleton();
    }
    return locale;
  }
}

package etherlandscore.etherlandscore.singleton;

import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.Date;
import java.util.Properties;

public class SettingsSingleton {
  public static Properties settings = null;

  public static Properties getSettings(){
    if(settings==null){
      readSettings();
    }
    return settings;
  }

  public static void readSettings(){
    String root =
        Bukkit.getServer()
            .getPluginManager()
            .getPlugin("EtherlandsCore")
            .getDataFolder()
            .getAbsolutePath();
    new File(root).mkdirs();

    InputStream inputStream = null;

    File properties = new File(root + "/config.properties");
    settings = new Properties();
    try {

      inputStream = new FileInputStream(properties);

      if (inputStream != null) {
        settings.load(inputStream);
      } else {
        throw new FileNotFoundException("property file '" + properties.getName() + "' not found in the classpath");
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    } finally {
      try{
        inputStream.close();
      }catch(Exception e){
        System.out.println("Exception: " + e);
      }
    }
  }
}

package etherlandscore.etherlandscore.singleton;

import com.google.gson.Gson;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordList {
  public static List<String> words;

  public static List<String> readFile() {
    List<String> words = new ArrayList<>();
    try {

      URL url = new URL("https://raw.githubusercontent.com/bitcoin/bips/master/bip-0039/english.txt");

      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

      String line;
      while ((line = in.readLine()) != null) {
        words.add(line);
      }
      in.close();
    }
    catch (MalformedURLException e) {
      System.out.println("Malformed URL: " + e.getMessage());
    }
    catch (IOException e) {
      System.out.println("I/O Error: " + e.getMessage());
    }
    return words;
  }

  public static List<String> getList() {
    if(words==null){
      words = readFile();
    }
    return words;
  }



}

package etherlandscore.etherlandscore.singleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WordList {
  public static List<String> words;

  public static List<String> getList() {
    if (words == null) {
      words = readFile();
    }
    return words;
  }

  public static List<String> readFile() {
    List<String> words = new ArrayList<>();
    try {

      URL url =
          new URL("https://raw.githubusercontent.com/bitcoin/bips/master/bip-0039/english.txt");
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String line;
      while ((line = in.readLine()) != null) {
        words.add(line);
      }
      in.close();
    } catch (MalformedURLException e) {
      System.out.println("Malformed URL: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("I/O Error: " + e.getMessage());
    }
    return words;
  }
}

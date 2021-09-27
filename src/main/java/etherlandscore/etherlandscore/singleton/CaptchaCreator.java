package etherlandscore.etherlandscore.singleton;

import java.util.List;
import java.util.Random;

public class CaptchaCreator {

  public static String[] createCaptcha() {
    String[] output = new String[3];
    List<String> wordlist = WordList.getList();
    Random rand = new Random();
    int a = rand.nextInt(2048);
    int b = rand.nextInt(2048);
    int c = rand.nextInt(2048);
    output[0] = wordlist.get(a);
    output[1] = wordlist.get(b);
    output[2] = wordlist.get(c);
    return output;
  }



}

package etherlandscore.etherlandscore.singleton;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.services.MasterService;
import etherlandscore.etherlandscore.singleton.WordList;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetlang.fibers.Fiber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import static etherlandscore.etherlandscore.services.MasterService.state;

public class CaptchaCreator {
  private final Channels channels;

  public CaptchaCreator(Channels channels) {
    this.channels = channels;
  }

  public String[] createCaptcha() {
    String[] output = new String[3];
    List<String> wordlist = WordList.getList();
    Random rand = new Random();
    int a = rand.nextInt(2048);
    int b = rand.nextInt(2048);
    int c = rand.nextInt(2048);
    while(!state().isValidCaptcha(a, b, c)){
      a = rand.nextInt(2048);
      b = rand.nextInt(2048);
      c = rand.nextInt(2048);
    }
    output[0] = wordlist.get(a);
    output[1] = wordlist.get(b);
    output[2] = wordlist.get(c);
    channels.master_command.publish(
        new Message<>(MasterCommand.context_store_captcha, a, b, c));
    return output;
  }



}

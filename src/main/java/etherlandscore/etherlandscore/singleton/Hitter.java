package etherlandscore.etherlandscore.singleton;

import etherlandscore.etherlandscore.services.ImpartialHitter;
import etherlandscore.etherlandscore.state.read.Gamer;

import java.util.Locale;

public class Hitter {

  public static void SetGamerPosition(Gamer gamer){
    if(gamer.getPlayer() != null){
      int x = (int) gamer.getPlayer().getLocation().getX();
      int y = (int) gamer.getPlayer().getLocation().getY();
      int z = (int) gamer.getPlayer().getLocation().getZ();
      ImpartialHitter.HitWorld(
          "gamer",
          gamer.getUuid().toString().toLowerCase(Locale.ROOT),
          "pos",
          Integer.toString(x),
          Integer.toString(y),
          Integer.toString(z)
      );

    }
  }

}

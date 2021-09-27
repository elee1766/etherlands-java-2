package etherlandscore.etherlandscore.singleton;

import etherlandscore.etherlandscore.services.ImpartialHitter;
import etherlandscore.etherlandscore.state.read.Gamer;

import java.util.Locale;

public class Hitter {

  public static void CreateLinkRequest(Gamer gamer, String a, String b, String c){
    ImpartialHitter.HitWorld("link_request", gamer.getUuid().toString(),a,b,c);
  }

  public static void RequestImageDownload(String collection, String id){
      ImpartialHitter.HitWorld("image_download",collection,id);
  }

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

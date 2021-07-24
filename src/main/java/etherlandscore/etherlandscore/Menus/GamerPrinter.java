package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GamerPrinter {
  private final Gamer gamer;

  public GamerPrinter(Gamer gamer) {
    super();
    this.gamer = gamer;
  }

  public void printGamer(Player sender){
    TextComponent titlebar = new TextComponent("");
    TextComponent allcomp = new TextComponent("");
    titlebar.addExtra("============== GamerInfo ==============\n\n");
    allcomp.addExtra(titlebar);
    TextComponent info = new TextComponent("");
    Field[] fields = gamer.getDeclaredFields();
    for(Field field : fields) {
      TextComponent f = new TextComponent("");
      f.addExtra(" ");
      try {
        f.addExtra(field.getName());
        f.addExtra(": ");
        f.addExtra(String.valueOf(field.get(this)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
      f.addExtra("\n");
      info.addExtra(f);
    }

    allcomp.addExtra(info);
    sender.sendMessage(allcomp);
  }

}

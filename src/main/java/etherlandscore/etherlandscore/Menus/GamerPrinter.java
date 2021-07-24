package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

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
    titlebar.setColor(ChatColor.AQUA);
    allcomp.addExtra(titlebar);
    TextComponent info = new TextComponent("");
    Field[] fields = gamer.getDeclaredFields();
    for(Field field : fields) {
      TextComponent f = new TextComponent("");
      f.addExtra(" ");
      try {
        TextComponent names = new TextComponent(field.getName());
        names.setColor(ChatColor.AQUA);
        f.addExtra(names);
        f.addExtra(": ");
        String val = String.valueOf(field.get(this.gamer));
        if(field.getName()=="address"){
          String edited = val.substring(0,4);
          edited = edited+"..."+val.substring(val.length()-3);
          val = edited;
        }
        TextComponent value = new TextComponent(val);
        value.setColor(ChatColor.DARK_AQUA);
        f.addExtra(value);
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

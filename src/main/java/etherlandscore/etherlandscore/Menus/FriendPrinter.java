package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.Gamer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static etherlandscore.etherlandscore.services.MasterService.state;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public class FriendPrinter {
  private final Gamer gamer;

  public FriendPrinter(Gamer gamer) {
    super();
    this.gamer = gamer;
  }

  public void printFriends() {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print);
    prettyPrint.addBar("=", "FriendList");
    Set<UUID> friends = gamer.getFriends();
    for(UUID friend : friends){
      Gamer f = state().getGamer(friend);
      String name = f.getPlayer().getName();
      String address = f.getAddress();
      prettyPrint.addFriend(name, address);
      prettyPrint.addLine();
    }
    prettyPrint.printOut(gamer.getPlayer());
  }
}

package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import static etherlandscore.etherlandscore.services.MasterService.state;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public class FriendPrinter extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final Gamer gamer;

  public FriendPrinter(Gamer gamer, Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.gamer = gamer;
  }

  public void printFriends() {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print, fiber, channels);
    prettyPrint.addBar("=", "FriendList");
    Set<UUID> friends = gamer.getFriends();
    for(UUID friend : friends){
      Gamer f = state().getGamer(friend);
      String name = Bukkit.getOfflinePlayer(friend).getName();
      String address = f.getAddress();
      prettyPrint.addFriend(name, address);
      prettyPrint.addLine();
    }
    prettyPrint.printOut(gamer.getPlayer());
  }
}

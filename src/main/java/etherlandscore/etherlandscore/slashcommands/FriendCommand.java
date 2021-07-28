package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.Menus.SelectorMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class FriendCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final LocaleStrings locales = new LocaleStrings();

  public FriendCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void friendAdd(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Gamer newFriend = (Gamer) args[0];
    if (!gamer.getFriends().contains(newFriend.getUuid())) {
      GamerSender.addFriend(this.channels, gamer, newFriend);
      sender.sendMessage("Friend added successfully");
    } else {
      sender.sendMessage("Friend failed to be added");
    }
  }

  void friendRemove(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Gamer oldFriend = (Gamer) args[0];
    if (gamer.getFriends().contains(oldFriend.getUuid())) {
      GamerSender.removeFriend(this.channels, gamer, oldFriend);
      sender.sendMessage("Friend successfully removed");
    } else {
      sender.sendMessage("Friend failed to be removed");
    }
  }

  void friendAddSelector(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    SelectorMenu.menu(gamer, getPlayerStrings(), "friend add");
  }

  void friendRemoveSelector(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    SelectorMenu.menu(gamer, getPlayerStrings(), "friend remove");
  }

  void friendList(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (gamer.getFriends() != null) {
      gamer.friendList();
    } else {
      sender.sendMessage("There are no friends");
    }
  }

  void help(Player sender, Object[] args){
    sender.sendMessage("add, remove, list");
  }

  public void register() {
    CommandAPICommand FriendAdd =
        new CommandAPICommand("add")
            .withArguments(gamerArgument("friend"))
            .withPermission("etherlands.public")
            .executesPlayer((this::friendAdd));
    CommandAPICommand FriendAddSelector =
        new CommandAPICommand("add")
            .withPermission("etherlands.public")
            .executesPlayer((this::friendAddSelector));
    CommandAPICommand FriendRemove =
        new CommandAPICommand("remove")
            .withArguments(gamerArgument("friend"))
            .withPermission("etherlands.public")
            .executesPlayer((this::friendRemove));
    CommandAPICommand FriendRemoveSelector =
        new CommandAPICommand("remove")
            .withPermission("etherlands.public")
            .executesPlayer(
                (this::friendRemoveSelector));
    CommandAPICommand List =
        new CommandAPICommand("list")
            .withPermission("etherlands.public")
            .executesPlayer((this::friendList));
    CommandAPICommand FriendCommand =
            new CommandAPICommand("friend")
                    .withSubcommand(FriendAdd)
                    .withSubcommand(FriendAddSelector)
                    .withSubcommand(FriendRemove)
                    .withSubcommand(FriendRemoveSelector)
                    .withSubcommand(List)
                    .withPermission("etherlands.public")
                    .executesPlayer((this::help));
    FriendCommand.register();
  }
}

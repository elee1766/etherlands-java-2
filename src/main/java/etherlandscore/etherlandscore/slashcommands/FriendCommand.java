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

  void friendAdd(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Gamer newFriend = (Gamer) args[0];
    if (!gamer.getFriends().contains(newFriend.getUuid())) {
      GamerSender.addFriend(this.channels, gamer, newFriend);
      sender.sendMessage("Friend added successfully");
    } else {
      sender.sendMessage("Friend failed to be added");
    }
  }

  void friendAddSelector(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    SelectorMenu.menu(gamer, getPlayerStrings(), "friend add");
  }

  void friendList(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (gamer.getFriends() != null) {
      gamer.friendList();
    } else {
      sender.sendMessage("There are no friends");
    }
  }

  void friendRemove(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Gamer oldFriend = (Gamer) args[0];
    if (gamer.getFriends().contains(oldFriend.getUuid())) {
      GamerSender.removeFriend(this.channels, gamer, oldFriend);
      sender.sendMessage("Friend successfully removed");
    } else {
      sender.sendMessage("Friend failed to be removed");
    }
  }

  void friendRemoveSelector(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    SelectorMenu.menu(gamer, getPlayerStrings(), "friend remove");
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage("add, remove, list");
  }

  public void register() {
    CommandAPICommand FriendCommand =
        new CommandAPICommand("friend")
            .withPermission("etherlands.public")
            .executesPlayer(this::help);
    FriendCommand.withSubcommand(
        new CommandAPICommand("add")
            .withArguments(gamerArgument("friend"))
            .executesPlayer(this::friendAdd));
    FriendCommand.withSubcommand(
        new CommandAPICommand("add").executesPlayer(this::friendAddSelector));
    FriendCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withArguments(gamerArgument("friend"))
            .executesPlayer(this::friendRemove));
    FriendCommand.withSubcommand(
        new CommandAPICommand("remove").executesPlayer(this::friendRemoveSelector));
    FriendCommand.withSubcommand(new CommandAPICommand("list").executesPlayer(this::friendList));
    FriendCommand.register();
  }
}

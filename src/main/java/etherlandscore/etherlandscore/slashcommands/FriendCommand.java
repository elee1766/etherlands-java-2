package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.Menus.FriendPrinter;
import etherlandscore.etherlandscore.Menus.SelectorMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class FriendCommand extends CommandProcessor {
  private final Fiber fiber;
  private final Channels channels;

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
    } else {
    }
  }

  void friendAddSelector(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    SelectorMenu.menu(gamer, getPlayerStrings(), "friend add");
  }

  void friendList(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (gamer.getFriends() != null) {
      FriendPrinter fp = new FriendPrinter(gamer, channels, fiber);
      fp.printFriends();
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
        createPlayerCommand("friend", SlashCommands.help,this::help)
            .withPermission("etherlands.public");
    FriendCommand.withSubcommand(
        createPlayerCommand("add",SlashCommands.friendAdd,this::friendAdd)
            .withArguments(gamerArgument("friend").replaceSuggestions(info -> getPlayerStrings()))
    );
    FriendCommand.withSubcommand(
        createPlayerCommand("add",SlashCommands.friendAddSelector,this::friendAddSelector)
    );
    FriendCommand.withSubcommand(
        createPlayerCommand("remove",SlashCommands.friendRemove,this::friendRemove)
            .withArguments(gamerArgument("friend").replaceSuggestions(info -> getPlayerStrings()))
    );
    FriendCommand.withSubcommand(
        createPlayerCommand("remove",SlashCommands.friendRemoveSelector,this::friendRemoveSelector)
    );
    FriendCommand.withSubcommand(
        createPlayerCommand("list",SlashCommands.list,this::friendList)
    );
    FriendCommand.register();
  }
}

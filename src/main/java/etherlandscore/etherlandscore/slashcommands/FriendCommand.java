package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.write.Gamer;
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
      if (!gamer.getUuid().equals(newFriend.getUuid())) {
        StateSender.addFriend(this.channels, gamer, newFriend);
      }
    }
  }

  void friendRemove(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Gamer oldFriend = (Gamer) args[0];
    if (gamer.getFriends().contains(oldFriend.getUuid())) {
      StateSender.removeFriend(this.channels, gamer, oldFriend);
      sender.sendMessage("Friend successfully removed");
    } else {
      sender.sendMessage("Friend failed to be removed");
    }
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage("add, remove, list");
  }

  public void register() {
    CommandAPICommand FriendCommand =
        createPlayerCommand("friend", SlashCommands.help, this::help)
            .withPermission("etherlands.public");
    FriendCommand.withSubcommand(
        createPlayerCommand("add", SlashCommands.friendAdd, this::friendAdd)
            .withArguments(gamerArgument("friend")));
    FriendCommand.withSubcommand(
        createPlayerCommand("remove", SlashCommands.friendRemove, this::friendRemove)
            .withArguments(gamerArgument("friend")));
    FriendCommand.register();
  }
}

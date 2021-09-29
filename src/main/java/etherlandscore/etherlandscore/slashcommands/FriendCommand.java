package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ImpartialHitter;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.Gamer;
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
    ImpartialHitter.HitWorld("gamer",
        gamer.getUuidString(),
        "add_friend",
        newFriend.getUuidString()
    );
  }

  void friendRemove(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Gamer oldFriend = (Gamer) args[0];

    ImpartialHitter.HitWorld("gamer",
        gamer.getUuidString(),
        "remove_friend",
        oldFriend.getUuidString()
    );
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

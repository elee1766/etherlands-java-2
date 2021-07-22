package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.LocaleStrings;
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

  public void register() {
    CommandAPICommand FriendCommand =
        new CommandAPICommand("friend")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  sender.sendMessage("add");
                });
    FriendCommand.withSubcommand(
        new CommandAPICommand("add")
            .withArguments(new PlayerArgument("friend"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Gamer newFriend = context.getGamer(((Player) args[0]).getUniqueId());
                  if (!gamer.getFriends().contains(newFriend)) {
                    gamer.addFriend(this.channels, newFriend);
                  } else {
                    sender.sendMessage(locales.getFriends().get("fail"));
                  }
                  sender.sendMessage(locales.getFriends().get("success"));
                }));

    FriendCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withArguments(new PlayerArgument("friend"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Gamer newFriend = context.getGamer(((Player) args[0]).getUniqueId());
                  if (gamer.getFriends().contains(newFriend.getUuid())) {
                    gamer.removeFriend(this.channels, newFriend);
                  } else {
                    sender.sendMessage(locales.getFriends().get("fail"));
                  }
                  sender.sendMessage(locales.getFriends().get("success"));
                }));
    FriendCommand.register();
  }
}

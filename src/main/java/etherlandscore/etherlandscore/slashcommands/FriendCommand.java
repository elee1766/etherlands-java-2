package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.Menus.SelectorMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.sender.GamerSender;
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
                  sender.sendMessage("add, remove, list");
                });
    FriendCommand.withSubcommand(
        new CommandAPICommand("add")
            .withArguments(gamerArgument("friend"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Gamer newFriend = (Gamer) args[0];
                  if (!gamer.getFriends().contains(newFriend.getUuid())) {
                    GamerSender.addFriend(this.channels, gamer, newFriend);
                    sender.sendMessage("Friend added successfully");
                  } else {
                    sender.sendMessage("Friend failed to be added");
                  }
                }));
    FriendCommand.withSubcommand(
        new CommandAPICommand("add")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  SelectorMenu.menu(gamer, getPlayerStrings(), "friend add");
                }));

    FriendCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withArguments(gamerArgument("friend"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Gamer oldFriend = (Gamer) args[0];
                  if (gamer.getFriends().contains(oldFriend.getUuid())) {
                    GamerSender.removeFriend(this.channels, gamer, oldFriend);
                    sender.sendMessage("Friend successfully added");
                  } else {
                    sender.sendMessage("Friend failed to be added");
                  }
                }));
    FriendCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  SelectorMenu.menu(gamer, getPlayerStrings(), "friend remove");
                }));
    FriendCommand.withSubcommand(
        new CommandAPICommand("list")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  if (gamer.getFriends() != null) {
                    gamer.friendList();
                  } else {
                    sender.sendMessage("There are no friends");
                  }
                }));
    FriendCommand.register();
  }
}

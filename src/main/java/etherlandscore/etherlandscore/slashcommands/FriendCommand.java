package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleSingleton;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
            .withArguments(new PlayerArgument("friend").replaceSuggestions(info->getPlayerStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Gamer newFriend = context.getGamer(((Player) args[0]).getUniqueId());
                  if (!gamer.getFriends().contains(newFriend)) {
                    gamer.addFriend(this.channels, newFriend);
                  } else {
                    sender.sendMessage(LocaleSingleton.getLocale().getFriends().get("fail"));
                  }
                  sender.sendMessage(LocaleSingleton.getLocale().getFriends().get("success"));
                }));

    FriendCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withArguments(new PlayerArgument("friend").replaceSuggestions(info->getPlayerStrings()))
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
      FriendCommand.withSubcommand(
              new CommandAPICommand("list")
                      .withPermission("etherlands.public")
                      .executesPlayer(
                              (sender, args) -> {
                                  Gamer gamer = context.getGamer(sender.getUniqueId());
                                  if(gamer.getFriends()!=null) {
                                      gamer.friendList(channels);
                                  }else{
                                      sender.sendMessage(locales.getFriends().get("empty"));
                                  }
                              }));
    FriendCommand.register();
  }
}

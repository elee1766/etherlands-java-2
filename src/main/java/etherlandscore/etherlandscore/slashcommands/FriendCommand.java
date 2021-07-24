package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleSingleton;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.Menus.SelectorMenu;
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
                  sender.sendMessage("add, remove, list");
                });
    FriendCommand.withSubcommand(
        new CommandAPICommand("add")
            .withArguments(gamerArgument("friend"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Gamer newFriend = context.getGamer(((Player) args[0]).getUniqueId());
                  if (!gamer.getFriends().contains(newFriend.getUuid())) {
                    gamer.addFriend(this.channels, newFriend);
                    sender.sendMessage(LocaleSingleton.getLocale().getFriends().get("success"));
                  } else {
                    sender.sendMessage(LocaleSingleton.getLocale().getFriends().get("fail"));
                  }
                }));
    FriendCommand.withSubcommand(
            new CommandAPICommand("add")
                    .withPermission("etherlands.public")
                    .executesPlayer(
                            (sender, args) -> {
                              Gamer gamer = context.getGamer(sender.getUniqueId());
                              SelectorMenu.menu(gamer,getPlayerStrings(), "friend add");
                            }));

    FriendCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withArguments(gamerArgument("friend"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Gamer newFriend = context.getGamer(((Player) args[0]).getUniqueId());
                  if (gamer.getFriends().contains(newFriend.getUuid())) {
                    gamer.removeFriend(this.channels, newFriend);
                    sender.sendMessage(LocaleSingleton.getLocale().getFriends().get("success"));
                  } else {
                    sender.sendMessage(LocaleSingleton.getLocale().getFriends().get("fail"));
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
                                  if(gamer.getFriends()!=null) {
                                      gamer.friendList();
                                  }else{
                                      LocaleSingleton.getLocale().getFriends().get("empty");
                                  }
                              }));
    FriendCommand.register();
  }
}

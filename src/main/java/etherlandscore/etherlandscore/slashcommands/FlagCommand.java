package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.Menus.FlagMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class FlagCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final LocaleStrings locales = new LocaleStrings();

  public FlagCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void register() {
    CommandAPICommand flagMenuDistrictPlayer =
        (new CommandAPICommand("district_player")
            .withArguments(teamDistrictArgument("district"))
            .withArguments(
                new PlayerArgument("gamer").replaceSuggestions(info -> getPlayerStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer runner = context.getGamer(sender.getUniqueId());
                  FlagMenu.clickMenu(
                      runner,
                      "player",
                      "district set_player",
                      (District) args[0],
                      (Player) args[1]);
                }));
    CommandAPICommand flagMenuDistrictGroup =
        (new CommandAPICommand("district_group")
            .withArguments(teamDistrictArgument("district"))
            .withArguments(teamGroupArgument("group"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer runner = context.getGamer(sender.getUniqueId());
                  FlagMenu.clickMenu(
                      runner, "group", "district set_group", (District) args[0], (Group) args[1]);
                }));
    CommandAPICommand flagMenu =
        new CommandAPICommand("menu")
            .withSubcommand(flagMenuDistrictGroup)
            .withSubcommand(flagMenuDistrictPlayer)
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  // stuff
                });
    CommandAPICommand FlagCommand =
        new CommandAPICommand("flag")
            .withSubcommand(flagMenu)
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  sender.sendMessage("global, plot");
                });

    FlagCommand.register();
  }
}

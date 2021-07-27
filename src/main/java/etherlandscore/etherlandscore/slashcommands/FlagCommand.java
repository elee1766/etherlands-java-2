package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.FlagMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Group;
import etherlandscore.etherlandscore.state.Region;
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
    CommandAPICommand flagMenuRegionPlayer = (
    new CommandAPICommand("region_player")
            .withArguments(teamRegionArgument("region"))
            .withArguments(
                    new PlayerArgument("gamer").replaceSuggestions(info -> getPlayerStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(
                    (sender, args) -> {
                      Gamer runner = context.getGamer(sender.getUniqueId());
                      FlagMenu.clickMenu(runner,"player","region set_player", (Region) args[0], (Player) args[1]);
                    }));
    CommandAPICommand flagMenuRegionGroup = (
            new CommandAPICommand("region_group")
                    .withArguments(teamRegionArgument("region"))
                    .withArguments(teamGroupArgument("group"))
                    .withPermission("etherlands.public")
                    .executesPlayer(
                            (sender, args) -> {
                              Gamer runner = context.getGamer(sender.getUniqueId());
                              FlagMenu.clickMenu(runner,"group","region set_group", (Region) args[0], (Group) args[1]);
                            }));
    CommandAPICommand flagMenu =
            new CommandAPICommand("menu")
                    .withSubcommand(flagMenuRegionGroup)
                    .withSubcommand(flagMenuRegionPlayer)
                    .withPermission("etherlands.public")
                    .executesPlayer((sender, args) -> {
                      //stuff
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

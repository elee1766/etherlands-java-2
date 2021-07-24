package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.Menus.FlagMenu;
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
    CommandAPICommand FlagCommand =
        new CommandAPICommand("flags")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  sender.sendMessage("global, plot");
                });
    FlagCommand.withSubcommand(
        new CommandAPICommand("global")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  //idk wtf we gonn do w this
                }));

    FlagCommand.withSubcommand(
        new CommandAPICommand("plot")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  FlagMenu.plotMenu(gamer);
                }));
    FlagCommand.withSubcommand(
            new CommandAPICommand("set")
                    .withArguments(new StringArgument("flag").replaceSuggestions(info -> getAccessFlagStrings()),new StringArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
                    .withPermission("etherlands.public")
                    .executesPlayer(
                            (sender, args) -> {
                              //set the flags here boiii
                            }));


    FlagCommand.register();
  }
}

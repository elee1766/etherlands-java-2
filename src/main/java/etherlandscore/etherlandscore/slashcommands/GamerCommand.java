package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.Menus.GamerPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.read.Gamer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class GamerCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final LocaleStrings locales = new LocaleStrings();

  public GamerCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void info(Player sender, Object[] args){
    Player player = (Player) args[0];
    Gamer gamer = context.getGamer(player.getUniqueId());
    GamerPrinter printer = new GamerPrinter(gamer);
    printer.printGamer(sender);
  }

  public void register() {
    CommandAPICommand GamerCommand =
        new CommandAPICommand("gamer")
            .withPermission("etherlands.public");
    GamerCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(new PlayerArgument("gamer").replaceSuggestions(info -> getPlayerStrings()))
            .executesPlayer(this::info));

    GamerCommand.register();
  }
}

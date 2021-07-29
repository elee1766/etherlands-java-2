package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import org.bouncycastle.util.Arrays;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class EthCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public EthCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void reLink(Player sender, Object[] args) {}

  public void register() {
    CommandAPICommand EthCommand =
        new CommandAPICommand("eth")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand);
    EthCommand.withSubcommand(new CommandAPICommand("help").executesPlayer(this::runHelpCommand));
    EthCommand.withSubcommand(
        new CommandAPICommand("relink")
            .withArguments(
                new StringArgument("player")
                    .includeSuggestions(info -> Arrays.append(getPlayerStrings(), "__global__")))
            .executesPlayer(this::reLink));
    EthCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("relink");
  }
}

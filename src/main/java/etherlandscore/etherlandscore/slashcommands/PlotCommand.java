package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.EthersCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.PlotSender;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class PlotCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public PlotCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void register() {
    CommandAPICommand ChunkCommand =
        new CommandAPICommand("plot")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand);
    ChunkCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("This does not exist anymore");
  }
}

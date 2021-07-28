package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.Menus.PlotPrinter;
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
import org.bukkit.Chunk;
import org.bukkit.Location;
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
    ChunkCommand.withSubcommand(
        new CommandAPICommand("help")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("info")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Location loc = sender.getLocation();
                  Chunk chunk = loc.getChunk();
                  int x = chunk.getX();
                  int z = chunk.getZ();
                  Plot writePlot = context.getPlot(x, z);
                  if (writePlot == null) {
                    TextComponent unclaimed = new TextComponent("This Land is unclaimed");
                    unclaimed.setColor(ChatColor.YELLOW);
                    sender.sendMessage(unclaimed);
                  } else {
                    PlotPrinter printer = new PlotPrinter(writePlot);
                    printer.printPlot(sender);
                  }
                }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(
                new IntegerArgument("chunkId").replaceSuggestions(info -> getChunkStrings()))
            .withPermission("etherlands.public")
            .executes(
                (sender, args) -> {
                  Plot writePlot = context.getPlot((int) args[0]);
                  if (writePlot == null) {
                    TextComponent unclaimed = new TextComponent("This Land is unclaimed");
                    unclaimed.setColor(ChatColor.YELLOW);
                    sender.sendMessage(unclaimed);
                  } else {
                    PlotPrinter printer = new PlotPrinter(writePlot);
                    printer.printPlot((Player) sender);
                  }
                }));

    ChunkCommand.withSubcommand(
        new CommandAPICommand("update")
            .withArguments(new IntegerRangeArgument("chunkId"))
            .withPermission("etherlands.public")
            .executes(
                (sender, args) -> {
                  IntegerRange range = (IntegerRange) args[0];
                  for (int i = range.getLowerBound();
                      i <= Math.min(1000000, range.getUpperBound());
                      i++) {
                    this.channels.ethers_command.publish(
                        new Message<>(EthersCommand.ethers_query_nft, i));
                  }
                }));

    ChunkCommand.withSubcommand(
        new CommandAPICommand("reclaim")
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  IntegerRange range = (IntegerRange) args[0];
                  for (int i = range.getLowerBound();
                      i <= Math.min(context.getPlots().size(), range.getUpperBound());
                      i++) {
                    if (context.getPlot(i).getOwner().equals(gamer.getUuid())) {
                      PlotSender.reclaimPlot(this.channels, context.getPlot(i));
                    }
                  }
                }));

    ChunkCommand.withSubcommand(
        new CommandAPICommand("reclaim")
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Chunk chunk = gamer.getPlayer().getChunk();
                  Plot writePlot = context.getPlot(chunk.getX(), chunk.getZ());
                  if (writePlot.getOwner().equals(gamer.getUuid())) {
                    PlotSender.reclaimPlot(this.channels, writePlot);
                  }
                }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team writeTeam = gamer.getTeamObject();
                  IntegerRange range = (IntegerRange) args[0];
                  for (int i = range.getLowerBound();
                      i <= Math.min(context.getPlots().size(), range.getUpperBound());
                      i++) {
                    if (context.getPlot(i).getOwner().equals(gamer.getUuid())) {
                      TeamSender.delegatePlot(this.channels, context.getPlot(i), writeTeam);
                    }
                  }
                }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team writeTeam = gamer.getTeamObject();
                  Chunk chunk = gamer.getPlayer().getChunk();
                  Plot writePlot = context.getPlot(chunk.getX(), chunk.getZ());
                  if (writePlot.getOwner().equals(gamer.getUuid())) {
                    TeamSender.delegatePlot(this.channels, writePlot, writeTeam);
                  }
                }));
    ChunkCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("update info invite join delete");
  }
}

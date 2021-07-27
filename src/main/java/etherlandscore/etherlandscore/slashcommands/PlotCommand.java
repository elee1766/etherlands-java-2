package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.Menus.PlotPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.EthersCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.state.Team;
import etherlandscore.etherlandscore.stateWrites.PlotWrites;
import etherlandscore.etherlandscore.stateWrites.TeamWrites;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bouncycastle.util.Arrays;
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
        new CommandAPICommand("set")
            .withPermission("etherlands.public")
            .withArguments(
                new StringArgument("flag").replaceSuggestions(info -> getAccessFlagStrings()))
            .withArguments(
                new StringArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
            .executesPlayer((sender, args) -> {}));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("set")
            .withPermission("etherlands.public")
            .withArguments(
                new IntegerArgument("chunkId").replaceSuggestions(info -> getChunkStrings()))
            .withArguments(
                new StringArgument("player")
                    .includeSuggestions(info -> Arrays.append(getPlayerStrings(), "__global__")))
            .withArguments(
                new StringArgument("flag").replaceSuggestions(info -> getAccessFlagStrings()))
            .withArguments(
                new StringArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
            .executesPlayer((sender, args) -> {}));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("info")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Location loc = sender.getLocation();
                  Chunk chunk = loc.getChunk();
                  int x = chunk.getX();
                  int z = chunk.getZ();
                  Plot plot = context.getPlot(x, z);
                  if (plot == null) {
                    TextComponent unclaimed = new TextComponent("This Land is unclaimed");
                    unclaimed.setColor(ChatColor.YELLOW);
                    sender.sendMessage(unclaimed);
                  } else {
                    PlotPrinter printer = new PlotPrinter(plot);
                    printer.printPlot(sender);
                  }
                }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(
                new IntegerArgument("chunkId").replaceSuggestions(info -> getChunkStrings()))
            .withPermission("etherlands.public")
            .executes((sender, args) -> {
              Plot plot = context.getPlot((int)args[0]);
              if (plot == null) {
                TextComponent unclaimed = new TextComponent("This Land is unclaimed");
                unclaimed.setColor(ChatColor.YELLOW);
                sender.sendMessage(unclaimed);
              } else {
                PlotPrinter printer = new PlotPrinter(plot);
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
                      PlotWrites.reclaimPlot(this.channels, context.getPlot(i));
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
                  Plot plot = context.getPlot(chunk.getX(), chunk.getZ());
                  if (plot.getOwner().equals(gamer.getUuid())) {
                    PlotWrites.reclaimPlot(this.channels, plot);
                  }
                }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  IntegerRange range = (IntegerRange) args[0];
                  for (int i = range.getLowerBound();
                      i <= Math.min(context.getPlots().size(), range.getUpperBound());
                      i++) {
                    if (context.getPlot(i).getOwner().equals(gamer.getUuid())) {
                      TeamWrites.delegatePlot(this.channels, context.getPlot(i),team);
                    }
                  }
                }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  Chunk chunk = gamer.getPlayer().getChunk();
                  Plot plot = context.getPlot(chunk.getX(), chunk.getZ());
                  if (plot.getOwner().equals(gamer.getUuid())) {
                    TeamWrites.delegatePlot(this.channels, plot, team);
                  }
                }));
    ChunkCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("update info invite join delete");
  }
}

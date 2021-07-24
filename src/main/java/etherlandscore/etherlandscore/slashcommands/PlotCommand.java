package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.EthersCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.Menus.Prettifier;
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
            .executesPlayer((sender, args) -> {
              Gamer gamer = context.getGamer(sender.getUniqueId());
              Location loc = sender.getLocation();
              Chunk chunk = loc.getChunk();
              int x = chunk.getX();
              int z = chunk.getZ();
              Plot plot = context.findPlot(x,z);
              if(!plot.equals(null)) {
                plot.info(gamer);
              }else{
                sender.sendMessage("This land is unclaimed");
              }
            }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(
                new IntegerArgument("chunkId").replaceSuggestions(info -> getChunkStrings()))
            .withPermission("etherlands.public")
            .executes((sender, args) -> { }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("update")
            .withArguments(new IntegerArgument("chunkId"))
            .withPermission("etherlands.public")
            .executes(
                (sender, args) -> {
                  this.channels.ethers_command.publish(
                      new Message<>(EthersCommand.ethers_query_nft, args[0]));
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
                      context.getPlot(i).reclaimPlot(this.channels);
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
                  Plot plot = context.findPlot(chunk.getX(), chunk.getZ());
                  if (plot.getOwner().equals(gamer.getUuid())) {
                    plot.reclaimPlot(this.channels);
                  }
                }));
    ChunkCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("update info invite join delete");
  }
}

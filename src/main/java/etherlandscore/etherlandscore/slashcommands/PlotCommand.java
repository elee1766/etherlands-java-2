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

  void delegate(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    IntegerRange range = (IntegerRange) args[0];
    for (int i = range.getLowerBound();
        i <= range.getUpperBound();
        i++) {
      if (context.getPlot(i).getOwnerUUID().equals(gamer.getUuid())) {
        TeamSender.delegatePlot(this.channels, context.getPlot(i), writeTeam);
        sender.sendMessage("Plot: " + i + " has been delegated to " + writeTeam.getName());
      } else {
        sender.sendMessage("You do not own this plot");
      }
    }
  }

  void delegateLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    Chunk chunk = gamer.getPlayer().getChunk();
    Plot writePlot = context.getPlot(chunk.getX(), chunk.getZ());
    if (writePlot.getOwnerUUID().equals(gamer.getUuid())) {
      TeamSender.delegatePlot(this.channels, writePlot, writeTeam);
      sender.sendMessage(writePlot.getIdInt() + " has been delegated to " + writeTeam.getName());
    } else {
      sender.sendMessage("You do not own this plot");
    }
  }

  void infoGiven(CommandSender sender, Object[] args) {
    Plot writePlot = context.getPlot((int) args[0]);
    if (writePlot == null) {
      TextComponent unclaimed = new TextComponent("This Land is unclaimed");
      unclaimed.setColor(ChatColor.YELLOW);
      sender.sendMessage(unclaimed);
    } else {
      PlotPrinter printer = new PlotPrinter(writePlot);
      printer.printPlot((Player) sender);
    }
  }

  void infoLocal(Player sender, Object[] args) {
    Plot writePlot;
    Location loc = sender.getLocation();
    Chunk chunk = loc.getChunk();
    int x = chunk.getX();
    int z = chunk.getZ();
    writePlot = context.getPlot(x, z);
    if (writePlot == null) {
      TextComponent unclaimed = new TextComponent("This Land is unclaimed");
      unclaimed.setColor(ChatColor.YELLOW);
      sender.sendMessage(unclaimed);
    } else {
      PlotPrinter printer = new PlotPrinter(writePlot);
      printer.printPlot(sender);
    }
  }

  void reclaim(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    IntegerRange range = (IntegerRange) args[0];
    for (int i = range.getLowerBound();
         i <= range.getUpperBound();
         i++) {
      Plot writePlot = context.getPlot(i);
      Bukkit.getLogger().warning(gamer.getAddress() + " is trying to claim " + writePlot.getOwnerAddress() + "'s plot");
      if(writePlot != null) {
        if (writePlot.getOwnerAddress().equals(gamer.getAddress())) {
          PlotSender.reclaimPlot(this.channels, writePlot);
          sender.sendMessage("Plot: " + i + " has been reclaimed");
        } else {
          sender.sendMessage("You do not own plot:" + i);
        }
      }else{
        sender.sendMessage("That plot is currently unclaimed");
      }
    }
  }

  void reclaimLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Chunk chunk = gamer.getPlayer().getChunk();
    Plot writePlot = context.getPlot(chunk.getX(), chunk.getZ());
    if (writePlot.getOwnerUUID().equals(gamer.getUuid())) {
      PlotSender.reclaimPlot(this.channels, writePlot);
      sender.sendMessage(writePlot.getIdInt() + " has been reclaimed");
    }else {
      sender.sendMessage("You do not own this plot");
    }
  }

  public void register() {
    CommandAPICommand ChunkCommand =
        new CommandAPICommand("plot")
            .withPermission("etherlands.public")
            .executesPlayer(this::infoLocal);
    ChunkCommand.withSubcommand(new CommandAPICommand("help").executesPlayer(this::runHelpCommand));
    ChunkCommand.withSubcommand(new CommandAPICommand("info").executesPlayer(this::infoLocal));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(
                new IntegerArgument("chunkId").replaceSuggestions(info -> getChunkStrings()))
            .executes(this::infoGiven));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("update")
            .withArguments(new IntegerRangeArgument("chunkId"))
            .executes(this::update));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("reclaim")
            .withArguments(new IntegerRangeArgument("chunkId"))
            .executesPlayer(this::reclaim));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("reclaim")
            .executesPlayer(this::reclaimLocal));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .executesPlayer(this::delegate));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("delegate").executesPlayer(this::delegateLocal));
    ChunkCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("update info invite join delete");
  }

  void update(CommandSender sender, Object[] args) {
    sender.sendMessage(args[0] + " is being updated...");
    IntegerRange range = (IntegerRange) args[0];
    for (int i = range.getLowerBound(); i <= Math.min(1000000, range.getUpperBound()); i++) {
      this.channels.ethers_command.publish(new Message<>(EthersCommand.ethers_query_nft, i));
    }
    sender.sendMessage(args[0] + " have been updated");
  }
}

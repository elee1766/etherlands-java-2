package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.read.Plot;
import io.lettuce.core.models.role.RedisNodeDescription;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Set;

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
        new CommandAPICommand("plot").withAliases("p")
            .withPermission("etherlands.public")
            .withArguments(new StringArgument("PlotID"))
            .executesPlayer(this::coord);
    ChunkCommand.register();
    CommandAPICommand ChunkCommandidGiven =
        new CommandAPICommand("plot")
            .withPermission("etherlands.public")
            .withArguments(new StringArgument("X"))
            .withArguments(new StringArgument("Z"))
            .executesPlayer(this::idGiven);
    ChunkCommandidGiven.register();
    CommandAPICommand ChunkCommandidLocal =
        new CommandAPICommand("plot")
            .withPermission("etherlands.public")
            .executesPlayer(this::idLocal);
    ChunkCommandidLocal.register();
  }

  void coord(Player player, Object[] args) {
    String idString = (String) args[0];
    String x = RedisGetter.getPlotX(idString);
    String z = RedisGetter.getPlotZ(idString);
    Double district = RedisGetter.getDistrictOfPlot(idString);
    player.sendMessage("Plot coords: " + x + ", " + z + " district: " + district);
  }

  void idLocal(Player sender, Object[] args) {
    Location loc = sender.getLocation();
    String x = String.valueOf(loc.getChunk().getX());
    String z = String.valueOf(loc.getChunk().getZ());
    Set<String> plotIDs = RedisGetter.getPlotID(x, z);
    if(plotIDs==null){
      sender.sendMessage("There is no plot here");
    }else {
      sender.sendMessage("Plot ID: " + plotIDs);
    }
  }

  void idGiven(Player sender, Object[] args) {
    String x = (String) args[0];
    String z = (String) args[1];
    Set<String> plotIDs = RedisGetter.getPlotID(x, z);
    if(plotIDs==null){
      sender.sendMessage("There is no plot here");
    }else{
      Double district = RedisGetter.getDistrictOfPlot(plotIDs.iterator().next());
      sender.sendMessage("Plot coords: " + x + ", " + z + " district: " + district);
    }
  }
}

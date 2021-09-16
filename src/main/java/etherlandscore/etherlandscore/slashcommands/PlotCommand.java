package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class PlotCommand extends CommandProcessor {

  public PlotCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    register();
  }

  public void register() {

    CommandAPICommand ChunkCommand =
        createPlayerCommand("plot",SlashCommands.plot,this::coord)
            .withAliases("p")
            .withPermission("etherlands.public")
            .withArguments(new StringArgument("PlotID"));
    ChunkCommand.register();

    CommandAPICommand ChunkCommandidGiven =
        createPlayerCommand("plot",SlashCommands.idGiven,this::idGiven)
            .withPermission("etherlands.public")
            .withArguments(new StringArgument("X"))
            .withArguments(new StringArgument("Z"));
    ChunkCommandidGiven.register();

    CommandAPICommand ChunkCommandidLocal =
        createPlayerCommand("plot", SlashCommands.idLocal,this::idLocal)
            .withPermission("etherlands.public");
    ChunkCommandidLocal.register();
  }

  void coord(Player player, Object[] args) {
    String idString = (String) args[0];
    String x = RedisGetter.GetPlotX(idString);
    String z = RedisGetter.GetPlotZ(idString);
    Integer district = RedisGetter.GetDistrictOfPlot(idString);
    player.sendMessage("Plot coords: " + x + ", " + z + " district: " + district);
  }

  void idLocal(Player sender, Object[] args) {
    Location loc = sender.getLocation();
    String x = String.valueOf(loc.getChunk().getX());
    String z = String.valueOf(loc.getChunk().getZ());
    Integer plotIDs = RedisGetter.GetPlotID(x, z);
    if(plotIDs==null){
      sender.sendMessage("There is no plot here");
    }else {
      sender.sendMessage("Plot ID: " + plotIDs);
    }
  }

  void idGiven(Player sender, Object[] args) {
    String x = (String) args[0];
    String z = (String) args[1];
    Integer plotIDs = RedisGetter.GetPlotID(x, z);
    if(plotIDs==null){
      sender.sendMessage("There is no plot here");
    }else{
      Integer district = RedisGetter.GetDistrictOfPlot(plotIDs.toString());
      sender.sendMessage("Plot coords: " + x + ", " + z + " district: " + district);
    }
  }
}

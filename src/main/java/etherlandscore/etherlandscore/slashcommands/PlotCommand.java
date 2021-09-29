package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.singleton.WorldAsker;
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

  void coord(Player player, Object[] args) {
    Integer id = (Integer) args[0];
    String x = WorldAsker.GetPlotX(id).toString();
    String z = WorldAsker.GetPlotZ(id).toString();
    Integer district = WorldAsker.GetDistrictOfPlot(id);
    player.sendMessage("Plot coords: " + x + ", " + z + " district: " + district);
  }

  void idGiven(Player sender, Object[] args) {
    Integer x = (Integer) args[0];
    Integer z = (Integer) args[1];
    Integer plotIDs = WorldAsker.GetPlotID(x, z);
    if (plotIDs == null) {
      sender.sendMessage("There is no plot here");
    } else {
      Integer district = WorldAsker.GetDistrictOfPlot(plotIDs.toString());
      sender.sendMessage("Plot coords: " + x + ", " + z + " district: " + district);
    }
  }

  void idLocal(Player sender, Object[] args) {
    Location loc = sender.getLocation();
    int x = loc.getChunk().getX();
    int z = loc.getChunk().getZ();
    Integer plotIDs = WorldAsker.GetPlotID(x, z);
    if (plotIDs == null) {
      sender.sendMessage("There is no plot here");
    } else {
      sender.sendMessage("Plot ID: " + plotIDs);
    }
  }

  public void register() {

    CommandAPICommand ChunkCommand =
        createPlayerCommand("plot", SlashCommands.plot, this::coord)
            .withAliases("p")
            .withPermission("etherlands.public")
            .withArguments(new IntegerArgument("PlotID"));
    ChunkCommand.register();

    CommandAPICommand ChunkCommandidGiven =
        createPlayerCommand("plot", SlashCommands.idGiven, this::idGiven)
            .withPermission("etherlands.public")
            .withArguments(new IntegerArgument("X"))
            .withArguments(new IntegerArgument("Z"));
    ChunkCommandidGiven.register();

    CommandAPICommand ChunkCommandidLocal =
        createPlayerCommand("plot", SlashCommands.idLocal, this::idLocal)
            .withPermission("etherlands.public");
    ChunkCommandidLocal.register();
  }
}

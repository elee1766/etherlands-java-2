package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Region;
import etherlandscore.etherlandscore.state.Team;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class RegionCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public RegionCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void register() {
    CommandAPICommand ChunkCommand =
        new CommandAPICommand("region")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand);
    ChunkCommand.withSubcommand(
        new CommandAPICommand("help")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand)
    );
    ChunkCommand.withSubcommand(
        new CommandAPICommand("create")
            .withArguments(cleanNameArgument("regionname"))
            .withPermission("etherlands.public")
            .executesPlayer((sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                    if(team != null){
                      team.createRegion(this.channels, (String) args[0]);
                    }else{
                      runNoTeam(sender);
                  }
                }
            )
    );
    ChunkCommand.withSubcommand(
        new CommandAPICommand("add")
            .withArguments(new StringArgument("regionname"))
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  Region region = team.getRegion((String) args[0]);
                  if (region != null) {
                    if (team.getOwnerUUID().equals(gamer.getUuid())) {
                      IntegerRange range = (IntegerRange) args[1];
                      for (int i = range.getLowerBound();
                          i <= Math.min(context.getPlots().size(), range.getUpperBound());
                          i++) {
                        region.addPlot(this.channels, context.getPlot(i));
                      }
                    }
                  } else {
                    runNoTeam(sender);
                  }
                }));
    ChunkCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withArguments(new StringArgument("region-name"))
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  Region region = team.getRegion((String) args[0]);
                  if (region != null) {
                    if (team.getOwnerUUID().equals(gamer.getUuid())) {
                      IntegerRange range = (IntegerRange) args[1];
                      for (int i = range.getLowerBound();
                           i <= Math.min(context.getPlots().size(), range.getUpperBound());
                           i++) {
                        region.removePlot(this.channels, context.getPlot(i));
                      }
                    }
                  } else {
                    runNoTeam(sender);
                  }
                }));

    ChunkCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("create");
  }
  void runNoTeam(Player sender){
    sender.sendMessage("you must be in a team to manage regions");
  }
}

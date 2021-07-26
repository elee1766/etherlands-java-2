package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.Menus.RegionPrinter;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Group;
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
    CommandAPICommand RegionCommand =
        new CommandAPICommand("region")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand);
    RegionCommand.withSubcommand(
        new CommandAPICommand("help")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand));
    RegionCommand.withSubcommand(
        new CommandAPICommand("create")
            .withArguments(cleanNameArgument("regionname"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  if (team.isManager(gamer)) {
                    team.createRegion(this.channels, (String) args[0]);
                  }else{
                    sender.sendMessage("ur not manager");
                  }
                }));
    RegionCommand.withSubcommand(
        new CommandAPICommand("delete")
            .withArguments(teamRegionArgument("regionname"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  if (team.isManager(gamer)) {
                    team.deleteRegion(this.channels, (Region) args[0]);
                  }else{
                    sender.sendMessage("ur not manager");
                  }
                }));
    RegionCommand.withSubcommand(
        new CommandAPICommand("add")
            .withAliases("addPlot")
            .withArguments(teamRegionArgument("regionname"))
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  if (team.isManager(gamer)) {
                    Region region = (Region) args[0];
                      IntegerRange range = (IntegerRange) args[1];
                      for (int i = range.getLowerBound();
                          i <= Math.min(context.getPlots().size(), range.getUpperBound());
                          i++) {
                        if (team.getPlots().contains(i)) {
                          region.addPlot(this.channels, context.getPlot(i));
                        }
                      }
                    }
                }));
    RegionCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withAliases("removePlot")
            .withArguments(teamRegionArgument("region-name"))
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  if (team.isManager(gamer)) {
                  Region region = team.getRegion((String) args[0]);
                    IntegerRange range = (IntegerRange) args[1];
                  for (int i = range.getLowerBound();
                      i <= Math.min(context.getPlots().size(), range.getUpperBound());
                      i++) {
                      region.removePlot(this.channels, context.getPlot(i));
                    }
                  }
                }));
    RegionCommand.withSubcommand(
        new CommandAPICommand("set_player")
            .withAliases("setp","setplayer","setPlayer")
            .withArguments(teamRegionArgument("region"))
            .withArguments(teamMemberArgument("member"))
            .withArguments(accessFlagArgument("flag"))
            .withArguments(flagValueArgument("value"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer manager = context.getGamer(sender.getUniqueId());
                  Team team = manager.getTeamObject();
                  if(team.isManager(manager)){
                    Region region = (Region) args[0];
                    Gamer member = (Gamer) args[1];
                    AccessFlags flag = (AccessFlags) args[2];
                    FlagValue value = (FlagValue) args[3];
                    region.setGamerPermission(channels,member,flag,value);
                  }
                }));
    RegionCommand.withSubcommand(
        new CommandAPICommand("set_group")
            .withAliases("setg","setgroup","setGroup")
            .withArguments(teamRegionArgument("region"))
            .withArguments(teamGroupArgument("group"))
            .withArguments(accessFlagArgument("flag"))
            .withArguments(flagValueArgument("value").replaceSuggestions(info->getFlagValueStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer manager = context.getGamer(sender.getUniqueId());
                  Team team = manager.getTeamObject();
                  if(team.isManager(manager)){
                    Region region = (Region) args[0];
                    Group member = (Group) args[1];
                    AccessFlags flag = (AccessFlags) args[2];
                    FlagValue value = (FlagValue) args[3];
                    region.setGroupPermission(channels,member,flag,value);
                  }
                }));
    RegionCommand.withSubcommand(
            new CommandAPICommand("info")
                    .withArguments(new StringArgument("region").replaceSuggestions(info->getTeamStrings()))//make this suggest groups
                    .withPermission("etherlands.public")
                    .executesPlayer(
                            (sender, args) -> {
                              Player player = sender.getPlayer();
                              Gamer gamer = context.getGamer(sender.getUniqueId());
                              Team team = context.getTeam(gamer.getTeamName());
                              RegionPrinter printer = new RegionPrinter(team.getRegions());
                              printer.printRegion(sender);
                            }));

    RegionCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("create");
  }

  void runNoTeam(Player sender) {
    sender.sendMessage("you must be in a team to manage regions");
  }
}

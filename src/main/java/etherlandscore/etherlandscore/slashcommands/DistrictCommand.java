package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.Menus.DistrictPrinter;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.DistrictSender;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class DistrictCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public DistrictCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage("create");
  }

  void noTeam(Player sender) {
    sender.sendMessage("you must be in a team to manage districts");
  }

  void create(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      TeamSender.createDistrict(this.channels, (String) args[0], writeTeam);
    } else {
      sender.sendMessage("ur not manager");
    }
  }

  void delete(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      TeamSender.deleteDistrict(this.channels, (District) args[0], writeTeam);
    } else {
      sender.sendMessage("ur not manager");
    }
  }

  void add(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      District writeDistrict = (District) args[0];
      IntegerRange range = (IntegerRange) args[1];
      for (int i = range.getLowerBound();
           i <= Math.min(context.getPlots().size(), range.getUpperBound());
           i++) {
        if (writeTeam.getPlots().contains(i)) {
          DistrictSender.addPlot(this.channels, context.getPlot(i), writeDistrict);
        }
      }
    }
  }

  void remove(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      District writeDistrict = writeTeam.getDistrict((String) args[0]);
      IntegerRange range = (IntegerRange) args[1];
      for (int i = range.getLowerBound();
           i <= Math.min(context.getPlots().size(), range.getUpperBound());
           i++) {
        DistrictSender.removePlot(this.channels, context.getPlot(i), writeDistrict);
      }
    }
  }

  void setPlayer(Player sender, Object[] args){
    Gamer manager = context.getGamer(sender.getUniqueId());
    Team writeTeam = manager.getTeamObject();
    if (writeTeam.isManager(manager)) {
      District writeDistrict = (District) args[0];
      Gamer member = (Gamer) args[1];
      AccessFlags flag = (AccessFlags) args[2];
      FlagValue value = (FlagValue) args[3];
      DistrictSender.setGamerPermission(channels, member, flag, value, writeDistrict);
    }
  }

  void setGroup(Player sender, Object[] args){
    Gamer manager = context.getGamer(sender.getUniqueId());
    Team writeTeam = manager.getTeamObject();
    if (writeTeam.isManager(manager)) {
      District writeDistrict = (District) args[0];
      Group member = (Group) args[1];
      AccessFlags flag = (AccessFlags) args[2];
      FlagValue value = (FlagValue) args[3];
      DistrictSender.setGroupPermission(channels, member, flag, value, writeDistrict);
    }
  }

  void info(Player sender, Object[] args){
    Player player = sender.getPlayer();
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = context.getTeam(gamer.getTeamName());
    DistrictPrinter printer = new DistrictPrinter(writeTeam.getDistricts());
    printer.printDistrict(sender);
  }

  public void register() {
    CommandAPICommand DistrictCommand =
        new CommandAPICommand("district")
            .withPermission("etherlands.public")
            .executesPlayer(this::help);
    DistrictCommand.withSubcommand(
        new CommandAPICommand("help")
            .executesPlayer(this::help));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("create")
            .withArguments(cleanNameArgument("districtname"))
            .executesPlayer(this::create));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("delete")
            .withArguments(teamDistrictArgument("districtname"))
            .executesPlayer(this::delete));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("add")
            .withAliases("addPlot")
            .withArguments(teamDistrictArgument("districtname"))
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .executesPlayer(this::add));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withAliases("removePlot")
            .withArguments(teamDistrictArgument("district-name"))
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .executesPlayer(this::remove));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("set_player")
            .withAliases("setp", "setplayer", "setPlayer")
            .withArguments(teamDistrictArgument("district"))
            .withArguments(teamMemberArgument("member"))
            .withArguments(accessFlagArgument("flag"))
            .withArguments(flagValueArgument("value"))
            .executesPlayer(this::setPlayer));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("set_group")
            .withAliases("setg", "setgroup", "setGroup")
            .withArguments(teamDistrictArgument("district"))
            .withArguments(teamGroupArgument("group"))
            .withArguments(accessFlagArgument("flag"))
            .withArguments(flagValueArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(this::setGroup));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("info")
            .executesPlayer(this::info));

    DistrictCommand.register();
  }
}

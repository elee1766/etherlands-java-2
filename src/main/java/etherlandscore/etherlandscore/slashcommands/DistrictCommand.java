package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
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
import etherlandscore.etherlandscore.state.write.WriteDistrict;
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

  void add(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    System.out.println(gamer.getUuid());
    System.out.println(writeTeam.getOwnerUUID());
    if (writeTeam.isManager(gamer)) {
      District writeDistrict = (District) args[0];
      IntegerRange range = (IntegerRange) args[1];
      for (int i = range.getLowerBound(); i <= (range.getUpperBound()); i++) {
        if (writeTeam.getPlots().contains(i)) {
          DistrictSender.addPlot(this.channels, context.getPlot(i), writeDistrict);
        }
      }
      sender.sendMessage("Plots " + args[1] + " have been added to district");
    }else {
      sender.sendMessage("You are not a manager");
    }
  }

  void create(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      sender.sendMessage(args[0] + " district has been created");
      TeamSender.createDistrict(this.channels, (String) args[0], writeTeam);
    } else {
      sender.sendMessage("You are not manager");
    }
  }

  void delete(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      TeamSender.deleteDistrict(this.channels, (District) args[0], writeTeam);
      sender.sendMessage(args[0] + " district has been deleted");
    } else {
      sender.sendMessage("You are not manager");
    }
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage("create");
  }

  void info(Player sender, Object[] args) {
    Player player = sender.getPlayer();
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = context.getTeam(gamer.getTeam());
    DistrictPrinter printer = new DistrictPrinter(writeTeam.getDistricts());
    printer.printDistrict(sender);
  }

  void noTeam(Player sender) {
    sender.sendMessage("You must be in a team to manage districts");
  }

  public void register() {
    CommandAPICommand DistrictCommand =
        new CommandAPICommand("district")
            .withPermission("etherlands.public")
            .executesPlayer(this::help);
    DistrictCommand.withSubcommand(new CommandAPICommand("help").executesPlayer(this::help));
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
            .withArguments(
                flagValueArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(this::setGroup));
    DistrictCommand.withSubcommand(new CommandAPICommand("info").executesPlayer(this::info));

    DistrictCommand.register();
  }

  void remove(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      District writeDistrict = (WriteDistrict) args[0];
      IntegerRange range = (IntegerRange) args[1];
      for (int i = range.getLowerBound();
          i <= range.getUpperBound();
          i++) {
        DistrictSender.removePlot(this.channels, context.getPlot(i), writeDistrict);
      }
      sender.sendMessage(args[0] + " plot has been removed");
    } else {
      sender.sendMessage("You not a manager");
    }
  }

  void setGroup(Player sender, Object[] args) {
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

  void setPlayer(Player sender, Object[] args) {
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
}

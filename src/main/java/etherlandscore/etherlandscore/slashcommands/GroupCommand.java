package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.Menus.GroupPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.GroupSender;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class GroupCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public GroupCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void add(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer subject = (Gamer) args[0];
    Group writeGroup = (Group) args[1];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam != null) {
      if (writeTeam.canAction(manager, subject)) {
        if (subject.getTeam().equals(writeTeam.getName())) {
          GroupSender.addMember(channels, writeGroup, subject);
          sender.sendMessage(
              subject.getPlayer().getName() + " has been added to " + writeGroup.getName());
        } else {
          sender.sendMessage(subject.getPlayer().getName() + " is not in your team");
        }
      } else {
        sender.sendMessage("You are not a manager");
      }
    } else {
      runNoTeam(sender);
    }
  }

  void create(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      TeamSender.createGroup(this.channels, (String) args[0], writeTeam);
      sender.sendMessage(args[0] + " has been created");
    } else {
      sender.sendMessage("You are not manager");
    }
  }

  void delete(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      TeamSender.deleteGroup(this.channels, (String) args[0], writeTeam);
      sender.sendMessage(args[0] + " has been deleted");
    } else {
      sender.sendMessage("You are not manager");
    }
  }

  void info(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    GroupPrinter printer = new GroupPrinter((Group) args[0]);
    printer.printGroup(sender);
  }

  public void register() {
    CommandAPICommand GroupCommand =
        new CommandAPICommand("group")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand);
    GroupCommand.withSubcommand(new CommandAPICommand("help").executesPlayer(this::runHelpCommand));
    GroupCommand.withSubcommand(
        new CommandAPICommand("create")
            .withArguments(cleanNameArgument("groupname"))
            .executesPlayer(this::create));
    GroupCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(teamGroupArgument("group"))
            .executesPlayer(this::info));
    GroupCommand.withSubcommand(
        new CommandAPICommand("delete")
            .withArguments(cleanNameArgument("groupname"))
            .executesPlayer(this::delete));
    GroupCommand.withSubcommand(
        new CommandAPICommand("add")
            .withAliases("addPlayer")
            .withArguments(teamMemberArgument("player"))
            .withArguments(teamGroupArgument("group"))
            .executesPlayer(this::add));
    GroupCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withAliases("removePlayer")
            .withArguments(teamMemberArgument("player"))
            .withArguments(teamGroupArgument("group"))
            .executesPlayer(this::remove));

    GroupCommand.register();
  }

  void remove(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer subject = (Gamer) args[0];
    Group writeGroup = (Group) args[1];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam != null) {
      if (writeTeam.canAction(manager, subject)) {
        if (subject.getTeam().equals(writeTeam.getName())) {
          GroupSender.removeMember(channels, writeGroup, subject);
          sender.sendMessage(
              subject.getPlayer().getName() + " has been removed from " + writeGroup.getName());
        } else {
          sender.sendMessage(subject.getPlayer().getName() + " is not in your team");
        }
      } else {
        sender.sendMessage("You are not a manager");
      }
    } else {
      runNoTeam(sender);
    }
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("create");
  }

  void runNoTeam(Player sender) {
    sender.sendMessage("You must be in a team to manage groups");
  }
}

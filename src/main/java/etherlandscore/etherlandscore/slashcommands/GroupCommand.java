package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.Menus.GroupPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.GroupSender;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class GroupCommand extends CommandProcessor {
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
    GroupPrinter printer = new GroupPrinter((Group) args[0], fiber, channels);
    printer.printGroup(sender);
  }

  public void register() {
    CommandAPICommand GroupCommand =
        createPlayerCommand("group",SlashCommands.help,this::runHelpCommand)
            .withAliases("gr")
            .withPermission("etherlands.public");

    GroupCommand.withSubcommand(
        createPlayerCommand("help",SlashCommands.help)
    );
    GroupCommand.withSubcommand(
        createPlayerCommand("create",SlashCommands.create,this::create)
            .withAliases("cre")
            .withArguments(cleanNameArgument("groupname"))
    );
    GroupCommand.withSubcommand(
        createPlayerCommand("info",SlashCommands.info,this::info)
            .withAliases("i")
            .withArguments(teamGroupArgument("group"))
    );
    GroupCommand.withSubcommand(
        createPlayerCommand("delete",SlashCommands.delete,this::delete)
            .withAliases("del")
            .withArguments(cleanNameArgument("groupname"))
    );
    GroupCommand.withSubcommand(
        createPlayerCommand("add",SlashCommands.add,this::add)
            .withAliases("addPlayer")
            .withArguments(teamMemberArgument("player"))
            .withArguments(teamGroupArgument("group"))
    );
    GroupCommand.withSubcommand(
        createPlayerCommand("remove", SlashCommands.remove,this::remove)
            .withAliases("removePlayer")
            .withArguments(teamMemberArgument("player"))
            .withArguments(teamGroupArgument("group"))
    );

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

package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.GroupPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.GroupSender;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import org.bukkit.Bukkit;
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


  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("create");
  }

  void runNoTeam(Player sender) {
    sender.sendMessage("you must be in a team to manage groups");
  }

  void create(Player sender, Object[] args){
    Bukkit.getLogger().info("Running /group create");
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      TeamSender.createGroup(this.channels, (String) args[0], writeTeam);
    } else {
      sender.sendMessage("ur not manager");
    }
    Bukkit.getLogger().info("/group create complete");
  }

  void info(Player sender, Object[] args){
    Gamer gamer = context.getGamer(sender.getUniqueId());
    GroupPrinter printer = new GroupPrinter((Group) args[0]);
    printer.printGroup(sender);
  }

  void delete(Player sender, Object[] args){
    Bukkit.getLogger().info("running /group delete");
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    if (writeTeam.isManager(gamer)) {
      TeamSender.deleteGroup(this.channels, (String) args[0], writeTeam);
    } else {
      sender.sendMessage("ur not manager");
    }
    Bukkit.getLogger().info("/group delete complete");
  }

  void add(Player sender, Object[] args){
    Bukkit.getLogger().info("running /group add");
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer subject = (Gamer) args[0];
    Group writeGroup = (Group) args[1];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam != null) {
      if (writeTeam.canAction(manager, subject)) {
        if (subject.getTeamName().equals(writeTeam.getName())) {
          GroupSender.addMember(channels, writeGroup, subject);
        }
      }
    } else {
      runNoTeam(sender);
    }
    Bukkit.getLogger().info("/group add complete");
  }

  void remove(Player sender, Object[] args){
    Bukkit.getLogger().info("running /group remove");
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer subject = (Gamer) args[0];
    Group writeGroup = (Group) args[1];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam != null) {
      if (writeTeam.canAction(manager, subject)) {
        if (subject.getTeamName().equals(writeTeam.getName())) {
          GroupSender.removeMember(channels, writeGroup, subject);
        }
      }
    } else {
      runNoTeam(sender);
    }
    Bukkit.getLogger().info("/group remove complete");
  }

  public void register() {
    CommandAPICommand GroupCommand =
        new CommandAPICommand("group")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand);
    GroupCommand.withSubcommand(
        new CommandAPICommand("help")
            .executesPlayer(this::runHelpCommand));
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
}

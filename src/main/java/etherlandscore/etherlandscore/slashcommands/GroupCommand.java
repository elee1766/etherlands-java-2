package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.GamerPrinter;
import etherlandscore.etherlandscore.Menus.GroupPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Group;
import etherlandscore.etherlandscore.state.Team;
import etherlandscore.etherlandscore.stateWrites.GroupWrites;
import etherlandscore.etherlandscore.stateWrites.TeamWrites;
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

  public void register() {
    CommandAPICommand GroupCommand =
        new CommandAPICommand("group")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand);
    GroupCommand.withSubcommand(
        new CommandAPICommand("help")
            .withPermission("etherlands.public")
            .executesPlayer(this::runHelpCommand));
    GroupCommand.withSubcommand(
        new CommandAPICommand("create")
            .withArguments(cleanNameArgument("groupname"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  if (team.isManager(gamer)) {
                    TeamWrites.createGroup(this.channels, (String) args[0], team);
                  } else {
                    sender.sendMessage("ur not manager");
                  }
                }));
    GroupCommand.withSubcommand(
            new CommandAPICommand("info")
                    .withArguments(new StringArgument("group").replaceSuggestions(info->getTeamStrings()))//make this suggest groups
                    .withPermission("etherlands.public")
                    .executesPlayer(
                            (sender, args) -> {
                              Player player = sender.getPlayer();
                              Gamer gamer = context.getGamer(sender.getUniqueId());
                              Group group = context.getTeam(gamer.getTeamName()).getGroup((String) args[0]);
                              GroupPrinter printer = new GroupPrinter(group);
                              printer.printGroup(sender);
                            }));

    GroupCommand.withSubcommand(
        new CommandAPICommand("delete")
            .withArguments(cleanNameArgument("groupname"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  if (team.isManager(gamer)) {
                    TeamWrites.deleteGroup(this.channels, (String) args[0], team);
                  } else {
                    sender.sendMessage("ur not manager");
                  }
                }));
    GroupCommand.withSubcommand(
        new CommandAPICommand("add")
            .withAliases("addPlayer")
            .withArguments(teamMemberArgument("player"))
            .withArguments(teamGroupArgument("group"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer manager = context.getGamer(sender.getUniqueId());
                  Gamer subject = (Gamer) args[0];
                  Group group = (Group) args[1];
                  Team team = manager.getTeamObject();
                  if (team != null) {
                    if (team.canAction(manager, subject)) {
                      if (subject.getTeamName().equals(team.getName())) {
                        GroupWrites.addMember(channels, group, subject);
                      }
                    }
                  } else {
                    runNoTeam(sender);
                  }
                }));
    GroupCommand.withSubcommand(
        new CommandAPICommand("remove")
            .withAliases("removePlayer")
            .withArguments(teamMemberArgument("player"))
            .withArguments(teamGroupArgument("group"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer manager = context.getGamer(sender.getUniqueId());
                  Gamer subject = (Gamer) args[0];
                  Group group = (Group) args[1];
                  Team team = manager.getTeamObject();
                  if (team != null) {
                    if (team.canAction(manager, subject)) {
                      if (subject.getTeamName().equals(team.getName())) {
                        GroupWrites.removeMember(channels, group, subject);
                      }
                    }
                  } else {
                    runNoTeam(sender);
                  }
                }));



    GroupCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("create");
  }

  void runNoTeam(Player sender) {
    sender.sendMessage("you must be in a team to manage groups");
  }
}

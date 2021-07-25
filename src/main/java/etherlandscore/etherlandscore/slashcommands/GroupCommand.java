package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Group;
import etherlandscore.etherlandscore.state.Team;
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
                    team.createGroup(this.channels, (String) args[0]);
                  } else {
                    sender.sendMessage("ur not manager");
                  }
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
                    team.deleteGroup(this.channels, (String) args[0]);
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
                        group.addMember(channels, subject);
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
                        group.removeMember(channels, subject);
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

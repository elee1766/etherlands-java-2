package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.GroupPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.readonly.ReadGamer;
import etherlandscore.etherlandscore.readonly.ReadGroup;
import etherlandscore.etherlandscore.readonly.ReadTeam;
import etherlandscore.etherlandscore.services.ListenerClient;
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
                  ReadGamer gamer = context.getGamer(sender.getUniqueId());
                  ReadTeam team = gamer.getTeamObject();
                  if (team.isManager(gamer)) {
                    team.createGroup(this.channels, (String) args[0]);
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
                              ReadGamer gamer = context.getGamer(sender.getUniqueId());
                              ReadGroup group = context.getTeam(gamer.getTeamName()).getGroup((String) args[0]);
                              GroupPrinter printer = new GroupPrinter(group.obj());
                              printer.printGroup(sender);
                            }));

    GroupCommand.withSubcommand(
        new CommandAPICommand("delete")
            .withArguments(cleanNameArgument("groupname"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  ReadGamer gamer = context.getGamer(sender.getUniqueId());
                  ReadTeam team = gamer.getTeamObject();
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
                  ReadGamer manager = context.getGamer(sender.getUniqueId());
                  ReadGamer subject = new ReadGamer(args[0]);
                  ReadGroup group = new ReadGroup(args[1]);
                  ReadTeam team = manager.getTeamObject();
                  if (team != null) {
                    if (team.canAction(manager, subject)) {
                      if (subject.teamIs(team)) {
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
                  ReadGamer manager = context.getGamer(sender.getUniqueId());
                  ReadGamer subject = new ReadGamer(args[0]);
                  ReadGroup group = new ReadGroup(args[1]);
                  ReadTeam team = manager.getTeamObject();
                    if (subject.teamIs(team)) {
                      if (team.canAction(manager, subject)) {
                        group.removeMember(channels, subject);
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

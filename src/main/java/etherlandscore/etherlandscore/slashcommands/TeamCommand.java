package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.Menus.GamerPrinter;
import etherlandscore.etherlandscore.Menus.TeamPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.state.Team;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class TeamCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final Map<String, Map<UUID, Long>> invites = new HashMap<>();

  public TeamCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void register() {
    CommandAPICommand TeamCommand =
        new CommandAPICommand("team")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  sender.sendMessage("create info invite join");
                });
    TeamCommand.withSubcommand(
        new CommandAPICommand("help")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  sender.sendMessage("create info invite join delete leave kick delegate");
                }));
    TeamCommand.withSubcommand(
        new CommandAPICommand("info")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  if (!gamer.hasTeam()) {
                    team_info(sender, gamer.getTeamObject());
                  } else {
                    sender.sendMessage("/team info <teamname>");
                  }
                }));
    TeamCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(new StringArgument("team").replaceSuggestions(info -> getTeamStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Player player = (Player)args[0];
                  TeamPrinter printer = new TeamPrinter(context.getTeam((String) args[0]));
                  printer.printTeam(sender);
                }));

    TeamCommand.withSubcommand(
        new CommandAPICommand("create")
            .withArguments(cleanNameArgument("teamname"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  if (context.hasTeam((String) args[0])) {
                    sender.sendMessage("a team already exists by that name");
                    return;
                  }
                  if (context.hasGamer(sender.getUniqueId())) {
                    context.createTeam(this.channels, state().getGamer(sender.getUniqueId()), (String) args[0]);
                    sender.sendMessage("team created!");
                  }
                }));
    TeamCommand.withSubcommand(
        new CommandAPICommand("invite")
            .withArguments(
                new PlayerArgument("player").replaceSuggestions(info -> getOnlinePlayerStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer inviter = context.getGamer(sender.getUniqueId());
                  Gamer receiver = context.getGamer(((Player) args[0]).getUniqueId());
                  if (inviter != null) {
                    Team team = inviter.getTeamObject();
                    if (team != null) {
                      if (team.canInvite(inviter)) {
                        if (!this.invites.containsKey(team.getName())) {
                          this.invites.put(team.getName(), new HashMap<>());
                        }
                        team.inviteGamer(this.invites.get(team.getName()), receiver.getUuid());
                        receiver
                            .getPlayer()
                            .sendMessage("you have been invited to " + inviter.getTeamName());
                        receiver
                            .getPlayer()
                            .sendMessage(
                                "send command \"/team join "
                                    + inviter.getTeamName()
                                    + "\" to join");
                      }
                    }
                  }
                }));
    TeamCommand.withSubcommand(
        new CommandAPICommand("join")
            .withArguments(new StringArgument("team").replaceSuggestions(info -> getTeamStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer joiner = context.getGamer(sender.getUniqueId());
                  if (joiner != null) {
                    Team team = context.getTeam((String) args[0]);
                    if (team != null) {
                      if (team.canJoin(
                          this.invites.getOrDefault(team.getName(), new HashMap<>()), joiner)) {
                        team.addMember(this.channels, joiner);
                        sender.sendMessage("welcome to " + args[0]);
                      } else {
                        sender.sendMessage("you must be invited before joining " + args[0]);
                      }
                    }
                  }
                }));
    TeamCommand.withSubcommand(
        new CommandAPICommand("leave")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  if (!gamer.getTeamName().equals("")) {
                    Team team = context.getTeam(gamer.getTeamName());
                    if (team.getOwnerUUID().equals(gamer.getUuid())) {
                      sender.sendMessage("you cannot leave the team you own");
                    } else {
                      team.removeMember(channels, gamer);
                      sender.sendMessage("you have left " + gamer.getTeamName());
                    }
                  } else {
                    sender.sendMessage("you are not in a team");
                  }
                }));
    TeamCommand.withSubcommand(
        new CommandAPICommand("kick")
            .withPermission("etherlands.public")
            .withArguments(teamMemberArgument("member"))
            .executesPlayer(
                (sender, args) -> {
                  Gamer manager = context.getGamer(sender.getUniqueId());
                  Gamer kicked = (Gamer) args[0];
                  Team team = manager.getTeamObject();
                  if (team.isManager(manager)) {
                    if (!team.isManager(kicked)) {
                      team.removeMember(channels, kicked);
                      sender.sendMessage("you kicked " + kicked.getPlayer().getName());
                    }
                    {
                      sender.sendMessage("cant kick manager");
                    }
                  } else {
                    sender.sendMessage("you must be manager of a team to kick players");
                  }
                }));

    TeamCommand.withSubcommand(
        new CommandAPICommand("kick")
            .withPermission("etherlands.public")
            .withArguments(teamMemberArgument("member"))
            .executesPlayer(
                (sender, args) -> {
                  Gamer manager = context.getGamer(sender.getUniqueId());
                  Gamer kicked = (Gamer) args[0];
                  Team team = manager.getTeamObject();
                  if (team.isOwner(manager)) {
                    team.removeMember(channels, kicked);
                    sender.sendMessage("you kicked " + kicked.getPlayer().getName());
                    return;
                  }
                  if (team.isManager(manager)) {
                    if (!team.isManager(kicked)) {
                      team.removeMember(channels, kicked);
                      sender.sendMessage("you kicked " + kicked.getPlayer().getName());
                    }
                    {
                      sender.sendMessage("cant kick manager");
                    }
                  } else {
                    sender.sendMessage("you must be manager of a team to kick players");
                  }
                }));

    TeamCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  IntegerRange range = (IntegerRange) args[0];
                  for (int i = range.getLowerBound();
                      i <= Math.min(context.getPlots().size(), range.getUpperBound());
                      i++) {
                    if (context.getPlot(i).getOwner().equals(gamer.getUuid())) {
                      team.delegatePlot(this.channels, context.getPlot(i));
                    }
                  }
                }));
    TeamCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  Chunk chunk = gamer.getPlayer().getChunk();
                  Plot plot = context.getPlot(chunk.getX(), chunk.getZ());
                  if (plot.getOwner().equals(gamer.getUuid())) {
                    team.delegatePlot(this.channels, plot);
                  }
                }));

    TeamCommand.withSubcommand(
        new CommandAPICommand("delete")
            .withPermission("etherlands.public")
            .withArguments(new StringArgument("teamname"))
            .executesPlayer(
                (sender, args) -> {
                  Gamer manager = context.getGamer(sender.getUniqueId());
                  String name = (String) args[0];
                  Team team = manager.getTeamObject();
                  if (team.isOwner(manager)) {
                    if (manager.getTeamObject().getName().equals(name)) {
                      team.delete(channels);
                    }
                  }
                }));

    TeamCommand.register();
  }

  private void team_info(CommandSender sender, Team team) {
    if(team==null){
      return;
    }
    sender.sendMessage("team name:" + team.getName());
    sender.sendMessage("team owner:" + team.getOwner());
  }
}

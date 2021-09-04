package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.Menus.TeamPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
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

  void create(Player sender, Object[] args) {
    if (context.hasTeam((String) args[0])) {
      sender.sendMessage("A team already exists by that name");
      return;
    }
    if (context.hasGamer(sender.getUniqueId())) {
      if(context.getGamer(sender.getUniqueId()).hasTeam()){
        sender.sendMessage("You are already in a team");
        return;
      }
      channels.master_command.publish(new Message<>(MasterCommand.team_create_team, state().getGamer(sender.getUniqueId()), args[0]));
      sender.sendMessage("Team created!");
    }
  }

  void delegateLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    Chunk chunk = gamer.getPlayer().getChunk();
    District writeDistrict = context.getDistrict(chunk.getX(), chunk.getZ());
    if (writeDistrict.getOwnerUUID().equals(gamer.getUuid())) {
      TeamSender.delegateDistrict(this.channels, writeDistrict, writeTeam);
      sender.sendMessage(
          "District: " + writeDistrict.getIdInt() + " has been delegated to " + writeTeam.getName());
    } else {
      sender.sendMessage("You do not own this plot");
    }
  }

  void delegatePlot(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    IntegerRange range = (IntegerRange) args[0];
    for (int i = range.getLowerBound();
        i <= Math.min(context.getPlots().size(), range.getUpperBound());
        i++) {
      if (context.getPlot(i).getOwnerUUID().equals(gamer.getUuid())) {
        TeamSender.delegateDistrict(this.channels, context.getDistrict(i), writeTeam);
        sender.sendMessage("Plot: " + i + " has been delegated to " + writeTeam.getName());
      } else {
        sender.sendMessage("You do not own this plot");
      }
    }
  }

  void deleteTeam(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    String name = (String) args[0];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam.isOwner(manager)) {
      if (manager.getTeamObject().getName().equals(name)) {
        TeamSender.delete(channels, writeTeam);
        sender.sendMessage("Team has been deleted");
      } else {
        sender.sendMessage("You are not a manager");
      }
    }
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage("Create Info Invite Join Delete Leave Lick Delegate");
  }

  void info(Player sender, Object[] args) {
    TeamPrinter printer = new TeamPrinter(context.getTeam((String) args[0]));
    printer.printTeam(sender);
  }

  void infoLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (gamer.hasTeam()) {
      TeamPrinter printer = new TeamPrinter(gamer.getTeamObject());
      printer.printTeam(sender);
    } else {
      sender.sendMessage("/team info <teamname>");
    }
  }

  void invite(Player sender, Object[] args) {
    Gamer inviter = context.getGamer(sender.getUniqueId());
    Gamer receiver = context.getGamer(((Player) args[0]).getUniqueId());
    if (inviter != null) {
      Team writeTeam = inviter.getTeamObject();
      if (writeTeam != null) {
        if (writeTeam.canInvite(inviter)) {
          if (!this.invites.containsKey(writeTeam.getName())) {
            this.invites.put(writeTeam.getName(), new HashMap<>());
          }
          writeTeam.inviteGamer(this.invites.get(writeTeam.getName()), receiver.getUuid());
          receiver.getPlayer().sendMessage("You have been invited to " + inviter.getTeam());
          TextComponent join = new TextComponent("click here to join");
          join.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/team join " + inviter.getTeam()));
          receiver.getPlayer().sendMessage(join);
          receiver
              .getPlayer()
              .sendMessage("Or send command \"/team join " + inviter.getTeam() + "\" to join");
        }
      }
    }
  }

  void join(Player sender, Object[] args) {
    Gamer joiner = context.getGamer(sender.getUniqueId());
    if (joiner != null) {
      Team team = context.getTeam((String) args[0]);
      if (team != null) {
        if (team.canJoin(this.invites.getOrDefault(team.getName(), new HashMap<>()), joiner)) {
          TeamSender.addMember(this.channels, joiner, team);
          sender.sendMessage("Welcome to " + args[0]);
        } else {
          sender.sendMessage("You must be invited before joining " + args[0]);
        }
      }
    }
  }

  void kick(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer kicked = (Gamer) args[0];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam.isManager(manager)) {
      if (!writeTeam.isManager(kicked)) {
        TeamSender.removeMember(channels, kicked, writeTeam);
        sender.sendMessage("You kicked " + kicked.getPlayer().getName());
      }
      {
        sender.sendMessage("Can't kick manager");
      }
    } else {
      sender.sendMessage("You must be manager of a team to kick players");
    }
  }

  void kickOwner(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer kicked = (Gamer) args[0];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam.isOwner(manager)) {
      TeamSender.removeMember(channels, kicked, writeTeam);
      sender.sendMessage("You kicked " + kicked.getPlayer().getName());
      return;
    }
    if (writeTeam.isManager(manager)) {
      if (!writeTeam.isManager(kicked)) {
        TeamSender.removeMember(channels, kicked, writeTeam);
        sender.sendMessage("You kicked " + kicked.getPlayer().getName());
      }
      {
        sender.sendMessage("Can't kick manager");
      }
    } else {
      sender.sendMessage("You must be manager of a team to kick players");
    }
  }

  void leave(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (!gamer.getTeam().equals("")) {
      Team writeTeam = context.getTeam(gamer.getTeam());
      if (writeTeam.getOwnerUUID().equals(gamer.getUuid())) {
        sender.sendMessage("You cannot leave the team you own");
      } else {
        TeamSender.removeMember(channels, gamer, writeTeam);
        sender.sendMessage("You have left " + gamer.getTeam());
      }
    } else {
      sender.sendMessage("You are not in a team");
    }
  }

  public void register() {
    CommandAPICommand TeamCommand =
        new CommandAPICommand("team")
            .withPermission("etherlands.public")
            .executesPlayer(this::infoLocal);
    TeamCommand.withSubcommand(new CommandAPICommand("help").executesPlayer(this::help));
    TeamCommand.withSubcommand(new CommandAPICommand("info").executesPlayer(this::infoLocal));
    TeamCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(new StringArgument("team").replaceSuggestions(info -> getTeamStrings()))
            .executesPlayer(this::info));
    TeamCommand.withSubcommand(
        new CommandAPICommand("create")
            .withArguments(cleanNameArgument("teamname"))
            .executesPlayer(this::create));
    TeamCommand.withSubcommand(
        new CommandAPICommand("invite")
            .withArguments(
                new PlayerArgument("player").replaceSuggestions(info -> getOnlinePlayerStrings()))
            .executesPlayer(this::invite));
    TeamCommand.withSubcommand(
        new CommandAPICommand("join")
            .withArguments(new StringArgument("team").replaceSuggestions(info -> getTeamStrings()))
            .executesPlayer(this::join));
    TeamCommand.withSubcommand(new CommandAPICommand("leave").executesPlayer(this::leave));
    TeamCommand.withSubcommand(
        new CommandAPICommand("kick")
            .withArguments(teamMemberArgument("member"))
            .executesPlayer(this::kick));
    TeamCommand.withSubcommand(
        new CommandAPICommand("kick")
            .withArguments(teamMemberArgument("member"))
            .executesPlayer(this::kickOwner));
    TeamCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withArguments(new IntegerRangeArgument("plot-ids"))
            .executesPlayer(this::delegatePlot));
    TeamCommand.withSubcommand(
        new CommandAPICommand("delegate").executesPlayer(this::delegateLocal));
    TeamCommand.withSubcommand(
        new CommandAPICommand("delete")
            .withArguments(new StringArgument("teamname"))
            .executesPlayer(this::deleteTeam));

    TeamCommand.register();
  }
}

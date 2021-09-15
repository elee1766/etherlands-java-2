package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.TeamPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.GroupSender;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import etherlandscore.etherlandscore.state.write.WriteGroup;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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
  private TextComponent response = new TextComponent("");

  public TeamCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void create(Player sender, Object[] args) {
    if (context.hasTeam((String) args[0])) {
      response.setText("A team already exists by that name");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      return;
    }
    if (context.hasGamer(sender.getUniqueId())) {
      if(context.getGamer(sender.getUniqueId()).hasTeam()){
        response.setText("You are already in a team");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
        return;
      }
      response.setText("Team created!");
      channels.master_command.publish(new Message<>(MasterCommand.team_create_team, state().getGamer(sender.getUniqueId()), args[0]));
    }
  }

  void delegateLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
    Chunk chunk = gamer.getPlayer().getChunk();
    District writeDistrict = context.getDistrict(chunk.getX(), chunk.getZ());
    if (writeDistrict.getOwnerUUID().equals(gamer.getUuid())) {
      TeamSender.delegateDistrict(this.channels, writeDistrict, writeTeam, new Message(ChatTarget.team_delegate_district, gamer, writeDistrict));
      response.setText(
          "District: " + writeDistrict.getIdInt() + " has been delegated to " + writeTeam.getName());
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
    } else {
      response.setText("You do not own this plot");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
    }
  }

  void delegateDistrict(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team writeTeam = gamer.getTeamObject();
     Integer i = (Integer) args[0];
    if(writeTeam == null){
      response.setText("ur not in a team");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      return;
    }
      if (context.getDistrict(i).getOwnerUUID().equals(gamer.getUuid())) {
        TeamSender.delegateDistrict(this.channels, context.getDistrict(i), writeTeam, new Message(ChatTarget.team_delegate_district, gamer, context.getDistrict(i)));
        response.setText("District: " + i + " has been delegated to " + writeTeam.getName());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      } else {
        response.setText("You do not own this plot");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      }
  }

  void deleteTeam(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    String name = (String) args[0];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam.isOwner(manager)) {
      if (manager.getTeamObject().getName().equals(name)) {
        TeamSender.delete(channels, writeTeam);
        response.setText("Team has been deleted");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      } else {
        response.setText("You are not a manager");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      }
    }
  }

  void help(Player sender, Object[] args) {
    response.setText("Create Info Invite Join Delete Leave Lick Delegate");
    channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
  }

  void info(Player sender, Object[] args) {
    TeamPrinter printer = new TeamPrinter(context.getTeam((String) args[0]), fiber, channels);
    printer.printTeam(sender);
  }

  void infoLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (gamer.hasTeam()) {
      TeamPrinter printer = new TeamPrinter(gamer.getTeamObject(), fiber, channels);
      printer.printTeam(sender);
    } else {
      response.setText("/team info <teamname>");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
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
          response.setText("You have invited " + receiver.getPlayer().getName() + " to your team!");
          channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
          writeTeam.inviteGamer(this.invites.get(writeTeam.getName()), receiver.getUuid());
          TextComponent invite = new TextComponent("You have been invited to " + inviter.getTeam() + "\n");
          TextComponent join = new TextComponent("click here to join");
          join.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/team join " + inviter.getTeam()));
          TextComponent command = new TextComponent("Or send command \"/team join " + inviter.getTeam() + "\" to join");
          invite.addExtra(join);
          invite.addExtra(command);
          Gamer gamer = context.getGamer(receiver.getPlayer().getUniqueId());
          channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, invite));
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
          WriteGroup writeGroup = (WriteGroup) team.getGroup("member");
          TeamSender.addMember(this.channels, joiner, team);
          GroupSender.addMember(this.channels, writeGroup, joiner);
          response.setText("Welcome to " + args[0]);
          channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
          if(Bukkit.getPlayer(team.getOwnerUUID()) != null){
            response.setText(sender.getName() + " has joined your team!");
            Gamer gamer = context.getGamer(team.getOwnerUUID());
            channels.chat_message.publish(new Message<>(ChatTarget.gamer,gamer, response));
          }
        } else {
          response.setText("You must be invited before joining " + args[0]);
          channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
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
        response.setText("You kicked " + kicked.getPlayer().getName());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,manager, response));
        response.setText("You have been kicked from " + writeTeam.getName());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,kicked, response));
      }
      {
        response.setText("Can't kick manager");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));

      }
    } else {
      response.setText("You must be manager of a team to kick players");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
    }
  }

  void kickOwner(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer kicked = (Gamer) args[0];
    Team writeTeam = manager.getTeamObject();
    if (writeTeam.isOwner(manager)) {
      TeamSender.removeMember(channels, kicked, writeTeam);
      response.setText("You kicked " + kicked.getPlayer().getName());
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      return;
    }
    if (writeTeam.isManager(manager)) {
      if (!writeTeam.isManager(kicked)) {
        TeamSender.removeMember(channels, kicked, writeTeam);
        response.setText("You kicked " + kicked.getPlayer().getName());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      }
      {
        response.setText("Can't kick manager");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      }
    } else {
      response.setText("You must be manager of a team to kick players");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
    }
  }

  void leave(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (!gamer.getTeam().equals("")) {
      Team writeTeam = context.getTeam(gamer.getTeam());
      if (writeTeam.getOwnerUUID().equals(gamer.getUuid())) {
        response.setText("You cannot leave the team you own");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      } else {
        TeamSender.removeMember(channels, gamer, writeTeam);
        response.setText("You have left " + gamer.getTeam());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
        Gamer owner = context.getGamer(writeTeam.getOwnerUUID());
        response.setText(sender.getName() + " has left your team :(");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,owner, response));
      }
    } else {
      response.setText("You are not in a team");
    }
  }

  public void register() {
    CommandAPICommand TeamCommand =
        new CommandAPICommand("team").withAliases("t")
            .withPermission("etherlands.public")
            .executesPlayer(this::infoLocal);
    TeamCommand.withSubcommand(new CommandAPICommand("help").executesPlayer(this::help));
    TeamCommand.withSubcommand(new CommandAPICommand("info").executesPlayer(this::infoLocal));
    TeamCommand.withSubcommand(
        new CommandAPICommand("info").withAliases("i")
            .withArguments(new StringArgument("team").replaceSuggestions(info -> getTeamStrings()))
            .executesPlayer(this::info));
    TeamCommand.withSubcommand(
        new CommandAPICommand("create").withAliases("cre")
            .withArguments(cleanNameArgument("teamname"))
            .executesPlayer(this::create));
    TeamCommand.withSubcommand(
        new CommandAPICommand("invite").withAliases("inv")
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
            .withArguments(new IntegerArgument("distrit-id"))
            .executesPlayer(this::delegateDistrict));
    TeamCommand.withSubcommand(
        new CommandAPICommand("delegate").executesPlayer(this::delegateLocal));
    TeamCommand.withSubcommand(
        new CommandAPICommand("delete")
            .withArguments(new StringArgument("teamname"))
            .executesPlayer(this::deleteTeam));

    TeamCommand.register();
  }
}

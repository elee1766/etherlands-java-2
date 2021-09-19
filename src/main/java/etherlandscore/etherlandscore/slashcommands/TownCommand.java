package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Town;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import etherlandscore.etherlandscore.state.sender.TownSender;
import etherlandscore.etherlandscore.state.write.WriteTeam;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class TownCommand extends CommandProcessor {
  private final Channels channels;
  private final Map<String, Map<UUID, Long>> invites = new HashMap<>();
  private final TextComponent response = new TextComponent("");

  public TownCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    register();
  }

  void create(Player sender, Object[] args) {
    if (context.hasTown((String) args[0])) {
      response.setText("A town already exists by that name");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      return;
    }
    if (context.hasGamer(sender.getUniqueId())) {
      if(context.getGamer(sender.getUniqueId()).hasTown()){
        response.setText("You are already in a town");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
        return;
      }
      response.setText("Town created!");
      channels.master_command.publish(new Message<>(MasterCommand.town_create_town, state().getGamer(sender.getUniqueId()), args[0]));
    }
  }

  void deleteTown(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    String name = (String) args[0];
    Town writeTown = manager.getTownObject();
    if (writeTown.isOwner(manager)) {
      if (manager.getTownObject().getName().equals(name)) {
        TownSender.delete(channels, writeTown);
        response.setText("Town has been deleted");
      } else {
        response.setText("You are not a manager");
      }
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
    }
  }

  void help(Player sender, Object[] args) {
    response.setText("Create Info Invite Join Delete Leave Lick Delegate");
    channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
  }

  void info(Player sender, Object[] args) {
    Town town = context.getTown((String) args[0]);
    Gamer gamer = context.getGamer(sender.getUniqueId());
    TownSender.sendInfo(this.channels,gamer,town);
  }

  void infoLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (gamer.hasTown()) {
      TownSender.sendInfo(this.channels,gamer,gamer.getTownObject());
    } else {
      response.setText("/town info <town_name>");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
    }
  }

  void invite(Player sender, Object[] args) {
    Gamer inviter = context.getGamer(sender.getUniqueId());
    Gamer receiver = context.getGamer(((Player) args[0]).getUniqueId());
    if (inviter != null) {
      Town writeTown = inviter.getTownObject();
      if (writeTown != null) {
        if (writeTown.canInvite(inviter)) {
          if (!this.invites.containsKey(writeTown.getName())) {
            this.invites.put(writeTown.getName(), new HashMap<>());
          }
          response.setText("You have invited " + receiver.getPlayer().getName() + " to your town!");
          channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
          writeTown.inviteGamer(this.invites.get(writeTown.getName()), receiver.getUuid());
          TextComponent invite = new TextComponent("You have been invited to " + inviter.getTown() + "\n");
          TextComponent join = new TextComponent("click here to join");
          join.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/town join " + inviter.getTown()));
          TextComponent command = new TextComponent("Or send command \"/town join " + inviter.getTown() + "\" to join");
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
      Town town = context.getTown((String) args[0]);
      if (town != null) {
        if (town.canJoin(this.invites.getOrDefault(town.getName(), new HashMap<>()), joiner)) {
          WriteTeam writeTeam = (WriteTeam) town.getTeam("member");
          TownSender.addMember(this.channels, joiner, town);
          TeamSender.addMember(this.channels, writeTeam, joiner);
          response.setText("Welcome to " + args[0]);
          channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
          if(Bukkit.getPlayer(town.getOwnerUUID()) != null){
            response.setText(sender.getName() + " has joined your town!");
            Gamer gamer = context.getGamer(town.getOwnerUUID());
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
    Town writeTown = manager.getTownObject();
    if (writeTown.isManager(manager)) {
      if (!writeTown.isManager(kicked)) {
        TownSender.removeMember(channels, kicked, writeTown);
        response.setText("You kicked " + kicked.getPlayer().getName());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,manager, response));
        response.setText("You have been kicked from " + writeTown.getName());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,kicked, response));
      }
      {
        response.setText("Can't kick manager");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));

      }
    } else {
      response.setText("You must be manager of a town to kick players");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
    }
  }

  void kickOwner(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer kicked = (Gamer) args[0];
    Town writeTown = manager.getTownObject();
    if (writeTown.isOwner(manager)) {
      TownSender.removeMember(channels, kicked, writeTown);
      response.setText("You kicked " + kicked.getPlayer().getName());
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      return;
    }
    if (writeTown.isManager(manager)) {
      if (!writeTown.isManager(kicked)) {
        TownSender.removeMember(channels, kicked, writeTown);
        response.setText("You kicked " + kicked.getPlayer().getName());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      }
      {
        response.setText("Can't kick manager");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      }
    } else {
      response.setText("You must be manager of a town to kick players");
      channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
    }
  }

  void leave(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (!gamer.getTown().equals("")) {
      Town writeTown = context.getTown(gamer.getTown());
      if (writeTown.getOwnerUUID().equals(gamer.getUuid())) {
        response.setText("You cannot leave the town you own");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
      } else {
        TownSender.removeMember(channels, gamer, writeTown);
        response.setText("You have left " + gamer.getTown());
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,context.getGamer(sender.getUniqueId()), response));
        Gamer owner = context.getGamer(writeTown.getOwnerUUID());
        response.setText(sender.getName() + " has left your town :(");
        channels.chat_message.publish(new Message<>(ChatTarget.gamer,owner, response));
      }
    } else {
      response.setText("You are not in a town");
    }
  }

  public void register() {
    CommandAPICommand TownCommand =
        createPlayerCommand("town",SlashCommands.infoLocal,this::infoLocal)
            .withAliases("t")
            .withPermission("etherlands.public");
    TownCommand.withSubcommand(
        createPlayerCommand("help",SlashCommands.help,this::help)
    );
    TownCommand.withSubcommand(
        createPlayerCommand("create",SlashCommands.create,this::create)
            .withAliases("cre")
            .withAliases("new")
            .withArguments(cleanNameArgument("town_name"))
    );
    TownCommand.withSubcommand(
        createPlayerCommand("invite",SlashCommands.invite,this::invite).withAliases("inv")
            .withArguments(
                new PlayerArgument("player").replaceSuggestions(info -> getOnlinePlayerStrings()))
    );
    TownCommand.withSubcommand(
        createPlayerCommand("join",SlashCommands.join,this::join)
            .withArguments(new StringArgument("town").replaceSuggestions(info -> getTownStrings()))
    );
    TownCommand.withSubcommand(
        createPlayerCommand("leave",SlashCommands.leave,this::leave)
    );
    TownCommand.withSubcommand(
        createPlayerCommand("kick",SlashCommands.kick,this::kick)
            .withArguments(townMemberArgument("member"))
    );
    TownCommand.withSubcommand(
        createPlayerCommand("kick",SlashCommands.kickOwner,this::kickOwner)
            .withArguments(townMemberArgument("member"))
    );
    TownCommand.withSubcommand(
        createPlayerCommand("delete",SlashCommands.deleteTown, this::deleteTown)
            .withArguments(new StringArgument("town_name"))
            .executesPlayer(this::deleteTown));

    createPlayerCommand("town",SlashCommands.infoLocal,this::infoLocal).register();
    createPlayerCommand("town",SlashCommands.info,this::info)
        .withArguments(new StringArgument("town").replaceSuggestions(info -> getTownStrings())).register();


    TownCommand.register();
  }
}

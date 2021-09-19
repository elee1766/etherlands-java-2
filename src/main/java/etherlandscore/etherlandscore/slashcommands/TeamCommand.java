package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.read.Town;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import etherlandscore.etherlandscore.state.sender.TownSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class TeamCommand extends CommandProcessor {
  private final Fiber fiber;
  private final Channels channels;

  public TeamCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }
  public void register() {
    CommandAPICommand TeamCommand =
        createPlayerCommand("team",SlashCommands.info,this::info)
            .withAliases("gr")
            .withArguments(townTeamArgument("team"));
    TeamCommand.withSubcommand(
        createPlayerCommand("help",SlashCommands.help,this::runHelpCommand)
    );
    TeamCommand.withSubcommand(
        createPlayerCommand("create",SlashCommands.create,this::create)
            .withAliases("new")
            .withArguments(cleanNameArgument("teamname"))
    );
    TeamCommand.withSubcommand(
        createPlayerCommand("delete",SlashCommands.delete,this::delete)
            .withAliases("del")
            .withArguments(cleanNameArgument("teamname"))
    );
    TeamCommand.register();
    createPlayerCommand("team",SlashCommands.modify,this::modify)
        .withArguments(townTeamArgument("team"))
        .withArguments(new MultiLiteralArgument("add", "remove"))
        .withArguments(townMemberArgument("player"))
        .register();

    hook(SlashCommands.add,this::add);
    hook(SlashCommands.remove,this::remove);
  }

  void modify(Player sender, Object[] args) {
    switch((String) args[1]){
      case "add":
        runAsync(SlashCommands.add, sender, args);
        break;
      case "remove":
        runAsync(SlashCommands.remove, sender, args);
        break;
    }
  }
  void add(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer subject = (Gamer) args[2];
    Team team = (Team) args[0];
    Town town = manager.getTownObject();
    if (town != null) {
      if (town.canAction(manager, subject)) {
        if (subject.getTown().equals(town.getName())) {
          TeamSender.addMember(channels, team, subject);
          sender.sendMessage(
              subject.getName() + " has been added to " + team.getName());
        } else {
          sender.sendMessage(subject.getName() + " is not in your town");
        }
      } else {
        sender.sendMessage("You are not a manager");
      }
    } else {
      runNoTown(sender);
    }
  }
  void remove(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer subject = (Gamer) args[2];
    Team team = (Team) args[0];
    Town town = manager.getTownObject();
    if (town != null) {
      if (town.canAction(manager, subject)) {
        if (subject.getTown().equals(town.getName())) {
          TeamSender.removeMember(channels, team, subject);
          sender.sendMessage(
              subject.getName() + " has been removed from " + team.getName());
        } else {
          sender.sendMessage(subject.getName() + " is not in your town");
        }
      } else {
        sender.sendMessage("You are not a manager");
      }
    } else {
      runNoTown(sender);
    }
  }

  void create(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    if (town.isManager(gamer)) {
      TownSender.createTeam(this.channels, (String) args[0], town);
      sender.sendMessage(args[0] + " has been created");
    } else {
      sender.sendMessage("You are not manager");
    }
  }

  void delete(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    if (town.isManager(gamer)) {
      TownSender.deleteTeam(this.channels, (String) args[0], town);
      sender.sendMessage(args[0] + " has been deleted");
    } else {
      sender.sendMessage("You are not manager");
    }
  }

  void info(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    TeamSender.sendTeamInfo(channels,gamer,(Team) args[0]);
  }



  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("create");
  }

  void runNoTown(Player sender) {
    sender.sendMessage("You must be in a town to manage teams");
  }
}

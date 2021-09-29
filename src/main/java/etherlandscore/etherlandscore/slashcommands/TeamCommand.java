package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ImpartialHitter;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Team;
import etherlandscore.etherlandscore.state.Town;
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

  void remove(Player sender, Object[] args) {
    Gamer gamer = new Gamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    Team team = (Team) args[0];
    Gamer target = (Gamer) args[2];
    ImpartialHitter.HitWorld(
        "town",
        town.getName(),
        "team",
        team.getName(),
        "removemember",
        gamer.getUuidString(),
        target.getUuidString()
    );
  }

  void add(Player sender, Object[] args) {
    Gamer gamer = new Gamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    Team team = (Team) args[0];
    Gamer target = (Gamer) args[2];
    ImpartialHitter.HitWorld(
        "town",
        town.getName(),
        "team",
        team.getName(),
        "addmember",
        gamer.getUuidString(),
        target.getUuidString()
    );
  }

  void create(Player sender, Object[] args) {
    Gamer gamer = new Gamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    String name = (String) args[0];
    ImpartialHitter.HitWorld(
        "town",
        town.getName(),
        "team",
        name,
        "create",
        gamer.getUuidString()
    );
  }

  void delete(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    Team team = (Team) args[0];
    ImpartialHitter.HitWorld(
        "town",
        town.getName(),
        "team",
        team.getName(),
        "create",
        gamer.getUuidString()
    );
  }

  void info(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    StateSender.sendTeamInfo(channels, gamer, (Team) args[0]);
  }

  void modify(Player sender, Object[] args) {
    switch ((String) args[1]) {
      case "add":
        runAsync(SlashCommands.add, sender, args);
        break;
      case "remove":
        runAsync(SlashCommands.remove, sender, args);
        break;
    }
  }

  public void register() {
    CommandAPICommand TeamCommand =
        createPlayerCommand("team", SlashCommands.info, this::info)
            .withAliases("gr")
            .withArguments(townTeamArgument("team"));
    TeamCommand.withSubcommand(
        createPlayerCommand("help", SlashCommands.help, this::runHelpCommand));
    TeamCommand.withSubcommand(
        createPlayerCommand("create", SlashCommands.create, this::create)
            .withAliases("new")
            .withArguments(cleanNameArgument("teamname")));
    TeamCommand.withSubcommand(
        createPlayerCommand("delete", SlashCommands.delete, this::delete)
            .withAliases("del")
            .withArguments(cleanNameArgument("teamname")));
    TeamCommand.register();
    createPlayerCommand("team", SlashCommands.modify, this::modify)
        .withArguments(townTeamArgument("team"))
        .withArguments(new MultiLiteralArgument("add", "remove"))
        .withArguments(gamerArgument("player"))
        .register();

    hook(SlashCommands.add, this::add);
    hook(SlashCommands.remove, this::remove);
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("create");
  }

  void runNoTown(Player sender) {
    sender.sendMessage("You must be in a town to manage teams");
  }
}

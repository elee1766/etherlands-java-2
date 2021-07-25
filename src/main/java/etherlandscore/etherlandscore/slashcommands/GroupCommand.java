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
            .executesPlayer(this::runHelpCommand)
    );
    GroupCommand.withSubcommand(
        new CommandAPICommand("create")
            .withArguments(new StringArgument("group-name"))
            .withPermission("etherlands.public")
            .executesPlayer((sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                    if(team!= null){
                      team.createRegion(this.channels, (String) args[0]);
                    }else{
                      runNoTeam(sender);
                    }
                }
            )
    );

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
        new CommandAPICommand("add")
            .withArguments(gamerArgument("player"))
            .withPermission("etherlands.public")
            .executesPlayer((sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  Team team = gamer.getTeamObject();
                  if(team!= null){
                    team.createRegion(this.channels, (String) args[0]);
                  }else{
                    runNoTeam(sender);
                  }
                }
            )
    );

    GroupCommand.register();
  }

  void runHelpCommand(Player sender, Object[] args) {
    sender.sendMessage("create");
  }
  void runNoTeam(Player sender){
    sender.sendMessage("you must be in a team to manage groups");
  }
}

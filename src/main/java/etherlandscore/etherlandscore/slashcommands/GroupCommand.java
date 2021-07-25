package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
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
            .withArguments(cleanNameArgument("groupname"))
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
        new CommandAPICommand("add")
            .withArguments(teamMemberArgument("player"))
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

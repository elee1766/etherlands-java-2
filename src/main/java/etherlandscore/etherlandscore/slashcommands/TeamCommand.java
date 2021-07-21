package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.stateholder.TeamState;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

public class TeamCommand extends ListenerClient {
    private final Fiber fiber;
    private final Channels channels;
    public TeamCommand(Channels channels, Fiber fiber) {
        super(channels, fiber);
        this.fiber = fiber;
        this.channels = channels;
    }


    public void register(){
        CommandAPICommand TeamCommand = new CommandAPICommand("team").withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    sender.sendMessage("create info invite join delete");
                });
        TeamCommand.withSubcommand(new CommandAPICommand("help")
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    sender.sendMessage("create info invite join delete");
                })
        );
        TeamCommand.withSubcommand(new CommandAPICommand("info")
                .withArguments(new StringArgument("team").replaceSuggestions(info->getTeamStrings()))
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    if(!globalState.getTeams().containsKey(args[0])){
                        sender.sendMessage("could not find team with name name " + args[0]);
                    }
                    TeamState team = globalState.getTeams().get(args[0]);
                    sender.sendMessage("team name:" + team.getName());
                    sender.sendMessage("team owner:" + team.getOwner());
                }));

        TeamCommand.withSubcommand(new CommandAPICommand("info")
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    sender.sendMessage("/team info <teamname>");
                }));
        TeamCommand.withSubcommand(new CommandAPICommand("create")
                .withArguments(new StringArgument("team-name"))
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    if(globalState.getTeams().containsKey(args[0])){
                        sender.sendMessage("a team already exists by that name");
                        return;
                    }
                    if(globalState.getGamers().containsKey(sender.getUniqueId())){
                            globalState.createTeam(this.channels,globalState.getGamers().get(sender.getUniqueId()),(String) args[0]);
                            sender.sendMessage("team created!");
                    }
                })
        );
        TeamCommand.withSubcommand(new CommandAPICommand("invite")
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                })
        );
        TeamCommand.register();
    }

}


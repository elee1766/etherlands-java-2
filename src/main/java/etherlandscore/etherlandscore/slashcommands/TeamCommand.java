package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamCommand extends ListenerClient {
    private final Fiber fiber;
    private final Channels channels;
    private final Map<UUID, Long> invites = new HashMap<>();

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
                    team_info(sender, (String) args[0]);
                }));

        TeamCommand.withSubcommand(new CommandAPICommand("info")
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    Gamer gamer = context.getGamer(sender.getUniqueId());
                    if(!gamer.getTeam().equals("")) {
                        team_info(sender,gamer.getTeam());
                        sender.sendMessage("you have left "+ gamer.getTeam());
                    }else {
                        sender.sendMessage("/team info <team-name>");
                    }
                }));
        TeamCommand.withSubcommand(new CommandAPICommand("create")
                .withArguments(new StringArgument("team-name"))
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    if(context.getTeams().containsKey((String) args[0])){
                        sender.sendMessage("a team already exists by that name");
                        return;
                    }
                    if(context.getGamers().containsKey(sender.getUniqueId())){
                        context.createTeam(this.channels, context.getGamers().get(sender.getUniqueId()),(String) args[0]);
                        sender.sendMessage("team created!");
                    }
                })
        );
        TeamCommand.withSubcommand(new CommandAPICommand("invite")
                .withArguments(new PlayerArgument("player"))
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    Gamer inviter = context.getGamer(sender.getUniqueId());
                    Gamer receiver = context.getGamer(((Player) args[0]).getUniqueId());
                    if(inviter != null) {
                        Team team = context.getTeam(inviter.getTeam());
                        if (team != null) {
                            if (team.canInvite(inviter)) {
                                team.inviteGamer(this.invites, receiver.getUuid());
                                receiver.getPlayer().sendMessage("you have 5 min to accept invite to " + inviter.getTeam());
                            }
                        }
                    }
                })
        );

        TeamCommand.withSubcommand(new CommandAPICommand("join")
                .withArguments(new StringArgument("team").replaceSuggestions(info->getTeamStrings()))
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    Gamer joiner = context.getGamer(sender.getUniqueId());
                    if(joiner != null){
                        Team team = context.getTeam((String) args[0]);
                        if(team != null){
                            if(team.canJoin(this.invites, joiner)){
                                team.addMember(this.channels,joiner);
                                sender.sendMessage("welcome to " + args[0]);
                            }else{
                                sender.sendMessage("you must be invited before joining " + args[0]);
                            }
                        }
                    }
                })
        );
        TeamCommand.withSubcommand(new CommandAPICommand("leave")
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                    Gamer gamer = context.getGamer(sender.getUniqueId());
                    if(!gamer.getTeam().equals("")) {
                        Team team = context.getTeam(gamer.getTeam());
                        team.removeMember(this.channels,gamer);
                    }else {
                        sender.sendMessage("you are not in a team");
                    }
                })
        );
        TeamCommand.register();
    }

    private boolean team_info(CommandSender sender, String name) {
        if (!context.getTeams().containsKey(name)) {
            sender.sendMessage("could not find team with name name " + name);
            return false;
        }
        Team team = context.getTeams().get(name);
        sender.sendMessage("team name:" + team.getName());
        sender.sendMessage("team owner:" + team.getOwner());
        sender.sendMessage("team size:" + team.getMembers().size());
        return true;
    }
}


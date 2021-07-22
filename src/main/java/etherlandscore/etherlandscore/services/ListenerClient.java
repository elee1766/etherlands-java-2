package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.state.Context;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetlang.fibers.Fiber;

import java.util.Arrays;
import java.util.stream.Stream;

public class ListenerClient extends ServerModule {

    Channels channels;
    Fiber fiber;

    public Context context;

    protected ListenerClient(Channels channels, Fiber fiber) {
        super(fiber);
        this.channels = channels;
        this.fiber = fiber;
        this.context = new Context();
        this.register();
    }
    private void register(){
        channels.global_update.subscribe(fiber, global->{
            this.context = global;
        });
        channels.gamer_update.subscribe( fiber, player-> {
            this.context.getGamers().put(player.getUuid(),player);
        });
        channels.team_update.subscribe( fiber, team-> {
            this.context.getTeams().put(team.getName(),team);
        });
    }

    protected String[] getChunkStrings() {
        return this.context.getTeams().keySet().stream().map(Object::toString).toArray(String[]::new);
    }

    protected String[] getAccessFlagStrings(){
        return Stream.of(AccessFlags.values()).map(AccessFlags::name).toArray(String[]::new);
    }

    protected String[] getFlagValueStrings(){
        return Stream.of(FlagValue.values()).map(FlagValue::name).toArray(String[]::new);
    }

    protected String[] getPlayerStrings(){
        OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();
        return Arrays.stream(players).map(OfflinePlayer::getName).toArray(String[]::new);
    }

    protected String[] getTeamStrings(){
        return this.context.getTeams().keySet().stream().map(Object::toString).toArray(String[]::new);
    }


}

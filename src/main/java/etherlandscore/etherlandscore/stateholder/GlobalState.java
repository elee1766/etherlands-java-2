package etherlandscore.etherlandscore.stateholder;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalState {

    private final Map<UUID,GamerState> gamers = new HashMap<>();
    private final Map<String,TeamState> teams = new HashMap<>();

    public GlobalState createState(GlobalState state) {
        return state;
    }
    public Map<UUID, GamerState> getGamers() {
        return gamers;
    }
    public Map<String, TeamState> getTeams() {
        return teams;
    }

    public void createTeam(Channels channels, GamerState gamer, String name){
        channels.command.publish(new Message("team_create_team", gamer,name));
    }
}

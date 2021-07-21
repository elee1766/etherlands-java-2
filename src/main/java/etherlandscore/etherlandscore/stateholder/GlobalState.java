package etherlandscore.etherlandscore.stateholder;


import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalState {

    private final Map<UUID,GamerState> gamers = new HashMap<>();
    private final Map<String,TeamState> teams = new HashMap<>();
    private final Map<String,UUID> linked = new HashMap<>();
    private final Map<Integer,PlotState> plots = new HashMap<>();

    public GlobalState createState(GlobalState state) {
        return state;
    }
    public Map<UUID, GamerState> getGamers() {
        return gamers;
    }
    public Map<String,UUID> getLinked(){return linked;}
    public Map<String, TeamState> getTeams() {
        return teams;
    }

    public void createTeam(Channels channels, GamerState gamer, String name){
        channels.master_command.publish(new Message("team_create_team", gamer,name));
    }

    public PlotState getPlot(Integer id) {
        return plots.get(id);
    }
}

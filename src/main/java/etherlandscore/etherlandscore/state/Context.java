package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Context {

    private final Map<UUID, Gamer> gamers = new HashMap<>();
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<String, UUID> linked = new HashMap<>();
    private final Map<Integer, Plot> plots = new HashMap<>();
    private final Map<Integer, Map<Integer, Integer>> plotLocations  = new HashMap<>();

    public Context createState(Context state) {
        return state;
    }

    public Map<UUID, Gamer> getGamers() {
        return gamers;
    }

    public Map<String, UUID> getLinked() {
        return linked;
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public void createTeam(Channels channels, Gamer gamer, String name) {
        channels.master_command.publish(new Message("team_create_team", gamer, name));
    }

    public Plot findPlot(Integer x, Integer z){
        Integer id = plotLocations.getOrDefault(x,new HashMap<>()).getOrDefault(z,null);
        if(id!=null){
            return getPlot(id);
        }
        return null;
    }

    public Plot getPlot(Integer id) {
        return plots.get(id);
    }

    public Gamer getGamer(UUID uuid) {
        return gamers.get(uuid);
    }

    public Team getTeam(String team) {
        return teams.get(team);
    }

    public Map<Integer, Plot> getPlots() {
        return plots;
    }
}

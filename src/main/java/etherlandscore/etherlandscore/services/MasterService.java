package etherlandscore.etherlandscore.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import etherlandscore.etherlandscore.stateholder.GamerState;
import etherlandscore.etherlandscore.stateholder.GlobalState;
import etherlandscore.etherlandscore.stateholder.TeamState;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.util.Objects;
import java.util.UUID;

public class MasterService extends ServerModule {
    private final Channels channels;
    private final Fiber fiber;

    private final Gson gson;

    private final GlobalState globalState;
    private final JsonPersister<GlobalState> globalStatePersister;

    public MasterService(Channels channels, Fiber fiber) {
        super(fiber);
        this.channels = channels;
        this.fiber = fiber;

        this.gson = new GsonBuilder().create();
        String root = Bukkit.getServer().getPluginManager().getPlugin("EtherlandsCore").getDataFolder().getAbsolutePath();
        this.globalStatePersister =new JsonPersister<>(root + "/db.json");
        GlobalState writer = globalStatePersister.readJson(gson, GlobalState.class);
        this.globalState = Objects.requireNonNullElseGet(writer, GlobalState::new);
        this.channels.global_update.publish(globalState);
        this.channels.command.subscribe(fiber,this::process_command);
    }

    public void save(){
        globalStatePersister.overwrite(gson.toJson(this.globalState));
    }

    public void team_create_team(GamerState a, String b){
        GamerState gamer = globalState.getGamers().get(a.getUuid());
        if(gamer != null & b != null) {
            TeamState team = new TeamState(gamer, b);
            if(!globalState.getTeams().containsKey(b)) {
                globalState.getTeams().put(b,team);
                this.channels.global_update.publish(globalState);
            }
        }
    }

    public void team_add_gamer(TeamState a, GamerState b) {
        GamerState gamer = globalState.getGamers().get(b.getUuid());
        TeamState team = globalState.getTeams().get(a.getName());
        if(team != null && gamer != null) {
            team.addMember(gamer);
            gamer_update(gamer);
            team_update(team);
        }
    }

    public void team_remove_gamer(TeamState a, GamerState b) {
        GamerState gamer = globalState.getGamers().get(b.getUuid());
        TeamState team = globalState.getTeams().get(a.getName());
        if(team != null && gamer != null) {
            team.removeMember(gamer);
            gamer_update(gamer);
            team_update(team);
        }
    }

    public void gamer_create_gamer(UUID uuid){
        if(!this.globalState.getGamers().containsKey(uuid)){
            GamerState gamer = new GamerState(uuid);
            this.globalState.getGamers().put(uuid,gamer);
            gamer_update(gamer);
        }
    }

    private void process_command(Message message){
        Object[] args = message.getArgs();
        switch(message.getCommand()) {
            case "gamer_create_gamer":
                gamer_create_gamer((UUID) args[0]);
                break;
            case "team_add_gamer":
                team_add_gamer((TeamState) args[0], (GamerState) args[1]);
                break;
            case "team_remove_gamer":
                team_remove_gamer((TeamState) args[0],(GamerState) args[1]);
                break;
            case "team_create_team":
                team_create_team((GamerState) args[0], (String) args[1]);
                break;
            default:
                break;
        }
        save();
    }

    private void gamer_update(GamerState gamer){
        this.channels.gamer_update.publish(gamer);
    }

    private void team_update(TeamState team){
        this.channels.team_update.publish(team);
    }
}

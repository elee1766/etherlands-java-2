package etherlandscore.etherlandscore.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.state.Team;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.util.Objects;
import java.util.UUID;

public class MasterService extends ServerModule {
    private final Channels channels;
    private final Fiber fiber;

    private final Gson gson;

    private final Context context;
    private final JsonPersister<Context> globalStatePersister;

    public MasterService(Channels channels, Fiber fiber) {
        super(fiber);
        this.channels = channels;
        this.fiber = fiber;

        this.gson = new GsonBuilder().create();
        String root = Bukkit.getServer().getPluginManager().getPlugin("EtherlandsCore").getDataFolder().getAbsolutePath();
        this.globalStatePersister =new JsonPersister<>(root + "/db.json");
        Context writer = globalStatePersister.readJson(gson, Context.class);
        this.context = Objects.requireNonNullElseGet(writer, Context::new);
        this.channels.global_update.publish(context);
        this.channels.master_command.subscribe(fiber,this::process_command);
    }

    public void save(){
        globalStatePersister.overwrite(gson.toJson(this.context));
    }

    public void team_create_team(Gamer a, String b){
        Gamer gamer = context.getGamers().get(a.getUuid());
        if(gamer != null & b != null) {
            Team team = new Team(gamer, b);
            if(!context.getTeams().containsKey(b)) {
                context.getTeams().put(b,team);
                this.channels.global_update.publish(context);
            }
        }
    }

    public void team_add_gamer(Team a, Gamer b) {
        Gamer gamer = context.getGamers().get(b.getUuid());
        Team team = context.getTeams().get(a.getName());
        if(team != null && gamer != null) {
            team.addMember(gamer);
            gamer_update(gamer);
            team_update(team);
        }
    }

    public void team_remove_gamer(Team a, Gamer b) {
        Gamer gamer = context.getGamers().get(b.getUuid());
        Team team = context.getTeams().get(a.getName());
        if(team != null && gamer != null) {
            team.removeMember(gamer);
            gamer_update(gamer);
            team_update(team);
        }
    }

    public void gamer_create_gamer(UUID uuid){
        if(!this.context.getGamers().containsKey(uuid)){
            Gamer gamer = new Gamer(uuid);
            this.context.getGamers().put(uuid,gamer);
            gamer_update(gamer);
        }
    }

    public void friend_add(Gamer a, Gamer b) {
        Gamer gamer1 = context.getGamers().get(a.getUuid());
        Gamer gamer2 = context.getGamers().get(b.getUuid());
        gamer1.addFriend(gamer2);
    }

    public void plot_set_owner(Plot a, String address){
        UUID ownerUUID = this.context.getLinked().getOrDefault(address,null);
        Plot plot = this.context.getPlot(a.getId());

    }

    private void process_command(Message message){
        Object[] args = message.getArgs();
        switch(message.getCommand()) {
            case "gamer_create_gamer":
                gamer_create_gamer((UUID) args[0]);
                break;
            case "team_add_gamer":
                team_add_gamer((Team) args[0], (Gamer) args[1]);
                break;
            case "team_remove_gamer":
                team_remove_gamer((Team) args[0],(Gamer) args[1]);
                break;
            case "team_create_team":
                team_create_team((Gamer) args[0], (String) args[1]);
                break;
            case "friend_add":
                friend_add((Gamer) args[0], (Gamer) args[1]);
                break;
            default:
                break;
        }
        save();
    }

    private void gamer_update(Gamer gamer){
        this.channels.gamer_update.publish(gamer);
    }

    private void team_update(Team team){
        this.channels.team_update.publish(team);
    }
}

package etherlandscore.etherlandscore.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.state.Team;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MasterService extends ServerModule {
    private final Channels channels;

    private final Gson gson;

    private final Context context;
    private final JsonPersister<Context> globalStatePersister;

    public MasterService(Channels channels, Fiber fiber) {
        super(fiber);
        this.channels = channels;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        String root = Bukkit.getServer().getPluginManager().getPlugin("EtherlandsCore").getDataFolder().getAbsolutePath();
        this.globalStatePersister = new JsonPersister<>(root + "/db.json");
        Context writer = globalStatePersister.readJson(gson, Context.class);
        this.context = Objects.requireNonNullElseGet(writer, Context::new);
        this.channels.global_update.publish(context);
        this.channels.master_command.subscribe(fiber, this::process_command);
    }

    public void save() {
        globalStatePersister.overwrite(gson.toJson(this.context));
    }

    public void team_create_team(Gamer a, String b) {
        Gamer gamer = context.getGamers().get(a.getUuid());
        if (gamer != null & b != null) {
            Team team = new Team(gamer, b);
            if (!context.getTeams().containsKey(b)) {
                context.getTeams().put(b, team);
                gamer.setTeam(team.getName());
                this.channels.global_update.publish(context);
            }
        }
    }

    public void team_add_gamer(Team a, Gamer b) {
        Gamer gamer = context.getGamers().get(b.getUuid());
        Team team = context.getTeams().get(a.getName());
        if (team != null && gamer != null) {
            team.addMember(gamer);
            gamer.setTeam(team.getName());
            gamer_update(gamer);
            team_update(team);
        }
    }

    public void team_remove_gamer(Team a, Gamer b) {
        Gamer gamer = context.getGamers().get(b.getUuid());
        Team team = context.getTeams().get(a.getName());
        if (team != null && gamer != null) {
            team.removeMember(gamer);
            gamer.setTeam("");
            gamer_update(gamer);
            team_update(team);
        }
    }

    public void gamer_create_gamer(UUID uuid) {
        if (!this.context.getGamers().containsKey(uuid)) {
            Gamer gamer = new Gamer(uuid);
            this.context.getGamers().put(uuid, gamer);
            gamer_update(gamer);
        }
    }

    public void gamer_add_friend(Gamer a, Gamer b) {
        Gamer gamer1 = context.getGamers().get(a.getUuid());
        Gamer gamer2 = context.getGamers().get(b.getUuid());
        gamer1.addFriend(gamer2);
        gamer_update(gamer1);
        gamer_update(gamer2);
    }


    private void gamer_remove_friend(Gamer a, Gamer b) {
        Gamer gamer1 = context.getGamers().get(a.getUuid());
        Gamer gamer2 = context.getGamers().get(b.getUuid());
        gamer1.removeFriend(gamer2);
        gamer_update(gamer1);
        gamer_update(gamer2);
    }

    private void gamer_friend_list(Gamer a) {
        Gamer gamer = context.getGamers().get(a.getUuid());
        gamer.friendList();
    }

    public void plot_update_plot(Integer id, Integer x, Integer z, String owner) {
        if (!context.getPlots().containsKey(id)) {
            context.getPlots().put(id,new Plot(id,x,z,owner));
        }
        Plot plot = context.getPlot(id);
        if(!context.getPlotLocations().containsKey(plot.getX())){
            context.getPlotLocations().put(plot.getX(),new HashMap<>());
        }
        context.getPlotLocations().get(plot.getX()).put(plot.getZ(), plot.getId());
        plot_set_owner(context.getPlot(id),owner);
        if(context.getLinked().containsKey(owner)){
            context.getPlot(id).setOwner(owner,context.getLinked().get(owner));
        }
        plot_update(context.getPlot(id));
    }

    public void plot_set_owner(Plot a, String address) {
        UUID ownerUUID = this.context.getLinked().getOrDefault(address, null);
        Plot plot = this.context.getPlot(a.getId());
    }

    private void process_command(Message<MasterCommand> message) {
        Object[] args = message.getArgs();
        switch (message.getCommand()) {
            case gamer_create_gamer -> gamer_create_gamer((UUID) args[0]);
            case plot_update_plot -> plot_update_plot((Integer) args[0], (Integer) args[1], (Integer) args[2],(String) args[3]);
            case team_add_gamer -> team_add_gamer((Team) args[0], (Gamer) args[1]);
            case team_remove_gamer -> team_remove_gamer((Team) args[0], (Gamer) args[1]);
            case team_create_team -> team_create_team((Gamer) args[0], (String) args[1]);
            case gamer_add_friend -> gamer_add_friend((Gamer) args[0], (Gamer) args[1]);
            case gamer_remove_friend -> gamer_remove_friend((Gamer) args[0],(Gamer) args[1]);
            case gamer_friend_list -> gamer_friend_list((Gamer) args[0]);
            case plot_set_owner -> plot_set_owner((Plot) args[0], (String) args[1]);
            case region_set_priority, player_link_address, region_add_plot, region_remove_plot -> {
            }
        }
        save();
    }


    private void gamer_update(Gamer gamer) {
        this.channels.gamer_update.publish(gamer);
    }

    private void team_update(Team team) {
        this.channels.team_update.publish(team);
    }

    private void plot_update(Plot plot) {
        this.channels.plot_update.publish(plot);
    }
}

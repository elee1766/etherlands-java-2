package etherlandscore.etherlandscore.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import etherlandscore.etherlandscore.readonly.ReadContext;
import etherlandscore.etherlandscore.state.*;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class MasterService extends ServerModule {
    static Context context;
    private final Channels channels;
    private final Gson gson;
    private final JsonPersister<Context> globalStatePersister;

    public MasterService(Channels channels, Fiber fiber) {
        super(fiber);
        this.channels = channels;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        String root = Bukkit.getServer().getPluginManager().getPlugin("EtherlandsCore").getDataFolder().getAbsolutePath();
        File json = new File(root + "/db.json");
        try {
            json.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.globalStatePersister = new JsonPersister<>(root + "/db.json");
        Context writer = globalStatePersister.readJson(gson, Context.class);
        context = Objects.requireNonNullElseGet(writer, Context::new);
        this.channels.global_update.publish(context);
        this.channels.master_command.subscribe(fiber, this::process_command);
    }

    public static ReadContext state() {
        return new ReadContext(context);
    }

    public void save() {
        globalStatePersister.overwrite(gson.toJson(context));
    }

    public void team_create_team(Gamer gamer, String name) {
        Team team = new Team(gamer, name);
        if (!context.getTeams().containsKey(name)) {
            context.getTeams().put(name, team);
            gamer.setTeam(team.getName());
        }
    }

    public void team_add_gamer(Team team, Gamer gamer) {
        team.addMember(gamer);
        gamer.setTeam(team.getName());
    }

    public void team_remove_gamer(Team team, Gamer gamer) {
        team.removeMember(gamer);
        gamer.setTeam("");
        gamer.clearGroups();
    }

    public void context_create_gamer(UUID uuid) {
        if (!context.getGamers().containsKey(uuid)) {
            Gamer gamer = new Gamer(uuid);
            context.getGamers().put(uuid, gamer);
        }
    }

    public void gamer_add_friend(Gamer a, Gamer b) {
        a.addFriend(b);
    }


    private void gamer_remove_friend(Gamer a, Gamer b) {
        a.removeFriend(b);
    }

    private void region_add_plot(Region region, Plot plot){
      region.addPlot(plot);
    }
    private void region_remove_plot(Region region, Plot plot){
        region.removePlot(plot);
    }

    public void plot_update_plot(Integer id, Integer x, Integer z, String owner) {
        if (!context.getPlots().containsKey(id)) {
            context.getPlots().put(id,new Plot(id,x,z,owner));
        }
        Plot plot = context.getPlot(id);
        context.getPlotLocations().put(plot.getX(),plot.getZ(),plot.getId());
        plot_set_owner(plot,owner);
    }


    public void plot_set_owner(Plot plot, String address) {
        UUID ownerUUID = context.getLinks().getOrDefault(address, null);
        plot.setOwner(address,ownerUUID);
    }

    private void team_delegate_plot(Team team, Plot plot) {
        plot_reclaim_plot(plot);
        team.addPlot(plot);
        plot.setTeam(team.getName());
    }

    private void plot_reclaim_plot(Plot plot) {
        plot.removeTeam();
    }

    private void gamer_link_address(Gamer gamer, String address){
      gamer.setAddress(address);
      context.getLinks().put(address,gamer.getUuid());
    }

    private void team_create_region(Team team, String name) {
      team.createRegion(name);
    }

    private void team_delete_region(Team team, Region region) {
        team.removeRegion(region.getName());
    }
    private void group_add_gamer(Group group, Gamer gamer) {
        gamer.addGroup(group);
        group.addMember(gamer);
    }
    private void group_remove_gamer(Group group, Gamer gamer) {
        group.removeMember(gamer);
        gamer.removeGroup(group.getName());
    }

    private void team_create_group(Team team, String name) {
        team.createGroup(name);
    }

    private void group_set_priority(Group group, Integer b) {
        group.setPriority(b);
    }

    private void team_delete_group(Team a, Group b) {
    }

    private void process_command(Message<MasterCommand> message) {
        Object[] _args = message.getArgs();
        switch (message.getCommand()) {
            case context_create_gamer -> context_create_gamer((UUID) _args[0]);
            // gamer commands
            case gamer_add_friend -> gamer_add_friend((Gamer) _args[0], (Gamer) _args[1]);
            case gamer_remove_friend -> gamer_remove_friend((Gamer) _args[0],(Gamer) _args[1]);
            case gamer_link_address -> gamer_link_address((Gamer) _args[0], (String) _args[1]);
            //plot commands
            case plot_update_plot -> plot_update_plot((Integer) _args[0], (Integer) _args[1], (Integer) _args[2],(String) _args[3]);
            case plot_set_owner -> plot_set_owner((Plot) _args[0], (String) _args[1]);
            case plot_reclaim_plot -> plot_reclaim_plot((Plot) _args[0]);
            //team commands
            case team_add_gamer -> team_add_gamer((Team) _args[0], (Gamer) _args[1]);
            case team_remove_gamer -> team_remove_gamer((Team) _args[0], (Gamer) _args[1]);
            case team_create_team -> team_create_team((Gamer) _args[0], (String) _args[1]);
            case team_create_group -> team_create_group((Team) _args[0], (String) _args[1]);
            case team_delete_group -> team_delete_group((Team) _args[0], (Group) _args[1]);
            case team_create_region -> team_create_region((Team) _args[0], (String) _args[1]);
            case team_delete_region -> team_delete_region((Team) _args[0], (Region) _args[1]);
            case team_delegate_plot -> team_delegate_plot((Team)_args[0], (Plot) _args[1]);
            // region commands
            case region_add_plot -> region_add_plot((Region) _args[0], (Plot) _args[1]);
            case region_remove_plot -> region_remove_plot((Region) _args[0], (Plot) _args[1]);
            case region_set_priority ->region_set_priority((Region) _args[0], (Integer) _args[1]);
            //group commands
            case group_add_gamer -> group_add_gamer((Group) _args[0], (Gamer) _args[1]);
            case group_remove_gamer -> group_remove_gamer((Group) _args[0], (Gamer) _args[1]);
            case group_set_priority -> group_set_priority((Group) _args[0], (Integer) _args[1]);
            //flag commands
        }
        global_update();
        save();
    }


    private void region_set_priority(Region a, Integer priority){

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
    private void global_update(){
        this.channels.global_update.publish(context);
    }

}

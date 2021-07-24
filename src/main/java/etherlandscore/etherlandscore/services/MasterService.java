package etherlandscore.etherlandscore.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import etherlandscore.etherlandscore.readonly.ReadContext;
import etherlandscore.etherlandscore.slashcommands.FlagCommand;
import etherlandscore.etherlandscore.state.*;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.io.IOException;
import java.io.File;
import java.util.HashMap;
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

    public void team_create_team(Gamer a, String b) {
        Gamer gamer = context.getGamers().get(a.getUuid());
        if (gamer != null & b != null) {
            Team team = new Team(gamer, b);
            if (!context.getTeams().containsKey(b)) {
                context.getTeams().put(b, team);
                gamer.setTeam(team.getName());
            }
        }
    }

    public void team_add_gamer(Team a, Gamer b) {
        Gamer gamer = context.getGamers().get(b.getUuid());
        Team team = context.getTeams().get(a.getName());
        if (team != null && gamer != null) {
            team.addMember(gamer);
            gamer.setTeam(team.getName());
        }
    }

    public void team_remove_gamer(Team a, Gamer b) {
        Gamer gamer = context.getGamers().get(b.getUuid());
        Team team = context.getTeams().get(a.getName());
        if (team != null && gamer != null) {
            team.removeMember(gamer);
            gamer.setTeam("");
            gamer.clearGroups();
        }
    }

    public void context_create_gamer(UUID uuid) {
        if (!context.getGamers().containsKey(uuid)) {
            Gamer gamer = new Gamer(uuid);
            context.getGamers().put(uuid, gamer);
        }
    }

    public void gamer_add_friend(Gamer a, Gamer b) {
        Gamer gamer1 = context.getGamers().get(a.getUuid());
        Gamer gamer2 = context.getGamers().get(b.getUuid());
        gamer1.addFriend(gamer2);
    }


    private void gamer_remove_friend(Gamer a, Gamer b) {
        Gamer gamer1 = context.getGamers().get(a.getUuid());
        Gamer gamer2 = context.getGamers().get(b.getUuid());
        gamer1.removeFriend(gamer2);
    }

    private void gamer_friend_list(Gamer a) {
        Gamer gamer = context.getGamers().get(a.getUuid());
        gamer.friendList();
    }
    private void region_add_plot(Region a, Plot b){
      Team team = context.getTeam(a.getTeam());
      Region region = team.getRegion(a.getName());
      Plot plot = context.getPlot(b.getId());
      region.addPlot(plot);
    }
    private void region_remove_plot(Region a, Plot b){
        Team team = context.getTeam(a.getTeam());
        Region region = team.getRegion(a.getName());
        Plot plot = context.getPlot(b.getId());
        region.removePlot(plot);
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
        plot_set_owner(plot,owner);
    }


    public void plot_set_owner(Plot a, String address) {
        UUID ownerUUID = context.getLinks().getOrDefault(address, null);
        Plot plot = context.getPlot(a.getId());
        plot.setOwner(address,ownerUUID);
    }

    private void team_delegate_plot(Team a, Plot b) {
        Team team = context.getTeam(a.getName());
        Plot plot = context.getPlot(b.getId());
        plot_reclaim_plot(plot);
        team.addPlot(plot);
        plot.setTeam(team.getName());
    }

    private void plot_reclaim_plot(Plot a) {
        Plot plot = context.getPlot(a.getId());
        plot.removeTeam();
    }

    private void gamer_link_address(Gamer a, String address){
      Gamer gamer = context.getGamer(a.getUuid());
      gamer.setAddress(address);
      context.getLinks().put(address,gamer.getUuid());
    }

    private void team_create_region(Team a, String name) {
      Team team = context.getTeam(a.getName());
      team.createRegion(name);
    }

    private void team_delete_region(Team a, Region region) {
        Team team = context.getTeam(a.getName());
        team.removeRegion(region.getName());
    }
    private void group_add_gamer(Group a, Gamer b) {
        Gamer gamer = context.getGamer(b.getUuid());
        Group group = gamer.getGroupObject(a.getName());
        gamer.addGroup(group);
        group.addMember(gamer);
    }
    private void group_remove_gamer(Group a, Gamer b) {
        Team team = a.getTeamObject();
        Gamer gamer = context.getGamer(b.getUuid());
        Group group = team.getGroup(a.getName());
        group.removeMember(b);
        gamer.removeGroup(a.getName());
    }

    private void team_create_group(Team a, String b) {
        Team team = context.getTeam(a.getName());
        team.createGroup(b);
    }

    private void group_set_priority(Group a, Integer b) {
        Team team = context.getTeam(a.getTeamObject().getName());
        Group group = team.getGroup(a.getName());
        group.setPriority(b);
    }

    private void team_delete_group(Team a, Group b) {
    }

    private void flag_plot(Gamer gamer){
        Gamer g = context.getGamer(gamer.getUuid());
        FlagCommand.plotMenu(g);
    }

    private void process_command(Message<MasterCommand> message) {
        Object[] _args = message.getArgs();
        switch (message.getCommand()) {
            case context_create_gamer -> context_create_gamer((UUID) _args[0]);
            // gamer commands
            case gamer_add_friend -> gamer_add_friend((Gamer) _args[0], (Gamer) _args[1]);
            case gamer_remove_friend -> gamer_remove_friend((Gamer) _args[0],(Gamer) _args[1]);
            case gamer_friend_list -> gamer_friend_list((Gamer) _args[0]);
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
            //menu commands
            case flag_plot -> flag_plot((Gamer) _args[0]);
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

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
    private static Context context;
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

    private void gamer_update(Gamer gamer) {
        this.channels.gamer_update.publish(gamer);
    }

    private void global_update(){
        this.channels.global_update.publish(context);
    }

    private void plot_update(Plot plot) {
        this.channels.plot_update.publish(plot);
    }

    private void process_command(Message<MasterCommand> message) {
        Object[] _args = message.getArgs();
        switch (message.getCommand()) {
            case context_create_gamer -> context.context_create_gamer((UUID) _args[0]);
            // gamer commands
            case gamer_add_friend ->context. gamer_add_friend((Gamer) _args[0], (Gamer) _args[1]);
            case gamer_remove_friend -> context.gamer_remove_friend((Gamer) _args[0],(Gamer) _args[1]);
            case gamer_link_address -> context.gamer_link_address((Gamer) _args[0], (String) _args[1]);
            //plot commands
            case plot_update_plot -> context.plot_update_plot((Integer) _args[0], (Integer) _args[1], (Integer) _args[2],(String) _args[3]);
            case plot_set_owner -> context.plot_set_owner((Plot) _args[0], (String) _args[1]);
            case plot_reclaim_plot -> context.plot_reclaim_plot((Plot) _args[0]);
            //team commands
            case team_add_gamer -> context.team_add_gamer((Team) _args[0], (Gamer) _args[1]);
            case team_remove_gamer -> context.team_remove_gamer((Team) _args[0], (Gamer) _args[1]);
            case team_create_team -> context.team_create_team((Gamer) _args[0], (String) _args[1]);
            case team_delete_team -> context.team_delete_team((Team) _args[0]);
            case team_create_group -> context.team_create_group((Team) _args[0], (String) _args[1]);
            case team_delete_group -> context.team_delete_group((Team) _args[0], (Group) _args[1]);
            case team_create_region -> context.team_create_region((Team) _args[0], (String) _args[1]);
            case team_delete_region -> context.team_delete_region((Team) _args[0], (Region) _args[1]);
            case team_delegate_plot -> context.team_delegate_plot((Team)_args[0], (Plot) _args[1]);
            // region commands
            case region_add_plot -> context.region_add_plot((Region) _args[0], (Plot) _args[1]);
            case region_remove_plot -> context.region_remove_plot((Region) _args[0], (Plot) _args[1]);
            case region_set_priority ->context.region_set_priority((Region) _args[0], (Integer) _args[1]);
            //group commands
            case group_add_gamer -> context.group_add_gamer((Group) _args[0], (Gamer) _args[1]);
            case group_remove_gamer -> context.group_remove_gamer((Group) _args[0], (Gamer) _args[1]);
            case group_set_priority -> context.group_set_priority((Group) _args[0], (Integer) _args[1]);
            //flag commands
        }
        global_update();
        save();
    }

    public void save() {
        globalStatePersister.overwrite(gson.toJson(context));
    }

    private void team_update(Team team) {
        this.channels.team_update.publish(team);
    }

}

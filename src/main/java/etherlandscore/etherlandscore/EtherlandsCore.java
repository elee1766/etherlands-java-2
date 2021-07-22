package etherlandscore.etherlandscore;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.listener.PlayerEventListener;
import etherlandscore.etherlandscore.services.MasterService;
import etherlandscore.etherlandscore.slashcommands.CommandDisabler;
import etherlandscore.etherlandscore.slashcommands.FriendCommand;
import etherlandscore.etherlandscore.slashcommands.PlotCommand;
import etherlandscore.etherlandscore.slashcommands.TeamCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

import java.util.ArrayList;
import java.util.List;

public final class EtherlandsCore extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
        List<ServerModule> modules = new ArrayList<>();
        Channels channels = new Channels();
        getLogger().info("Hooking Event ListenerS");
        Fiber playerEventListenerFiber = new ThreadFiber();
        PlayerEventListener playerEventListener = new PlayerEventListener(channels, playerEventListenerFiber);
        modules.add(playerEventListener);
        getServer().getPluginManager().registerEvents(playerEventListener, this);
        new CommandDisabler().disable();
        Fiber teamCommandFiber = new ThreadFiber();
        TeamCommand teamCommand = new TeamCommand(channels, teamCommandFiber);
        modules.add(teamCommand);
        teamCommand.register();
        Fiber plotCommandFiber = new ThreadFiber();
        PlotCommand plotCommand = new PlotCommand(channels, plotCommandFiber);
        modules.add(plotCommand);
        plotCommand.register();
        Fiber friendCommandFiber = new ThreadFiber();
        FriendCommand friendCommand = new FriendCommand(channels, friendCommandFiber);
        modules.add(friendCommand);
        friendCommand.register();
        Fiber databaseFiber = new ThreadFiber();
        modules.add(new MasterService(channels, databaseFiber));
        for (var m : modules) {
            getLogger().info(String.format("Starting MODULE %s", m.getClass().getName()));
            m.start();
        }
        getLogger().info("onEnable is done!");
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

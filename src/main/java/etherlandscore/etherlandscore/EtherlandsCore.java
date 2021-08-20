package etherlandscore.etherlandscore;

import etherlandscore.etherlandscore.Menus.FlagMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.listener.BlockEventListener;
import etherlandscore.etherlandscore.listener.PlayerEventListener;
import etherlandscore.etherlandscore.services.EthereumService;
import etherlandscore.etherlandscore.services.MasterService;
import etherlandscore.etherlandscore.singleton.CouchSingleton;
import etherlandscore.etherlandscore.singleton.EthSingleton;
import etherlandscore.etherlandscore.singleton.LocaleSingleton;
import etherlandscore.etherlandscore.slashcommands.*;
import etherlandscore.etherlandscore.state.Context;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class EtherlandsCore extends JavaPlugin {

  @Override
  public void onDisable() {
    Channels channels = new Channels();
    Bukkit.getServer().getConsoleSender().sendMessage("Saving context and shutting down...");
    channels.master_command.publish(
        new Message<>(MasterCommand.context_save_all));
  }

  @Override
  public void onEnable() {
    getLogger().info("onEnable is called!");
    List<ServerModule> modules = new ArrayList<>();
    Channels channels = new Channels();

    getLogger().info("Creating Locale Singleton");
    LocaleSingleton.getLocale();

    getLogger().info("Creating Couch Singleton");
    CouchSingleton.getCouchSettings();

    getLogger().info("Creating Eth Singleton");
    EthSingleton.getEthSettings();

    var manager = getServer().getPluginManager();
    getLogger().info("Hooking Player Event Listener");
    Fiber playerEventListenerFiber = new ThreadFiber();
    PlayerEventListener playerEventListener =
        new PlayerEventListener(channels, playerEventListenerFiber);
    modules.add(playerEventListener);
    manager.registerEvents(playerEventListener, this);
    getLogger().info("Hooking Block Event Listener");
    Fiber blockEventListenerFiber = new ThreadFiber();
    BlockEventListener blockEventListener =
        new BlockEventListener(channels, blockEventListenerFiber);
    modules.add(blockEventListener);
    manager.registerEvents(blockEventListener, this);

    getLogger().info("Hooking Commands");
    new CommandDisabler().disable();
    Fiber districtCommandFiber = new ThreadFiber();
    modules.add(new DistrictCommand(channels, districtCommandFiber));
    Fiber groupCommandFiber = new ThreadFiber();
    modules.add(new GroupCommand(channels, groupCommandFiber));
    Fiber teamCommandFiber = new ThreadFiber();
    modules.add(new TeamCommand(channels, teamCommandFiber));
    Fiber plotCommandFiber = new ThreadFiber();
    modules.add(new PlotCommand(channels, plotCommandFiber));
    Fiber friendCommandFiber = new ThreadFiber();
    modules.add(new FriendCommand(channels, friendCommandFiber));
    Fiber flagCommandFiber = new ThreadFiber();
    modules.add(new FlagCommand(channels, flagCommandFiber));
    Fiber flagMenuFiber = new ThreadFiber();
    modules.add(new FlagMenu(channels, flagMenuFiber));
    Fiber gamerCommandFiber = new ThreadFiber();
    modules.add(new GamerCommand(channels, gamerCommandFiber));
    Fiber mapCommandFiber = new ThreadFiber();
    modules.add(new MapCommand(channels, mapCommandFiber));
    getLogger().info("Hooking Ethers");
    Fiber ethersFiber = new ThreadFiber();
    try {
      modules.add(new EthereumService(channels, ethersFiber));
    } catch (IOException e) {
      e.printStackTrace();
    }

    getLogger().info("initializing master service");
    Fiber databaseFiber = new ThreadFiber();
    modules.add(new MasterService(channels, databaseFiber));
    for (var m : modules) {
      getLogger().info(String.format("Starting MODULE %s", m.getClass().getName()));
      m.start();
    }
    getLogger().info("onEnable is done!");
    // Plugin startup logic
  }
}

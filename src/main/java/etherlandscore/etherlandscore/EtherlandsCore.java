package etherlandscore.etherlandscore;

import dev.jorel.commandapi.CommandAPI;
import etherlandscore.etherlandscore.Menus.FlagMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.listener.BlockEventListener;
import etherlandscore.etherlandscore.listener.ChatEventListener;
import etherlandscore.etherlandscore.listener.PlayerEventListener;
import etherlandscore.etherlandscore.listener.SignEventListener;
import etherlandscore.etherlandscore.services.ChatService;
import etherlandscore.etherlandscore.services.EthereumService;
import etherlandscore.etherlandscore.services.MasterService;
import etherlandscore.etherlandscore.services.Scheduler;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.slashcommands.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

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

    getLogger().info("Creating Settings Singleton");
    SettingsSingleton.getSettings();

    var manager = getServer().getPluginManager();
    getLogger().info("Hooking Sign Event Listener");
    Fiber signEventListenerFiber = new ThreadFiber();
    SignEventListener signEventListener =
        new SignEventListener(channels, signEventListenerFiber);
    modules.add(signEventListener);
    manager.registerEvents(signEventListener, this);
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
    getLogger().info("Hooking Chat Event Listener");
    Fiber chatEventListenerFiber = new ThreadFiber();
    ChatEventListener chatEventListener =
        new ChatEventListener(channels, chatEventListenerFiber);
    modules.add(chatEventListener);
    manager.registerEvents(chatEventListener, this);

    getLogger().info("Hooking Commands");
    new CommandDisabler().disable();
    CommandAPI.unregister("help");
    Fiber districtCommandFiber = new ThreadFiber();
    modules.add(new DistrictCommand(channels, districtCommandFiber));
    Fiber scheduleFiber = new ThreadFiber();
    Scheduler scheduler = new Scheduler(channels, scheduleFiber);
    modules.add(scheduler);
    Fiber imageCommandFiber = new ThreadFiber();
    modules.add(new ImageCommand(channels, imageCommandFiber));
    Fiber chatCommandFiber = new ThreadFiber();
    modules.add(new ChatCommand(channels, chatCommandFiber));
    Fiber tradeCommandFiber = new ThreadFiber();
    modules.add(new TradeCommand(channels, tradeCommandFiber));
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
    Fiber generalCommandFiber = new ThreadFiber();
    modules.add(new GeneralCommand(channels, generalCommandFiber));

    getLogger().info("Hooking Ethers");
    Fiber ethersFiber = new ThreadFiber();
    try {
      EthereumService es = new EthereumService(channels, ethersFiber);
      modules.add(es);
    } catch (Exception e) {
      e.printStackTrace();
    }
    getLogger().info("initializing master service");
    Fiber databaseFiber = new ThreadFiber();
    modules.add(new MasterService(channels, databaseFiber));
    getLogger().info("initializing chat service");
    Fiber chatFiber = new ThreadFiber();
    modules.add(new ChatService(channels, chatFiber));
    for (var m : modules) {
      getLogger().info(String.format("Starting MODULE %s", m.getClass().getName()));
      m.start();
    }

    getLogger().info("onEnable is done!");
    channels.master_command.publish(
        new Message<>(MasterCommand.map_rerender_maps));
  }
}

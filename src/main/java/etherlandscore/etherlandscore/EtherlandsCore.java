package etherlandscore.etherlandscore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import etherlandscore.etherlandscore.Menus.FlagMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.listener.BlockEventListener;
import etherlandscore.etherlandscore.listener.PlayerEventListener;
import etherlandscore.etherlandscore.services.EthereumService;
import etherlandscore.etherlandscore.services.MasterService;
import etherlandscore.etherlandscore.singleton.LocaleSingleton;
import etherlandscore.etherlandscore.slashcommands.*;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class EtherlandsCore extends JavaPlugin {

  public EtherlandsCore()
  {
    super();
  }

  protected EtherlandsCore(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
  {
    super(loader, description, dataFolder, file);
  }

  @Override
  public void onEnable() {
    CommandAPI.onEnable(this);
    getLogger().info("onEnable is called!");
    List<ServerModule> modules = new ArrayList<>();
    Channels channels = new Channels();
    getLogger().info("Creating Locale Singleton");
    LocaleSingleton.getLocale();
    getLogger().info("Hooking Event Listeners");
    Fiber playerEventListenerFiber = new ThreadFiber();
    PlayerEventListener playerEventListener =
        new PlayerEventListener(channels, playerEventListenerFiber);
    modules.add(playerEventListener);
    getServer().getPluginManager().registerEvents(playerEventListener, this);
    Fiber blockEventListenerFiber = new ThreadFiber();
    BlockEventListener blockEventListener =
        new BlockEventListener(channels, blockEventListenerFiber);
    modules.add(blockEventListener);
    getServer().getPluginManager().registerEvents(blockEventListener, this);
    new CommandDisabler().disable();
    Fiber regionCommandFiber = new ThreadFiber();
    modules.add(new RegionCommand(channels,regionCommandFiber));
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
    Fiber ethersFiber = new ThreadFiber();
    try {
      modules.add(new EthereumService(channels, ethersFiber));
    } catch (IOException e) {
      e.printStackTrace();
    }
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
  public void onLoad(){
    CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true)); //Load with verbose output
  }

}

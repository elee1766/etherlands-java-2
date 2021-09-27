package etherlandscore.etherlandscore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.jorel.commandapi.CommandAPI;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.listener.BlockEventListener;
import etherlandscore.etherlandscore.listener.ChatEventListener;
import etherlandscore.etherlandscore.listener.PlayerEventListener;
import etherlandscore.etherlandscore.listener.SignEventListener;
import etherlandscore.etherlandscore.services.*;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.slashcommands.*;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandDisabler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

import java.util.ArrayList;
import java.util.List;

public final class EtherlandsCore extends JavaPlugin {
  private ProtocolManager protocolManager;
  @Override
  public void onDisable() {
    Channels channels = new Channels();
    Bukkit.getServer().getConsoleSender().sendMessage("Saving context and shutting down...");
    channels.master_command.publish(
        new Message<>(MasterCommand.context_save_all));
  }

  @Override
  public void onEnable() {
    protocolManager = ProtocolLibrary.getProtocolManager();
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
    Fiber imageCommandFiber = new ThreadFiber();
    modules.add(new ImageCommand(channels, imageCommandFiber));
    Fiber toggleCommandFiber = new ThreadFiber();
    modules.add(new ToggleCommand(channels, toggleCommandFiber));
    Fiber chatCommandFiber = new ThreadFiber();
    modules.add(new ChatCommand(channels, chatCommandFiber));
    Fiber tradeCommandFiber = new ThreadFiber();
    modules.add(new TradeCommand(channels, tradeCommandFiber));
    Fiber teamCommandFiber = new ThreadFiber();
    modules.add(new TeamCommand(channels, teamCommandFiber));
    Fiber townCommandFiber = new ThreadFiber();
    modules.add(new TownCommand(channels, townCommandFiber));
    Fiber plotCommandFiber = new ThreadFiber();
    modules.add(new PlotCommand(channels, plotCommandFiber));
    Fiber friendCommandFiber = new ThreadFiber();
    modules.add(new FriendCommand(channels, friendCommandFiber));
    Fiber gamerCommandFiber = new ThreadFiber();
    modules.add(new GamerCommand(channels, gamerCommandFiber));
    Fiber mapCommandFiber = new ThreadFiber();
    modules.add(new MapCommand(channels, mapCommandFiber));
    Fiber generalCommandFiber = new ThreadFiber();
    modules.add(new GeneralCommand(channels, generalCommandFiber));

    getLogger().info("initializing master service");
    Fiber databaseFiber = new ThreadFiber();
    modules.add(new MasterService(channels, databaseFiber));
    getLogger().info("initializing chat service");
    Fiber chatFiber = new ThreadFiber();
    modules.add(new ChatService(channels, chatFiber));

    Fiber scheduleFiber = new ThreadFiber();
    modules.add(new Scheduler(channels, scheduleFiber));

    Fiber metadataFiber = new ThreadFiber();
    modules.add(new ExternalMetadataService(channels, metadataFiber));

    Fiber askerFiber= new ThreadFiber();
    modules.add(new ImpatientAsker(channels, askerFiber));

    Fiber hitterFiber = new ThreadFiber();
    modules.add(new ImpartialHitter(channels, hitterFiber));

    Fiber nftRenderFiber= new ThreadFiber();
    modules.add(new NftRenderingService(channels, nftRenderFiber));

    for (var m : modules) {
      getLogger().info(String.format("Starting MODULE %s", m.getClass().getName()));
      m.start();
    }
    getLogger().info("onEnable is done!");
  }
}

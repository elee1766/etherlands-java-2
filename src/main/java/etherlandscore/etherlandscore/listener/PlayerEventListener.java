package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetlang.fibers.Fiber;

public class PlayerEventListener extends ListenerClient implements Listener {

  private final Channels channels;

  public PlayerEventListener(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Bukkit.getServer().getConsoleSender().sendMessage("hello there!");
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_create_gamer, event.getPlayer().getUniqueId()));
  }
}

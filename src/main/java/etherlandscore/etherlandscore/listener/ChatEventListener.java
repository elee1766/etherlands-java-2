package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import etherlandscore.etherlandscore.state.write.WriteTown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetlang.fibers.Fiber;

public class ChatEventListener extends ListenerClient implements Listener {

  private final Channels channels;

  public ChatEventListener(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
  }

  @EventHandler
  public void messageSent(AsyncPlayerChatEvent event) {
    Bukkit.getLogger().info("MESSAGE SENT");
    String message = event.getMessage();
    Player p = event.getPlayer();
    WriteGamer gamer = (WriteGamer) context.getGamer(p.getUniqueId());
    WriteTown town = (WriteTown) gamer.getTownObject();
    if(gamer.preferences.checkPreference(MessageToggles.TEAM_CHAT)) {
      Bukkit.getLogger().info("town chat enabled");
      event.getRecipients().clear();
      channels.chat_message.publish(new Message<>(ChatTarget.town, town, message, gamer));
    }else if(gamer.preferences.checkPreference(MessageToggles.LOCAL_CHAT)){
      Bukkit.getLogger().info("local chat enabled");
      event.getRecipients().clear();
      channels.chat_message.publish(new Message<>(ChatTarget.local, gamer, 100, message, gamer));
    }else{
      event.getRecipients().clear();
      Bukkit.getLogger().info("nothing enabled");
      channels.chat_message.publish(new Message<>(ChatTarget.global, message, gamer));
    }
  }
}

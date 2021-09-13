package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.Menus.MapMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ChatService;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import etherlandscore.etherlandscore.state.write.WriteShop;
import etherlandscore.etherlandscore.state.write.WriteTeam;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetlang.fibers.Fiber;

public class ChatEventListener extends ListenerClient implements Listener {

  private final Channels channels;
  private final Fiber fiber;

  public ChatEventListener(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    this.fiber = fiber;
  }

  @EventHandler
  public void messageSent(AsyncPlayerChatEvent event) {
    Bukkit.getLogger().info("MESSAGE SENT");
    TextComponent message = new TextComponent("");
    String formatted = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
    message.addExtra(formatted);
    Player p = event.getPlayer();
    WriteGamer gamer = (WriteGamer) context.getGamer(p.getUniqueId());
    WriteTeam team = (WriteTeam) gamer.getTeamObject();
    if(gamer.preferences.teamChat()) {
      Bukkit.getLogger().info("teamchat enabled");
      event.getRecipients().clear();
      channels.chat_message.publish(new Message<>(ChatTarget.team, team, message));
    }else if(gamer.preferences.localChat()){
      Bukkit.getLogger().info("local chat enabled");
      event.getRecipients().clear();
      channels.chat_message.publish(new Message<>(ChatTarget.local, gamer, 100, message));
    }else{
      event.getRecipients().clear();
      Bukkit.getLogger().info("nothing enabled");
      channels.chat_message.publish(new Message<>(ChatTarget.global, message));
    }
  }
}

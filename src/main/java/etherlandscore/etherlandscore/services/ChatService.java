package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Collection;
import java.util.UUID;

public class ChatService extends ListenerClient {

  private final Channels channels;
  private final Fiber fiber;


  public ChatService(Channels channels, Fiber fiber){
    super(channels,fiber);
    this.channels = channels;
    this.fiber = fiber;


    this.channels.chat_message.subscribe(fiber,this::process_chat);
  }

  private void process_chat(Message<ChatTarget> message){
    Object[] _args = message.getArgs();
    switch(message.getCommand()){
      case global -> this.send_global((TextComponent) _args[0]);
      case local -> this.send_local((Gamer) _args[0], (Integer) _args[1], (TextComponent) _args[2]);
      case gamer -> this.send_gamer((Gamer) _args[0], (TextComponent) _args[1]);
      case team -> this.send_team((Team) _args[0], (TextComponent) _args[1]);
    }
  }

  private void send_global(TextComponent message){
    for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
      onlinePlayer.sendMessage(message);
    }
  }
  private void send_team(Team team,TextComponent message){
    for (UUID member : team.getMembers()) {
      Player player = Bukkit.getServer().getPlayer(member);
      if(player != null){
        player.sendMessage(message);
      }
    }
  }
  private void send_local(Gamer gamer,Integer range, TextComponent message){
    Player player = gamer.getPlayer();
    if(player != null){
      Collection<Entity> entities = player.getNearbyEntities(range,range,range);
      for (Entity entity : entities) {
        if(entity.getType() == EntityType.PLAYER){
          entity.sendMessage(message);
        }
      }
    }
  }
  private void send_gamer(Gamer gamer,TextComponent message) {
    Player player = gamer.getPlayer();
    if(player != null){
      player.sendMessage(message);
    }
  }

}

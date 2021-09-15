package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import net.md_5.bungee.api.ChatColor;
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

      case gamer_add_friend_response -> this.gamer_add_friend_response((Gamer) _args[0], (Gamer) _args[1]);
    }
  }

  private void gamer_add_friend_response(Gamer arg, Gamer arg1) {
    if(arg.hasFriend(arg1.getPlayer())){
      arg.getPlayer().sendMessage("You are now friends with " + arg1.getPlayer().getName());
    }else{
      arg.getPlayer().sendMessage("Friend failed to be added");
    }
  }

  private void send_global(TextComponent message){
    Bukkit.getLogger().info("Sending global message");
    TextComponent globalChat = new TextComponent("[G] ");
    globalChat.setColor(ChatColor.GOLD);
    globalChat.addExtra(message);
    for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
      WriteGamer gamer = (WriteGamer) context.getGamer(onlinePlayer.getUniqueId());
      if(gamer.preferences.checkPreference(MessageToggles.GLOBAL_CHAT)){
        onlinePlayer.sendMessage(globalChat);
      }
    }
  }
  private void send_team(Team team,TextComponent message){
    Bukkit.getLogger().info("Sending team message");
    TextComponent teamChat = new TextComponent("[T] ");
    teamChat.setColor(ChatColor.AQUA);
    teamChat.addExtra(message);
    Player owner = Bukkit.getPlayer(team.getOwnerUUID());
    if(owner!=null){
      owner.sendMessage(teamChat);
    }
    for (UUID member : team.getMembers()) {
      Player player = Bukkit.getServer().getPlayer(member);
      if(player != null){
        player.sendMessage(teamChat);
      }
    }
  }
  private void send_local(Gamer gamer,Integer range, TextComponent message){
    Bukkit.getLogger().info("Sending local message");
    TextComponent local = new TextComponent("[L] ");
    local.setColor(ChatColor.LIGHT_PURPLE);
    local.addExtra(message);
    Player player = gamer.getPlayer();
    player.sendMessage(local);
    if(player != null){
      Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("EtherlandsCore"), new Runnable() {
        @Override
        public void run() {
          Collection<Entity> entities = player.getNearbyEntities(range,range,range);
          for (Entity entity : entities) {
            if(entity.getType() == EntityType.PLAYER){
              entity.sendMessage(local);
            }
          }
        }
      });
    }
  }
  private void send_gamer(Gamer gamer,TextComponent message) {
    Bukkit.getLogger().info("Sending gamer message");
    Player player = gamer.getPlayer();
    if(player != null){
      player.sendMessage(message);
    }
  }

}

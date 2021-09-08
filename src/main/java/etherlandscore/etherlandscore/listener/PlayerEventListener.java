package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    Bukkit.getLogger().info("Creating Gamer for: " + event.getPlayer().getUniqueId());
    channels.master_command.publish(
        new Message<>(MasterCommand.context_create_gamer, event.getPlayer().getUniqueId()));
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    int fromx = event.getFrom().getChunk().getX();
    int fromz = event.getFrom().getChunk().getZ();
    int tox = event.getTo().getChunk().getX();
    int toz = event.getTo().getChunk().getZ();

    if(context.getDistrict(fromx, fromz)==null){
      if(context.getDistrict(tox, toz)!=null){
        District d = context.getDistrict(tox, toz);
        String teamname = "none";
        if(d.getTeamObject()!=null){
          teamname = d.getTeamObject().getName();
        }
        event.getPlayer().sendTitle("Entering District: " + d.getIdInt(), "Managed by team: " + teamname, 10, 60, 10);
      }
    }else{
      if(context.getDistrict(tox, toz)!=null){
        District d = context.getDistrict(tox, toz);
        if(d!=context.getDistrict(fromx, fromz)){
          String teamname = "none";
          if(d.getTeamObject()!=null){
            teamname = d.getTeamObject().getName();
          }
          event.getPlayer().sendMessage("Moving to District: " + d.getIdInt() + " Managed by team: " + teamname);
        }
      }else{
        District d = context.getDistrict(fromx, fromz);
        String teamname = "none";
        if(d.getTeamObject()!=null){
          teamname = d.getTeamObject().getName();
        }
        event.getPlayer().sendTitle("Leaving District: " + d.getIdInt(), "Managed by team: " + teamname, 10, 60, 10);
      }
    }
  }
}

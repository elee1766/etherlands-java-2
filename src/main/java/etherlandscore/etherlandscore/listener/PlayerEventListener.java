package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.Menus.MapMenu;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import etherlandscore.etherlandscore.state.write.WriteShop;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetlang.fibers.Fiber;

public class PlayerEventListener extends ListenerClient implements Listener {

  private final Channels channels;
  private final Fiber fiber;

  public PlayerEventListener(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    this.fiber = fiber;
  }

  @EventHandler
  public void clickblock(PlayerInteractEvent event) {
    Player p = event.getPlayer();
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Block b = event.getClickedBlock();
      if (b.getState() instanceof Chest){
        Bukkit.getLogger().info("Clicked on chest");
        WriteShop shop = context.getShop(b.getLocation());
        if(shop!=null){
          Bukkit.getLogger().info("opening inventory");
          p.closeInventory();
          p.openInventory(shop.getInventory());
        }
      }
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    TextComponent welcome = new TextComponent("Welcome to Etherlands!");
    welcome.setColor(ChatColor.GOLD);
    WriteGamer joiner = (WriteGamer) context.getGamer(event.getPlayer().getUniqueId());
    channels.chat_message.publish(new Message<>(ChatTarget.gamer,joiner, welcome));
    Bukkit.getLogger().info("Creating Gamer for: " + event.getPlayer().getUniqueId());
    channels.master_command.publish(
        new Message<>(MasterCommand.context_create_gamer, event.getPlayer().getUniqueId()));
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if(context.getGamer(event.getPlayer().getUniqueId())==null){
      return;
    }
    WriteGamer gamer = (WriteGamer) context.getGamer(event.getPlayer().getUniqueId());
    if(gamer.readToggle(MessageToggles.DISTRICT).equals(ToggleValues.ENABLED)) {
      int fromx = event.getFrom().getChunk().getX();
      int fromz = event.getFrom().getChunk().getZ();
      int tox = event.getTo().getChunk().getX();
      int toz = event.getTo().getChunk().getZ();

      if (context.getDistrict(fromx, fromz) == null) {
        if (context.getDistrict(tox, toz) != null) {
          District d = context.getDistrict(tox, toz);
          String teamname = "none";
          if (d.getTeamObject() != null) {
            teamname = d.getTeamObject().getName();
          }
          event.getPlayer().sendTitle("Entering District: " + d.getIdInt(), "Managed by team: " + teamname, 10, 60, 10);
        }
      } else {
        if (context.getDistrict(tox, toz) != null) {
          District d = context.getDistrict(tox, toz);
          if (d != context.getDistrict(fromx, fromz)) {
            String teamname = "none";
            if (d.getTeamObject() != null) {
              teamname = d.getTeamObject().getName();
            }
            TextComponent move = new TextComponent("Moving to District: " + d.getIdInt() + " Managed by team: " + teamname);
            channels.chat_message.publish(new Message<>(ChatTarget.gamer,gamer, move));
          }
        } else {
          District d = context.getDistrict(fromx, fromz);
          String teamname = "none";
          if (d.getTeamObject() != null) {
            teamname = d.getTeamObject().getName();
          }
          event.getPlayer().sendTitle("Leaving District: " + d.getIdInt(), "Managed by team: " + teamname, 10, 60, 10);
        }
      }
    }
    if(gamer.readToggle(MessageToggles.MAP).equals(ToggleValues.ENABLED)){
      if(!(event.getFrom().getChunk().equals(event.getTo().getChunk()))){
        MapMenu map = new MapMenu(gamer, this.channels, this.fiber);
        map.mapMenu();
      }
    }
  }
}

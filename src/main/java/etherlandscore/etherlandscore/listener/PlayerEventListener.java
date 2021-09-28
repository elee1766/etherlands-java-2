package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.Menus.MapCreator;
import etherlandscore.etherlandscore.actions.BlockAction.PlayerInteractAction;
import etherlandscore.etherlandscore.actions.BlockAction.PlayerSwitchAction;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.Asker;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.write.District;
import etherlandscore.etherlandscore.state.write.Gamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
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
  public void inventoryMove(InventoryMoveItemEvent event){
  }

  @EventHandler
  public void onChestMoveItems (InventoryClickEvent event)
  {

  }

  @EventHandler
  public void onHit(EntityDamageByEntityEvent event){
    if(event.getEntity() instanceof Player){
      if(event.getDamager() instanceof  Player){
        Gamer origin = new Gamer(event.getDamager().getUniqueId());
        Gamer target = new Gamer(event.getEntity().getUniqueId());
        if(origin.hasTown() & target.hasTown()){
          if(origin.getTown().equals(target.getTown())){
            event.setDamage(0);
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onInventoryOpenEvent(InventoryOpenEvent e){
    Inventory inventory = e.getInventory();
    if (inventory.getHolder() instanceof Chest || inventory.getHolder() instanceof DoubleChest){
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    try{
      if(useShop(event)){
        return;
      }
      PlayerInteractAction interactAction;
      PlayerSwitchAction switchAction;
      switch (event.getAction()) {
      case RIGHT_CLICK_AIR, LEFT_CLICK_AIR, LEFT_CLICK_BLOCK:
        interactAction = new PlayerInteractAction(event);
        interactAction.process();
        StateSender.gamerFailAction(channels, interactAction);
        break;
      case RIGHT_CLICK_BLOCK:
        if (
            event.getClickedBlock().getBlockData().getAsString().contains("door") ||
                event.getClickedBlock().getBlockData().getAsString().contains("button") ||
                event.getClickedBlock().getBlockData().getAsString().contains("lever") ||
                event.getClickedBlock().getBlockData().getAsString().contains("pressure")
        ) {
          switchAction = new PlayerSwitchAction(event);
          switchAction.process();
          StateSender.gamerFailAction(channels, switchAction);
        }else{
          interactAction = new PlayerInteractAction(event);
          interactAction.process();
          StateSender.gamerFailAction(channels, interactAction);
        }
        break;
      case PHYSICAL:
        break;
    }
    }catch(Exception e){
      Bukkit.getLogger().warning("Failed to parse Player Interact Event");
      e.printStackTrace();
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    TextComponent welcome = new TextComponent("Welcome to Etherlands!");
    welcome.setColor(ChatColor.GOLD);
    Gamer joiner = (Gamer) Asker.GetGamer(event.getPlayer().getUniqueId());
    channels.chat_message.publish(new Message<>(ChatTarget.gamer,joiner, welcome));
    Bukkit.getLogger().info("Creating Gamer for: " + event.getPlayer().getUniqueId());
    channels.master_command.publish(
        new Message<>(MasterCommand.context_create_gamer, event.getPlayer().getUniqueId()));
    channels.master_command.publish(
        new Message<>(MasterCommand.store_gamer_location, joiner, event.getPlayer().getLocation()));
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Gamer gamer = Asker.GetGamer(event.getPlayer().getUniqueId());
    if(event.getFrom().getChunk()!=event.getTo().getChunk()){
      if(gamer.preferences.checkPreference(MessageToggles.MAP)){
        if(!(event.getFrom().getChunk().equals(event.getTo().getChunk()))){
          MapCreator mapCreator = new MapCreator(Asker.GetGamer(event.getPlayer().getUniqueId()), event.getPlayer().getLocation().getChunk().getX(), event.getPlayer().getLocation().getChunk().getZ());
          BaseComponent map = mapCreator.combined();
          StateSender.sendMap(channels, map, Asker.GetGamer(event.getPlayer().getUniqueId()));
        }
        if(gamer.preferences.checkPreference(MessageToggles.DISTRICT)) {
          int fromx = event.getFrom().getChunk().getX();
          int fromz = event.getFrom().getChunk().getZ();
          int tox = event.getTo().getChunk().getX();
          int toz = event.getTo().getChunk().getZ();
          District fromDistrict = Asker.GetDistrict(fromx,fromz);
          District toDistrict = Asker.GetDistrict(tox,toz);
          if (fromDistrict== null) {
            if (toDistrict != null) {
              String town_name = "none";
              if (toDistrict.getTownObject() != null) {
                town_name = toDistrict.getTownObject().getName();
              }
              event.getPlayer().sendTitle("Entering District: " + toDistrict.getIdInt(), "Managed by town: " + town_name, 10, 60, 10);
            }
          } else {
            if (toDistrict != null) {
              if (toDistrict != Asker.GetDistrict(fromx, fromz)) {
                String town_name = "none";
                if (toDistrict.getTownObject() != null) {
                  town_name = toDistrict.getTownObject().getName();
                }
                TextComponent move = new TextComponent("Moving to District: " + toDistrict.getIdInt() + " ManagetoDistrict by town: " + town_name);
                channels.chat_message.publish(new Message<>(ChatTarget.gamer,gamer, move));
              }
            } else {
              String town_name = "none";
              if (fromDistrict.getTownObject() != null) {
                town_name = fromDistrict.getTownObject().getName();
              }
              event.getPlayer().sendTitle("Leaving District: " + fromDistrict.getIdInt(), "ManagetoDistrict by town: " + town_name, 10, 60, 10);
            }
          }
        }

      }
    }

  }

  private boolean useShop(PlayerInteractEvent event){
    return false;
  }
}

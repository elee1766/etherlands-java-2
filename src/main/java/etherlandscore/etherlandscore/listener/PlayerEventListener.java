package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.Menus.MapMenu;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
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
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetlang.fibers.Fiber;

import java.util.HashSet;
import java.util.Set;

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
    ItemStack item = event.getItem();
    Inventory destination = event.getDestination();
    for(WriteShop shop : context.getShops().values()){
      if(shop.getInventory().equals(destination)){
        if(!(item.equals(shop.getItem()))){
          event.setCancelled(true);
          return;
        }
      }else{
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler
  public void onChestMoveItems (InventoryClickEvent event)
  {
    if (event.getClickedInventory() == null)
    {
      return;
    }
    if ((event.getClickedInventory().getType().equals(InventoryType.CHEST) && event.getCursor() != null) || event.isShiftClick())
    {
      Inventory shopInventory = event.getView().getTopInventory();
      for(WriteShop shop : context.getShops().values()) {
        if(shop.getInventory().equals(shopInventory)){
          if(event.isShiftClick()){
            if(!(event.getCurrentItem().equals(shop.getItem()))){
              event.setCancelled(true);
            }
          } else if(!(event.getCursor().equals(shop.getItem()))) {
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
      for(WriteShop shop : context.getShops().values()){
        if(inventory.equals(shop.getInventory())){
          if(!(context.getGamer(e.getPlayer().getUniqueId()).equals(shop.getOwner()))){
            e.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void clickblock(PlayerInteractEvent event) {
    Player p = event.getPlayer();
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Block b = event.getClickedBlock();
      if (b.getState() instanceof Chest){
        WriteShop shop = context.getShop(b.getLocation());
        if(shop!=null){
          p.closeInventory();
          Set<ItemStack> items = new HashSet<>();
          if(shop.getInventory().isEmpty()){
            p.sendMessage("This shop is empty");
            return;
          }else {
            items.add(shop.getItem());
            GamerTransaction gt = new GamerTransaction(shop.getOwner(), context.getGamer(p.getUniqueId()), 0, shop.getPrice(), shop.getInventory(), p.getInventory(), items, null);
            this.channels.master_command.publish(new Message<>(MasterCommand.context_process_gamer_transaction, gt));
          }
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
    if(gamer.preferences.checkPreference(MessageToggles.DISTRICT)) {
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
    if(gamer.preferences.checkPreference(MessageToggles.MAP)){
      if(!(event.getFrom().getChunk().equals(event.getTo().getChunk()))){
        MapMenu map = new MapMenu(gamer, this.channels, this.fiber);
        map.mapMenu();
      }
    }
  }
}

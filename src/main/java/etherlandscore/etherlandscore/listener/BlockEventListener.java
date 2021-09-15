package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.actions.BlockAction.BlockBreakAction;
import etherlandscore.etherlandscore.actions.BlockAction.BlockPlaceAction;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.write.WriteShop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetlang.fibers.Fiber;

import java.util.Iterator;
import java.util.List;

public class BlockEventListener extends ListenerClient implements Listener {

  public final Fiber fiber;
  public final Channels channels;

  public BlockEventListener(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent breakEvent) {
    if(breakEvent.getBlock().getState() instanceof Chest){
      WriteShop shop = context.getShop(breakEvent.getBlock().getLocation());
      if(shop!=null){
        if(context.getGamer(breakEvent.getPlayer().getUniqueId()).equals(shop.getOwner())){
          shop.getLabel().remove();
          return;
        }else{
          breakEvent.setCancelled(true);
        }
      }
    }
    try {
      BlockBreakAction action = new BlockBreakAction(context, breakEvent);
      boolean code = action.process();
      if (!code) {
        District d = context.getPlot(action.getChunkX(),action.getChunkZ()).getDistrict();
        if(d != null){
          breakEvent.getPlayer().sendMessage("you do not have permission to DESTROY in district " + d.getIdInt());
        }else{
          breakEvent.getPlayer().sendMessage("The Plot at [" + breakEvent.getBlock().getChunk().getX() + ", " + breakEvent.getBlock().getChunk().getZ() + "] is unclaimed");
        }
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      breakEvent.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockSpread(BlockSpreadEvent spreadEvent) {
    Block spreadFrom = spreadEvent.getSource();
    Block spreadTo = spreadEvent.getBlock();
    try {
      District fromDistrict = context.getDistrict(spreadFrom.getChunk().getX(), spreadFrom.getChunk().getZ());
      District toDistrict = context.getDistrict(spreadTo.getChunk().getX(), spreadTo.getChunk().getZ());
      if(fromDistrict!=toDistrict){
        spreadEvent.setCancelled(true);
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      spreadEvent.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockBurn(BlockBurnEvent burnEvent) {
    Block spreadFrom = burnEvent.getIgnitingBlock();
    Block spreadTo = burnEvent.getBlock();
    try {
      District fromDistrict = context.getDistrict(spreadFrom.getChunk().getX(), spreadFrom.getChunk().getZ());
      District toDistrict = context.getDistrict(spreadTo.getChunk().getX(), spreadTo.getChunk().getZ());
      if(fromDistrict!=toDistrict){
        burnEvent.setCancelled(true);
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      burnEvent.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockExplode(EntityExplodeEvent explodeEvent) {
    try {
      Gamer placer = null;
      Entity entity = explodeEvent.getEntity();
      if(entity instanceof TNTPrimed){
        Entity source = ((TNTPrimed) entity).getSource();
        if(source!=null && source instanceof Player){
          placer = context.getGamer(source.getUniqueId());
        }
      }
      if(placer!=null){
        //Bukkit.getLogger().warning(placer.getUuid() + " Placed TNT on " + explodeEvent.getLocation());
      }
      District d = context.getDistrict(explodeEvent.getLocation().getChunk().getX(), explodeEvent.getLocation().getChunk().getX());
      List<Block> blockList = explodeEvent.blockList();
      Iterator<Block> it = blockList.iterator();
      while (it.hasNext()) {
        Block block = it.next();
        if (context.getDistrict(block.getChunk().getX(), block.getChunk().getZ())==null) {
          it.remove();
        }else{
          District db = context.getDistrict(block.getChunk().getX(), block.getChunk().getZ());
          if(db!=d){
            it.remove();
          }
        }
      }
    } catch(Exception e){
      Bukkit.getLogger().warning(e.toString());
      explodeEvent.setCancelled(true);
    }
  }


  @EventHandler
  public void onLiquidFlow(BlockFromToEvent flowEvent) {
    try {
      District dFrom = context.getDistrict(flowEvent.getBlock().getChunk().getX(), flowEvent.getBlock().getChunk().getZ());
      District dTo = context.getDistrict(flowEvent.getToBlock().getChunk().getX(), flowEvent.getToBlock().getChunk().getZ());
      if(dFrom!=dTo){
        flowEvent.setCancelled(true);
      }
    } catch(Exception e){
      Bukkit.getLogger().warning(e.toString());
      flowEvent.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent placeEvent) {
    if(placeEvent.getBlock().getState() instanceof Chest){
      Block chest = placeEvent.getBlock();
      Location loc = chest.getLocation();
      World world = loc.getWorld();
      for(int i = -1; i<2; i++){
        for(int j = -1; j<2; j++){
          Location check = new Location(world, loc.getX()+i, loc.getY(), loc.getZ()+j);
          if(!check.equals(loc)) {
            if (world.getBlockAt(check).getState() instanceof Chest) {
              placeEvent.setCancelled(true);
              return;
            }
          }
        }
      }
    }
    try {
      BlockPlaceAction action = new BlockPlaceAction(context, placeEvent);
      boolean code = action.process();
      if (!code) {
        District dID = context.getPlot(placeEvent.getBlock().getChunk().getX(), placeEvent.getBlock().getChunk().getZ()).getDistrict();
        if(dID != null){
          placeEvent.getPlayer().sendMessage("you do not have permission to BUILD in district " + dID.getIdInt());
        }else{
          placeEvent.getPlayer().sendMessage("The Plot at [" + placeEvent.getBlock().getChunk().getX() + ", " + placeEvent.getBlock().getChunk().getZ() + "] is unclaimed");
        }
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      placeEvent.setCancelled(true);
    }
  }
}

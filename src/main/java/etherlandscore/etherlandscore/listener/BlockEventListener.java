package etherlandscore.etherlandscore.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import etherlandscore.etherlandscore.actions.BlockAction.BlockBreakAction;
import etherlandscore.etherlandscore.actions.BlockAction.BlockExplodeAction;
import etherlandscore.etherlandscore.actions.BlockAction.BlockPlaceAction;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
    try {
      BlockBreakAction action = new BlockBreakAction(context, breakEvent);
      boolean code = action.process();
      if (!code) {
        if(context.getPlot(breakEvent.getBlock().getChunk().getX(), breakEvent.getBlock().getChunk().getZ())!=null) {
          int dID = context.getPlot(breakEvent.getBlock().getChunk().getX(), breakEvent.getBlock().getChunk().getZ()).getDistrict();
          breakEvent.getPlayer().sendMessage("you do not have permission to DESTROY in district " + dID);
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
  public void onBlockExplode(EntityExplodeEvent explodeEvent) {
    Bukkit.getLogger().warning("TNT GO BOOM BOOM");
    try {
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
    try {
      BlockPlaceAction action = new BlockPlaceAction(context, placeEvent);
      boolean code = action.process();
      if (!code) {
        if(context.getPlot(placeEvent.getBlock().getChunk().getX(), placeEvent.getBlock().getChunk().getZ())!=null) {
          int dID = context.getPlot(placeEvent.getBlock().getChunk().getX(), placeEvent.getBlock().getChunk().getZ()).getDistrict();
          placeEvent.getPlayer().sendMessage("you do not have permission to DESTROY in district " + dID);
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

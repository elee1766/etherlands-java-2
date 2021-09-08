package etherlandscore.etherlandscore.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import etherlandscore.etherlandscore.actions.BlockAction.BlockBreakAction;
import etherlandscore.etherlandscore.actions.BlockAction.BlockDestoryAction;
import etherlandscore.etherlandscore.actions.BlockAction.BlockPlaceAction;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetlang.fibers.Fiber;

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
  public void onBlockDestory(BlockDestroyEvent destoryEvent) {
    try {
      BlockDestoryAction action = new BlockDestoryAction(context, destoryEvent);
      boolean code = action.process();
      if(!code){
        Bukkit.getLogger().warning("TNT GO BOOM BOOM");
      }
    } catch(Exception e){
      Bukkit.getLogger().warning(e.toString());
      destoryEvent.setCancelled(true);
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

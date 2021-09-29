package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.actions.BlockAction.BlockBreakAction;
import etherlandscore.etherlandscore.actions.BlockAction.BlockPlaceAction;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.District;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetlang.fibers.Fiber;

import java.util.Iterator;
import java.util.List;

import static etherlandscore.etherlandscore.services.MasterService.state;

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
      BlockBreakAction action = new BlockBreakAction(breakEvent);
      action.process();
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      breakEvent.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockBurn(BlockBurnEvent burnEvent) {
    Block spreadFrom = burnEvent.getIgnitingBlock();
    Block spreadTo = burnEvent.getBlock();
    try {
      District fromDistrict =
          state().getDistrict(spreadFrom.getChunk().getX(), spreadFrom.getChunk().getZ());
      District toDistrict =
          state().getDistrict(spreadTo.getChunk().getX(), spreadTo.getChunk().getZ());
      if (fromDistrict != toDistrict) {
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
      District d =
          state()
              .getDistrict(
                  explodeEvent.getLocation().getChunk().getX(),
                  explodeEvent.getLocation().getChunk().getX());
      List<Block> blockList = explodeEvent.blockList();
      Iterator<Block> it = blockList.iterator();
      while (it.hasNext()) {
        Block block = it.next();
        if (state().getDistrict(block.getChunk().getX(), block.getChunk().getZ()) == null) {
          it.remove();
        } else {
          District db = state().getDistrict(block.getChunk().getX(), block.getChunk().getZ());
          if (db != d) {
            it.remove();
          }
        }
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      explodeEvent.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent placeEvent) {
    try {
      BlockPlaceAction action = new BlockPlaceAction(placeEvent);
      action.process();
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      placeEvent.setCancelled(true);
    }
  }

  @EventHandler
  public void onBlockSpread(BlockSpreadEvent spreadEvent) {
    Block spreadFrom = spreadEvent.getSource();
    Block spreadTo = spreadEvent.getBlock();
    try {
      District fromDistrict =
          state().getDistrict(spreadFrom.getChunk().getX(), spreadFrom.getChunk().getZ());
      District toDistrict =
          state().getDistrict(spreadTo.getChunk().getX(), spreadTo.getChunk().getZ());
      if (fromDistrict != toDistrict) {
        spreadEvent.setCancelled(true);
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      spreadEvent.setCancelled(true);
    }
  }

  @EventHandler
  public void onLiquidFlow(BlockFromToEvent flowEvent) {
    try {
      District dFrom =
          state()
              .getDistrict(
                  flowEvent.getBlock().getChunk().getX(), flowEvent.getBlock().getChunk().getZ());
      District dTo =
          state()
              .getDistrict(
                  flowEvent.getToBlock().getChunk().getX(),
                  flowEvent.getToBlock().getChunk().getZ());
      if (dFrom != dTo) {
        flowEvent.setCancelled(true);
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      flowEvent.setCancelled(true);
    }
  }
}

package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.actions.BlockAction.BlockBreakAction;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
        breakEvent.getPlayer().sendMessage("you do not have permission to DESTROY here");
      }
    } catch (Exception e) {
      Bukkit.getLogger().warning(e.toString());
      breakEvent.setCancelled(true);
    }
  }
}

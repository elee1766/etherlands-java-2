package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
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
  public void onBlockBreak(BlockBreakEvent breakEvent) {}
}

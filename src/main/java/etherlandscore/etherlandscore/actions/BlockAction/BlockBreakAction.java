package etherlandscore.etherlandscore.actions.BlockAction;

import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.read.ReadContext;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakAction extends PermissionedAction {
  private final BlockBreakEvent event;
  private final AccessFlags flag = AccessFlags.DESTROY;

  public BlockBreakAction(ReadContext context, BlockBreakEvent event) {
    super(context, event);
    this.event = event;
  }

  @Override
  public boolean process() {
    Gamer gamer = getContext().getGamer(event.getPlayer().getUniqueId());
    Plot writePlot =
        getContext()
            .getPlot(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
    if(writePlot==null){
      return super.process();
    }
    Boolean canPerform = writePlot.canGamerPerform(this.flag, gamer);
    // ops can always destroy
    if (gamer.getPlayer().isOp()) {
      return super.process();
    }
    if (!canPerform) {
      return super.rollback();
    }
    return super.process();
  }
}

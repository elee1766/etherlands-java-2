package etherlandscore.etherlandscore.actions.BlockAction;

import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.ReadContext;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceAction extends PermissionedAction {
  private final BlockPlaceEvent event;
  private final AccessFlags flag = AccessFlags.BUILD;

  public BlockPlaceAction(ReadContext context, BlockPlaceEvent event) {
    super(context, event);
    this.event = event;
  }

  @Override
  public boolean process() {
    Gamer gamer = getContext().getGamer(event.getPlayer().getUniqueId());
    District district =
        getContext()
            .getDistrict(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
    // ops can always destroy
    if (gamer.getPlayer().isOp()) {
      return super.process();
    }
    if (district == null) {
      return super.rollback();
    }
    boolean canPerform = district.canGamerPerform(this.flag, gamer);
    if (!canPerform) {
      return super.rollback();
    }
    return super.process();
  }
}

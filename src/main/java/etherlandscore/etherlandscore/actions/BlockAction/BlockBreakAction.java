package etherlandscore.etherlandscore.actions.BlockAction;

import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakAction extends PermissionedAction {
  private final BlockBreakEvent event;
  private final AccessFlags flag = AccessFlags.DESTROY;


  public BlockBreakAction(BlockBreakEvent event) {
    super(event);
    this.event = event;
  }

  public Integer getChunkX(){
    return event.getBlock().getChunk().getX();
  }

  public Integer getChunkZ(){
    return event.getBlock().getChunk().getZ();
  }

  @Override
  public boolean process() {
    Gamer gamer = getContext().getGamer(event.getPlayer().getUniqueId());
    // ops can always destroy
    if (gamer.getPlayer().isOp()) {
      return super.process();
    }

    District writeDistrict=
        getContext()
            .getDistrict(getChunkX(), getChunkZ());
    if (writeDistrict == null) {
      return super.rollback();
    }
    Boolean canPerform = writeDistrict.canGamerPerform(this.flag, gamer);
    if (!canPerform) {
      return super.rollback();
    }
    return super.process();
  }
}

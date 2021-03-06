package etherlandscore.etherlandscore.actions.BlockAction;

import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.District;
import etherlandscore.etherlandscore.state.Gamer;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakAction extends PermissionedAction {
  private final BlockBreakEvent event;
  private final AccessFlags flag = AccessFlags.DESTROY;

  public BlockBreakAction(BlockBreakEvent event) {
    super(event);
    this.event = event;
  }

  public Integer getChunkX() {
    return event.getBlock().getChunk().getX();
  }

  public Integer getChunkZ() {
    return event.getBlock().getChunk().getZ();
  }

  @Override
  public District getDistrict() {
    return getContext()
        .getDistrict(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
  }

  @Override
  public AccessFlags getFlag() {
    return flag;
  }

  @Override
  public Gamer getGamer() {
    return new Gamer(event.getPlayer().getUniqueId());
  }

  @Override
  public boolean process() {
    // ops can always destroy
    if (getGamer().getPlayer().isOp()) {
      return super.process();
    }
    District writeDistrict = getContext().getDistrict(getChunkX(), getChunkZ());
    if (writeDistrict == null) {
      return super.rollback();
    }
    boolean canPerform = getDistrict().canGamerPerform(this.flag, getGamer());
    if (!canPerform) {
      return super.rollback();
    }
    return super.process();
  }
}

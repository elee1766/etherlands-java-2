package etherlandscore.etherlandscore.actions.BlockAction;

import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceAction extends PermissionedAction {
  private final BlockPlaceEvent event;
  private final AccessFlags flag = AccessFlags.BUILD;

  public BlockPlaceAction(BlockPlaceEvent event) {
    super(event);
    this.event = event;
  }

  @Override
  public Integer getChunkX(){
    return event.getBlock().getChunk().getX();
  }
  @Override
  public Integer getChunkZ(){
    return event.getBlock().getChunk().getZ();
  }

  @Override
  public Gamer getGamer() {return getContext().getGamer(event.getPlayer().getUniqueId());}

  @Override
  public District getDistrict() {
    return
        getContext()
            .getDistrict(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
  }

  @Override
  public AccessFlags getFlag() {
    return flag;
  }

  @Override
  public boolean process() {
    Gamer gamer = getContext().getGamer(event.getPlayer().getUniqueId());
    // ops can always destroy
    if (gamer.getPlayer().isOp()) {
      return super.process();
    }
    if (getDistrict() == null) {
      return super.rollback();
    }
    boolean canPerform = getDistrict().canGamerPerform(this.flag, gamer);
    if (!canPerform) {
      return super.rollback();
    }
    return super.process();
  }
}

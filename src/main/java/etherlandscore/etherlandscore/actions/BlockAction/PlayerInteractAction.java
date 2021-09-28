package etherlandscore.etherlandscore.actions.BlockAction;

import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.write.District;
import etherlandscore.etherlandscore.state.write.Gamer;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractAction extends PermissionedAction {
  private final PlayerInteractEvent event;
  private final AccessFlags flag = AccessFlags.INTERACT;

  public PlayerInteractAction(PlayerInteractEvent event) {
    super(event);
    this.event = event;
  }

  public Integer getChunkX() {
    if (event.getClickedBlock() == null) {
      return event.getPlayer().getChunk().getX();
    }
    return event.getClickedBlock().getChunk().getX();
  }

  public Integer getChunkZ() {
    if (event.getInteractionPoint() == null) {
      return event.getPlayer().getChunk().getZ();
    }
    return event.getInteractionPoint().getChunk().getZ();
  }

  @Override
  public District getDistrict() {
    return getContext().getDistrict(getChunkX(), getChunkZ());
  }

  @Override
  public AccessFlags getFlag() {
    return flag;
  }

  @Override
  public Gamer getGamer() {
    return getContext().getGamer(event.getPlayer().getUniqueId());
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

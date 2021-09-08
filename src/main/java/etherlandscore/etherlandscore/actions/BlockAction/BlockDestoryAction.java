package etherlandscore.etherlandscore.actions.BlockAction;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.ReadContext;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockDestoryAction extends PermissionedAction {
  private final BlockDestroyEvent event;
  private final AccessFlags flag = AccessFlags.DESTROY;

  public BlockDestoryAction(ReadContext context, BlockDestroyEvent event) {
    super(context, event);
    this.event = event;
  }

  @Override
  public boolean process() {
    District writeDistrict=
        getContext()
            .getDistrict(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
    // ops can always destroy
    if (writeDistrict == null) {
      Bukkit.getLogger().info("there is no district here");
      return super.rollback();
    }
    return super.process();
  }
}

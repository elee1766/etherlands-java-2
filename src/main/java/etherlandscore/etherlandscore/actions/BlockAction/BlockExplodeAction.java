package etherlandscore.etherlandscore.actions.BlockAction;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.ReadContext;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;

public class BlockExplodeAction extends PermissionedAction {
  private final BlockExplodeEvent event;

  public BlockExplodeAction(ReadContext context, BlockExplodeEvent event) {
    super(context, event);
    this.event = event;
  }

  @Override
  public boolean process() {
    District writeDistrict=
        getContext()
            .getDistrict(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
    if (writeDistrict == null) {
      Bukkit.getLogger().info("there is no district here rolling back");
      return super.rollback();
    }else {
      Bukkit.getLogger().info("tnt smash");
      return super.process();
    }
  }
}

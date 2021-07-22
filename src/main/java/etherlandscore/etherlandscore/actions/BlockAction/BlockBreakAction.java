package etherlandscore.etherlandscore.actions.BlockAction;

import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.*;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class BlockBreakAction extends PermissionedAction {
  private final BlockBreakEvent event;
  private final AccessFlags flag = AccessFlags.DESTROY;

  public BlockBreakAction(Context context, BlockBreakEvent event) {
    super(context, event);
    this.event = event;
  }

  @Override
  public boolean process() {
    Gamer gamer = getContext().getGamer(event.getPlayer().getUniqueId());
    Plot plot =
        getContext()
            .findPlot(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
    // ops can always build
    if (gamer.getPlayer().isOp()) {
      return super.process();
    }
    if (plot == null) {
      return super.rollback();
    }
    // first check if plot is in a team
    if (plot.hasTeam()) {
      Team team = getContext().getTeam(plot.getTeam());
      Set<String> regionNames = plot.getRegions();
      SortedSet<Region> regionSet = new TreeSet<>();
      for (String regionName : regionNames) {
        regionSet.add(team.getRegion(regionName));
      }
      for (Region region : regionSet) {
        switch (region.checkFlags(this.flag, gamer)) {
          case ALLOW:
            return super.process();
          case DENY:
            return super.rollback();
          default:
        }
      }
      super.rollback();
    } else {
      Gamer owner = getContext().getGamer(plot.getOwner());
      if (owner == null) {
        return super.rollback();
      }
      if (owner.getFriends().contains(gamer.getUuid())) {
        return super.process();
      }
    }
    return super.rollback();
  }
}

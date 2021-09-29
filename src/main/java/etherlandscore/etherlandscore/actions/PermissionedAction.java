package etherlandscore.etherlandscore.actions;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.read.ReadContext;
import etherlandscore.etherlandscore.state.District;
import etherlandscore.etherlandscore.state.Gamer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import static etherlandscore.etherlandscore.services.MasterService.state;

public abstract class PermissionedAction {
  private final Event event;
  private boolean gamer_failed = false;

  public PermissionedAction(Event event) {
    this.event = event;
  }

  public abstract Integer getChunkX();

  public abstract Integer getChunkZ();

  public ReadContext getContext() {
    return state();
  }

  public abstract District getDistrict();

  public abstract AccessFlags getFlag();

  public abstract Gamer getGamer();

  public boolean hasFailed() {
    return this.gamer_failed;
  }

  public boolean process() {
    return true;
  }

  public boolean rollback() {
      this.gamer_failed = true;
      if (event instanceof Cancellable) {
        ((Cancellable) event).setCancelled(true);
      }
    return false;
  }
}

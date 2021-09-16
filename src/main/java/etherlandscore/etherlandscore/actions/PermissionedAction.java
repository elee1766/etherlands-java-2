package etherlandscore.etherlandscore.actions;

import etherlandscore.etherlandscore.state.read.ReadContext;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class PermissionedAction {
  private final Event event;

  public PermissionedAction(Event event) {
    this.event = event;
  }

  public ReadContext getContext() {
    return state();
  }

  public boolean process() {
    return true;
  }

  public boolean rollback() {
    if (event instanceof Cancellable) {
      ((Cancellable) event).setCancelled(true);
    }
    return false;
  }
}

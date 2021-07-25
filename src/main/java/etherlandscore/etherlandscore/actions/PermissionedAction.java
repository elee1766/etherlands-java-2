package etherlandscore.etherlandscore.actions;

import etherlandscore.etherlandscore.readonly.ReadContext;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PermissionedAction {
  private final Event event;
  private final ReadContext context;

  public PermissionedAction(ReadContext context, Event event) {
    this.event = event;
    this.context = context;
  }

  public ReadContext getContext() {
    return context;
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

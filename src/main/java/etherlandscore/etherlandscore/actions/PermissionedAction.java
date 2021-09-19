package etherlandscore.etherlandscore.actions;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.ReadContext;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import static etherlandscore.etherlandscore.services.MasterService.state;

public abstract class PermissionedAction {
  private final Event event;
  private boolean gamer_failed = false;
  private TextComponent failureMessage;

  public PermissionedAction(Event event) {
    this.event = event;
  }

  public abstract Integer getChunkX();
  public abstract Integer getChunkZ();
  public abstract District getDistrict();

  public abstract AccessFlags getFlag();

  public abstract Gamer getGamer();

  public ReadContext getContext() {
    return state();
  }

  public boolean process() {
    return true;
  }
  public boolean hasFailed(){
    return this.gamer_failed;
  }
  public boolean hasPermission(){
    return false;
  }

  public boolean rollback() {
    if (!this.hasPermission()) {
      this.gamer_failed = true;
      if (event instanceof Cancellable) {
        ((Cancellable) event).setCancelled(true);
      }
    }
    return false;
  }
}

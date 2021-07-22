package etherlandscore.etherlandscore.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public interface PlayerDriven extends Cancellable {
  @NotNull
  Player getPlayer();
}

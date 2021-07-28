package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.Menus.MapMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import org.jetlang.fibers.Fiber;

public class MapCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public MapCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void register() {
    CommandAPICommand MapCommand =
        new CommandAPICommand("map")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  MapMenu map =
                      new MapMenu(
                          context.getGamer(sender.getUniqueId()), this.channels, this.fiber);
                  map.mapMenu();
                });
    MapCommand.register();
  }
}

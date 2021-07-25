package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.Menus.GamerPrinter;
import etherlandscore.etherlandscore.Menus.MapMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.Gamer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class MapCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final LocaleStrings locales = new LocaleStrings();

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
                  MapMenu map = new MapMenu(context.getGamer(sender.getUniqueId()),channels,fiber);
                  map.mapMenu();
                });
    MapCommand.register();
  }
}

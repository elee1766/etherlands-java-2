package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import etherlandscore.etherlandscore.Menus.ComponentCreator;
import etherlandscore.etherlandscore.Menus.MapCreator;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.Gamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class MapCommand extends CommandProcessor {
  private final Fiber fiber;
  private final Channels channels;

  public MapCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void auto(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    switch (gamer.getPreferences().toggle(MessageToggles.MAP)) {
      case ENABLED -> {
        StateSender.sendGamerComponent(channels,gamer, ComponentCreator.ColoredText("auto-map enabled", ChatColor.GREEN));
      }
      case DISABLED -> {
        StateSender.sendGamerComponent(channels,gamer, ComponentCreator.ColoredText("auto-map disabled",ChatColor.RED));
      }
    }
  }

  void map(Player sender, Object[] args) {
    MapCreator mapCreator =
        new MapCreator(
            context.getGamer(sender.getUniqueId()),
            sender.getLocation().getChunk().getX(),
            sender.getLocation().getChunk().getZ());
    BaseComponent map = mapCreator.combined();
    StateSender.sendMap(channels, map, context.getGamer(sender.getUniqueId()));
  }

  void mapCoords(Player sender, Object[] args) {
    MapCreator mapCreator =
        new MapCreator(
            context.getGamer(sender.getUniqueId()), (Integer) args[0], (Integer) args[1]);
    BaseComponent map = mapCreator.combined();
    StateSender.sendMap(channels, map, context.getGamer(sender.getUniqueId()));
  }

  public void register() {
    CommandAPICommand MapCommand =
        createPlayerCommand("map", SlashCommands.map, this::map)
            .withPermission("etherlands.public");
    MapCommand.withSubcommand(
        createPlayerCommand("coord", SlashCommands.coord, this::mapCoords)
            .withArguments(new IntegerArgument("x"))
            .withArguments(new IntegerArgument("z")));
    MapCommand.withSubcommand(createPlayerCommand("auto", SlashCommands.auto, this::auto));
    MapCommand.register();
  }
}

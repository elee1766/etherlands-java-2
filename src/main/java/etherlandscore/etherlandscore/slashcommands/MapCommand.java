package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.MapMenu;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
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

  void map(Player sender, Object[] args) {
    MapMenu map = new MapMenu(context.getGamer(sender.getUniqueId()), this.channels, this.fiber);
    map.mapMenu();
  }

  void mapCoords(Player sender, Object[] args) {
    MapMenu map = new MapMenu(context.getGamer(sender.getUniqueId()), this.channels, this.fiber);
    map.mapMenuCoord((String) args[0],(Integer) args[1],(Integer) args[2]);
  }

  void auto(Player sender, Object[] args){
    WriteGamer gamer = (WriteGamer) context.getGamer(sender.getUniqueId());
    if (gamer.preferences.checkPreference(MessageToggles.MAP)) {
      GamerSender.setMessageToggle(channels, MessageToggles.MAP, ToggleValues.DISABLED, gamer);
    }else{
      GamerSender.setMessageToggle(channels, MessageToggles.MAP, ToggleValues.ENABLED, gamer);
    }
  }

  public void register() {
    CommandAPICommand MapCommand =
        createPlayerCommand("map",SlashCommands.map,this::map).withPermission("etherlands.public");
    MapCommand.withSubcommand(
        createPlayerCommand("coord",SlashCommands.coord,this::mapCoords)
            .withArguments(new StringArgument("facing"))
            .withArguments(new IntegerArgument("x"))
            .withArguments(new IntegerArgument("z"))
    );
    MapCommand.withSubcommand(
        createPlayerCommand("auto", SlashCommands.auto,this::auto)
    );
    MapCommand.register();
  }
}

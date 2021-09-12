package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.MapMenu;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import org.bukkit.entity.Player;
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
    if (gamer.readToggle(MessageToggles.MAP).equals(ToggleValues.ENABLED)) {
      GamerSender.setMessageToggle(channels, MessageToggles.MAP, ToggleValues.DISABLED, gamer);
    }else{
      GamerSender.setMessageToggle(channels, MessageToggles.MAP, ToggleValues.ENABLED, gamer);
    }
  }

  public void register() {
    CommandAPICommand MapCommand =
        new CommandAPICommand("map").withPermission("etherlands.public").executesPlayer(this::map);
    MapCommand.withSubcommand(
        new CommandAPICommand("coord")
            .withArguments(new StringArgument("facing"))
            .withArguments(new IntegerArgument("x"))
            .withArguments(new IntegerArgument("z"))
        .executesPlayer(this::mapCoords));
    MapCommand.withSubcommand(
        new CommandAPICommand("auto")
        .executesPlayer(this::auto));
    MapCommand.register();
  }
}

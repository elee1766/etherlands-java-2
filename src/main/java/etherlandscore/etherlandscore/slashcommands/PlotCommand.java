package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.read.Plot;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Set;

public class PlotCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public PlotCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void register() {
    CommandAPICommand ChunkCommand =
        new CommandAPICommand("plot").withAliases("p")
            .withPermission("etherlands.public")
            .withArguments(new IntegerArgument("PlotID"))
            .executesPlayer(this::coord);
    ChunkCommand.register();
    CommandAPICommand ChunkCommandidGiven =
        new CommandAPICommand("plot")
            .withPermission("etherlands.public")
            .withArguments(new IntegerArgument("X"))
            .withArguments(new IntegerArgument("Z"))
            .executesPlayer(this::idGiven);
    ChunkCommandidGiven.register();
    CommandAPICommand ChunkCommandidLocal =
        new CommandAPICommand("plot")
            .withPermission("etherlands.public")
            .executesPlayer(this::idLocal);
    CommandAPICommand reddisID =
        new CommandAPICommand("plotRed")
            .withPermission("etherlands.public")
            .withArguments(new StringArgument("PlotID"))
            .executesPlayer(this::idRed);
    reddisID.register();
    ChunkCommandidLocal.register();
  }

  void idRed(Player player, Object[] args) {
    RedisGetter redis = new RedisGetter();
    String idString = (String) args[0];
    String x = redis.getPlotX(idString);
    String z = redis.getPlotZ(idString);
    Set<String> district = redis.getDistrictOfPlot(idString);
    player.sendMessage("Plot coords: " + x + ", " + z + " district: " + district);
  }

  void idGiven(Player sender, Object[] args) {
    Plot p = context.getPlot((int) args[0], (int) args[1]);
    if(p==null){
      sender.sendMessage("There is no plot here");
    }else{
      TextComponent response = new TextComponent("Plot ID: " + p.getIdInt());
      channels.chat_message.publish(new Message<>(ChatTarget.gamer, context.getGamer(sender.getUniqueId()), response));
    }
  }

  void idLocal(Player sender, Object[] args) {
    Location loc = sender.getLocation();
    Plot p = context.getPlot(loc.getBlockX(), loc.getBlockZ());
    if(p==null){
      sender.sendMessage("There is no plot here");
    }else {
      sender.sendMessage("Plot ID: " + p.getIdInt());
    }
  }

  void coord(Player sender, Object[] args) {
    Plot p = context.getPlot((int) args[0]);
    if(p==null){
      sender.sendMessage("There is no plot here");
    }else{
      sender.sendMessage("Plot coords: " + p.getX() + ", " + p.getZ() + " district: " + p.getDistrict());
    }
  }
}

package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.GamerPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class GamerCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public GamerCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void info(Player sender, Object[] args) {
    Player player = (Player) args[0];
    Gamer gamer = context.getGamer(player.getUniqueId());
    GamerPrinter printer = new GamerPrinter(gamer);
    printer.printGamer(sender);
  }

  void infoLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    GamerPrinter printer = new GamerPrinter(gamer);
    printer.printGamer(sender);
  }

  void link(Object o,Object[] args) {
    Player p = (Player) args[0];
    WriteGamer gamer = (WriteGamer) context.getGamer(p.getUniqueId());
    GamerSender.setAddress(channels, gamer, (String) args[1]);
    Bukkit.getLogger().info(gamer.getPlayer().getName() + " has been linked successfully");
  }

  public void register() {
    CommandAPICommand GamerCommand =
        new CommandAPICommand("gamer").withPermission("etherlands.public").executesPlayer(this::infoLocal);

    GamerCommand.withSubcommand(
        new CommandAPICommand("info")
            .withArguments(
                new PlayerArgument("gamer").replaceSuggestions(info -> getPlayerStrings()))
            .executesPlayer(this::info));

    GamerCommand.withSubcommand(
        new CommandAPICommand("info")
            .executesPlayer(this::infoLocal));

    GamerCommand.withSubcommand(
        new CommandAPICommand("link")
            .withArguments(
                new PlayerArgument("gamer").replaceSuggestions(info -> getPlayerStrings()))
            .withArguments(new StringArgument("address"))
            .executesConsole(this::link));

    GamerCommand.register();
  }
}

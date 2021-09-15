package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.Menus.GamerPrinter;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class GamerCommand extends CommandProcessor {
  private final Fiber fiber;
  private final Channels channels;

  public GamerCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void info(Player sender, Object[] args) {
    OfflinePlayer player = (OfflinePlayer) args[0];
    Gamer gamer = context.getGamer(player.getUniqueId());
    GamerPrinter printer = new GamerPrinter(gamer, fiber, channels);
    printer.printGamer(sender);
  }

  void infoLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    GamerPrinter printer = new GamerPrinter(gamer, fiber, channels);
    printer.printGamer(sender);
  }

  void link(Object o,Object[] args) {
    Player p = (Player) args[0];
    WriteGamer gamer = (WriteGamer) context.getGamer(p.getUniqueId());
    GamerSender.setAddress(channels, gamer, (String) args[1]);
    Bukkit.getLogger().info(gamer.getPlayer().getName() + " has been linked successfully");
  }

  void suicide(Player sender, Object[] args) {
    sender.setHealth(0.0D);
  }

  public void register() {
    CommandAPICommand GamerCommand =
        createPlayerCommand("gamer",SlashCommands.infoLocal,this::infoLocal)
            .withAliases("g")
            .withPermission("etherlands.public");

    // cannot do sender.setHealth async
    CommandAPICommand SuicideCommand =
        new CommandAPICommand("suicide")
            .withAliases("neckrope").withPermission("etherlands.public")
                .executesPlayer(this::suicide);

    GamerCommand.withSubcommand(
        createPlayerCommand("info",SlashCommands.infoGiven,this::info)
            .withAliases("i")
            .withArguments(
                new OfflinePlayerArgument("gamer")
                    .replaceSuggestions(info -> getPlayerStrings()))
    );

    GamerCommand.withSubcommand(
        createPlayerCommand("info", SlashCommands.infoLocal,this::infoLocal)
            .withAliases("i")
            .executesPlayer(this::infoLocal));

    GamerCommand.withSubcommand(
        new CommandAPICommand("link")
            .withArguments(
                new PlayerArgument("gamer").replaceSuggestions(info -> getPlayerStrings()))
            .withArguments(new StringArgument("address"))
            .executesConsole(this::link));

    GamerCommand.register();
    SuicideCommand.register();
  }
}

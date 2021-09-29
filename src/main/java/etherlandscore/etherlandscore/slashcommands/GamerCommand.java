package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.Gamer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class GamerCommand extends CommandProcessor {
  private final Channels channels;



  public GamerCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    register();
    registerSync();
  }

  void info(Player sender, Object[] args) {
    Gamer gamer = state().getGamer(sender.getUniqueId());
    Gamer target = (Gamer) args[0];
    StateSender.sendGamerInfo(channels, gamer, target);
  }

  void infoLocal(Player sender, Object[] args) {
    Gamer gamer = state().getGamer(sender.getUniqueId());
    StateSender.sendGamerInfo(channels, gamer, gamer);
  }

  public void register() {
    CommandAPICommand GamerCommand =
        createPlayerCommand("gamer", SlashCommands.infoLocal, this::infoLocal)
            .withAliases("g")
            .withPermission("etherlands.public");

    createPlayerCommand("gamer", SlashCommands.infoGiven, this::info)
        .withArguments(gamerArgument("gamer"))
        .register();


    GamerCommand.register();
  }

  public void registerSync() {
    // cannot do sender.setHealth async. DO NOT CONVERT THIS TO createPlayerCommand
    CommandAPICommand SuicideCommand =
        new CommandAPICommand("suicide")
            .withAliases("neckrope")
            .withPermission("etherlands.public")
            .executesPlayer(this::suicide);

    SuicideCommand.register();
  }

  void suicide(Player sender, Object[] args) {
    sender.setHealth(0.0D);
  }
}

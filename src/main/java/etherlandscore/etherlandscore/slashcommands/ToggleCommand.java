package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.Menus.ComponentCreator;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.sender.StateSender;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class ToggleCommand extends CommandProcessor {
  private final Channels channels;



  public ToggleCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    register();
  }

  public void register() {
    CommandAPICommand ToggleCommand =
        new CommandAPICommand("toggle").withPermission("etherlands.public");
    CommandAPICommand MoveCommand =
        createPlayerCommand(
            "movement", SlashCommands.moveAlerts, this::toggleDistrictNotifications);
    ToggleCommand.withSubcommand(MoveCommand);
    ToggleCommand.register();
  }

  void toggleDistrictNotifications(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    switch (gamer.getPreferences().toggle(MessageToggles.DISTRICT)) {
      case ENABLED -> {
        StateSender.sendGamerComponent(channels,gamer, ComponentCreator.ColoredText("movement notifications enabled", ChatColor.GREEN));
      }
      case DISABLED -> {
        StateSender.sendGamerComponent(channels,gamer, ComponentCreator.ColoredText("movement notifications disabled",ChatColor.RED));
      }
    }
  }
}

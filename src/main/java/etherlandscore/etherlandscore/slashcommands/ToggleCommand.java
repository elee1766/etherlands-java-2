package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class ToggleCommand extends CommandProcessor {
  private final Channels channels;

  public ToggleCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    register();
  }

  void toggleDistrictNotifications(Player sender, Object[] args) {
    WriteGamer gamer = (WriteGamer) context.getGamer(sender.getUniqueId());
    if(gamer.preferences.checkPreference(MessageToggles.DISTRICT)){
      Bukkit.getLogger().info("Disabling district alerts");
      GamerSender.setMessageToggle(channels, MessageToggles.DISTRICT, ToggleValues.DISABLED, gamer);
    }else{
      Bukkit.getLogger().info("Enabling district alerts");
      GamerSender.setMessageToggle(channels, MessageToggles.DISTRICT, ToggleValues.ENABLED, gamer);
    }
  }

  public void register() {
    CommandAPICommand ToggleCommand =
        new CommandAPICommand("toggle")
            .withPermission("etherlands.public");
    CommandAPICommand MoveCommand =
        createPlayerCommand("movealerts", SlashCommands.moveAlerts,this::toggleDistrictNotifications);
    ToggleCommand.withSubcommand(MoveCommand);
    ToggleCommand.register();
  }
}

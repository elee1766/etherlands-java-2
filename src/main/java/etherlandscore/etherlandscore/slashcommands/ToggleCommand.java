package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Map;

public class ToggleCommand extends ListenerClient {
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();
  private final Fiber fiber;
  private final Channels channels;

  public ToggleCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void toggleDistrictNotifications(Player sender, Object[] args) {
    WriteGamer gamer = (WriteGamer) context.getGamer(sender.getUniqueId());
    if(gamer.preferences.checkPreference(MessageToggles.DISTRICT)){
      Bukkit.getLogger().info("Disabling movealerts");
      GamerSender.setMessageToggle(channels, MessageToggles.DISTRICT, ToggleValues.DISABLED, gamer);
    }else{
      Bukkit.getLogger().info("Enabling movealerts");
      GamerSender.setMessageToggle(channels, MessageToggles.DISTRICT, ToggleValues.ENABLED, gamer);
    }
  }

  public void register() {
    CommandAPICommand ToggleCommand =
        new CommandAPICommand("toggle")
            .withPermission("etherlands.public");
    CommandAPICommand MoveCommand =
        new CommandAPICommand("movealerts").executesPlayer(this::toggleDistrictNotifications);
    ToggleCommand.withSubcommand(MoveCommand);
    ToggleCommand.register();
  }
}

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
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Map;

public class ChatCommand extends ListenerClient {
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();
  private final Fiber fiber;
  private final Channels channels;

  public ChatCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void toggleTeam(Player sender, Object[] args) {
    WriteGamer gamer = (WriteGamer) context.getGamer(sender.getUniqueId());
    if (gamer.readToggle(MessageToggles.TEAM_CHAT).equals(ToggleValues.ENABLED)) {
      GamerSender.setMessageToggle(channels, MessageToggles.TEAM_CHAT, ToggleValues.DISABLED, gamer);
    }else{
      GamerSender.setMessageToggle(channels, MessageToggles.TEAM_CHAT, ToggleValues.ENABLED, gamer);
    }
  }

  void toggleGlobal(Player sender, Object[] args){
    WriteGamer gamer = (WriteGamer) context.getGamer(sender.getUniqueId());
    if (gamer.readToggle(MessageToggles.GLOBAL_CHAT).equals(ToggleValues.ENABLED)) {
      GamerSender.setMessageToggle(channels, MessageToggles.GLOBAL_CHAT, ToggleValues.DISABLED, gamer);
    }else{
      GamerSender.setMessageToggle(channels, MessageToggles.GLOBAL_CHAT, ToggleValues.ENABLED, gamer);
    }
  }

  void help(Player sender, Object[] args) {
    TextComponent help = new TextComponent("===CHAT TOGGLES===\n/chat toggle global\n/chat toggle team");
    help.setColor(ChatColor.GOLD);
    channels.chat_message.publish(new Message<>(ChatTarget.gamer, context.getGamer(sender.getUniqueId()), help));
  }

  public void register() {
    CommandAPICommand ChatCommand =
        new CommandAPICommand("chat")
            .withPermission("etherlands.public")
            .executesPlayer(this::help);
    CommandAPICommand ToggleCommand =
        new CommandAPICommand("toggle");
    CommandAPICommand TeamCommand =
        new CommandAPICommand("team").executesPlayer(this::toggleTeam);
    CommandAPICommand GlobalCommand =
        new CommandAPICommand("global").executesPlayer(this::toggleGlobal);
    ChatCommand.register();
  }
}

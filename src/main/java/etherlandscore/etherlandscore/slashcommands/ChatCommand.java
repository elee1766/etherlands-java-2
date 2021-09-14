package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
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
import org.checkerframework.checker.i18n.qual.LocalizableKey;
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
    GamerSender.setMessageToggle(channels, MessageToggles.LOCAL_CHAT, ToggleValues.DISABLED, gamer);
    GamerSender.setMessageToggle(channels, MessageToggles.TEAM_CHAT, ToggleValues.ENABLED, gamer);
  }

  void toggleLocal(Player sender, Object[] args) {
    WriteGamer gamer = (WriteGamer) context.getGamer(sender.getUniqueId());
    GamerSender.setMessageToggle(channels, MessageToggles.TEAM_CHAT, ToggleValues.DISABLED, gamer);
    GamerSender.setMessageToggle(channels, MessageToggles.LOCAL_CHAT, ToggleValues.ENABLED, gamer);
  }

  void toggleGlobal(Player sender, Object[] args){
    WriteGamer gamer = (WriteGamer) context.getGamer(sender.getUniqueId());
    if (gamer.preferences.checkPreference(MessageToggles.GLOBAL_CHAT)) {
      GamerSender.setMessageToggle(channels, MessageToggles.GLOBAL_CHAT, ToggleValues.DISABLED, gamer);
    }else{
      GamerSender.setMessageToggle(channels, MessageToggles.GLOBAL_CHAT, ToggleValues.ENABLED, gamer);
    }
  }

  void sendGlobal(Player sender, Object[] args){
    WriteGamer gamer = (WriteGamer) context.getGamer(sender.getUniqueId());
    GamerSender.setMessageToggle(channels, MessageToggles.LOCAL_CHAT, ToggleValues.DISABLED, gamer);
    GamerSender.setMessageToggle(channels, MessageToggles.TEAM_CHAT, ToggleValues.DISABLED, gamer);
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
    CommandAPICommand GlobalCommand =
        new CommandAPICommand("global").executesPlayer(this::toggleGlobal);
    CommandAPICommand TeamChat =
        new CommandAPICommand("team").executesPlayer(this::toggleTeam);
    CommandAPICommand LocalChat =
        new CommandAPICommand("local").executesPlayer(this::toggleLocal);
    CommandAPICommand GlobalChat =
        new CommandAPICommand("global").executesPlayer(this::sendGlobal);
    ToggleCommand.withSubcommand(GlobalCommand);
    ChatCommand.withSubcommand(TeamChat);
    ChatCommand.withSubcommand(ToggleCommand);
    ChatCommand.withSubcommand(LocalChat);
    ChatCommand.withSubcommand(GlobalChat);
    ChatCommand.register();
  }
}

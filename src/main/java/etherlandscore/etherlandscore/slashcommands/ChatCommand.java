package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class ChatCommand extends CommandProcessor {
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
        createPlayerCommand("chat", SlashCommands.help,this::help);
    CommandAPICommand ToggleCommand =
        new CommandAPICommand("toggle");
    CommandAPICommand GlobalCommand =
        createPlayerCommand("global", SlashCommands.toggleGlobal,this::toggleGlobal);
    CommandAPICommand TeamChat =
        createPlayerCommand("team", SlashCommands.toggleTeam,this::toggleTeam);
    CommandAPICommand LocalChat =
        createPlayerCommand("local", SlashCommands.toggleLocal,this::toggleLocal);
    CommandAPICommand GlobalChat =
        createPlayerCommand("global", SlashCommands.sendGlobal,this::sendGlobal);

    ToggleCommand.withSubcommand(GlobalCommand);
    ChatCommand.withSubcommand(TeamChat);
    ChatCommand.withSubcommand(ToggleCommand);
    ChatCommand.withSubcommand(LocalChat);
    ChatCommand.withSubcommand(GlobalChat);
    ChatCommand.register();
  }

}

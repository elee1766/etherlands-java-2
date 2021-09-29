package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.preferences.UserPreferences;
import etherlandscore.etherlandscore.state.Gamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class ChatCommand extends CommandProcessor {
  private final Channels channels;

  public ChatCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    register();
  }

  void help(Player sender, Object[] args) {
    TextComponent help =
        new TextComponent("===CHAT Channels===\nMute Global: /chat global toggle\nLocal: /chat local\nTown: /chat town\nGlobal: /chat global");
    help.setColor(ChatColor.GOLD);
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer, context.getGamer(sender.getUniqueId()), help));
  }

  public void register() {
    CommandAPICommand ChatCommand = createPlayerCommand("chat", SlashCommands.help, this::help);
    CommandAPICommand GlobalCommand =
        createPlayerCommand("global", SlashCommands.toggleGlobal, this::toggleGlobal)
            .withArguments(new MultiLiteralArgument("toggle"));
    CommandAPICommand TownChat =
        createPlayerCommand("town", SlashCommands.toggleTown, this::toggleTown);
    CommandAPICommand LocalChat =
        createPlayerCommand("local", SlashCommands.toggleLocal, this::toggleLocal);
    CommandAPICommand GlobalChat =
        createPlayerCommand("global", SlashCommands.sendGlobal, this::sendGlobal);

    ChatCommand.withSubcommand(GlobalCommand);
    ChatCommand.withSubcommand(TownChat);
    ChatCommand.withSubcommand(LocalChat);
    ChatCommand.withSubcommand(GlobalChat);
    ChatCommand.register();
  }

  void sendGlobal(Player sender, Object[] args) {
    Gamer gamer = new Gamer(sender.getUniqueId());
    UserPreferences preferences = gamer.getPreferences();
    preferences.set(MessageToggles.TEAM_CHAT, ToggleValues.DISABLED);
    preferences.set(MessageToggles.LOCAL_CHAT, ToggleValues.DISABLED);
    preferences.set(MessageToggles.GLOBAL_CHAT, ToggleValues.ENABLED);
}

  void toggleGlobal(Player sender, Object[] args) {
    Gamer gamer = new Gamer(sender.getUniqueId());
    gamer.getPreferences().toggle(MessageToggles.GLOBAL_CHAT);
  }

  void toggleLocal(Player sender, Object[] args) {
    Gamer gamer = new Gamer(sender.getUniqueId());
    UserPreferences preferences = gamer.getPreferences();
    preferences.set(MessageToggles.TEAM_CHAT, ToggleValues.DISABLED);
    preferences.set(MessageToggles.LOCAL_CHAT, ToggleValues.ENABLED);
  }

  void toggleTown(Player sender, Object[] args) {
    Gamer gamer = new Gamer(sender.getUniqueId());
    UserPreferences preferences = gamer.getPreferences();
    preferences.set(MessageToggles.LOCAL_CHAT, ToggleValues.DISABLED );
    preferences.set(MessageToggles.TEAM_CHAT, ToggleValues.ENABLED);
  }
}

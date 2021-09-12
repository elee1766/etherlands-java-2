package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bouncycastle.util.Arrays;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;
import java.util.Map;

public class GeneralCommand extends ListenerClient {
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();
  private final Fiber fiber;
  private final Channels channels;

  public GeneralCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void rules(Player sender, Object[] args) {
    TextComponent rules = new TextComponent("====== RULES ======\n\n");
    rules.addExtra(settings.get("rules"));
    rules.setColor(ChatColor.GOLD);
    sender.sendMessage(rules);
  }

  void help(Player sender, Object[] args) {
    TextComponent rules = new TextComponent("====== HELP ======\n\n");
    rules.addExtra("\\district \\flag \\friend \n\\gamer \\rules \\group \n\\image \\map \\plot \\team");
    rules.setColor(ChatColor.GOLD);
    sender.sendMessage(rules);
  }

  public void register() {
    CommandAPICommand RulesCommand =
        new CommandAPICommand("rules")
            .withPermission("etherlands.public")
            .executesPlayer(this::rules);
    RulesCommand.register();

    CommandAPICommand HelpCommand =
        new CommandAPICommand("help")
            .withPermission("etherlands.public")
            .executesPlayer(this::help);
    HelpCommand.register();
  }
}

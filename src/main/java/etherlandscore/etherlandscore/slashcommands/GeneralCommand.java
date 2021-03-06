package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.Gamer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class GeneralCommand extends CommandProcessor {
  private final Fiber fiber;
  private final Channels channels;

  public GeneralCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void link(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    StateSender.captcha(channels, gamer);
    Bukkit.getLogger().info(gamer.getUuid() + " captcha sent");
  }

  public void register() {
    CommandAPICommand RulesCommand =
        createPlayerCommand("rules", SlashCommands.rules, this::rules)
            .withPermission("etherlands.public");
    RulesCommand.register();
    CommandAPICommand LinkCommand = new CommandAPICommand("link").executesPlayer(this::link);
    LinkCommand.register();
  }

  void rules(Player sender, Object[] args) {
    TextComponent rules = new TextComponent("====== RULES ======\n\n");
    //rules.addExtra(settings.get("rules").toString());
    //rules.setColor(ChatColor.GOLD);
    //sender.sendMessage(rules);
  }
}

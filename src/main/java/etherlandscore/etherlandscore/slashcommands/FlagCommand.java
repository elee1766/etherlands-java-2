package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.Menus.FlagMenu;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class FlagCommand extends CommandProcessor {
  private final Fiber fiber;
  private final Channels channels;

  public FlagCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void districtGroup(Player sender, Object[] args) {
    Bukkit.getLogger().info(args[0] + ", " + args[1]);
    Gamer runner = context.getGamer(sender.getUniqueId());
    District d = context.getDistrict((int) args[0]);
    FlagMenu.clickMenu(runner, "group", "district set_group", d, (Group) args[1]);
  }

  void districtPlayer(Player sender, Object[] args) {
    Bukkit.getLogger().info(args[0] + ", " + args[1]);
    Gamer runner = context.getGamer(sender.getUniqueId());
    FlagMenu.clickMenu(
        runner, "player", "district set_player", (District) args[0], (Player) args[1]);
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage(
        "/flag menu district_group or district_player, use the onscreen menu to set or unset flags");
  }

  public void register() {
    CommandAPICommand flagMenuDistrictPlayer =
        createPlayerCommand("player", SlashCommands.districtPlayer,this::districtPlayer)
            .withAliases("p")
            .withArguments(new IntegerArgument("districtID"))
            .withArguments(
                new PlayerArgument("gamer").replaceSuggestions(info -> getPlayerStrings())
            );
    CommandAPICommand flagMenuDistrictGroup =
        createPlayerCommand("group",SlashCommands.districtGroup, this::districtGroup)
            .withAliases("g")
            .withArguments(new IntegerArgument("districtID"))
            .withArguments(teamGroupArgument("group"));
    CommandAPICommand FlagCommand =
        createPlayerCommand("flags",SlashCommands.help,this::help)
            .withAliases("f")
            .withArguments(new IntegerArgument("district"))
            .withSubcommand(flagMenuDistrictGroup)
            .withSubcommand(flagMenuDistrictPlayer)
            .withPermission("etherlands.public");

    FlagCommand.register();
  }
}

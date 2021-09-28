package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ImpartialHitter;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.write.Gamer;
import etherlandscore.etherlandscore.state.write.Town;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TownCommand extends CommandProcessor {
  private final Channels channels;
  private final Map<String, Map<UUID, Long>> invites = new HashMap<>();
  private final TextComponent response = new TextComponent("");

  public TownCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    register();
  }

  void create(Player sender, Object[] args) {
    ImpartialHitter.HitWorld(
        "gamer", sender.getUniqueId().toString(), "create_town", (String) args[0]);
  }

  void deleteTown(Player sender, Object[] args) {
    String name = (String) args[0];
    ImpartialHitter.HitWorld("town", name, "delete", sender.getUniqueId().toString(),name,name);
  }

  void help(Player sender, Object[] args) {
    response.setText("Create Info Invite Join Delete Leave Lick Delegate");
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer, context.getGamer(sender.getUniqueId()), response));
  }

  void info(Player sender, Object[] args) {
    Town town = new Town((String) args[0]);
    Gamer gamer = context.getGamer(sender.getUniqueId());
    StateSender.sendInfo(this.channels, gamer, town);
  }

  void infoLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    if (gamer.hasTown()) {
      StateSender.sendInfo(this.channels, gamer, gamer.getTownObject());
    } else {
      response.setText("/town <town_name>");
      channels.chat_message.publish(
          new Message<>(ChatTarget.gamer, context.getGamer(sender.getUniqueId()), response));
    }
  }

  void invite(Player sender, Object[] args) {
    Gamer inviter = context.getGamer(sender.getUniqueId());
    Gamer receiver = context.getGamer(((Player) args[0]).getUniqueId());
    Town town = inviter.getTownObject();
    ImpartialHitter.HitWorld(
        "town",
        town.getName(),
        "invite",
        inviter.getUuid().toString(),
        receiver.getUuid().toString());
  }

  void join(Player sender, Object[] args) {
    Gamer joiner = new Gamer(sender.getUniqueId());
    Town town = new Town((String) args[0]);
    ImpartialHitter.HitWorld("town", town.getName(), "join", joiner.getUuid().toString());
  }

  void kick(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Gamer kicked = (Gamer) args[0];
    Town town = manager.getTownObject();
    ImpartialHitter.HitWorld("town", town.getName(), "kick", manager.getUuid().toString(), kicked.getUuid().toString());
  }

  void leave(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    ImpartialHitter.HitWorld("town", town.getName(), "leave", gamer.getUuid().toString());
  }

  public void register() {
    CommandAPICommand TownCommand =
        createPlayerCommand("town", SlashCommands.infoLocal, this::infoLocal)
            .withPermission("etherlands.public");
    TownCommand.withSubcommand(
        createPlayerCommand("create", SlashCommands.create, this::create)
            .withAliases("new")
            .withArguments(cleanNameArgument("town_name")));
    TownCommand.withSubcommand(
        createPlayerCommand("invite", SlashCommands.invite, this::invite)
            .withAliases("inv")
            .withArguments(
                new PlayerArgument("player").replaceSuggestions(info -> getOnlinePlayerStrings())));
    TownCommand.withSubcommand(
        createPlayerCommand("join", SlashCommands.join, this::join)
            .withArguments(
                new StringArgument("town").replaceSuggestions(info -> getTownStrings())));
    TownCommand.withSubcommand(createPlayerCommand("leave", SlashCommands.leave, this::leave));
    TownCommand.withSubcommand(
        createPlayerCommand("kick", SlashCommands.kick, this::kick)
            .withArguments(townMemberArgument("member")));
    TownCommand.withSubcommand(
        createPlayerCommand("delete", SlashCommands.deleteTown, this::deleteTown)
            .withArguments(new StringArgument("town_name"))
            .executesPlayer(this::deleteTown));

    createPlayerCommand("town", SlashCommands.info, this::info)
        .withArguments(new StringArgument("town").replaceSuggestions(info -> getTownStrings()))
        .register();

    TownCommand.register();
  }
}

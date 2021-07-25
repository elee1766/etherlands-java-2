package etherlandscore.etherlandscore.services;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.readonly.ReadContext;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ListenerClient extends ServerModule {

  public ReadContext context;
  Channels channels;
  Fiber fiber;

  protected ListenerClient(Channels channels, Fiber fiber) {
    super(fiber);
    this.channels = channels;
    this.fiber = fiber;
    this.context = new ReadContext(new Context());
    register();
  }

  public Argument cleanNameArgument(String nodeName) {
    return new CustomArgument<>(nodeName, input -> input.replaceAll("[^a-zA-Z0-9_]", ""));
  }

  public Argument gamerArgument(String nodeName) {
    return new CustomArgument<Gamer>(
            nodeName,
            input -> {
              Player player = Bukkit.getPlayer(input);
              if (player != null) {
                return context.getGamer(player.getUniqueId());
              } else {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("Player not found."));
              }
            })
        .replaceSuggestions(
            sender -> {
              String[] strings = getOnlinePlayerStrings();
              List<String> list = new ArrayList<String>(Arrays.asList(strings));
              list.remove(sender.sender().getName());
              return list.toArray(new String[0]);
            });
  }

  protected String[] getAccessFlagStrings() {
    return Stream.of(AccessFlags.values()).map(AccessFlags::name).toArray(String[]::new);
  }

  protected String[] getChunkStrings() {
    return this.context.getPlots().keySet().stream().map(Object::toString).toArray(String[]::new);
  }

  protected String[] getFlagValueStrings() {
    return Stream.of(FlagValue.values()).map(FlagValue::name).toArray(String[]::new);
  }

  protected String[] getOnlinePlayerStrings() {
    Player[] players = Bukkit.getServer().getOnlinePlayers().toArray(Player[]::new);
    return Arrays.stream(players).map(Player::getName).toArray(String[]::new);
  }

  protected String[] getPlayerStrings() {
    OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();
    return Arrays.stream(players).map(OfflinePlayer::getName).toArray(String[]::new);
  }

  protected String[] getTeamStrings() {
    return this.context.getTeams().keySet().stream().map(Object::toString).toArray(String[]::new);
  }

  private void register() {
    channels.global_update.subscribe(
        fiber,
        global -> {
          context = new ReadContext(global);
        });
  }

  public Argument teamMemberArgument(String nodeName) {
    return new CustomArgument<>(
            nodeName,
            input -> {
              Player player = Bukkit.getPlayer(input);
              if (player != null) {
                return context.getGamer(player.getUniqueId());
              } else {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("Player not found."));
              }
            })
        .replaceSuggestions(
            sender -> {
              String[] strings = getOnlinePlayerStrings();
              List<String> list = new ArrayList<String>(Arrays.asList(strings));
              list.remove(sender.sender().getName());
              return list.toArray(new String[0]);
            });
  }
}

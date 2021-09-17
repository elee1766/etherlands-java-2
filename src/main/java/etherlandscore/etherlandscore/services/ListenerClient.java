package etherlandscore.etherlandscore.services;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.ReadContext;
import etherlandscore.etherlandscore.state.read.Team;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class ListenerClient extends ServerModule {

  public ReadContext context;
  Channels channels;
  Fiber fiber;

  protected ListenerClient(Channels channels, Fiber fiber) {
    super(fiber);
    this.channels = channels;
    this.fiber = fiber;
    register();
  }

  public Argument accessFlagArgument(String nodeName) {
    return new CustomArgument<>(
            nodeName,
            input -> {
              try {
                return AccessFlags.valueOf(input.toUpperCase());
              } catch (Exception e) {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("flag not recognized"));
              }
            })
        .replaceSuggestions(sender -> getAccessFlagStrings());
  }

  public Argument cleanNameArgument(String nodeName) {
    return new CustomArgument<>(nodeName, input -> input.replaceAll("[^a-zA-Z0-9_]", ""));
  }

  public Argument flagValueArgument(String nodeName) {
    return new CustomArgument<>(
            nodeName,
            input -> {
              try {
                return FlagValue.valueOf(input.toUpperCase());
              } catch (Exception e) {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("flag value not recognized"));
              }
            })
        .replaceSuggestions(sender -> getFlagValueStrings());
  }

  public Argument gamerArgument(String nodeName) {
    return new CustomArgument<>(
        nodeName,
        input -> {
          OfflinePlayer player = Bukkit.getOfflinePlayer(input);
          if (!player.hasPlayedBefore()) {
            throw new CustomArgument.CustomArgumentException(
                new CustomArgument.MessageBuilder("Player not found."));
          } else {
            return context.getGamer(player.getUniqueId());
          }
        })
        .replaceSuggestions(
            sender -> {
              String[] strings = getOnlinePlayerStrings();
              List<String> list = new ArrayList<>(Arrays.asList(strings));
              list.remove(sender.sender().getName());
              return list.toArray(new String[0]);
            });
  }

  protected String[] getAccessFlagStrings() {
    return Stream.of(AccessFlags.values()).map(AccessFlags::name).toArray(String[]::new);
  }

  protected String[] getFlagValueStrings() {
    return Stream.of(FlagValue.values()).map(FlagValue::name).toArray(String[]::new);
  }

  protected String[] getOnlinePlayerStrings() {
    Player[] players = Bukkit.getServer().getOnlinePlayers().toArray(Player[]::new);
    return Arrays.stream(players).map(Player::getName).toArray(String[]::new);
  }

  protected Player[] getOnlinePlayers() {
    String[] playerNames = getOnlinePlayerStrings();
    int playerCount = playerNames.length;
    Player[] players = new Player[playerCount];
    for (int i = 0; i < playerCount; i++) {
      players[i] = Bukkit.getPlayer(playerNames[i]);
    }
    return players;
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
        global -> context = new ReadContext(global,channels));
  }

  public Argument teamDistrictArgument(String districtID) {
    return new CustomArgument<>(
            districtID,
            (sender, input) -> {
              Player player = Bukkit.getPlayer(sender.getName());
              if (player != null) {
                Gamer gamer = state().getGamer(player.getUniqueId());
                Team writeTeam = gamer.getTeamObject();
                return writeTeam.getDistrict(Integer.parseInt(districtID));
              } else {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("District not found."));
              }
            });
    /*
        .replaceSuggestions(
            sender -> {
              Player player = Bukkit.getPlayer(sender.sender().getName());
              if (player != null) {
                Gamer gamer = state().getGamer(player.getUniqueId());
                return gamer.getTeamObject().getDistricts().keySet().toArray(new String[0]);
              }
              return null;
            });

     */
  }

  public Argument teamGroupArgument(String nodeName) {
    return new CustomArgument<>(
            nodeName,
            (sender, input) -> {
              Player player = Bukkit.getPlayer(sender.getName());
              if (player != null) {
                Gamer gamer = state().getGamer(player.getUniqueId());
                Team writeTeam = gamer.getTeamObject();
                return writeTeam.getGroup(input);
              } else {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("Group not found."));
              }
            })
        .replaceSuggestions(
            sender -> {
              Player player = Bukkit.getPlayer(sender.sender().getName());
              if (player != null) {
                Gamer gamer = state().getGamer(player.getUniqueId());
                return gamer.getTeamObject().getGroups().keySet().toArray(new String[0]);
              }
              return null;
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
              List<String> list = new ArrayList<>(Arrays.asList(strings));
              list.remove(sender.sender().getName());
              return list.toArray(new String[0]);
            });
  }
}

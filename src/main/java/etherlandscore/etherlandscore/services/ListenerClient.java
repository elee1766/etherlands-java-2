package etherlandscore.etherlandscore.services;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.singleton.Asker;
import etherlandscore.etherlandscore.state.read.ReadContext;
import etherlandscore.etherlandscore.state.write.District;
import etherlandscore.etherlandscore.state.write.Gamer;
import etherlandscore.etherlandscore.state.write.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.*;
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
    this.context = state();
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
                    new CustomArgument.MessageBuilder("Invalid flag"));
              }
            })
        .replaceSuggestions(sender -> getAccessFlagStrings());
  }

  public Argument cleanNameArgument(String nodeName) {
    return new CustomArgument<>(nodeName, input -> input.replaceAll("[^a-zA-Z0-9_]", ""));
  }

  public Argument districtArgument(String districtID) {
    return new CustomArgument<>(districtID, input -> {
      try {
        Integer district_id = Asker.GetDistrictOfName(input);
        return new District(district_id);
      } catch (Exception e) {
        throw new CustomArgument.CustomArgumentException(
            new CustomArgument.MessageBuilder("Invalid district"));
      }
    })
        .replaceSuggestions(
            sender -> {
              Set<String> names = Asker.GetDistrictNames();
              ArrayList<String> output = new ArrayList<>();
              for (String name : names) {
                output.add(name.replace("#", ""));
              }
              return output.toArray(new String[0]);
            });
  }

  public Argument flagValueArgument(String nodeName) {
    return new CustomArgument<>(
            nodeName,
            input -> {
              try {
                return FlagValue.valueOf(input.toUpperCase());
              } catch (Exception e) {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("Invalid flag"));
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
                    new CustomArgument.MessageBuilder("Player not found"));
              } else {
                return context.getGamer(player.getUniqueId());
              }
            })
        .replaceSuggestions(
            sender -> {
              Set<String> names = new HashSet<>();
              OfflinePlayer[] players = Bukkit.getOfflinePlayers();

              for (OfflinePlayer player : players) {
                Gamer gamer = new Gamer(player.getUniqueId());
                if (!gamer.getName().equals("??????")) {
                  names.add(gamer.getName());
                }
              }
              return names.toArray(String[]::new);
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

  protected String[] getTownStrings() {
    return this.context.getTowns().keySet().stream().map(Object::toString).toArray(String[]::new);
  }

  private void register() {
    channels.global_update.subscribe(fiber, global -> context = new ReadContext(global, channels));
  }

  public Argument townMemberArgument(String nodeName) {
    return new CustomArgument<>(
            nodeName,
            input -> {
              OfflinePlayer player = Bukkit.getOfflinePlayer(input);
              if (player.hasPlayedBefore()) {
                return context.getGamer(player.getUniqueId());
              } else {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("Town member not found."));
              }
            })
        .replaceSuggestions(
            sender -> {
              Player player = Bukkit.getPlayer(sender.sender().getName());
              if (player != null) {
                List<String> list = new ArrayList<>();
                Gamer gamer = state().getGamer(player.getUniqueId());
                Town town = gamer.getTownObject();
                Set<Gamer> members = town.getMembers();
                for (Gamer member: members) {
                  list.add(member.getName());
                }
                return list.toArray(new String[0]);
              }
              return null;
            });
  }

  public Argument townTeamArgument(String nodeName) {
    return new CustomArgument<>(
            nodeName,
            (sender, input) -> {
              Player player = Bukkit.getPlayer(sender.getName());
              if (player != null) {
                Gamer gamer = state().getGamer(player.getUniqueId());
                Town town = gamer.getTownObject();
                return town.getTeam(input);
              } else {
                throw new CustomArgument.CustomArgumentException(
                    new CustomArgument.MessageBuilder("Team not found."));
              }
            })
        .replaceSuggestions(
            sender -> {
              Player player = Bukkit.getPlayer(sender.sender().getName());
              if (player != null) {
                Gamer gamer = state().getGamer(player.getUniqueId());
                if (gamer.getTownObject() != null) {
                  return gamer.getTownObject().getTeams().keySet().toArray(new String[0]);
                }
              }
              return null;
            });
  }
}

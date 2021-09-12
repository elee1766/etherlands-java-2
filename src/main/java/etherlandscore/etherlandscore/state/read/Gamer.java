package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface Gamer {
  @Override
  boolean equals(Object o);

  void friendList();

  String getAddress();

  Field[] getDeclaredFields();

  Set<UUID> getFriends();

  Group getGroupObject(String name);

  Set<String> getGroups();

  Player getPlayer();

  String getTeam();

  Team getTeamObject();

  UUID getUuid();

  boolean hasFriend(Player player);

  ToggleValues readToggle(MessageToggles toggle);

  Map<MessageToggles, ToggleValues> getMessageToggles();

  boolean hasTeam();

  @Override
  int hashCode();
}

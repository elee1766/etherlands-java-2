package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.state.preferences.UserPreferences;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
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

  UserPreferences getPreferences();

  String getTeam();

  Team getTeamObject();

  String getName();

  UUID getUuid();

  boolean hasFriend(UUID player);

  boolean hasTeam();

  @Override
  int hashCode();
}

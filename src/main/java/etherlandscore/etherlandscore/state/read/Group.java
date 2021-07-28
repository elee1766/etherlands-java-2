package etherlandscore.etherlandscore.state.read;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public interface Group extends Comparable<Group> {
  @Override
  int compareTo(@NotNull Group o);

  Field[] getDeclaredFields();

  boolean getDefault();

  Set<UUID> getMembers();

  String getName();

  Integer getPriority();

  Team getTeamObject();

  boolean hasMember(Player player);

  boolean isDefault();

  int memberCount();
}

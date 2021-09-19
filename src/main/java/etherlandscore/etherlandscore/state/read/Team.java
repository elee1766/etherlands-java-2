package etherlandscore.etherlandscore.state.read;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public interface Team extends Comparable<Team> {
  @Override
  int compareTo(@NotNull Team o);

  Field[] getDeclaredFields();

  boolean getDefault();

  Set<UUID> getMembers();

  String getName();

  Integer getPriority();

  Town getTownObject();

  boolean hasMember(Player player);

  boolean isDefault();

  int memberCount();
}

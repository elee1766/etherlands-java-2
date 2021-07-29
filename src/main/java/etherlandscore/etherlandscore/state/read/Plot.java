package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.enums.AccessFlags;
import org.bukkit.Chunk;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public interface Plot {
  boolean canGamerPerform(AccessFlags flag, Gamer gamer);

  @Override
  boolean equals(Object o);

  Chunk getChunk();

  Field[] getDeclaredFields();

  String getDeedHolder();

  Set<String> getDistricts();

  Integer getIdInt();

  UUID getOwnerUUID();

  Gamer getOwnerObject();

  String getTeam();

  Team getTeamObject();

  Integer getX();

  Integer getZ();

  boolean hasTeam();

  @Override
  int hashCode();

  boolean isOwner(Gamer gamer);
}

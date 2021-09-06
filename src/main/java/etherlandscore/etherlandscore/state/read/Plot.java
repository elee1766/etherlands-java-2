package etherlandscore.etherlandscore.state.read;

import org.bukkit.Chunk;

import java.lang.reflect.Field;
import java.util.UUID;

public interface Plot {

  @Override
  boolean equals(Object o);

  Chunk getChunk();

  Field[] getDeclaredFields();

  String getDeedHolder();

  Integer getDistrict();

  Integer getIdInt();

  UUID getOwnerUUID();

  String getOwnerAddress();

  Gamer getOwnerObject();

  String getTeam();

  Team getTeamObject();

  District getDistrictObject();

  Integer getX();

  Integer getZ();

  boolean hasTeam();

  @Override
  int hashCode();

  boolean isOwner(Gamer gamer);
}

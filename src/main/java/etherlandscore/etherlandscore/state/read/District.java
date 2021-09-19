package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.util.Map2;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public interface District extends Comparable<District> {

  boolean canGamerPerform(AccessFlags flag, Gamer gamer);

  FlagValue checkFlags(AccessFlags flag, Gamer gamer, FlagValue def);

  FlagValue checkFlags(AccessFlags flag, Team writeTeam, FlagValue def);

  @Override
  int compareTo(District r);


  String getNickname();

  Set<Integer> getPlots();

  Field[] getDeclaredFields();

  Gamer getOwnerObject();

  String getOwnerAddress();

  Integer getPriority();

  String getTown();

  Town getTownObject();

  UUID getOwnerUUID();

  Map2<UUID, AccessFlags, FlagValue> getGamerPermissionMap();

  Map2<String, AccessFlags, FlagValue> getTeamPermissionMap();

  boolean hasTown();

  boolean isOwner(Gamer gamer);

  FlagValue readGamerPermission(Gamer gamer, AccessFlags flag);

  FlagValue readTeamPermission(Team writeTeam, AccessFlags flag);

  Integer getIdInt();

}

package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.util.Map2;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public interface District extends Comparable<District> {

  boolean canGamerPerform(AccessFlags flag, Gamer gamer);

  FlagValue checkFlags(AccessFlags flag, Gamer gamer);

  FlagValue checkFlags(AccessFlags flag, Group writeGroup);

  @Override
  int compareTo(District r);

  Field[] getDeclaredFields();

  Set<Plot> getPlotObjects();

  Gamer getOwnerObject();

  String getOwnerAddress();

  Integer getPriority();

  Team getTeamObject();

  UUID getOwnerUUID();

  Map2<UUID, AccessFlags, FlagValue> getGamerPermissionMap();

  Map2<String, AccessFlags, FlagValue> getGroupPermissionMap();

  boolean hasTeam();

  FlagValue readGamerPermission(Gamer gamer, AccessFlags flag);

  FlagValue readGroupPermission(Group writeGroup, AccessFlags flag);

  Integer getIdInt();

}

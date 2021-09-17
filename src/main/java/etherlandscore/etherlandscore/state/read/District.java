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

  FlagValue checkFlags(AccessFlags flag, Group writeGroup,FlagValue def);

  @Override
  int compareTo(District r);


  String getNickname();

  Set<Integer> getPlots();

  Field[] getDeclaredFields();

  Gamer getOwnerObject();

  String getOwnerAddress();

  Integer getPriority();

  String getTeam();

  Team getTeamObject();

  UUID getOwnerUUID();

  Map2<UUID, AccessFlags, FlagValue> getGamerPermissionMap();

  Map2<String, AccessFlags, FlagValue> getGroupPermissionMap();

  boolean hasTeam();

  boolean isOwner(Gamer gamer);

  FlagValue readGamerPermission(Gamer gamer, AccessFlags flag);

  FlagValue readGroupPermission(Group writeGroup, AccessFlags flag);

  Integer getIdInt();

}

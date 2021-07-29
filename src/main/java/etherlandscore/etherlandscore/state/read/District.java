package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;

import java.lang.reflect.Field;
import java.util.Set;

public interface District extends Comparable<District> {
  FlagValue checkFlags(AccessFlags flag, Gamer gamer);

  FlagValue checkFlags(AccessFlags flag, Group writeGroup);

  @Override
  int compareTo(District r);

  Field[] getDeclaredFields();

  String getName();

  Set<Plot> getPlotObjects();

  Integer getPriority();

  Team getTeamObject();

  boolean isDefault();

  FlagValue readGamerPermission(Gamer gamer, AccessFlags flag);

  FlagValue readGroupPermission(Group writeGroup, AccessFlags flag);
}

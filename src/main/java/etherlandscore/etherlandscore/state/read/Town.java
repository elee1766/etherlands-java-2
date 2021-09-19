package etherlandscore.etherlandscore.state.read;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface Town {
  boolean canAction(Gamer actor, Gamer receiver);

  boolean canInvite(Gamer inviter);

  boolean canJoin(Map<UUID, Long> orDefault, Gamer joiner);

  @Override
  boolean equals(Object o);

  Field[] getDeclaredFields();

  District getDistrict(Integer x);

  Set<District> getDistrictObjects();

  Set<Integer> getDistricts();

  Team getTeam(String name);

  Map<String, Team> getTeams();

  Set<UUID> getMembers();

  String getName();

  String getOwnerServerName();

  UUID getOwnerUUID();

  Set<Integer> getPlots();

  @Override
  int hashCode();

  void inviteGamer(Map<UUID, Long> invites, UUID arg);

  boolean isManager(Gamer manager);

  boolean isMember(Gamer gamer);

  boolean isOwner(Gamer manager);
}
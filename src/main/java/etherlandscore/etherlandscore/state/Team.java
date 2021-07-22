package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.*;

public class Team extends StateHolder {
  private final String name;
  private final UUID owner;
  private final Set<UUID> members = new HashSet<>();
  private final Map<String, Region> regions = new HashMap<>();

  public Team(Gamer gamer, String name) {
    this.name = name;
    this.owner = gamer.getUuid();
  }

  public void addMember(Channels channels, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.team_add_gamer, this, gamer));
  }

  public void addMember(Gamer gamer) {
    members.add(gamer.getUuid());
  }

  public void removeMember(Channels channels, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.team_remove_gamer, this, gamer));
  }

  public void removeMember(Gamer gamer) {
    members.remove(gamer.getUuid());
  }

  public Set<UUID> getMembers() {
    return members;
  }

  public String getName() {
    return name;
  }

  public String getOwner() {
    return Bukkit.getPlayer(this.owner).getName();
  }

  public UUID getOwnerUUID() {
    return this.owner;
  }

  public Region getRegion(String x) {
    return this.regions.getOrDefault(x, null);
  }

  public boolean canInvite(Gamer inviter) {
    return inviter.getUuid().equals(this.owner);
  }

  public void inviteGamer(Map<UUID, Long> invites, UUID arg) {
    invites.put(arg, (Instant.now().getEpochSecond()) + 5 * 60);
    Bukkit.getLogger().info(arg.toString() + " " + invites.get(arg).toString());
  }

  public boolean canJoin(Map<UUID, Long> invites, Gamer joiner) {
    Long invite = invites.get(joiner.getUuid());
    if (invite != null) {
      Bukkit.getLogger().info(invite.toString());
      return invite > Instant.now().getEpochSecond();
    }
    return false;
  }
}

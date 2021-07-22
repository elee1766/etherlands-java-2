package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Gamer extends StateHolder {

  private final UUID uuid;

  private String team;
  private Set<Gamer> friends;

  public Gamer(UUID uuid) {
    this.uuid = uuid;
  }

  public void addFriend(Channels channels, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.gamer_add_friend, this, gamer));
  }

  public void removeFriend(Channels channels, Gamer newFriend) {
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_remove_friend, this, newFriend));
  }

  public void addFriend(Gamer gamer) {
    friends.add(gamer);
  }

  public void removeFriend(Gamer gamer) {
    friends.remove(gamer);
  }

  public Set getFriends() {
    if (friends == null) {
      friends = new HashSet<>();
    }
    return friends;
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getTeam() {
    return team;
  }

  public void setTeam(String team) {
    this.team = team;
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }
}

package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Gamer extends StateHolder {

  private final UUID uuid;
  private final Set<String> groups = new HashSet<>();
  private String team = "";
  private String address;
  private Set<UUID> friends;

  public Gamer(UUID uuid) {
    this.uuid = uuid;
  }

  public void addFriend(Channels channels, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.gamer_add_friend, this, gamer));
  }

  public void addFriend(Gamer gamer) {
    friends.add(gamer.getUuid());
  }

  public void addGroup(Group group) {
    groups.add(group.getName());
  }

  public void clearGroups() {
    groups.clear();
  }

  public void friendList() {
    Set<UUID> flist = this.getFriends();
    StringBuilder friends = new StringBuilder();
    for (UUID value : flist) {
      friends.append(Bukkit.getPlayer(value).getName()).append(", ");
    }
    this.getPlayer().sendMessage(friends.toString());
  }

  public String getAddress() {
    return address;
  }

  public Set<String> getGroups() {
    return groups;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Field[] getDeclaredFields() {
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field f : fields) {
      f.setAccessible(true);
    }
    return fields;
  }

  public Set<UUID> getFriends() {
    if (friends == null) {
      friends = new HashSet<>();
    }
    return friends;
  }

  public Group getGroupObject(String name) {
    return state().getTeam(this.getTeamName()).getGroup(name);
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  public String getTeamName() {
    return team;
  }

  public Team getTeamObject() {
    return state().getTeam(team);
  }

  public UUID getUuid() {
    return uuid;
  }

  public boolean hasTeam() {
    if (team == null) {
      return false;
    }
    return team.equals("");
  }

  public void removeFriend(Channels channels, Gamer newFriend) {
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_remove_friend, this, newFriend));
  }

  public void removeFriend(Gamer gamer) {
    friends.remove(gamer.getUuid());
  }

  public void removeGroup(String name) {
    groups.remove(name);
  }

  public void setTeam(String team) {
    this.team = team;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Gamer gamer = (Gamer) o;
    return getUuid() != null ? getUuid().equals(gamer.getUuid()) : gamer.getUuid() == null;
  }

  @Override
  public int hashCode() {
    return getUuid() != null ? getUuid().hashCode() : 0;
  }
}

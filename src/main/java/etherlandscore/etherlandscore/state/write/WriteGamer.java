package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteGamer extends CouchDocument implements Gamer {
  private final UUID uuid;
  private final Set<String> groups;
  private String team;
  private String address;
  private Set<UUID> friends;

  @JsonProperty("_id")
  private String _id;

  @JsonCreator
  public WriteGamer(@JsonProperty("_id") UUID uuid, @JsonProperty("groups") Set<String> groups) {
    this.uuid = uuid;
    this.groups = groups;
    this.address = "";
  }

  public WriteGamer(UUID uuid) {
    this.uuid = uuid;
    this.groups = new HashSet<>();
  }

  public void addFriend(Gamer gamer) {
    friends.add(gamer.getUuid());
  }

  public void addGroup(Group writeGroup) {
    groups.add(writeGroup.getName());
  }

  public void clearGroups() {
    groups.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Gamer gamer = (Gamer) o;
    return getUuid() != null ? getUuid().equals(gamer.getUuid()) : gamer.getUuid() == null;
  }

  @Override
  public void friendList() {
    Set<UUID> flist = this.getFriends();
    StringBuilder friends = new StringBuilder();
    for (UUID value : flist) {
      friends.append(Bukkit.getPlayer(value).getName()).append(", ");
    }
    this.getPlayer().sendMessage(friends.toString());
  }

  @Override
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  @JsonIgnore
  public Field[] getDeclaredFields() {
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field f : fields) {
      f.setAccessible(true);
    }
    return fields;
  }

  @Override
  public Set<UUID> getFriends() {
    if (friends == null) {
      friends = new HashSet<>();
    }
    return friends;
  }

  @Override
  @JsonIgnore
  public Group getGroupObject(String name) {
    return state().getTeam(this.getTeam()).getGroup(name);
  }

  @Override
  public Set<String> getGroups() {
    return groups;
  }

  @JsonProperty("_id")
  public String getId() {
    return this.uuid.toString();
  }

  @JsonProperty("_id")
  public void setId(String string) {
    this._id = uuid.toString();
  }

  @Override
  @JsonIgnore
  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  @Override
  public String getTeam() {
    return team;
  }

  public void setTeam(String team) {
    this.team = team;
    if (hasTeam()) {
      groups.add("member");
    } else {
      groups.remove("member");
    }
  }

  @Override
  @JsonIgnore
  public Team getTeamObject() {
    return state().getTeam(team);
  }

  @Override
  @JsonIgnore
  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean hasFriend(Player player) {
    return this.getFriends().contains(player.getUniqueId());
  }

  @Override
  public boolean hasTeam() {
    if (team == null) {
      return false;
    }
    return team.equals("");
  }

  @Override
  public int hashCode() {
    return getUuid() != null ? getUuid().hashCode() : 0;
  }

  public void removeFriend(Gamer gamer) {
    friends.remove(gamer.getUuid());
  }

  public void removeGroup(String name) {
    groups.remove(name);
  }
}
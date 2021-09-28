package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonIgnore;
import etherlandscore.etherlandscore.services.ImpatientAsker;
import etherlandscore.etherlandscore.state.preferences.UserPreferences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Gamer {
  private final UUID uuid;
  private final Set<String> teams;
  public UserPreferences preferences;
  private Set<UUID> friends;

  public Gamer(UUID uuid) {
    this.uuid = uuid;
    this.teams = new HashSet<>();
    this.preferences = new UserPreferences();
  }

  public void addFriend(Gamer gamer) {
    friends.add(gamer.getUuid());
  }

  public void addTeam(Team team) {
    teams.add(team.getName());
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Gamer gamer = (Gamer) o;
    return getUuid() != null ? getUuid().equals(gamer.getUuid()) : gamer.getUuid() == null;
  }

  public void friendList() {
    Set<UUID> uuids = this.getFriends();
    StringBuilder friends = new StringBuilder();
    for (UUID value : uuids) {
      friends.append(state().getGamer(value).getName()).append(", ");
    }
    this.getPlayer().sendMessage(friends.toString());
  }

  public String getAddress() {
    return ImpatientAsker.AskWorld("gamer", uuid.toString().toLowerCase(Locale.ROOT), "address");
  }


  @JsonIgnore
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

  public String getId() {
    return this.uuid.toString();
  }


  @JsonIgnore
  public String getName() {
    if (this.getPlayer() != null) {
      return this.getPlayer().getName();
    }
    if (this.getUuid() != null) {
      String name = Bukkit.getOfflinePlayer(this.getUuid()).getName();
      if (name != null) {
        return name;
      }
    }
    return "??????";
  }


  @JsonIgnore
  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }


  public UserPreferences getPreferences() {
    return preferences;
  }


  public Set<String> getTeams() {
    if (hasTown()) {
      teams.add("member");
    }
    return teams;
  }

  public String getTown() {
    return ImpatientAsker.AskWorld("gamer", getUuid().toString(), "town");
  }


  @JsonIgnore
  public Town getTownObject() {
    return state().getTown(getTown());
  }


  @JsonIgnore
  public UUID getUuid() {
    return uuid;
  }


  public boolean hasFriend(UUID player) {
    return this.getFriends().contains(player);
  }


  public boolean hasTown() {
    return getTown() != null;
  }


  public int hashCode() {
    return getUuid() != null ? getUuid().hashCode() : 0;
  }

  public boolean isOnline() {
    return Bukkit.getPlayer(getUuid()) != null;
  }

  public void removeFriend(Gamer gamer) {
    friends.remove(gamer.getUuid());
  }

  public void removeTeam(String name) {
    teams.remove(name);
  }


  public String toString() {
    return this.uuid.toString();
  }

}

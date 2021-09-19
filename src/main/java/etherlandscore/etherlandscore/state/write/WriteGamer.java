package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.preferences.UserPreferences;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.read.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;
@JsonIgnoreProperties(ignoreUnknown = true)
public class WriteGamer extends CouchDocument implements Gamer {
  private final UUID uuid;
  private final Set<String> teams;
  private String town;
  private String address;
  private Set<UUID> friends;
  public UserPreferences preferences;

  @JsonProperty("_id")
  private String _id;

  @JsonCreator
  public WriteGamer(@JsonProperty("_id") UUID uuid, @JsonProperty("teams") Set<String> teams) {
    this.uuid = uuid;
    this.teams = teams;
    this.address = "";
    this.preferences = new UserPreferences();
  }

  public WriteGamer(UUID uuid) {
    this.uuid = uuid;
    this.teams = new HashSet<>();
    this.preferences = new UserPreferences();
  }
  @Override
  public String toString(){
    return this.uuid.toString();
  }

  @Override
  @JsonIgnore
  public String getName(){
    if(this.getPlayer() != null){
      return this.getPlayer().getName();
    }
    if (this.getUuid() != null) {
      String name = Bukkit.getOfflinePlayer(this.getUuid()).getName();
      if(name != null){
        return name;
      }
    }
    return "??????";
  }

  public void addFriend(Gamer gamer) {
    friends.add(gamer.getUuid());
  }

  public void addTeam(WriteTeam writeTeam) {
    teams.add(writeTeam.getName());
  }

  public void clearTeams() {
    teams.clear();
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
    Set<UUID> uuids = this.getFriends();
    StringBuilder friends = new StringBuilder();
    for (UUID value : uuids) {
      friends.append(state().getGamer(value).getName()).append(", ");
    }
    this.getPlayer().sendMessage(friends.toString());
  }

  @Override
  public String getAddress() {
    this.address = state().getAddressOf(this.getUuid());
    return this.address;
  }

  public void setAddress(String address) {
    this.address = state().getAddressOf(this.getUuid());
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
  public Team getTeamObject(String name) {
    return state().getTown(this.getTown()).getTeam(name);
  }

  @Override
  public Set<String> getTeams() {
    if (hasTown()) {
      teams.add("member");
    }
    return teams;
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
  public UserPreferences getPreferences() {
    return preferences;
  }

  @Override
  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;

  }

  @Override
  @JsonIgnore
  public Town getTownObject() {
    return state().getTown(this.town);
  }

  @Override
  @JsonIgnore
  public UUID getUuid() {
    return uuid;
  }

  @Override
  public boolean hasFriend(UUID player) {
    return this.getFriends().contains(player);
  }


  @Override
  public boolean hasTown() {
    if (town == null) {
      return false;
    }
    return !town.equals("");
  }

  @Override
  public int hashCode() {
    return getUuid() != null ? getUuid().hashCode() : 0;
  }

  public void removeFriend(Gamer gamer) {
    friends.remove(gamer.getUuid());
  }

  public void removeTeam(String name) {
    teams.remove(name);
  }
}

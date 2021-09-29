package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.services.ImpatientAsker;
import etherlandscore.etherlandscore.state.preferences.UserPreferences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Gamer {
  private final UUID uuid;
  private final Set<String> teams;

  public Gamer(UUID uuid) {
    this.uuid = uuid;
    this.teams = new HashSet<>();
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Gamer gamer = (Gamer) o;
    return getUuid() != null ? getUuid().equals(gamer.getUuid()) : gamer.getUuid() == null;
  }


  public String getAddress() {
    return ImpatientAsker.AskWorld(15,"gamer", getUuidString(), "address");
  }


  public Set<UUID> getFriends() {
    return ImpatientAsker.AskWorldUUIDSet("gamer",getUuidString(),"friends");
  }

  public String getId() {
    return this.uuid.toString();
  }

  public String getUuidString() {
    return this.uuid.toString().toLowerCase();
  }


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
    return getUuidString().substring(0,6)+"...";
  }


  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }


  public UserPreferences getPreferences() {
    return UserPreferences.GetPreferences(this.getUuid());
  }


  public Set<String> getTeams() {
    if (hasTown()) {
      teams.add("member");
    }
    return teams;
  }

  public String getTown() {
    return ImpatientAsker.AskWorld(2, "gamer", getUuidString(), "town");
  }


  public Town getTownObject() {
    return state().getTown(getTown());
  }


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


  public String toString() {
    return this.uuid.toString();
  }

}

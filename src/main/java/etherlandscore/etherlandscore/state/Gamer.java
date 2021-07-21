package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Gamer extends StateHolder {

    private final UUID uuid;
    private String team = "";

    public Gamer(UUID uuid) {
        this.uuid = uuid;
    }
    private List<Gamer> friends;

    public void addFriend(Channels channels, Gamer gamer) {
        channels.master_command.publish(new Message("friend_add", this,gamer));
    }

    public void addFriend(Gamer gamer) {
        friends.add(gamer);
    }

    public List getFriends() {return friends;}
    public void setTeam(String team) {
        this.team = team;
    }
    public UUID getUuid() {
        return uuid;
    }
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getTeam() {
        return team;
    }
}

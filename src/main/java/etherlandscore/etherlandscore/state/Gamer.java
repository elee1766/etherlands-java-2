package etherlandscore.etherlandscore.state;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Gamer extends StateHolder {

    private final UUID uuid;

    private String team;

    public Gamer(UUID uuid) {
        this.uuid = uuid;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}

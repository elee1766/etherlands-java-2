package etherlandscore.etherlandscore.stateholder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamerState extends StateHolder {

    private final UUID uuid;

    private String team;

    public GamerState(UUID uuid) {
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

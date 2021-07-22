package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Plot extends StateHolder {
    private final transient Chunk chunk;

    private final Integer id;
    private final Integer x;
    private final Integer z;
    private String ownerAddress;
    private UUID ownerUUID;
    private String ownerServerName;

    public Plot(Integer id, Integer x, Integer z, String ownerAddress) {
        this.chunk = Bukkit.getWorld("world").getChunkAt(x, z);
        this.id = id;
        this.x = x;
        this.z = z;
        this.ownerAddress = ownerAddress;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setOwner(Channels channels, String ownerAddress) {
        channels.master_command.publish(new Message("plot_set_owner", ownerAddress));
    }

    public void setOwner(String ownerAddress, UUID ownerUUID) {
        this.ownerAddress = ownerAddress;
        this.ownerUUID = ownerUUID;
        if (this.ownerUUID != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(this.ownerUUID);
            if (player.hasPlayedBefore()) {
                this.ownerServerName = player.getName();
            } else {
                this.ownerServerName = "player-uuid: [" + ownerUUID + "]";
            }
        }
    }

    public String getDeedHolder() {
        return this.ownerAddress;
    }

    public Integer getId() {
        return this.id;
    }
}

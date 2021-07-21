package etherlandscore.etherlandscore.stateholder;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.management.BufferPoolMXBean;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamState extends StateHolder{
    private final String name;
    private UUID owner;
    private Set<UUID> members = new HashSet<>();

    public TeamState(GamerState gamer, String name) {
        this.name = name;
        this.owner = gamer.getUuid();
    }
    public void addMember(Channels channels, GamerState gamer){
        channels.command.publish(new Message("team_addMember", this,gamer));
    }

    public void addMember(GamerState gamer){
        members.add(gamer.getUuid());
    }
    public void removeMember(Channels channels, GamerState gamer){
        channels.command.publish(new Message("team_removeMember",this,gamer));
    }

    public void removeMember(GamerState gamer){
        members.remove(gamer.getUuid());
    }

    public Set<UUID> getMembers(){
        return members;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return Bukkit.getPlayer(this.owner).getName();
    }
}

package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.util.*;

public class Team extends StateHolder{
    private final String name;
    private UUID owner;
    private Set<UUID> members = new HashSet<>();

    private transient Map<UUID, Long> invites = new HashMap<UUID, Long>();

    public Team(Gamer gamer, String name) {
        this.name = name;
        this.owner = gamer.getUuid();
    }
    public void addMember(Channels channels, Gamer gamer){
        if(gamer.getTeam().equals("")) {
            channels.master_command.publish(new Message("team_addMember", this, gamer));
        }
    }

    public void addMember(Gamer gamer){
        members.add(gamer.getUuid());
    }
    public void removeMember(Channels channels, Gamer gamer){
        if(gamer.getTeam().equals(name)) {
            channels.master_command.publish(new Message("team_removeMember", this, gamer));
        }
    }

    public void removeMember(Gamer gamer){
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

    public boolean canInvite(Gamer inviter) {
        return inviter.getUuid() == this.owner;
    }

    public void inviteGamer(UUID arg) {
        this.invites.put(arg,(Instant.now().getEpochSecond()) + 5 * 60);
    }

    public boolean canJoin(Gamer joiner) {
        Long invite = this.invites.get(joiner.getUuid());
        if(invite != null){
            return invite > Instant.now().getEpochSecond();
        }
        return false;
    }
}

package etherlandscore.etherlandscore.fibers;

import etherlandscore.etherlandscore.stateholder.GamerState;
import etherlandscore.etherlandscore.stateholder.GlobalState;
import etherlandscore.etherlandscore.stateholder.TeamState;
import org.jetlang.channels.MemoryChannel;

import java.util.UUID;

public class Channels {

    public final MemoryChannel<UUID> requestLinkRescan = new MemoryChannel<>();

    public final MemoryChannel<Message> command = new MemoryChannel<>();

    public final MemoryChannel<GlobalState> global_update = new MemoryChannel<etherlandscore.etherlandscore.stateholder.GlobalState>();
    public final MemoryChannel<GamerState> gamer_update = new MemoryChannel<>();
    public final MemoryChannel<TeamState> team_update = new MemoryChannel<>();

}

package etherlandscore.etherlandscore.fibers;

import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.read.Team;
import org.jetlang.channels.MemoryChannel;

public class Channels {

  public final MemoryChannel<Message<MasterCommand>> master_command = new MemoryChannel<>();
  public final MemoryChannel<Message<EthersCommand>> ethers_command = new MemoryChannel<>();

  public final MemoryChannel<Context> global_update = new MemoryChannel<Context>();
  public final MemoryChannel<Gamer> gamer_update = new MemoryChannel<>();
  public final MemoryChannel<Team> team_update = new MemoryChannel<>();

  public final MemoryChannel<Plot> plot_update = new MemoryChannel<>();
}

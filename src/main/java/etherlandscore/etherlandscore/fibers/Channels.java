package etherlandscore.etherlandscore.fibers;

import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.write.*;
import org.jetlang.channels.MemoryChannel;

public class Channels {

  public final MemoryChannel<Message<MasterCommand>> master_command = new MemoryChannel<>();
  public final MemoryChannel<Message<EthersCommand>> ethers_command = new MemoryChannel<>();

  public final MemoryChannel<Message<ChatTarget>> chat_message = new MemoryChannel<>();


  public final MemoryChannel<Context> global_update = new MemoryChannel<Context>();

  public final MemoryChannel<WriteGamer> db_gamer = new MemoryChannel<>();
  public final MemoryChannel<WriteDistrict> db_district = new MemoryChannel<>();
  public final MemoryChannel<WriteTeam> db_team = new MemoryChannel<>();
  public final MemoryChannel<WriteNFT> db_nft = new MemoryChannel<>();
  public final MemoryChannel<WriteMap> db_map = new MemoryChannel<>();
  public final MemoryChannel<WriteBankRecord> db_bankrecord = new MemoryChannel<>();

  public final MemoryChannel<WriteGamer> db_gamer_delete = new MemoryChannel<>();
  public final MemoryChannel<WriteDistrict> db_district_delete = new MemoryChannel<>();
  public final MemoryChannel<WriteTeam> db_team_delete = new MemoryChannel<>();
  public final MemoryChannel<WriteNFT> db_nft_delete = new MemoryChannel<>();
  public final MemoryChannel<WriteMap> db_map_delete = new MemoryChannel<>();
}

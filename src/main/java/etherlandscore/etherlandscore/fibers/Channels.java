package etherlandscore.etherlandscore.fibers;

import etherlandscore.etherlandscore.state.*;
import org.jetlang.channels.MemoryChannel;

public class Channels {

  public final MemoryChannel<Message<MasterCommand>> master_command = new MemoryChannel<>();
  public final MemoryChannel<Message<ChatTarget>> chat_message = new MemoryChannel<>();

  public final MemoryChannel<Context> global_update = new MemoryChannel<>();

  public final MemoryChannel<Gamer> db_gamer = new MemoryChannel<>();
  public final MemoryChannel<District> db_district = new MemoryChannel<>();
  public final MemoryChannel<Town> db_town = new MemoryChannel<>();
  public final MemoryChannel<WriteNFT> db_nft = new MemoryChannel<>();
  public final MemoryChannel<BankRecord> db_bank_record = new MemoryChannel<>();

  public final MemoryChannel<Gamer> db_gamer_delete = new MemoryChannel<>();
  public final MemoryChannel<District> db_district_delete = new MemoryChannel<>();
  public final MemoryChannel<Town> db_town_delete = new MemoryChannel<>();
  public final MemoryChannel<WriteNFT> db_nft_delete = new MemoryChannel<>();
}

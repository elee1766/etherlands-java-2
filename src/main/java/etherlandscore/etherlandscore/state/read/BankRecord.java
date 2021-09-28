package etherlandscore.etherlandscore.state.read;

import java.util.UUID;

public interface BankRecord {
  Integer getDelta();

  UUID getFrom();

  Integer getTimestamp();

  UUID getTo();
}

package etherlandscore.etherlandscore.state.write;

import etherlandscore.etherlandscore.state.read.BankRecord;

import java.util.UUID;

public class WriteBankRecord  implements BankRecord {
  private final UUID from;
  private final UUID to;
  private final Integer timestamp;
  private final Integer delta;

  private String _id;

  public WriteBankRecord(
      String _id,
      UUID from,
      UUID to,
      Integer delta,
      Integer timestamp) {
    this._id = _id;
    this.from = from;
    this.to = to;
    this.timestamp = timestamp;
    this.delta = delta;
  }

  @Override
  public Integer getDelta() {
    return delta;
  }

  @Override
  public UUID getFrom() {
    return from;
  }

  public String getId() {
    return this._id;
  }

  public void setId(String string) {
    this._id = string;
  }

  @Override
  public Integer getTimestamp() {
    return timestamp;
  }

  @Override
  public UUID getTo() {
    return to;
  }
}

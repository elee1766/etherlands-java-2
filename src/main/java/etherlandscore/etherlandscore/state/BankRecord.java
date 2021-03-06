package etherlandscore.etherlandscore.state;


import java.util.UUID;

public class BankRecord {
  private final UUID from;
  private final UUID to;
  private final Integer timestamp;
  private final Integer delta;

  private String _id;

  public BankRecord(
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

  public Integer getDelta() {
    return delta;
  }

  public UUID getFrom() {
    return from;
  }

  public String getId() {
    return this._id;
  }

  public void setId(String string) {
    this._id = string;
  }

  public Integer getTimestamp() {
    return timestamp;
  }

  public UUID getTo() {
    return to;
  }
}

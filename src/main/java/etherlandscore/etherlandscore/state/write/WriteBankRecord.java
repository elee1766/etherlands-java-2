package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.state.read.BankRecord;

import java.util.UUID;

public class WriteBankRecord  implements BankRecord {
  private final UUID from;
  private final UUID to;
  private final Integer timestamp;
  private final Integer delta;

  @JsonProperty("_id")
  private String _id;

  @JsonCreator
  public WriteBankRecord(
      @JsonProperty("_id") String _id,
      @JsonProperty("from") UUID from,
      @JsonProperty("to") UUID to,
      @JsonProperty("delta") Integer delta,
      @JsonProperty("timestamp") Integer timestamp) {
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

  @JsonProperty("_id")
  public String getId() {
    return this._id;
  }

  @JsonProperty("_id")
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

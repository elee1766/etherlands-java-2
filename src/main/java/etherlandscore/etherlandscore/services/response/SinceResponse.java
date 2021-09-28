package etherlandscore.etherlandscore.services.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SinceResponse {
  private Integer[] update;
  private Integer block;

  @JsonCreator
  public SinceResponse(
      @JsonProperty("update") Integer[] update, @JsonProperty("block") Integer block) {
    this.update = update;
    this.block = block;
  }

  public Integer getBlock() {
    return block;
  }

  public void setBlock(Integer block) {
    this.block = block;
  }

  public Integer[] getUpdate() {
    return update;
  }

  public void setUpdate(Integer[] update) {
    this.update = update;
  }
}

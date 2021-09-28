package etherlandscore.etherlandscore.services.response;


public class SinceResponse {
  private Integer[] update;
  private Integer block;

  public SinceResponse(
      Integer[] update, Integer block) {
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

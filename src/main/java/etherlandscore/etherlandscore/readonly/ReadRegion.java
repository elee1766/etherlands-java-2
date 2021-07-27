package etherlandscore.etherlandscore.readonly;

import etherlandscore.etherlandscore.state.Region;

public class ReadRegion {
  private final Region region;
  public ReadRegion(Region region){
    this.region = region;
  }
  public ReadRegion(Object obj){
    this.region= (Region) obj;
  }
}

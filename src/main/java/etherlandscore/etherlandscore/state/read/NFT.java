package etherlandscore.etherlandscore.state.read;

public interface NFT {
  String getContract();

  String getId();

  String getItem();

  Integer getWidth();

  Integer getXloc();
  Integer getYloc();
  Integer getZloc();

  boolean isAir();

  boolean sendGamer(Gamer gamer);
}

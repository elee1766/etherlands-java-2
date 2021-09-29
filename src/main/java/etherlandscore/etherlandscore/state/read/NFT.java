package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.state.Gamer;

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

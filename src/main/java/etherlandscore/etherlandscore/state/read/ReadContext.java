package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.state.*;
import etherlandscore.etherlandscore.util.Map3;
import org.bukkit.Location;

import java.util.UUID;

public class ReadContext {
  private final Context context;

  public ReadContext(Context context) {
    this.context = context;
  }

  public Object getBalance(UUID uniqueId) {
    return context.getBalance(uniqueId);
  }

  public District getDistrict(int id) {
    return context.getDistrict(id);
  }

  public District getDistrict(String nickname) {
    return context.getDistrict(nickname);
  }

  public District getDistrict(int x, int z) {
    return context.getDistrict(x, z);
  }

  public Gamer getGamer(UUID uuid) {
    return context.getGamer(uuid);
  }

  public Location getGamerLocation(Gamer gamer) {
    return context.getGamerLocation(gamer);
  }

  public Map3<Integer, Integer, Integer, WriteNFT> getNFTs() {
    return context.getNfts();
  }

  public Map3<Integer, Integer, Integer, WriteNFT> getNfts() {
    return context.getNfts();
  }

  public Plot getPlot(Integer x, Integer z) {
    return context.getPlot(x, z);
  }

  public WriteShop getShop(Location location) {
    return context.getShop(location);
  }

  public Town getTown(String town) {
    return new Town(town);
  }

}

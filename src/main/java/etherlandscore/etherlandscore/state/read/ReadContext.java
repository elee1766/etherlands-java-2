package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.write.WriteDistrict;
import etherlandscore.etherlandscore.state.write.WriteMap;
import etherlandscore.etherlandscore.state.write.WriteNFT;
import etherlandscore.etherlandscore.state.write.WriteShop;
import etherlandscore.etherlandscore.util.Map2;
import org.bukkit.Location;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ReadContext {
  private final Context context;
  private final Channels channels;

  public ReadContext(Context context, Channels channels) {
    this.context = context;
    this.channels = channels;
  }

  public void createTown(Channels channels, Gamer gamer, String name) {
  }

  public Object getBalance(UUID uniqueId) {
    return context.getBalance(uniqueId);
  }

  public Gamer getGamer(UUID uuid) {
    return context.getGamer(uuid);
  }

  public Map<UUID, Gamer> getGamers() {
    return (Map) context.getGamers();
  }

  public Map2<String, String, WriteNFT> getNFTs() {
    return (Map2) context.getNfts();
  }

  public ReadPlot getPlot(Integer id) {
    return context.getPlot(id);
  }

  public ReadPlot getPlot(Integer x, Integer z) {
    return context.getPlot(x, z);
  }

  public Town getTown(String town) {
    return context.getTown(town);
  }

  public Map<Integer, WriteDistrict> getDistricts() {
    return context.getDistricts();
  }
  public District getDistrict(int id) {
    return context.getDistrict(id);
  }

  public District getDistrict(String nickname) {
    return context.getDistrict(nickname);
  }

  public Map<String, Town> getTowns() {
    return (Map) context.getTowns();
  }

  public boolean hasGamer(UUID uniqueId) {
    return getGamers().containsKey(uniqueId);
  }

  public boolean hasTown(String name) {
    return getTowns().containsKey(name);
  }

  public Map2<String, String, WriteNFT> getNfts() {return context.getNfts(); }

  public Map<String, WriteNFT> getNftUrls() {return context.getNftUrls(); }

  public Set<WriteMap> getMaps() {return context.getMaps(); }

  public District getDistrict(int x, int z) { return context.getDistrict(x, z); }

  public Map<Location, WriteShop> getShops() { return context.getShops();}

  public WriteShop getShop(Location location) { return context.getShop(location);}

  public Map<String, UUID> getLinks() { return context.getLinks();}

  public Map<Gamer, Location> getGamerLocations(){return context.getGamerLocations();}

  public Location getGamerLocation(Gamer gamer){return context.getGamerLocation(gamer);}
}

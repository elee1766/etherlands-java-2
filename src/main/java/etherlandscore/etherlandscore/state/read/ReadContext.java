package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.write.*;
import etherlandscore.etherlandscore.state.write.District;
import etherlandscore.etherlandscore.util.Map3;
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

  public void createTown(Channels channels, Gamer gamer, String name) {}

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

  public Map<Integer, etherlandscore.etherlandscore.state.write.District> getDistricts() {
    return context.getDistricts();
  }

  public Gamer getGamer(UUID uuid) {
    return context.getGamer(uuid);
  }

  public Location getGamerLocation(Gamer gamer) {
    return context.getGamerLocation(gamer);
  }

  public Map<Gamer, Location> getGamerLocations() {
    return context.getGamerLocations();
  }

  public Set<WriteMap> getMaps() {
    return context.getMaps();
  }

  public Map3<Integer, Integer, Integer, WriteNFT> getNFTs() {
    return context.getNfts();
  }

  public Map<String, WriteNFT> getNftUrls() {
    return context.getNftUrls();
  }

  public Map3<Integer, Integer, Integer, WriteNFT> getNfts() {
    return context.getNfts();
  }

  public ReadPlot getPlot(Integer id) {
    return context.getPlot(id);
  }

  public ReadPlot getPlot(Integer x, Integer z) {
    return context.getPlot(x, z);
  }

  public WriteShop getShop(Location location) {
    return context.getShop(location);
  }

  public Map<Location, WriteShop> getShops() {
    return context.getShops();
  }

  public Town getTown(String town) {
    return new Town(town);
  }

  public Map<String, Town> getTowns() {
    return (Map) context.getTowns();
  }

  public boolean hasTown(String name) {
    return getTowns().containsKey(name);
  }

  public boolean isValidCaptcha(int a, int b, int c) {
    return context.isValidCaptcha(a, b, c);
  }
}

package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.persistance.Couch.CouchPersister;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
import etherlandscore.etherlandscore.state.preferences.UserPreferences;
import etherlandscore.etherlandscore.state.read.*;
import etherlandscore.etherlandscore.state.write.*;
import etherlandscore.etherlandscore.util.Map2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Context {

  public final Map<UUID, WriteGamer> gamers = new HashMap<>();
  public final Map<Integer, WriteDistrict> districts = new HashMap<>();
  public final Map<String, WriteTown> towns = new HashMap<>();
  public final Map<String, UUID> linked = new HashMap<>();
  public final Map<String, WriteNFT> nftUrls = new HashMap<>();
  public final Map2<String,String,WriteNFT> nfts = new Map2<>();
  public final Set<WriteMap> maps = new HashSet<>();
  public final Map<String, WriteBankRecord> bankRecords = new HashMap<>();
  public final Map<Location, WriteShop> shops = new HashMap<>();
  public final Map<Gamer, Location> gamerLocations = new HashMap<>();

  public final Map<UUID,Integer> balanceCache = new HashMap<>();

  private final Channels channels;

  private CouchPersister couchPersister;

  public Context(Channels channels){
    this.channels = channels;
  }

  public void addPersister(CouchPersister persister){
    couchPersister = persister;
  }

  public Map<String, WriteBankRecord> getBankRecords() {
    return this.bankRecords;
  }

  public void saveAll(){
    couchPersister.saveContext(this);
  }

  public void storeGamerLocation(Gamer gamer, Location location){
    this.gamerLocations.put(gamer, location);
  }

  public Map<Gamer, Location> getGamerLocations(){
    return this.gamerLocations;
  }

  public Location getGamerLocation(Gamer gamer){
    return this.gamerLocations.get(gamer);
  }

  public void context_create_gamer(UUID uuid) {
    if (!this.getGamers().containsKey(uuid)) {
      WriteGamer gamer = new WriteGamer(uuid);
      this.getGamers().put(uuid, gamer);
      couchPersister.update(gamer);
    }
  }

  public Integer getBalance(UUID gamerId){
    return this.balanceCache.getOrDefault(gamerId,0);
  }
  private Integer getAbsoluteBalance(UUID gamerId) {
    Integer balance = 0;
    try {
      for (BankRecord bankRecord : bankRecords.values()) {
        if (bankRecord.getFrom().equals(gamerId)) {
          balance = balance - bankRecord.getDelta();
        }
        if (bankRecord.getTo().equals(gamerId)) {
          balance = balance + bankRecord.getDelta();
        }
      }
      this.balanceCache.put(gamerId, balance);
    }catch(Exception ex){
      return 0;
    }
    return balance;
  }

  public void context_mint_tokens(WriteGamer gamer, Integer amount){
    String id = UUID.randomUUID().toString();
    this.bankRecords.put(
        id,
        new WriteBankRecord(
            id,
            new UUID(0,0),
            gamer.getUuid(),
            amount,
            (int) System.currentTimeMillis())
    );
    getAbsoluteBalance(gamer.getUuid());
  }

  public void context_process_gamer_transaction(GamerTransaction transaction) {
    Bukkit.getLogger().info("Doing Transaction");
    Set<ItemStack> leftItems= transaction.getItemStacks().getFirst();
    Set<ItemStack> rightItems= transaction.getItemStacks().getSecond();

    // the final delta is the amount that gets "subtracted from left" and "added to "right"
    Integer final_delta = transaction.getDeltas().getFirst() - transaction.getDeltas().getSecond();
    Bukkit.getLogger().info(final_delta + " " + leftItems + " " + rightItems);
    if (final_delta != 0) {
        Bukkit.getLogger().info("delta not 0");
        Integer balanceLeft = this.getAbsoluteBalance(transaction.getGamers().getFirst().getUuid());
        Integer balanceRight = this.getAbsoluteBalance(transaction.getGamers().getSecond().getUuid());
        Bukkit.getLogger().info("Balances" + balanceLeft + " " + balanceRight);
        if ((balanceLeft - final_delta) >= 0 && (balanceRight + final_delta) >= 0) {
          String id = UUID.randomUUID().toString();
          this.bankRecords.put(
              id,
              new WriteBankRecord(
                  id,
                  transaction.getGamers().getFirst().getUuid(),
                  transaction.getGamers().getSecond().getUuid(),
                  final_delta,
                  (int) System.currentTimeMillis())
          );
          Bukkit.getLogger().info("Bank statement written");
          if(leftItems!=null) {
            for (ItemStack item : leftItems) {
              if (!transaction.getInventorys().getFirst().contains(item)) {
                return;
              }
            }
          }
          if(rightItems!=null){
            for (ItemStack item : rightItems) {
              if(!transaction.getInventorys().getSecond().contains(item)){
                return;
              }
            }
          }

          if(leftItems!=null){
            for(ItemStack item : leftItems){
              transaction.getInventorys().getFirst().removeItem(item);
              transaction.getInventorys().getSecond().addItem(item);
            }
          }
          if(rightItems!=null){
            for(ItemStack item : rightItems){
              transaction.getInventorys().getFirst().removeItem(item);
              transaction.getInventorys().getSecond().addItem(item);
            }
          }
        }else{
          if(leftItems!=null){
            for(ItemStack item : leftItems){
              transaction.getInventorys().getFirst().removeItem(item);
              transaction.getGamers().getFirst().getPlayer().getInventory().addItem(item);
            }
          }
          if(rightItems!=null){
            for(ItemStack item : rightItems){
              transaction.getInventorys().getSecond().removeItem(item);
              transaction.getGamers().getSecond().getPlayer().getInventory().addItem(item);
            }
          }
        }
    }else{
      if(leftItems!=null) {
        for (ItemStack item : leftItems) {
          if (!transaction.getInventorys().getFirst().contains(item)) {
            return;
          }
        }
      }
      if(rightItems!=null){
        for (ItemStack item : rightItems) {
          if(!transaction.getInventorys().getSecond().contains(item)){
            return;
          }
        }
      }

      if(leftItems!=null){
        for(ItemStack item : leftItems){
          transaction.getInventorys().getFirst().removeItem(item);
          transaction.getInventorys().getSecond().addItem(item);
        }
      }
      if(rightItems!=null){
        for(ItemStack item : rightItems){
          transaction.getInventorys().getFirst().removeItem(item);
          transaction.getInventorys().getSecond().addItem(item);
        }
      }
    }
    getAbsoluteBalance(transaction.getGamers().getFirst().getUuid());
    getAbsoluteBalance(transaction.getGamers().getSecond().getUuid());
    Bukkit.getLogger().info("Transaction Complete");
  }

  public void district_set_gamer_permission(
      WriteDistrict district, WriteGamer gamer, AccessFlags flag, FlagValue value) {
    district.setGamerPermission(gamer, flag, value);
    couchPersister.update(district);
  }

  public void district_set_team_permission(
      WriteDistrict district, WriteTeam writeTeam, AccessFlags flag, FlagValue value) {
    district.setTeamPermission(writeTeam, flag, value);
    couchPersister.update(district);
  }

  public void gamer_toggle_message(WriteGamer gamer, MessageToggles flag, ToggleValues value) {
    if(gamer.preferences == null){
      gamer.preferences = new UserPreferences();
    }
    gamer.preferences.set(flag, value);
    couchPersister.update(gamer);
  }

  public void gamer_add_friend(WriteGamer a, Gamer b) {
    a.addFriend(b);
    couchPersister.update(a);
  }

  public void gamer_link_address(WriteGamer gamer, String address) {
    gamer.setAddress(address);
    getLinks().put(address, gamer.getUuid());
    couchPersister.update(gamer);
  }

  public void gamer_remove_friend(WriteGamer a, Gamer b) {
    a.removeFriend(b);
    couchPersister.update(a);
  }

  public WriteGamer getGamer(UUID uuid) {
    if (!gamers.containsKey(uuid)) {
      this.channels.master_command.publish(new Message<>(MasterCommand.touch_gamer, uuid));
      return null;
    }
    return gamers.get(uuid);
  }

  public Map<UUID, WriteGamer> getGamers() {
    return gamers;
  }

  public Map2<String, String, WriteNFT> getNfts() {return nfts; }

  public Map<String, WriteNFT> getNftUrls() {return nftUrls; }

  public Set<WriteMap> getMaps() {return maps; }

  public Map<String, UUID> getLinks() {
    return linked;
  }

  public ReadPlot getPlot(Integer x, Integer z) {
    Integer id = RedisGetter.GetPlotID(x.toString(),z.toString());
    ReadPlot plot = new ReadPlot(id, x, z);
    return plot;
  }

  public ReadPlot getPlot(Integer id){
    Integer x = RedisGetter.GetPlotX(id);
    Integer z = RedisGetter.GetPlotZ(id);
    ReadPlot plot = new ReadPlot(id, x, z);
    return plot;
  }

  public Map<Integer, WriteDistrict> getDistricts() {
    return districts;
  }

  public Town getTown(String town) {
    return towns.get(town);
  }

  public Map<String, WriteTown> getTowns() {
    return towns;
  }


  public District getDistrict(String nickname){
    String clean = nickname.replace("#","");
    try{
      Integer numnick = Integer.parseInt(clean);
      if(this.getDistrict(numnick) != null){
        return this.getDistrict(numnick);
      }
    }catch(Exception ignored){}
    Integer district_id = RedisGetter.GetDistrictOfName(clean);
    if(district_id != null){
      return getDistrict(district_id);
    }
    return null;
  }

  public District getDistrict(int id) {
    if(this.districts.containsKey(id)){
      return this.districts.get(id);
    }
    this.channels.master_command.publish(new Message<>(MasterCommand.touch_district,id));
    return null;
  }

  public District getDistrict(int x, int z) {
    ReadPlot location = this.getPlot(x,z);
    District out = null;
    if(location != null){
      Integer district_id = RedisGetter.GetDistrictOfPlot(location.getIdInt());
      if(district_id != null){
        out = getDistrict(district_id);
      }
    }
    return out;
  }

  public void team_add_gamer(WriteTeam team, WriteGamer gamer) {
      gamer.addTeam(team);
      team.addMember(gamer);
      couchPersister.update((WriteTown) team.getTownObject());
      couchPersister.update(gamer);
  }

  public void team_remove_gamer(WriteTeam team, WriteGamer gamer) {
      team.removeMember(gamer);
      gamer.removeTeam(team.getName());
    couchPersister.update((WriteTown) team.getTownObject());
    couchPersister.update(gamer);
  }

  public void team_set_priority(WriteTeam team, Integer b) {
    team.setPrioritySafe(b);
    couchPersister.update((WriteTown) team.getTownObject());
  }

  public void district_reclaim_district(WriteDistrict district) {
    WriteTown town = (WriteTown) getTown(district.getTown());
    if(town!=null) {
      town.deleteDistrict(district.getIdInt());
    }
    district.removeTown();
    couchPersister.update(district);
    couchPersister.update(town);
  }

  public void town_add_gamer(WriteTown town, WriteGamer gamer) {
    town.addMember(gamer);
    gamer.setTown(town.getName());
    couchPersister.update(town);
    couchPersister.update(gamer);
  }

  public void town_create_team(WriteTown town, String name) {
    town.createTeam(name);
    couchPersister.update(town);
  }

  public void town_create_town(WriteGamer gamer, String name) {
    WriteTown town = new WriteTown(gamer, name);
    if (!this.getTowns().containsKey(name)) {
      this.getTowns().put(name, town);
      gamer.setTown(town.getName());
      couchPersister.update(gamer);
      couchPersister.update(town);
      System.out.println("Town created");
      return;
    }
    System.out.println("Town failed to create");
  }

  public void town_delegate_district(WriteTown town, WriteDistrict district) {
    district_reclaim_district(district);
    town.addDistrict(district);
    district.setTown(town.getName());
    district.setDefaults();
    couchPersister.update(town);
    couchPersister.update(district);
  }

  public void town_delete_district(WriteTown town, WriteDistrict writeDistrict) {
    town.deleteDistrict(writeDistrict.getIdInt());
    couchPersister.update(town);
  }

  public void town_delete_team(WriteTown town, WriteTeam writeTeam) {
    for (UUID member : town.getMembers()) {
      WriteGamer gamer = getGamer(member);
      gamer.setTown("");
      couchPersister.update(gamer);
    }
    WriteGamer gamer = getGamer(town.getOwnerUUID());
    gamer.setTown("");
    town.deleteTeam(writeTeam.getName());
    couchPersister.update(town);
    couchPersister.update(gamer);
  }

  public void town_delete_town(WriteTown writeTown) {
    for (UUID member : writeTown.getMembers()) {
      WriteGamer gamer = getGamer(member);
      gamer.setTown("");
      gamer.clearTeams();
      couchPersister.update(gamer);
    }
    WriteGamer gamer = getGamer(writeTown.getOwnerUUID());
    gamer.setTown("");
    gamer.clearTeams();
    couchPersister.update(gamer);
    for (District d: writeTown.getDistrictObjects()) {
      WriteDistrict wd = (WriteDistrict) getDistrict(d.getIdInt());
      wd.removeTown();
      couchPersister.update(wd);
    }
    couchPersister.delete(writeTown);
    towns.remove(writeTown.getName());
  }

  public void town_remove_gamer(WriteTown town, WriteGamer gamer) {
    town.removeMember(gamer);
    gamer.setTown("");
    gamer.clearTeams();
    couchPersister.update(town);
    couchPersister.update(gamer);
  }

  public void nft_create_nft(WriteNFT entity){
    this.getNftUrls().put(entity.getUrl(), entity);
    this.getNfts().put(entity.getContract(), entity.getItem(), entity);
    couchPersister.update(entity);
  }

  public void map_create_map(WriteMap entity){
    if(entity!=null) {
      couchPersister.update(entity);
    }
    this.getMaps().add(entity);
  }

  public void map_rerender_maps() {
    for(WriteMap map : this.maps) {
      Image image = null;
      try {
        image = ImageIO.read(map.getUrl());
      }catch(Exception ex){
        ex.printStackTrace();
      }
      Set<Integer> mapIds = map.getMaps();
      renderHelper(image, mapIds);
    }
  }

  public void renderHelper(Image image, Set<Integer> mapIds) {
    int width = (int) Math.sqrt(mapIds.size());
    Image tmp = image.getScaledInstance(width * 128, width * 128, Image.SCALE_SMOOTH);
    BufferedImage photo = new BufferedImage(width * 128, width * 128, BufferedImage.TYPE_INT_ARGB);
    ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < width; j++) {
        stacks.add(new ItemStack(Material.FILLED_MAP, 1));
        Graphics2D g2d = photo.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        images.add(photo.getSubimage(j * 128, i * 128, 128, 128));
      }
    }
    int i = 0;
    for (int map : mapIds) {
      for (MapRenderer render : Bukkit.getMap(map).getRenderers()) {
        Bukkit.getMap(map).removeRenderer(render);
      }
      int finalI = i;
      MapRenderer mr = new MapRenderer() {
        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
          canvas.drawImage(0, 0, images.get(finalI));
        }
      };
      Bukkit.getMap(map).addRenderer(mr);
      MapMeta meta = ((MapMeta) stacks.get(i).getItemMeta());
      meta.setMapView(Bukkit.getMap(map));
      stacks.get(i).setItemMeta(meta);
      i++;
    }
  }

  public void shop_create_shop(WriteShop shop) {
    if(shop!=null){
      Bukkit.getLogger().info("Creating shop");
      this.shops.put(shop.getLocation(), shop);
    }
  }

  public WriteShop getShop(Location location) {
    return this.shops.get(location);
  }

  public Map<Location, WriteShop> getShops() {
    return shops;
  }

  public void touch_district(Integer id) {
    String owner = RedisGetter.GetOwnerOfDistrict(id.toString());
    if (owner != null) {
      if (!this.districts.containsKey(id)) {
        WriteDistrict dis = new WriteDistrict(id);
        this.districts.putIfAbsent(id, dis);
        couchPersister.update(dis);
      }
    }
  }

  public void touch_gamer(UUID id) {
      context_create_gamer(id);
  }
}

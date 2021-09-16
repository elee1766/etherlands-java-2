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
  public final Map<String, WriteTeam> teams = new HashMap<>();
  public final Map<String, UUID> linked = new HashMap<>();
  public final Map<String, WriteNFT> nftUrls = new HashMap<>();
  public final Map2<String,String,WriteNFT> nfts = new Map2<>();
  public final Set<WriteMap> maps = new HashSet<>();
  public final Map<String, WriteBankRecord> bankRecords = new HashMap<>();
  public final Map<Location, WriteShop> shops = new HashMap<>();

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

  public void district_set_group_permission(
      WriteDistrict district, WriteGroup writeGroup, AccessFlags flag, FlagValue value) {
    district.setGroupPermission(writeGroup, flag, value);
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
    return new ReadPlot(id,x,z);
  }

  public ReadPlot getPlot(Integer id){
    Integer x = RedisGetter.GetPlotX(id);
    Integer z = RedisGetter.GetPlotZ(id);
    return new ReadPlot(id,x,z);
  }

  public Map<Integer, WriteDistrict> getDistricts() {
    return districts;
  }

  public Team getTeam(String team) {
    return teams.get(team);
  }

  public Map<String, WriteTeam> getTeams() {
    return teams;
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

  public void group_add_gamer(WriteGroup group, WriteGamer gamer) {
    gamer.addGroup(group);
    group.addMember(gamer);
    couchPersister.update((WriteTeam) group.getTeamObject());
    couchPersister.update(gamer);
  }

  public void group_remove_gamer(WriteGroup group, WriteGamer gamer) {
    group.removeMember(gamer);
    gamer.removeGroup(group.getName());
    couchPersister.update((WriteTeam) group.getTeamObject());
    couchPersister.update(gamer);
  }

  public void group_set_priority(WriteGroup group, Integer b) {
    group.setPrioritySafe(b);
    couchPersister.update((WriteTeam) group.getTeamObject());
  }

  public void district_reclaim_district(WriteDistrict district) {
    WriteTeam team = (WriteTeam) getTeam(district.getTeam());
    if(team!=null) {
      System.out.println("updating team");
      team.deleteDistrict(district.getIdInt());
    }
    district.removeTeam();
    couchPersister.update(district);
    couchPersister.update(team);
  }

  public void team_add_gamer(WriteTeam team, WriteGamer gamer) {
    team.addMember(gamer);
    gamer.setTeam(team.getName());
    couchPersister.update(team);
    couchPersister.update(gamer);
  }

  public void team_create_group(WriteTeam team, String name) {
    team.createGroup(name);
    couchPersister.update(team);
  }

  public void team_create_team(WriteGamer gamer, String name) {
    WriteTeam team = new WriteTeam(gamer, name);
    if (!this.getTeams().containsKey(name)) {
      this.getTeams().put(name, team);
      gamer.setTeam(team.getName());
      couchPersister.update(gamer);
      couchPersister.update(team);
      System.out.println("Team created");
      return;
    }
    System.out.println("Team failed to create");
  }

  public void team_delegate_district(WriteTeam team, WriteDistrict district) {
    district_reclaim_district(district);
    team.addDistrict(district);
    district.setTeam(team.getName());
    district.setDefaults();
    couchPersister.update(team);
    couchPersister.update(district);
  }

  public void team_delete_district(WriteTeam team, WriteDistrict writeDistrict) {
    team.deleteDistrict(writeDistrict.getIdInt());
    couchPersister.update(team);
  }

  public void team_delete_group(WriteTeam team, WriteGroup writeGroup) {
    for (UUID member : team.getMembers()) {
      WriteGamer gamer = getGamer(member);
      gamer.setTeam("");
      couchPersister.update(gamer);
    }
    WriteGamer gamer = getGamer(team.getOwnerUUID());
    gamer.setTeam("");
    team.deleteGroup(writeGroup.getName());
    couchPersister.update(team);
    couchPersister.update(gamer);
  }

  public void team_delete_team(WriteTeam writeTeam) {
    for (UUID member : writeTeam.getMembers()) {
      WriteGamer gamer = getGamer(member);
      gamer.setTeam("");
      gamer.clearGroups();
      couchPersister.update(gamer);
    }
    WriteGamer gamer = getGamer(writeTeam.getOwnerUUID());
    gamer.setTeam("");
    gamer.clearGroups();
    couchPersister.update(gamer);
    for (District d: writeTeam.getDistrictObjects()) {
      WriteDistrict wd = (WriteDistrict) getDistrict(d.getIdInt());
      wd.removeTeam();
      couchPersister.update(wd);
    }
    couchPersister.delete(writeTeam);
    teams.remove(writeTeam.getName());
  }

  public void team_remove_gamer(WriteTeam team, WriteGamer gamer) {
    team.removeMember(gamer);
    gamer.setTeam("");
    gamer.clearGroups();
    couchPersister.update(team);
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

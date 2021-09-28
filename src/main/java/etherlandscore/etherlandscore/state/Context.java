package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.singleton.Asker;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
import etherlandscore.etherlandscore.state.preferences.UserPreferences;
import etherlandscore.etherlandscore.state.read.BankRecord;
import etherlandscore.etherlandscore.state.read.ReadPlot;
import etherlandscore.etherlandscore.state.write.*;
import etherlandscore.etherlandscore.util.Map2;
import etherlandscore.etherlandscore.util.Map3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Context {

  public final Map<Integer, District> districts = new HashMap<>();
  public final Map<String, Town> towns = new HashMap<>();
  public final Map<String, WriteNFT> nftUrls = new HashMap<>();
  public final Map3<Integer, Integer, Integer, WriteNFT> nfts = new Map3<>();
  public final Set<WriteMap> maps = new HashSet<>();
  public final Map<String, WriteBankRecord> bankRecords = new HashMap<>();
  public final Map<Location, WriteShop> shops = new HashMap<>();
  public final Map2<Integer, Integer, Integer> captchas = new Map2<>();

  public final Map<Gamer, Location> gamerLocations = new HashMap<>();

  public final Map<UUID, Integer> balanceCache = new HashMap<>();

  private final Channels channels;


  public Context(Channels channels) {
    this.channels = channels;
  }

  public void context_create_gamer(UUID uuid) {
  }

  public void context_mint_tokens(Gamer gamer, Integer amount) {
    String id = UUID.randomUUID().toString();
    this.bankRecords.put(
        id,
        new WriteBankRecord(
            id, new UUID(0, 0), gamer.getUuid(), amount, (int) System.currentTimeMillis()));
    getAbsoluteBalance(gamer.getUuid());
  }

  public void context_process_gamer_transaction(GamerTransaction transaction) {
    Bukkit.getLogger().info("Doing Transaction");
    Set<ItemStack> leftItems = transaction.getItemStacks().getFirst();
    Set<ItemStack> rightItems = transaction.getItemStacks().getSecond();

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
                (int) System.currentTimeMillis()));
        Bukkit.getLogger().info("Bank statement written");
        if (leftItems != null) {
          for (ItemStack item : leftItems) {
            if (!transaction.getInventorys().getFirst().contains(item)) {
              return;
            }
          }
        }
        if (rightItems != null) {
          for (ItemStack item : rightItems) {
            if (!transaction.getInventorys().getSecond().contains(item)) {
              return;
            }
          }
        }

        if (leftItems != null) {
          for (ItemStack item : leftItems) {
            transaction.getInventorys().getFirst().removeItem(item);
            transaction.getInventorys().getSecond().addItem(item);
          }
        }
        if (rightItems != null) {
          for (ItemStack item : rightItems) {
            transaction.getInventorys().getFirst().removeItem(item);
            transaction.getInventorys().getSecond().addItem(item);
          }
        }
      } else {
        if (leftItems != null) {
          for (ItemStack item : leftItems) {
            transaction.getInventorys().getFirst().removeItem(item);
            transaction.getGamers().getFirst().getPlayer().getInventory().addItem(item);
          }
        }
        if (rightItems != null) {
          for (ItemStack item : rightItems) {
            transaction.getInventorys().getSecond().removeItem(item);
            transaction.getGamers().getSecond().getPlayer().getInventory().addItem(item);
          }
        }
      }
    } else {
      if (leftItems != null) {
        for (ItemStack item : leftItems) {
          if (!transaction.getInventorys().getFirst().contains(item)) {
            return;
          }
        }
      }
      if (rightItems != null) {
        for (ItemStack item : rightItems) {
          if (!transaction.getInventorys().getSecond().contains(item)) {
            return;
          }
        }
      }

      if (leftItems != null) {
        for (ItemStack item : leftItems) {
          transaction.getInventorys().getFirst().removeItem(item);
          transaction.getInventorys().getSecond().addItem(item);
        }
      }
      if (rightItems != null) {
        for (ItemStack item : rightItems) {
          transaction.getInventorys().getFirst().removeItem(item);
          transaction.getInventorys().getSecond().addItem(item);
        }
      }
    }
    getAbsoluteBalance(transaction.getGamers().getFirst().getUuid());
    getAbsoluteBalance(transaction.getGamers().getSecond().getUuid());
    Bukkit.getLogger().info("Transaction Complete");
  }

  public void district_reclaim_district(District district) {
  }

  public void district_set_gamer_permission(
      District district, Gamer gamer, AccessFlags flag, FlagValue value) {
  }

  public void district_set_team_permission(
      District district, Team team, AccessFlags flag, FlagValue value) {
  }

  public void gamer_add_friend(Gamer a, Gamer b) {
    a.addFriend(b);
  }

  public void gamer_remove_friend(Gamer a, Gamer b) {
    a.removeFriend(b);
  }

  public void gamer_toggle_message(Gamer gamer, MessageToggles flag, ToggleValues value) {
    if (gamer.preferences == null) {
      gamer.preferences = new UserPreferences();
    }
    gamer.preferences.set(flag, value);
  }

  private Integer getAbsoluteBalance(UUID gamerId) {
    int balance = 0;
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
    } catch (Exception ex) {
      return 0;
    }
    return balance;
  }

  public Integer getBalance(UUID gamerId) {
    return this.balanceCache.getOrDefault(gamerId, 0);
  }

  public Map<String, WriteBankRecord> getBankRecords() {
    return this.bankRecords;
  }

  public Map2<Integer, Integer, Integer> getCaptchas() {
    return this.captchas;
  }

  public District getDistrict(String nickname) {
    String clean = nickname.replace("#", "");
    try {
      int numnick = Integer.parseInt(clean);
      if (this.getDistrict(numnick) != null) {
        return this.getDistrict(numnick);
      }
    } catch (Exception ignored) {
    }
    Integer district_id = Asker.GetDistrictOfName(clean);
    if (district_id != null) {
      return getDistrict(district_id);
    }
    return null;
  }

  public District getDistrict(int id) {
    if (this.districts.containsKey(id)) {
      return this.districts.get(id);
    }
    if (id != 0) {
      this.channels.master_command.publish(new Message<>(MasterCommand.touch_district, id));
    }
    return null;
  }

  public District getDistrict(int x, int z) {
    ReadPlot location = this.getPlot(x, z);
    District out = null;
    if (location != null) {
      Integer district_id = Asker.GetDistrictOfPlot(location.getIdInt());
      if (district_id != null) {
        out = getDistrict(district_id);
      }
    }
    return out;
  }

  public Map<Integer, District> getDistricts() {
    return districts;
  }

  public Gamer getGamer(UUID uuid) {
    return new Gamer(uuid);
  }

  public Location getGamerLocation(Gamer gamer) {
    return this.gamerLocations.get(gamer);
  }

  public Map<Gamer, Location> getGamerLocations() {
    return this.gamerLocations;
  }

  public Set<WriteMap> getMaps() {
    return maps;
  }

  public Map<String, WriteNFT> getNftUrls() {
    return nftUrls;
  }

  public Map3<Integer, Integer, Integer, WriteNFT> getNfts() {
    return nfts;
  }

  public ReadPlot getPlot(Integer x, Integer z) {
    Integer id = Asker.GetPlotID(x, z);
    return new ReadPlot(id, x, z);
  }

  public ReadPlot getPlot(Integer id) {
    Integer x = Asker.GetPlotX(id);
    Integer z = Asker.GetPlotZ(id);
    return new ReadPlot(id, x, z);
  }

  public WriteShop getShop(Location location) {
    return this.shops.get(location);
  }

  public Map<Location, WriteShop> getShops() {
    return shops;
  }

  public Town getTown(String town) {
    return new Town(town);
  }

  public Map<String, Town> getTowns() {
    return towns;
  }

  public boolean isValidCaptcha(int a, int b, int c) {
    if (this.getCaptchas().get(a, b) == null) {
      return true;
    } else {
      return this.getCaptchas().get(a, b) != c;
    }
  }

  public void map_create_map(WriteMap entity) {
    if (entity != null) {
    }
    this.getMaps().add(entity);
  }

  public void nft_create_nft(WriteNFT entity) {
    this.getNfts().put(entity.getXloc(), entity.getYloc(), entity.getZloc(), entity);
  }

  public void nft_delete_nft(WriteNFT entity) {
    this.getNfts().put(entity.getXloc(), entity.getYloc(), entity.getZloc(), null);
  }


  public void saveAll() {
  }

  public void shop_create_shop(WriteShop shop) {
    if (shop != null) {
      Bukkit.getLogger().info("Creating shop");
      this.shops.put(shop.getLocation(), shop);
    }
  }


  public void team_add_gamer(Team team, Gamer gamer) {
    gamer.addTeam(team);
    team.addMember(gamer);
  }

  public void team_remove_gamer(Team team, Gamer gamer) {
  }

  public void team_set_priority(Team team, Integer b) {
  }

  public void touch_district(Integer id) {
  }

  public void touch_gamer(UUID id) {
    context_create_gamer(id);
  }

  public void town_add_gamer(Town town, Gamer gamer) {
  }

  public void town_create_team(Town town, String name) {
  }

  public void town_create_town(Gamer gamer, String name) {}

  public void town_delegate_district(Town town, District district) {
  }

  public void town_delete_district(Town town, District district) {
  }

  public void town_delete_team(Town town, Team team) {
  }

  public void town_delete_town(Town town) {
  }

  public void town_remove_gamer(Town town, Gamer gamer) {
  }
}

package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.singleton.WorldAsker;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
import etherlandscore.etherlandscore.util.Map2;
import etherlandscore.etherlandscore.util.Map3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Context {

  public final Map<String, WriteNFT> nftUrls = new HashMap<>();
  public final Map3<Integer, Integer, Integer, WriteNFT> nfts = new Map3<>();
  public final Map<String, BankRecord> bankRecords = new HashMap<>();
  public final Map<Location, WriteShop> shops = new HashMap<>();
  public final Map2<Integer, Integer, Integer> captchas = new Map2<>();

  public final Map<Gamer, Location> gamerLocations = new HashMap<>();

  public final Map<UUID, Integer> balanceCache = new HashMap<>();

  public Context() {}

  public void context_mint_tokens(Gamer gamer, Integer amount) {
    String id = UUID.randomUUID().toString();
    this.bankRecords.put(
        id,
        new BankRecord(
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
            new BankRecord(
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

  public Map<String, BankRecord> getBankRecords() {
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
    Integer district_id = WorldAsker.GetDistrictOfName(clean);
    if (district_id != null) {
      return getDistrict(district_id);
    }
    return null;
  }

  public District getDistrict(int id) {
    return new District(id);
  }

  public District getDistrict(int x, int z) {
    Plot location = this.getPlot(x, z);
    if (location != null) {
      Integer district_id = WorldAsker.GetDistrictOfPlot(location.getIdInt());
      if (district_id != null) {
        return new District(district_id);
      }
    }
    return null;
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


  public Map<String, WriteNFT> getNftUrls() {
    return nftUrls;
  }

  public Map3<Integer, Integer, Integer, WriteNFT> getNfts() {
    return nfts;
  }

  public Plot getPlot(Integer x, Integer z) {
    Integer id = WorldAsker.GetPlotID(x, z);
    return new Plot(id, x, z);
  }

  public Plot getPlot(Integer id) {
    Integer x = WorldAsker.GetPlotX(id);
    Integer z = WorldAsker.GetPlotZ(id);
    return new Plot(id, x, z);
  }

  public WriteShop getShop(Location location) {
    return this.shops.get(location);
  }

  public Map<Location, WriteShop> getShops() {
    return shops;
  }

  public void nft_create_nft(WriteNFT entity) {
    this.getNfts().put(entity.getXloc(), entity.getYloc(), entity.getZloc(), entity);
  }

  public void nft_delete_nft(WriteNFT entity) {
    this.getNfts().put(entity.getXloc(), entity.getYloc(), entity.getZloc(), null);
  }

  public void shop_create_shop(WriteShop shop) {
    if (shop != null) {
      Bukkit.getLogger().info("Creating shop");
      this.shops.put(shop.getLocation(), shop);
    }
  }
  }





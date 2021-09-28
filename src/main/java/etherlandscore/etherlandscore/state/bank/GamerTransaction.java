package etherlandscore.etherlandscore.state.bank;

import etherlandscore.etherlandscore.state.write.Gamer;
import kotlin.Pair;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class GamerTransaction {
  Gamer gamerLeft;
  Gamer gamerRight;

  Inventory inventoryLeft;
  Inventory inventoryRight;

  Set<ItemStack> itemsLeft;
  Set<ItemStack> itemsRight;

  Integer gamerLeftDelta;
  Integer gamerRightDelta;

  public GamerTransaction(
      Gamer gamerLeft,
      Gamer gamerRight,
      Integer gamerLeftDelta,
      Integer gamerRightDelta,
      Inventory inventoryLeft,
      Inventory inventoryRight,
      Set<ItemStack> itemsLeft,
      Set<ItemStack> itemsRight) {
    this.gamerLeft = gamerLeft;
    this.gamerRight = gamerRight;
    this.gamerLeftDelta = gamerLeftDelta;
    this.gamerRightDelta = gamerRightDelta;
    this.inventoryLeft = inventoryLeft;
    this.inventoryRight = inventoryRight;
    this.itemsLeft = itemsLeft;
    this.itemsRight = itemsRight;
  }

  public Pair<Integer, Integer> getDeltas() {
    return new Pair<>(gamerLeftDelta, gamerRightDelta);
  }

  public Pair<Gamer, Gamer> getGamers() {
    return new Pair<>(gamerLeft, gamerRight);
  }

  public Pair<Inventory, Inventory> getInventorys() {
    return new Pair<>(inventoryLeft, inventoryRight);
  }

  public Pair<Set<ItemStack>, Set<ItemStack>> getItemStacks() {
    return new Pair<>(itemsLeft, itemsRight);
  }
}

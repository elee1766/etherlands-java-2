package etherlandscore.etherlandscore.state.write;

import etherlandscore.etherlandscore.state.read.Shop;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WriteShop  implements Shop {
  private final District district;
  private final Inventory inventory;
  private final Chest chest;
  private final Gamer owner;
  private final ItemStack item;
  private final Integer price;
  private final ArmorStand label;
  private String _id;

  public WriteShop(
      Chest chest,
      District district,
      Gamer owner,
      Inventory shopInventory,
      ItemStack item,
      Integer price,
      ArmorStand armorStand) {
    this.district = district;
    this.label = armorStand;
    this.chest = chest;
    this.owner = owner;
    this.inventory = shopInventory;
    this.item = item;
    this.price = price;
    this._id =
        (chest.getLocation().getBlockX() + "_" + chest.getLocation().getY() + "_" + chest.getZ());
  }

  public void addToInventory(ItemStack item) {
    this.inventory.addItem(item);
  }

  public void checkIfInInventory(ItemStack item) {
    this.inventory.contains(item);
  }

  public String getId() {
    return this._id;
  }

  public void setId(String string) {
    this._id =
        (chest.getLocation().getBlockX() + "_" + chest.getLocation().getY() + "_" + chest.getZ());
  }

  @Override
  public Inventory getInventory() {
    return this.inventory;
  }

  @Override
  public ItemStack getItem() {
    return this.item;
  }

  @Override
  public ArmorStand getLabel() {
    return this.label;
  }

  @Override
  public Location getLocation() {
    return this.chest.getLocation();
  }

  @Override
  public Gamer getOwner() {
    return this.owner;
  }

  @Override
  public Integer getPrice() {
    return this.price;
  }

  public void removeFromInventory(ItemStack item) {
    this.inventory.removeItem(item);
  }
}

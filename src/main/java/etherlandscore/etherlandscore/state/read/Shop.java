package etherlandscore.etherlandscore.state.read;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Shop {

  Location getLocation();

  Inventory getInventory();

  Gamer getOwner();

  ItemStack getItem();

  Integer getPrice();

}

package etherlandscore.etherlandscore.state.read;

import etherlandscore.etherlandscore.state.Gamer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Shop {

  Inventory getInventory();

  ItemStack getItem();

  ArmorStand getLabel();

  Location getLocation();

  Gamer getOwner();

  Integer getPrice();
}

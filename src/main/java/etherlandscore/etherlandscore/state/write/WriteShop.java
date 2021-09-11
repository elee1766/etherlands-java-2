package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Shop;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WriteShop extends CouchDocument implements Shop {
    private final District district;
    private final Inventory inventory;
    private final Chest chest;
    private final Gamer owner;
    private String _id;

    @JsonCreator
    public WriteShop(@JsonProperty("chest") Chest chest, @JsonProperty("district") District district, @JsonProperty("owner") Gamer owner, @JsonProperty("inventory") Inventory shopInventory){
        this.district = district;
        this.chest = chest;
        this.owner = owner;
        this.inventory = shopInventory;
        this._id = (chest.getLocation().getBlockX() + "_" + chest.getLocation().getY() + "_" + chest.getZ());
    }

    @JsonProperty("_id")
    public String getId() {
        return this._id;
    }
    @JsonProperty("_id")
    public void setId(String string) {
        this._id = (chest.getLocation().getBlockX() + "_" + chest.getLocation().getY() + "_" + chest.getZ());
    }

    @Override
    public Location getLocation(){
        return this.chest.getLocation();
    }

    @Override
    public Inventory getInventory(){
        return this.inventory;
    }

    public void addToInventory(ItemStack item){
        this.inventory.addItem(item);
    }

    public void removeFromInventory(ItemStack item){
        this.inventory.removeItem(item);
    }

    public void checkIfInInventory(ItemStack item){
        this.inventory.contains(item);
    }

}

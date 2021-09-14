package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Shop;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WriteShop extends CouchDocument implements Shop {
    private final District district;
    private final Inventory inventory;
    private final Chest chest;
    private final Gamer owner;
    private final ItemStack item;
    private final Integer price;
    private final ArmorStand label;
    private String _id;

    @JsonCreator
    public WriteShop(@JsonProperty("chest") Chest chest, @JsonProperty("district") District district, @JsonProperty("owner") Gamer owner, @JsonProperty("inventory") Inventory shopInventory, @JsonProperty("Item") ItemStack item, @JsonProperty("Price") Integer price, @JsonProperty("Label")ArmorStand armorStand){
        this.district = district;
        this.label = armorStand;
        this.chest = chest;
        this.owner = owner;
        this.inventory = shopInventory;
        this.item = item;
        this.price = price;
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
    public ArmorStand getLabel() {return this.label;}

    @Override
    public Location getLocation(){
        return this.chest.getLocation();
    }

    @Override
    public ItemStack getItem() { return this.item ;}

    @Override
    public Integer getPrice() { return this.price ;}

    @Override
    public Gamer getOwner(){
        return this.owner;
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

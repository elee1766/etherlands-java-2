package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.EtherlandsCore;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.NFT;
import etherlandscore.etherlandscore.state.read.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteNFT extends CouchDocument implements NFT {
    private String url;
    private String contractAddr;
    private String itemID;
    private String ownerAddr;
    private String _id;
    private String filepath;

    @JsonCreator
    public WriteNFT(@JsonProperty("url") String image_url, @JsonProperty("contractAddr") String address, @JsonProperty("itemID") String token_id){
        this.url = image_url;
        this.contractAddr = address;
        this.itemID = token_id;
        this.ownerAddr = this.getOwnerAddr();
        this.filepath = Bukkit.getPluginManager().getPlugin("EtherlandsCore").getDataFolder()+"/"+address+"/"+token_id;
    }

    @JsonProperty("_id")
    public String getId() {
        return (this.contractAddr+"_"+this.itemID);
    }
    @JsonProperty("_id")
    public void setId(String string) {
        this._id = (this.contractAddr+"_"+this.itemID);
    }

    @Override
    @JsonIgnore
    public Field[] getDeclaredFields() {
        return new Field[0];
    }

    @Override
    @JsonIgnore
    public String getOwnerAddr() {
        return null;
    }

    @Override
    public String getContractAddr() {
        return contractAddr;
    }

    public void setContract(String contractAddr) {
        this.contractAddr = contractAddr;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    @Override
    public String getItemID() {
        return itemID;
    }

    @Override
    @JsonIgnore
    public String getFilePath() {
        return Bukkit.getPluginManager().getPlugin("EtherlandsCore").getDataFolder()+"/"+this.contractAddr+"/"+this.itemID;
    }

    public void setFilePath() {
        this.filepath = Bukkit.getPluginManager().getPlugin("EtherlandsCore").getDataFolder()+"/"+this.contractAddr+"/"+this.itemID;
    }

    @Override
    public String getURL() {

        return this.url;
    }
}

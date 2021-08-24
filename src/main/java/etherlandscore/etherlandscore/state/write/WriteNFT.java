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

    @JsonCreator
    public WriteNFT(@JsonProperty("image_url") String image_url, @JsonProperty("asset.address") String address, @JsonProperty("token_id") String id){
        this.url = image_url;
        this.contractAddr = address;
        this.itemID = id;
        this.ownerAddr = this.getOwnerAddr();
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String string) {

    }

    @Override
    public Field[] getDeclaredFields() {
        return new Field[0];
    }

    @Override
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

    @Override
    public String getItemID() {
        return itemID;
    }

    @Override
    public String getFilePath() {
        return Bukkit.getPluginManager().getPlugin("EtherlandsCore").getDataFolder()+"/"+contractAddr+"/"+itemID;
    }

    @Override
    public String getURL() {

        return this.url;
    }
}

package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.NFT;
import org.bukkit.Bukkit;

public class WriteNFT extends CouchDocument implements NFT {
    private final String url;
    private final String contract;
    private final String item;
    private String path;
    private String _id;

    @JsonCreator
    public WriteNFT(@JsonProperty("url") String url, @JsonProperty("contract") String contract, @JsonProperty("item") String item){
        this.url = url;
        this.contract = contract;
        this.item = item;
        this.path = Bukkit.getPluginManager().getPlugin("EtherlandsCore").getDataFolder()+"/"+contract+"/"+item;
    }

    @JsonProperty("_id")
    public String getId() {
        return (this.contract+"_"+this.item);
    }
    @JsonProperty("_id")
    public void setId(String string) {
        this._id = (this.contract+"_"+this.item);
    }

    @Override
    public String getContract() {
        return contract;
    }

    @Override
    public String getItem() {
        return item;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    public void setPath() {
        this.path = Bukkit.getPluginManager().getPlugin("EtherlandsCore").getDataFolder()+"/"+this.contract+"/"+this.item;
    }

    @Override
    public String getUrl() {
        return this.url;
    }
}

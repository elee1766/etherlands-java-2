package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.NFT;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Set;

public class WriteMap extends CouchDocument implements etherlandscore.etherlandscore.state.read.MapRead {
    private final Set<Integer> maps;
    private final URL url;
    private String _id;

    @JsonCreator
    public WriteMap(@JsonProperty("_id") String id, @JsonProperty("maps") Set<Integer> maps, @JsonProperty("url") URL url){
        this.maps = maps;
        this.url = url;
        this._id = id;
    }

    @JsonProperty("_id")
    public String getId() {
        return this._id;
    }

    @JsonProperty("_id")
    public void setId(String string) {
        this._id = string;
    }

    @Override
    public Set<Integer> getMaps() {
        return maps;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

}

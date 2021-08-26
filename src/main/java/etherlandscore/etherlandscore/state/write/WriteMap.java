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

@JsonIgnoreProperties(ignoreUnknown = true)
public class WriteMap extends CouchDocument implements etherlandscore.etherlandscore.state.read.MapRead {
    private Set<Integer> mapIDs;
    private URL image_url;
    private String id;
    private String _id;

    @JsonCreator
    public WriteMap(@JsonProperty("_id") String id, @JsonProperty("MapID") Set<Integer> mapIDs, @JsonProperty("image_url") URL image_url){
        this.mapIDs = mapIDs;
        this.image_url = image_url;
        this._id = id;
    }

    @JsonProperty("_id")
    public String getId() {
        return this.id;
    }
    @JsonProperty("_id")
    public void setId(String string) {
        this._id = this.id;
    }

    @Override
    public Set<Integer> getMapIDs() {
        return mapIDs;
    }

    @Override
    public URL getImage_url() {
        return this.image_url;
    }

    public void setUrl(URL url){
        this.image_url = url;
    }

    public void addId(int mapIDs) {
        this.mapIDs.add(mapIDs);
    }
}

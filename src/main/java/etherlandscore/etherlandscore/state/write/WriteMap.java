package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.NFT;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Set;

public class WriteMap extends CouchDocument implements etherlandscore.etherlandscore.state.read.MapRead {
    private Set<Integer> mapIDs;
    private URL image_url;
    private String id;

    @JsonCreator
    public WriteMap(@JsonProperty("_id") String id, Set<Integer> mapIDs, @JsonProperty("image_url") URL image_url){
        this.mapIDs = mapIDs;
        this.image_url = image_url;
        this.id = id;
    }

    @Override
    public Set<Integer> getMapID() {
        return mapIDs;
    }

    @Override
    public URL getImage_url() {
        return this.image_url;
    }

    public void setUrl(URL url){
        this.image_url = url;
    }

    public void addId(int mapID) {
        this.mapIDs.add(mapID);
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String string) {

    }
}

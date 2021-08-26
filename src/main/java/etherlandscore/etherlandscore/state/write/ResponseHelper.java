package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.NFT;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class ResponseHelper {
    private final String url;
    private final String itemId;

    @JsonCreator
    public ResponseHelper(@JsonProperty("image_url")String image_url,@JsonProperty("token_id") String token_id){
        this.url = image_url;
        this.itemId = token_id;
    }
    public String getURL() {
        return this.url;
    }
}

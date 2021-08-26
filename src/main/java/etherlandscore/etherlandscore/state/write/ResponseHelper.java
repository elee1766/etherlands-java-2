package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import etherlandscore.etherlandscore.persistance.Couch.CouchDocument;
import etherlandscore.etherlandscore.state.read.NFT;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {
    private final JSONObject[] assets;

    @JsonCreator
    public ResponseHelper(@JsonProperty("assets") JSONObject[] assets){
        this.assets = assets;
    }
    @JsonProperty("assets")
    public String getImageurl() {
        return this.assets[0].get("image_url").toString();
    }
}

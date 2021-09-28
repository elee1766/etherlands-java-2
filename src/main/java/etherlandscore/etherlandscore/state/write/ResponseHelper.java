package etherlandscore.etherlandscore.state.write;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.simple.JSONObject;

public class ResponseHelper {
  private final JSONObject[] assets;

  @JsonCreator
  public ResponseHelper(@JsonProperty("assets") JSONObject[] assets) {
    this.assets = assets;
  }

  @JsonProperty("assets")
  public String getImageurl() {
    return this.assets[0].get("image_url").toString();
  }
}

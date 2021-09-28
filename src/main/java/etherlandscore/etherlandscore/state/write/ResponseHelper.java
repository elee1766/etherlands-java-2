package etherlandscore.etherlandscore.state.write;

import org.json.simple.JSONObject;

public class ResponseHelper {
  private final JSONObject[] assets;

  public ResponseHelper(JSONObject[] assets) {
    this.assets = assets;
  }

  public String getImageurl() {
    return this.assets[0].get("image_url").toString();
  }
}

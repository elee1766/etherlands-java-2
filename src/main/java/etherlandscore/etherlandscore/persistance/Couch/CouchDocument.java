package etherlandscore.etherlandscore.persistance.Couch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.entity.ArmorStand;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class CouchDocument {

  @JsonProperty("_rev")
  private String rev;

  public abstract String getId();

  public abstract void setId(String string);

  @JsonProperty("_rev")
  public String getRevision() {
    return this.rev;
  }

  @JsonProperty("_rev")
  public void setRevision(String s) {
    this.rev = s;
  }

}

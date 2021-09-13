package etherlandscore.etherlandscore.state.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.state.write.WriteGamer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserPreferences {

  private ToggleValues automap;
  private ToggleValues global_chat;
  private ToggleValues team_chat;
  private ToggleValues district;
  private ToggleValues local_chat;

  public UserPreferences() {
    setDefaults();
  }

  private void setDefaults(){
    this.automap = ToggleValues.DISABLED;
    this.global_chat = ToggleValues.ENABLED;
    this.team_chat = ToggleValues.DISABLED;
    this.district = ToggleValues.ENABLED;
    this.local_chat = ToggleValues.DISABLED;
  }

  public void set(MessageToggles toggles, ToggleValues value){
    switch (toggles) {
      case MAP:
        automap = value;
      case GLOBAL_CHAT:
        global_chat = value;
      case TEAM_CHAT:
        team_chat = value;
      case DISTRICT:
        district = value;
      case LOCAL_CHAT:
        local_chat = value;
    }
  }
  public boolean automap(){
    return automap.equals(ToggleValues.ENABLED);
  }

  public boolean globalChat(){
    return global_chat.equals(ToggleValues.ENABLED);
  }

  public boolean teamChat(){
    return team_chat.equals(ToggleValues.ENABLED);
  }

  public boolean district(){
    return district.equals(ToggleValues.ENABLED);
  }

  public boolean localChat() {return local_chat.equals(ToggleValues.ENABLED);}

}

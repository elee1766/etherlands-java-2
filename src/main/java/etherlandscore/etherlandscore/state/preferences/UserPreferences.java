package etherlandscore.etherlandscore.state.preferences;

import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;

import java.util.HashMap;
import java.util.Map;

public class UserPreferences {

  public Map<MessageToggles, ToggleValues> storage;

  public UserPreferences() {
    this.storage = new HashMap<>();
    apply_defaults();
  }

  private void apply_defaults() {
    set(MessageToggles.MAP, ToggleValues.DISABLED);
    set(MessageToggles.GLOBAL_CHAT, ToggleValues.ENABLED);
    set(MessageToggles.TEAM_CHAT, ToggleValues.DISABLED);
    set(MessageToggles.DISTRICT, ToggleValues.ENABLED);
    set(MessageToggles.LOCAL_CHAT, ToggleValues.DISABLED);
  }

  public boolean checkPreference(MessageToggles preference) {
    return storage.getOrDefault(preference, ToggleValues.DISABLED).equals(ToggleValues.ENABLED);
  }

  public void set(MessageToggles toggles, ToggleValues value) {
    storage.put(toggles, value);
  }
}

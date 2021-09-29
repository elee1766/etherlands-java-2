package etherlandscore.etherlandscore.state.preferences;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserPreferences {

  static Cache<UUID, UserPreferences> cache = Caffeine.newBuilder()
      .build();

  public static UserPreferences GetPreferences(UUID uuid){
    return cache.get(uuid, UserPreferences::CreateDefaults);
  }

  static UserPreferences CreateDefaults(UUID uuid){
    return new UserPreferences(uuid);
  }

  private UUID key;

  public Map<MessageToggles, ToggleValues> storage;

  public UserPreferences(UUID key) {
    this.key = key;
    this.storage = new ConcurrentHashMap<>();
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
    return storage.get(preference).equals(ToggleValues.ENABLED);
  }

  public ToggleValues toggle(MessageToggles toggle){
    switch (storage.get(toggle)) {
      case ENABLED -> {
        set(toggle, ToggleValues.DISABLED);
        return ToggleValues.DISABLED;
      }
      case DISABLED -> {
        set(toggle, ToggleValues.ENABLED);
        return ToggleValues.ENABLED;
      }
    }
    return ToggleValues.DISABLED;
  }

  public void set(MessageToggles toggles, ToggleValues value) {
    storage.put(toggles, value);
  }
}

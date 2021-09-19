package etherlandscore.etherlandscore.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Map2<K1, K2, V> {
  Map<K1, Map<K2, V>> map = new HashMap<>();

  @JsonCreator
  public Map2() {}

  public void clearTeam(K1 name) {
    map.remove(name);
  }

  @JsonIgnore
  public V get(K1 key1, K2 key2) {
    if (!map.containsKey(key1)) {
      return null;
    }
    return map.get(key1).getOrDefault(key2, null);
  }

  public Map<K1, Map<K2, V>> getMap() {
    return map;
  }

  public void setMap(Map<K1, Map<K2, V>> map) {
    this.map = map;
  }

  @JsonIgnore
  public V getOrDefault(K1 key1, K2 key2, V o) {
    if (!map.containsKey(key1)) {
      return o;
    }
    return map.get(key1).getOrDefault(key2, o);
  }

  @JsonIgnore
  public void put(K1 key1, K2 key2, V value) {
    if (!map.containsKey(key1)) {
      map.put(key1, new HashMap<>());
    }
    map.get(key1).put(key2, value);
  }
}

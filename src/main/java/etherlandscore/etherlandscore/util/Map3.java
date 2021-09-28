package etherlandscore.etherlandscore.util;

import java.util.HashMap;
import java.util.Map;

public class Map3<K1, K2, K3, V> {
  Map<K1, Map<K2, Map<K3, V>>> map = new HashMap<>();

  public Map3() {}

  public V get(K1 key1, K2 key2, K3 key3) {
    if (!map.containsKey(key1)) {
      return null;
    }
    if (!map.get(key1).containsKey(key2)) {
      return null;
    }
    return map.get(key1).get(key2).getOrDefault(key3, null);
  }

  public Map<K1, Map<K2, Map<K3, V>>> getMap() {
    return map;
  }

  public void setMap(Map<K1, Map<K2, Map<K3, V>>> map) {
    this.map = map;
  }

  public V getOrDefault(K1 key1, K2 key2, K3 key3, V o) {
    if (!map.containsKey(key1)) {
      return o;
    }
    if (!map.get(key1).containsKey(key2)) {
      return o;
    }
    return map.get(key1).get(key2).getOrDefault(key3, o);
  }

  public void put(K1 key1, K2 key2, K3 key3, V value) {
    if (!map.containsKey(key1)) {
      map.put(key1, new HashMap<>());
    }
    if (!map.get(key1).containsKey(key2)) {
      map.get(key1).put(key2, new HashMap<>());
    }
    map.get(key1).get(key2).put(key3, value);
  }
}

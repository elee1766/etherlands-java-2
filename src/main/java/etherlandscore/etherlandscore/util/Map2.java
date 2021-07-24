package etherlandscore.etherlandscore.util;

import java.util.HashMap;
import java.util.Map;

public class Map2<K1, K2, V> {
  Map<K1, Map<K2,V>> map = new HashMap<>();
  public Map2(){
  }

  public void put(K1 key1, K2 key2, V value){
    if(!map.containsKey(key1)){
      map.put(key1,new HashMap<>());
    }
    map.get(key1).put(key2,value);
  }

  public V get(K1 key1, K2 key2){
    if(!map.containsKey(key1)){
      return null;
    }
    return map.get(key1).getOrDefault(key2,null);
  }

  public V getOrDefault(K1 key1, K2 key2, V o){
    if(!map.containsKey(key1)){
      return o;
    }
    return map.get(key1).getOrDefault(key2,o);
  }
}

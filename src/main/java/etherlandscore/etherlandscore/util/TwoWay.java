package etherlandscore.etherlandscore.util;

import java.util.HashMap;
import java.util.Map;

public class TwoWay<F, S> {

  Map<F, S> forward = new HashMap<>();
  Map<S, F> backward = new HashMap<>();

  public TwoWay() {}

  public S getFirst(F key) {
    if (!forward.containsKey(key)) {
      return null;
    }
    return forward.getOrDefault(key, null);
  }

  public S getFirstOrDefault(F key, S def) {
    return forward.getOrDefault(key, def);
  }

  public F getSecond(S key) {
    if (!backward.containsKey(key)) {
      return null;
    }
    return backward.getOrDefault(key, null);
  }

  public F getSecondOrDefault(S key, F def) {
    return backward.getOrDefault(key, def);
  }

  public void put(F first, S second) {
    this.forward.put(first, second);
    this.backward.put(second, first);
  }
}

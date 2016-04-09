package jp.thotta.ifinance.utilizer;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

public class Utility {
  public static List<String> sortedKeys(
      final Map<String, Double> m) {
    List<String> keys = new ArrayList<String>(m.keySet());
    Collections.sort(keys, new Comparator<String>() {
      @Override
      public int compare(String k1, String k2) {
        if(!m.get(k1).equals(m.get(k2))) {
          return m.get(k1) > m.get(k2) ? -1 : 1;
        } else {
          return k2.compareTo(k1);
        }
      }
    });
    return keys;
  }
}

package com.bear.core.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bear.core.Event;



public class MapLookup implements StrLookup {


  static final MapLookup MAIN_SINGLETON = new MapLookup(newMap(0));

  static Map<String, String> initMap(final String[] srcArgs, final Map<String, String> destMap) {
    for (int i = 0; i < srcArgs.length; i++) {
      final int next = i + 1;
      final String value = srcArgs[i];
      destMap.put(Integer.toString(i), value);
      destMap.put(value, next < srcArgs.length ? srcArgs[next] : null);
    }
    return destMap;
  }

  private static HashMap<String, String> newMap(final int initialCapacity) {
    return new HashMap<String, String>(initialCapacity);
  }


  public static void setMainArguments(final String[] args) {
    if (args == null) {
      return;
    }
    initMap(args, MAIN_SINGLETON.map);
  }

  static Map<String, String> toMap(final List<String> args) {
    if (args == null) {
      return null;
    }
    final int size = args.size();
    return initMap(args.toArray(new String[size]), newMap(size));
  }

  static Map<String, String> toMap(final String[] args) {
    if (args == null) {
      return null;
    }
    return initMap(args, newMap(args.length));
  }


  private final Map<String, String> map;


  public MapLookup() {
    this.map = null;
  }


  public MapLookup(final Map<String, String> map) {
    this.map = map;
  }

  @Override
  public String lookup(final Event event, final String key) {

    return null;
  }


  @Override
  public String lookup(final String key) {
    if (map == null) {
      return null;
    }
    return map.get(key);
  }

}

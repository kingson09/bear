package com.bear.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;



public class RecorderRegistry <T extends Recorder> {

  private final ConcurrentHashMap<String, T> map;


  public RecorderRegistry() {
    this.map = new ConcurrentHashMap<String, T>();
  }


  public T getRecorder(final String name) {
    return map.get(name);
  }


  public Collection<T> getRecorders() {
    return getRecorders(new ArrayList<T>());
  }

  public Collection<T> getRecorders(final Collection<T> destination) {
    for (final T inner : map.values()) {
      destination.add(inner);
    }
    return destination;
  }



  public boolean hasRecorder(final String name) {
    return map.containsKey(name);
  }


  public void putIfAbsent(final String name, final T recorder) {
    map.putIfAbsent( name, recorder);
  }
}

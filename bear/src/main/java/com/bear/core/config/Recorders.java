package com.bear.core.config;

import java.util.concurrent.ConcurrentMap;



public class Recorders {
  private final ConcurrentMap<String, RecorderConfig> map;
  private final RecorderConfig root;

  public Recorders(final ConcurrentMap<String, RecorderConfig> map, final RecorderConfig root) {
    this.map = map;
    this.root = root;
  }

  public ConcurrentMap<String, RecorderConfig> getMap() {
    return map;
  }

  public RecorderConfig getRoot() {
    return root;
  }
}

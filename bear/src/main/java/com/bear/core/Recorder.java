package com.bear.core;



public interface Recorder {
  void record(Event event);

  String getName();

  RecorderContext getContext();

  void updateConfiguration();
}

package com.bear.core;

import com.bear.core.config.RecorderConfig;



public class RecorderImpl implements Recorder {

  private String name;
  private RecorderContext context;
  private RecorderConfig recorderConfig;

  public RecorderImpl(RecorderContext context, String name) {
    this.name = name;
    this.context = context;
    updateConfiguration();
  }

  @Override
  public void record(Event event) {
    recorderConfig.record(event);
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public RecorderContext getContext() {
    return context;
  }

  @Override
  public void updateConfiguration() {
    recorderConfig = context.getConfiguration().getRecorderConfig(name);
  }
}

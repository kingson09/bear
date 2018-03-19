package com.bear.core;

import com.bear.core.config.Configuration;
import com.bear.core.config.DefaultConfiguration;


public class RecorderManager {
  private static RecorderContext recorderContext;

  public static void initialize() {
    recorderContext = new RecorderContext("default", new DefaultConfiguration());
    if (!recorderContext.isInitialized()) {
      recorderContext.initialize();
    }
  }

  public static void initialize(Configuration configuration) {

    recorderContext = new RecorderContext("default", configuration);
    if (!recorderContext.isInitialized()) {
      recorderContext.initialize();
    }
  }

  public static RecorderContext getRecorderContext() {
    return recorderContext;
  }

}

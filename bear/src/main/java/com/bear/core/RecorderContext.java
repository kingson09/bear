package com.bear.core;

import java.util.Collection;

import com.bear.core.config.Configuration;
import com.bear.core.util.Objects;


public class RecorderContext extends AbstractLifeCycle {

  private final RecorderRegistry<Recorder> recorderRegistry = new RecorderRegistry<>();

  private volatile Configuration configuration;
  private String contextName;



  public RecorderContext(final String name, final Configuration config) {
    this.contextName = name;
    setConfiguration(config);
  }

  public void initialize() {
    configuration.initialize();
    super.initialize();
  }


  public String getName() {
    return contextName;
  }


  public void setName(final String name) {
    contextName = Objects.requireNonNull(name);
  }


  public Collection<Recorder> getRecorders() {
    return recorderRegistry.getRecorders();
  }

  public Recorder getRecorder(final String name) {
    // Note: This is the only method where we add entries to the 'recorderRegistry' ivar.
    Recorder recorder = recorderRegistry.getRecorder(name);
    if (recorder != null) {
      return recorder;
    }
    recorder = newInstance(this, name);
    recorderRegistry.putIfAbsent(name, recorder);
    return recorderRegistry.getRecorder(name);
  }



  public boolean hasRecorder(final String name) {
    return recorderRegistry.hasRecorder(name);
  }



  public Configuration getConfiguration() {
    return configuration;
  }


  public void addFilter(final Filter filter) {
    configuration.addFilter(filter);
  }


  public void removeFilter(final Filter filter) {
    configuration.removeFilter(filter);
  }


  private void setConfiguration(final Configuration config) {
    if (config == null) {
      return;
    }
    this.configuration = config;
    updateRecorders();

  }



  public void updateRecorders() {
    for (final Recorder recorder : recorderRegistry.getRecorders()) {
      recorder.updateConfiguration();
    }
  }


  // LOG4J2-151: changed visibility from private to protected
  protected Recorder newInstance(final RecorderContext ctx, final String name) {
    return new RecorderImpl(ctx, name);
  }

}

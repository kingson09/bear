package com.bear.core;

import com.bear.core.util.StatusLogger;


public class AbstractLifeCycle implements LifeCycle {


  protected static final StatusLogger LOGGER = StatusLogger.getLogger();


  private volatile LifeCycle.State state = State.INITIALIZING;

  protected boolean equalsImpl(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final LifeCycle other = (LifeCycle) obj;
    if (state != other.getState()) {
      return false;
    }
    return true;
  }


  public LifeCycle.State getState() {
    return this.state;
  }

  protected int hashCodeImpl() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    return result;
  }

  @Override
  public boolean isInitialized() {
    return this.state == LifeCycle.State.INITIALIZED;
  }

  @Override
  public void initialize() {
    this.setState(State.INITIALIZING);
  }

  @Override
  public boolean isStopped() {
    return this.state == State.STOPED;
  }

  @Override
  public void shutdown() {
    this.setState(State.STOPED);
  }

  protected void setState(final LifeCycle.State newState) {
    this.state = newState;

  }

}

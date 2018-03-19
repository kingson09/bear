package com.bear.core;


public interface LifeCycle {

  public enum State {

    INITIALIZING,

    INITIALIZED,

    STOPED
  }


  State getState();

  void initialize();

  boolean isInitialized();

  void shutdown();

  boolean isStopped();
}

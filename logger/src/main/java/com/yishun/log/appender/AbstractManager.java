package com.yishun.log.appender;

import com.yishun.log.util.StatusLogger;


public abstract class AbstractManager {


  protected static final StatusLogger LOGGER = StatusLogger.getLogger();


  private final String name;

  protected AbstractManager(final String name) {
    this.name = name;
    LOGGER.debug("Starting {} {}", this.getClass().getSimpleName(), name);
  }

  public static <M extends AbstractManager, T> M getManager(final String name, final ManagerFactory<M, T> factory,
      final T data) {

    M manager = factory.createManager(name, data);
    if (manager == null) {
      throw new IllegalStateException("Unable to create a manager");
    }
    return manager;
  }

  public String getName() {
    return name;
  }

}

package com.yishun.log.appender.archive.strategy.action;

import com.yishun.log.util.StatusLogger;


public abstract class AbstractAction implements Action {

  protected static final StatusLogger LOGGER = StatusLogger.getLogger();

  private boolean complete = false;


  private boolean interrupted = false;


  protected AbstractAction() {
  }


  @Override
  public abstract boolean execute();


  @Override
  public synchronized void run() {
    if (!interrupted) {
      execute();
      complete = true;
      interrupted = true;
    }
  }


  @Override
  public synchronized void close() {
    interrupted = true;
  }


  @Override
  public boolean isComplete() {
    return complete;
  }


}

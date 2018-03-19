package com.yishun.log.appender.archive.strategy;

import com.bear.core.Event;
import com.yishun.log.appender.archive.RollingFileManager;
import com.yishun.log.appender.archive.strategy.action.Action;


public interface ArchiveAndRolloverStrategy {

  Action archive() throws SecurityException;

  Action rollover() throws SecurityException;



  void initialize(final RollingFileManager manager);


  boolean isTriggeringEvent(final Event event);
}

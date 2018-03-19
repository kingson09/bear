package com.bear.core.appender;

import java.util.concurrent.TimeUnit;

import com.bear.core.Appender;
import com.bear.core.ErrorHandler;
import com.bear.core.Event;




public class DefaultErrorHandler implements ErrorHandler {

//  private static final //TODO LOGGER //TODO LOGGER = Status//TODO LOGGER.get//TODO LOGGER();

  private static final int MAX_EXCEPTIONS = 3;

  private static final long EXCEPTION_INTERVAL = TimeUnit.MINUTES.toNanos(5);

  private int exceptionCount = 0;

  private long lastException = System.nanoTime() - EXCEPTION_INTERVAL - 1;

  private final Appender appender;

  public DefaultErrorHandler(final Appender appender) {
    this.appender = appender;
  }


  @Override
  public void error(final String msg) {
    final long current = System.nanoTime();
    if (current - lastException > EXCEPTION_INTERVAL || exceptionCount++ < MAX_EXCEPTIONS) {
      //TODO LOGGER.error(msg);
    }
    lastException = current;
  }


  @Override
  public void error(final String msg, final Throwable t) {
    final long current = System.nanoTime();
    if (current - lastException > EXCEPTION_INTERVAL || exceptionCount++ < MAX_EXCEPTIONS) {
      //TODO LOGGER.error(msg, t);
    }
    lastException = current;
    if (!appender.ignoreExceptions() && t != null && !(t instanceof AppenderRecordingException)) {
      throw new AppenderRecordingException(msg, t);
    }
  }


  @Override
  public void error(final String msg, final Event event, final Throwable t) {
    final long current = System.nanoTime();
    if (current - lastException > EXCEPTION_INTERVAL || exceptionCount++ < MAX_EXCEPTIONS) {
      //TODO LOGGER.error(msg, t);
    }
    lastException = current;
    if (!appender.ignoreExceptions() && t != null && !(t instanceof AppenderRecordingException)) {
      throw new AppenderRecordingException(msg, t);
    }
  }

  public Appender getAppender() {
    return appender;
  }
}

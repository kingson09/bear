package com.bear.core.config;


import com.bear.core.Appender;
import com.bear.core.Event;
import com.bear.core.appender.AppenderRecordingException;
import com.bear.core.filter.Filterable;
import com.bear.core.util.Objects;
import com.bear.core.util.TestClock;


public class AppenderControl {

  private final Appender appender;
  private final String appenderName;
  private final TestClock appenderFilterClock = TestClock.getClock("appenderFilterClock");
  private final TestClock appendersClock = TestClock.getClock("appendersClock");
  private final TestClock callAppenderPreventRecursion = TestClock.getClock("callAppenderPreventRecursion");

  public AppenderControl(final Appender appender) {
    this.appender = appender;
    this.appenderName = appender.getName();
  }


  public String getAppenderName() {
    return appenderName;
  }


  public Appender getAppender() {
    return appender;
  }


  public void callAppender(final Event event) {
    callAppenderPreventRecursion.start();
    callAppender0(event);
    callAppenderPreventRecursion.stop();
  }


  private String appenderErrorHandlerMessage(final String prefix) {
    final String result = createErrorMsg(prefix);
    appender.getHandler().error(result);
    return result;
  }

  private void callAppender0(final Event event) {
    if (!isFilteredByAppender(event)) {
      appendersClock.start();
      tryCallAppender(event);
      appendersClock.stop();
    }
  }

  private void handleError(final String prefix) {
    final String msg = appenderErrorHandlerMessage(prefix);
    if (!appender.ignoreExceptions()) {
      throw new AppenderRecordingException(msg);
    }
  }

  private String createErrorMsg(final String prefix) {
    return prefix + appender.getName();
  }

  private boolean isFilteredByAppender(final Event event) {
    appenderFilterClock.start();
    boolean result = appender instanceof Filterable && ((Filterable) appender).isFiltered(event);
    appenderFilterClock.stop();
    return result;
  }

  private void tryCallAppender(final Event event) {
    try {
      appender.accept(event);
    } catch (final RuntimeException ex) {
      handleAppenderError(ex);
    } catch (final Exception ex) {
      handleAppenderError(new AppenderRecordingException(ex));
    }
  }

  private void handleAppenderError(final RuntimeException ex) {
    appender.getHandler().error(createErrorMsg("An exception occurred processing Appender "), ex);
    if (!appender.ignoreExceptions()) {
      throw ex;
    }
  }

  // AppenderControl is a helper object whose purpose is to make it
  // easier for RecorderConfig to manage and invoke Appenders.
  // RecorderConfig manages Appenders by their name. To facilitate this,
  // two AppenderControl objects are considered equal if and only
  // if they have the same appender name.
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof AppenderControl)) {
      return false;
    }
    final AppenderControl other = (AppenderControl) obj;
    return Objects.equals(appenderName, other.appenderName);
  }

  @Override
  public int hashCode() {
    return appenderName.hashCode();
  }

  @Override
  public String toString() {
    return "[appender=" + appender + ", appenderName=" + appenderName+"]";
  }
}

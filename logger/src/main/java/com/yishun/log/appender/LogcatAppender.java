package com.yishun.log.appender;

import android.util.Log;

import com.bear.core.Event;
import com.bear.core.Filter;
import com.bear.core.Formatter;
import com.bear.core.appender.AbstractAppender;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.config.xml.object.ObjectElement;
import com.bear.core.config.xml.object.ObjectFactory;
import com.yishun.log.filter.Level;
import com.yishun.log.event.LogEvent;


public final class LogcatAppender extends AbstractAppender<LogEvent, String> {

  private LogcatAppender(String name, Formatter<LogEvent, String> format, Filter filter, boolean ignoreExceptions) {
    super(name, filter, format);
  }

  @ObjectFactory
  public static LogcatAppender createAppender(@ObjectAttribute("name") final String name,
      @ObjectAttribute(value = "ignoreExceptions", defaultBoolean = true) final boolean ignoreExceptions,
      @ObjectElement("Formatter") Formatter<LogEvent, String> format, @ObjectElement("filter") final Filter filter) {

    if (name == null) {
      //TODO LOGGER.error("No name provided for LogcatAppender");
      return null;
    }
    //TODO
    //    if (format == null) {
    //      format = PatternFormat.createDefaultFormat();
    //    }
    return new LogcatAppender(name, format, filter, ignoreExceptions);
  }

  @Override
  public void accept(Event event) {
    if (event instanceof LogEvent) {
      append((LogEvent) event);
    }
  }

  @Override
  public void append(LogEvent event) {
    LogEvent e = (LogEvent) event;

    if (e.getLevel() == Level.DEBUG) {
      Log.d(e.getLoggerName(), e.getMessage());
    } else if (e.getLevel() == Level.ERROR || e.getLevel() == Level.FATAL) {
      Log.e(e.getLoggerName(), e.getMessage());
    } else if (e.getLevel() == Level.INFO) {
      Log.i(e.getLoggerName(), e.getMessage());
    } else if (e.getLevel() == Level.WARN) {
      Log.w(e.getLoggerName(), e.getMessage());
    } else {
      Log.d(e.getLoggerName(), e.getMessage());
    }
  }
}
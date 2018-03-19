package com.bear.core.appender;

import com.bear.core.Appender;
import com.bear.core.ErrorHandler;
import com.bear.core.Event;
import com.bear.core.Filter;
import com.bear.core.Formatter;
import com.bear.core.config.Configuration;
import com.bear.core.filter.AbstractFilterable;
import com.bear.core.util.Integers;
import com.bear.core.util.Objects;


public abstract class AbstractAppender<E extends Event, F> extends AbstractFilterable implements Appender<E> {

  public abstract static class Builder<B extends Builder<B>> extends AbstractFilterable.Builder<B> {

    private boolean ignoreExceptions = true;


    private Formatter<?, ?> formatter;

    private String name;

    private Configuration configuration;

    public String getName() {
      return name;
    }

    public boolean isIgnoreExceptions() {
      return ignoreExceptions;
    }

    public Formatter<?, ?> getFormatter() {
      return formatter;
    }

    public B withName(final String name) {
      this.name = name;
      return asBuilder();
    }

    public B withIgnoreExceptions(final boolean ignoreExceptions) {
      this.ignoreExceptions = ignoreExceptions;
      return asBuilder();
    }

    public B withFormatter(final Formatter<?, ?> formatter) {
      this.formatter = formatter;
      return asBuilder();
    }

    public Formatter<?, ?> getOrCreateFormatter() {
      if (formatter == null) {
        //TODO
        //return PatternLayout.createDefaultLayout();
      }
      return formatter;
    }


    @Deprecated
    public B withConfiguration(final Configuration configuration) {
      this.configuration = configuration;
      return asBuilder();
    }

    public B setConfiguration(final Configuration configuration) {
      this.configuration = configuration;
      return asBuilder();
    }

    public Configuration getConfiguration() {
      return configuration;
    }

  }

  private final String name;
  private final boolean ignoreExceptions;
  protected final Formatter<E, F> formatter;
  private ErrorHandler handler = new DefaultErrorHandler(this);


  protected AbstractAppender(final String name, final Filter filter, final Formatter<E, F> formatter) {
    this(name, filter, formatter, true);
  }


  protected AbstractAppender(final String name, final Filter filter, final Formatter<E,F> formatter,
      final boolean ignoreExceptions) {
    super(filter);
    this.name = Objects.requireNonNull(name, "name");
    this.formatter = formatter;
    this.ignoreExceptions = ignoreExceptions;
  }

  public static int parseInt(final String s, final int defaultValue) {
    try {
      return Integers.parseInt(s, defaultValue);
    } catch (final NumberFormatException e) {
      //LOGGER.error("Could not parse \"{}\" as an integer,  using default value {}: {}", s, defaultValue, e);
      return defaultValue;
    }
  }


  public void error(final String msg) {
    handler.error(msg);
  }


  public void error(final String msg, final Event event, final Throwable t) {
    handler.error(msg, event, t);
  }


  public void error(final String msg, final Throwable t) {
    handler.error(msg, t);
  }


  @Override
  public ErrorHandler getHandler() {
    return handler;
  }


  @Override
  public Formatter<E, F> getFormatter() {
    return formatter;
  }


  @Override
  public String getName() {
    return name;
  }


  @Override
  public boolean ignoreExceptions() {
    return ignoreExceptions;
  }


  @Override
  public void setHandler(final ErrorHandler handler) {
    if (handler == null) {
      //LOGGER.error("The handler cannot be set to null");
    }
    this.handler = handler;
  }


  @Override
  public String toString() {
    return name;
  }


}

package com.yishun.log.appender;

import com.bear.core.Filter;
import com.bear.core.Formatter;
import com.bear.core.appender.AbstractAppender;
import com.yishun.log.event.LogEvent;


public abstract class AbstractOutputStreamAppender<M extends OutputStreamManager> extends
    AbstractAppender<LogEvent, byte[]> {

  private static final long serialVersionUID = 1L;


  protected boolean immediateFlush;

  private final M manager;

  protected AbstractOutputStreamAppender(final String name, final Formatter<LogEvent, byte[]> formatter,
      final Filter filter, final boolean ignoreExceptions, final boolean immediateFlush, final M manager) {
    super(name, filter, formatter, ignoreExceptions);
    this.manager = manager;
    this.immediateFlush = immediateFlush;
  }

  public M getManager() {
    return manager;
  }

  @Override
  public void append(final LogEvent event) {
    try {
      byte[] packet;
      if (getFormatter() != null) {
        packet = getFormatter().format(event);
      } else {
        packet = event.toString().getBytes();
      }
      final byte[] bytes = packet;
      if (bytes.length > 0) {
        manager.write(bytes);
        if (this.immediateFlush) {
          manager.flush();
        }
      }
    } catch (final AppenderLoggingException ex) {
      error("Unable to write to stream " + manager.getName() + " for appender " + getName());
    } finally {
    }
  }

  @Override
  public void shutdown() {
    super.shutdown();
    manager.flush();
    this.immediateFlush = true;
  }
}

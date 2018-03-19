package com.yishun.log.appender;

import java.io.IOException;
import java.io.OutputStream;


public abstract class OutputStreamManager extends AbstractManager {

  private volatile OutputStream os;


  protected OutputStreamManager(final OutputStream os, final String streamName) {
    super(streamName);
    this.os = os;
  }


  public static <T> OutputStreamManager getManager(final String name, final T data,
      final ManagerFactory<? extends OutputStreamManager, T> factory) {
    return AbstractManager.getManager(name, factory, data);
  }

  protected OutputStream getOutputStream() {
    return os;
  }

  protected void setOutputStream(final OutputStream os) {
    this.os = os;
  }


  protected synchronized void write(final byte[] bytes, final int offset, final int length) {
    //System.out.println("write " + count);
    try {
      os.write(bytes, offset, length);
    } catch (final IOException ex) {
      final String msg = "Error writing to stream " + getName();
      throw new AppenderLoggingException(msg, ex);
    }
  }


  protected void write(final byte[] bytes) {
    write(bytes, 0, bytes.length);
  }

  protected synchronized void close() {
    final OutputStream stream = os; // access volatile field only once per method
    if (stream == System.out || stream == System.err) {
      return;
    }
    try {
      stream.close();
    } catch (final IOException ex) {
      LOGGER.error("Unable to close stream " + getName() + ". " + ex);
    }
  }


  public synchronized void flush() {
    try {
      os.flush();
    } catch (final Exception ex) {
      final String msg = "Error flushing stream " + getName();
      throw new AppenderLoggingException(msg, ex);
    }
  }
}

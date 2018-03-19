package com.yishun.log.appender;

import com.bear.core.Event;
import com.bear.core.Filter;
import com.bear.core.Formatter;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.config.xml.object.ObjectElement;
import com.bear.core.config.xml.object.ObjectFactory;
import com.bear.core.format.Header;
import com.bear.core.util.Booleans;
import com.bear.core.util.Integers;
import com.yishun.log.event.LogEvent;


public final class FileAppender extends AbstractOutputStreamAppender<FileManager> {

  private static final int DEFAULT_BUFFER_SIZE = 8192;
  private final String fileName;
  private final Header<byte[]> header;

  private FileAppender(final String name, final Formatter<LogEvent, byte[]> formatter, final Header<byte[]> header,
      final Filter filter, final FileManager manager, final String filename, final boolean ignoreExceptions,
      final boolean immediateFlush) {
    super(name, formatter, filter, ignoreExceptions, immediateFlush, manager);
    this.fileName = filename;
    this.header = header;

  }


  public String getFileName() {
    return this.fileName;
  }

  @Override
  public void shutdown() {
    super.shutdown();
  }

  @ObjectFactory
  public static FileAppender createAppender(
      // @formatter:off
            @ObjectAttribute("fileName") final String fileName,
            @ObjectAttribute("append") final String append,
            @ObjectAttribute("locking") final String locking,
            @ObjectAttribute("name") final String name,
            @ObjectAttribute("immediateFlush") final String immediateFlush,
            @ObjectAttribute("ignoreExceptions") final String ignore,
            @ObjectAttribute("bufferedIo") final String bufferedIo,
            @ObjectAttribute("bufferSize") final String bufferSizeStr,
      @ObjectAttribute(value = "compress", defaultBoolean = true) final boolean compress,
            @ObjectElement("header") Header<byte[]> header,
            @ObjectElement("formatter") Formatter<LogEvent,byte[]> formatter,
            @ObjectElement("Filter") final Filter filter) {
        // @formatter:on
    final boolean isAppend = Booleans.parseBoolean(append, true);
    final boolean isLocking = Boolean.parseBoolean(locking);
    boolean isBuffered = Booleans.parseBoolean(bufferedIo, true);
    if (isLocking && isBuffered) {
      if (bufferedIo != null) {
        LOGGER.warn("Locking and buffering are mutually exclusive. No buffering will occur for " + fileName);
      }
      isBuffered = false;
    }
    final int bufferSize = Integers.parseInt(bufferSizeStr, DEFAULT_BUFFER_SIZE);
    if (!isBuffered && bufferSize > 0) {
      LOGGER.warn("The bufferSize is set to {} but bufferedIO is not true: {}", bufferSize, bufferedIo);
    }
    final boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
    final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

    if (name == null) {
      LOGGER.error("No name provided for FileAppender");
      return null;
    }

    if (fileName == null) {
      LOGGER.error("No filename provided for FileAppender with name " + name);
      return null;
    }
    if (formatter == null) {
      //      formatter = Patternformatter.createDefaultformatter();
    }

    final FileManager manager =
        FileManager.getFileManager(fileName, isAppend, isLocking, isBuffered, bufferSize, compress);
    if (manager == null) {
      return null;
    }

    return new FileAppender(name, formatter, header, filter, manager, fileName, ignoreExceptions, isFlush);
  }

  protected void writeHeader() {
    getManager().writeHeader(this.header.getHeader());
  }

  @Override
  public void accept(Event event) {
    if (event instanceof LogEvent) {
      if (this.header != null && getManager().isNewFile()) {
        writeHeader();
      }
      append((LogEvent) event);
    }
  }
}

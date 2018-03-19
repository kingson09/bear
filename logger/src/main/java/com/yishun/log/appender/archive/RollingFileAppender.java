package com.yishun.log.appender.archive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.bear.core.Event;
import com.bear.core.Filter;
import com.bear.core.Formatter;
import com.bear.core.async.Disruptor;
import com.bear.core.async.EventHandler;
import com.bear.core.command.CommandEvent;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.config.xml.object.ObjectElement;
import com.bear.core.config.xml.object.ObjectFactory;
import com.bear.core.format.Header;
import com.bear.core.util.Booleans;
import com.bear.core.util.Integers;
import com.yishun.log.appender.AbstractOutputStreamAppender;
import com.yishun.log.appender.archive.strategy.ArchiveAndRolloverStrategy;
import com.yishun.log.event.LogEvent;
import com.yishun.log.util.TestClock;


public class RollingFileAppender extends AbstractOutputStreamAppender<RollingFileManager> {
  private static final int MAX_QUEUE_SIZE = 256;
  private static final int DEFAULT_BUFFER_SIZE = 256 * 1024;
  private static final String COMMAND_ARCHIVE = "archive";
  private static final String COMMAND_SHUTDOWN = "shutdown";

  private final String fileName;
  private final Header<byte[]> header;

  private final boolean async;
  private Disruptor<Event> disruptor;
  private TestClock clock = TestClock.getClock("RollingFileAppender");

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  private RollingFileAppender(final String name, final boolean async, final Formatter<LogEvent, byte[]> formatter,
      final Header<byte[]> header, final Filter filter, final RollingFileManager manager, final String fileName,
      final boolean ignoreExceptions, final boolean immediateFlush) {
    super(name, formatter, filter, ignoreExceptions, immediateFlush, manager);
    this.fileName = fileName;
    this.header = header;
    this.async = async;
    if (async) {
      final LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
      disruptor = new Disruptor<Event>(eventQueue);
      disruptor.handleEventsWith(new EventHandler<Event>() {
        @Override
        public void onEvent(Event o) {
          realAccept(o);
        }
      });

      disruptor.start();
    }
  }

  @Override
  public void shutdown() {
    if (async) {
      disruptor.shutdown(false, 2000L);
    }
    super.shutdown();
  }

  @Override
  public void accept(final Event event) {
    if (async && !(event instanceof CommandEvent && ((CommandEvent) event).isSync())) {
      disruptor.publishEvent(event);
    } else {
      realAccept(event);
    }

  }

  public void realAccept(Event event) {
    if (event instanceof LogEvent) {
      clock.start();
      append((LogEvent) event);
      clock.stop();
    } else if (event instanceof CommandEvent) {
      onCommand((CommandEvent) event);
    }
  }

  private boolean onCommand(CommandEvent event) {
    if (event.getCommand().equals(COMMAND_ARCHIVE)) {
      getManager().archive();
      event.getHandler().onExecuteComplete(getManager().getArchiveDir());
      return true;
    } else if (event.getCommand().equals(COMMAND_SHUTDOWN)) {
      shutdown();
      return true;
    }
    return false;
  }

  @Override
  public void append(final LogEvent event) {
    getManager().checkArchive(event);
    if (this.header != null && getManager().isNewFile()) {
      writeHeader();
    }
    super.append(event);
  }

  protected void writeHeader() {
    getManager().writeHeader(this.header.getHeader());
  }

  public String getFileName() {
    return fileName;
  }


  @ObjectFactory
  public static RollingFileAppender createAppender(@ObjectAttribute("fileName") final String fileName,
      @ObjectAttribute(value = "async", defaultBoolean = true) final boolean async,
      @ObjectAttribute("archiveDir") final String archiveDir, @ObjectAttribute("append") final String append,
      @ObjectAttribute("name") final String name, @ObjectAttribute("bufferedIO") final String bufferedIO,
      @ObjectAttribute("bufferSize") final String bufferSizeStr,
      @ObjectAttribute(value = "compress", defaultBoolean = true) final boolean compress,
      @ObjectAttribute("immediateFlush") final String immediateFlush,
      @ObjectElement("Strategy") ArchiveAndRolloverStrategy strategy,
      @ObjectElement("Formatter") Formatter<LogEvent, byte[]> formatter, @ObjectElement("header") Header<byte[]> header,
      @ObjectElement("Filter") final Filter filter, @ObjectAttribute("ignoreExceptions") final String ignore) {

    final boolean isAppend = Booleans.parseBoolean(append, true);
    final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
    final boolean isBuffered = Booleans.parseBoolean(bufferedIO, true);
    final boolean isFlush = Booleans.parseBoolean(immediateFlush, false);
    final int bufferSize = Integers.parseInt(bufferSizeStr, DEFAULT_BUFFER_SIZE);
    if (!isBuffered && bufferSize > 0) {
      LOGGER.warn("The bufferSize is set to {} but bufferedIO is not true: {}", bufferSize, bufferedIO);
    }
    if (name == null) {
      LOGGER.error("No name provided for FileAppender");
      return null;
    }

    if (fileName == null) {
      LOGGER.error("No filename was provided for FileAppender with name " + name);
      return null;
    }

    if (strategy == null) {
      LOGGER.error("A TriggeringPolicy must be provided");
      return null;
    }

    final RollingFileManager manager = RollingFileManager
        .getFileManager(fileName, archiveDir.endsWith("/") ? archiveDir : archiveDir + "/", isAppend, isBuffered,
            bufferSize, compress, strategy);
    if (manager == null) {
      return null;
    }

    return new RollingFileAppender(name, async, formatter, header, filter, manager, fileName, ignoreExceptions,
        isFlush);
  }
}

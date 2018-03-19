package com.yishun.log.appender.archive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import com.bear.core.Event;
import com.yishun.log.Constants;
import com.yishun.log.appender.FileManager;
import com.yishun.log.appender.ManagerFactory;
import com.yishun.log.appender.archive.mmap.MappedBufferCreateFailedException;
import com.yishun.log.appender.archive.mmap.MappedBufferedFileOutputStream;
import com.yishun.log.appender.archive.strategy.ArchiveAndRolloverStrategy;
import com.yishun.log.appender.archive.strategy.action.Action;
import com.yishun.log.util.NullOutputStream;
import com.yishun.log.util.TestClock;
import com.yishun.log.util.snappy.MappedSnappyOutputStream;


public class RollingFileManager extends FileManager {
  private static RollingFileManagerFactory factory = new RollingFileManagerFactory();

  private long size;
  private long initialTime;
  private final ArchiveAndRolloverStrategy archiveStrategy;
  private final String archiveDir;
  private final String logsDir;
  private TestClock clock = TestClock.getClock("RollingFileManager");

  protected RollingFileManager(final String fileName, final String archiveDir, final String logsDir,
      final OutputStream os, final boolean append, final long size, final long time,
      final ArchiveAndRolloverStrategy archiveStrategy, final int bufferSize, final boolean compress,
      final boolean isNewFile) {
    super(fileName, os, append, false, bufferSize, compress, isNewFile);
    this.size = size;
    this.initialTime = time;
    this.archiveDir = archiveDir;
    this.logsDir = logsDir;
    archiveStrategy.initialize(this);
    this.archiveStrategy = archiveStrategy;
    pureExpiredFiles(archiveStrategy);
  }


  public static RollingFileManager getFileManager(final String fileName, final String archiveDir, final boolean append,
      final boolean bufferedIO, final int bufferSize, final boolean compress,
      final ArchiveAndRolloverStrategy strategy) {

    return (RollingFileManager) getManager(fileName,
        new FactoryData(archiveDir, append, bufferedIO, bufferSize, compress, strategy), factory);
  }

  @Override
  protected synchronized void write(final byte[] bytes, final int offset, final int length) {
    size += length;
    clock.start();
    super.write(bytes, offset, length);
    clock.stop();
  }


  public long getFileSize() {
    return size;
  }


  public long getFileTime() {
    return initialTime;
  }


  public String getArchiveDir() {
    return archiveDir;
  }


  public synchronized void checkArchive(final Event event) {
    if (archiveStrategy.isTriggeringEvent(event)) {
      archive();
    }
  }

  protected void createFileAfterArchive() throws IOException {
    OutputStream os = new FileOutputStream(getFileName(), isAppend());
    if (getBufferSize() > 0) { // negative buffer size means no buffering
      try {
        os = new MappedBufferedFileOutputStream(this.logsDir + File.separator + "mBuffer",
            Constants.LOG_MAPPED_BUFFER_NAME, getBufferSize(), (FileOutputStream) os);
      } catch (MappedBufferCreateFailedException exception) {
        os = new BufferedOutputStream(os, getBufferSize());
      }
    }
    if (isCompress()) {
      try {
        os = new MappedSnappyOutputStream(os, this.logsDir + File.separator + "mBuffer",
            Constants.LOG_MAPPED_SNAPPY_BUFFER_NAME, 32 * 1024);

      } catch (MappedBufferCreateFailedException exception) {
        os = new NullOutputStream();
      }
    }
    setOutputStream(os);
  }


  public ArchiveAndRolloverStrategy getArchiveStrategy() {
    return this.archiveStrategy;
  }

  private boolean pureExpiredFiles(final ArchiveAndRolloverStrategy strategy) {
    Action pure = strategy.rollover();
    if (pure == null) {
      return false;
    }
    return pure.execute();
  }

  public boolean archive() {
    Action archive = archiveStrategy.archive();
    if (archive == null) {
      return false;
    }
    close();
    boolean result = archive.execute();
    if (result) {
      size = 0;
      initialTime = Calendar.getInstance().get(Calendar.MILLISECOND);
      isNewFile = true;
    }
    try {
      createFileAfterArchive();
    } catch (final IOException ex) {
      LOGGER.error("FileManager (" + getFileName() + ") " + ex);
    }
    return result;
  }


  private static class FactoryData {
    private final boolean compress;
    private final boolean append;
    private final boolean bufferedIO;
    private final int bufferSize;
    private final String archiveDir;
    private final ArchiveAndRolloverStrategy strategy;


    public FactoryData(final String archiveDir, final boolean append, final boolean bufferedIO, final int bufferSize,
        final boolean compress, final ArchiveAndRolloverStrategy strategy) {
      this.archiveDir = archiveDir;
      this.append = append;
      this.bufferedIO = bufferedIO;
      this.bufferSize = bufferSize;
      this.compress = compress;
      this.strategy = strategy;
    }
  }


  private static class RollingFileManagerFactory implements ManagerFactory<RollingFileManager, FactoryData> {


    @Override
    public RollingFileManager createManager(final String name, final FactoryData data) {
      final File file = new File(name);
      final File parent = file.getParentFile();
      if (null != parent && !parent.exists()) {
        parent.mkdirs();
      }
      try {
        file.createNewFile();
      } catch (final IOException ioe) {
        LOGGER.error("Unable to create file " + name, ioe);
        return null;
      }
      long size = data.append ? file.length() : 0;
      OutputStream os;
      try {
        os = new FileOutputStream(name, data.append);
        int bufferSize = data.bufferSize;
        if (data.bufferedIO) {
          try {
            os = new MappedBufferedFileOutputStream(parent.getAbsolutePath() + File.separator + "mBuffer",
                Constants.LOG_MAPPED_BUFFER_NAME, bufferSize, (FileOutputStream) os);
            size += ((MappedBufferedFileOutputStream) os).getBufferSize();
          } catch (MappedBufferCreateFailedException exception) {
            exception.printStackTrace();
            os = new BufferedOutputStream(os, bufferSize);
          }

        } else {
          bufferSize = -1;
        }
        try {
          os = new MappedSnappyOutputStream(os, parent.getAbsolutePath() + File.separator + "mBuffer",
              Constants.LOG_MAPPED_SNAPPY_BUFFER_NAME, 32 * 1024);
        } catch (MappedBufferCreateFailedException exception) {
          return null;
        }

        final long time = file.lastModified();
        return new RollingFileManager(name, data.archiveDir, parent.getAbsolutePath(), os, data.append, size, time,
            data.strategy, bufferSize, data.compress, size == 0);
      } catch (final FileNotFoundException ex) {
        LOGGER.error("FileManager (" + name + ") " + ex);
      }
      return null;
    }
  }
}